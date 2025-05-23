// @ts-strict-ignore
import { Component, Input, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { ModalController } from "@ionic/angular";
import { TranslateService } from "@ngx-translate/core";
import { isBefore } from "date-fns";
import { ChannelAddress, Edge, EdgeConfig, Service, Utils, Websocket } from "src/app/shared/shared";
import { Role } from "src/app/shared/type/role";
import { DateTimeUtils } from "src/app/shared/utils/datetime/datetime-utils";
import { environment, Environment } from "src/environments";

@Component({
    selector: "storage-modal",
    templateUrl: "./modal.component.html",
    standalone: false,
})
export class StorageModalComponent implements OnInit, OnDestroy {

    // TODO after refactoring of Model: subscribe to EssActivePowerL1/L2/L3 here instead of Flat Widget

    @Input({ required: true }) protected edge!: Edge;
    @Input() protected component: EdgeConfig.Component | null = null;

    // reference to the Utils method to access via html
    public isLastElement = Utils.isLastElement;

    public formGroup: FormGroup = new FormGroup({});
    protected isAtLeastInstaller: boolean;
    protected isTargetTimeInValid: Map<string, boolean> = new Map();
    protected controllerIsRequiredEdgeVersion: boolean = false;
    protected hasRequiredEdgeVersion: boolean = false;
    protected config: EdgeConfig;
    protected essComponents: EdgeConfig.Component[] | null = null;
    protected chargerComponents!: EdgeConfig.Component[];
    protected readonly environment: Environment = environment;

    constructor(
        public service: Service,
        public translate: TranslateService,
        public modalCtrl: ModalController,
        public websocket: Websocket,
        public formBuilder: FormBuilder,
    ) { }

    ngOnInit() {
        this.edge.getFirstValidConfig(this.websocket).then(config => {
            this.config = config;
            this.essComponents = this.config
                .getComponentsImplementingNature("io.openems.edge.ess.api.SymmetricEss")
                .filter(component => component.isEnabled && !this.config
                    .getNatureIdsByFactoryId(component.factoryId)
                    .includes("io.openems.edge.ess.api.MetaEss"));

            this.chargerComponents = this.config
                .getComponentsImplementingNature("io.openems.edge.ess.dccharger.api.EssDcCharger")
                .filter(component => component.isEnabled);

            // Future Work: Remove when all ems are at least at this version
            this.controllerIsRequiredEdgeVersion = this.edge.isVersionAtLeast("2023.2.5");

            this.isAtLeastInstaller = this.edge.roleIsAtLeast(Role.INSTALLER);
            const emergencyReserveCtrl = this.config.getComponentsByFactory("Controller.Ess.EmergencyCapacityReserve");
            const prepareBatteryExtensionCtrl = this.config.getComponentsByFactory("Controller.Ess.PrepareBatteryExtension");
            this.hasRequiredEdgeVersion = this.edge.isVersionAtLeast("2024.12.3");
            const components = [...prepareBatteryExtensionCtrl, ...emergencyReserveCtrl].filter(component => component.isEnabled).reduce((result, component) => {
                const essId = component.properties["ess.id"];
                if (result[essId] == null) {
                    result[essId] = [];
                }
                result[essId].push(component);
                return result;
            }, {});

            const channelAddresses: ChannelAddress[] = [];
            channelAddresses.push(...this.chargerComponents.map(comp => new ChannelAddress(comp.id, "ActualPower")));

            if (this.hasRequiredEdgeVersion) {
                channelAddresses.push(new ChannelAddress("_meta", "IsEssChargeFromGridAllowed"));
            }
            for (const essId in prepareBatteryExtensionCtrl) {
                const controller = prepareBatteryExtensionCtrl[essId];
                channelAddresses.push(
                    new ChannelAddress(controller.id, "_PropertyIsRunning"),
                    new ChannelAddress(controller.id, "_PropertyTargetTime"),
                    new ChannelAddress(controller.id, "_PropertyTargetTimeSpecified"),
                    new ChannelAddress(controller.id, "_PropertyTargetSoc"),
                    new ChannelAddress(controller.id, "_PropertyTargetTimeBuffer"),
                    new ChannelAddress(controller.id, "ExpectedStartEpochSeconds"),
                );
            }
            this.edge.subscribeChannels(this.websocket, "storage", channelAddresses);

            this.edge.currentData
                .subscribe(currentData => {

                    const controls: FormGroup = new FormGroup({});
                    if (this.hasRequiredEdgeVersion) {
                        controls.addControl("_meta", this.formBuilder.group({
                            isEssChargeFromGridAllowed: new FormControl(currentData.channel["_meta/IsEssChargeFromGridAllowed"]),
                        }));
                    }
                    for (const essId of Object.keys(components)) {
                        const controllers = components[essId];

                        const controllerFrmGrp: FormGroup = new FormGroup({});
                        for (const controller of (controllers as EdgeConfig.Component[])) {

                            if (controller.factoryId == "Controller.Ess.EmergencyCapacityReserve") {
                                const reserveSoc = currentData.channel[controller.id + "/_PropertyReserveSoc"] ?? 20 /* default Reserve-Soc */;
                                const isReserveSocEnabled = currentData.channel[controller.id + "/_PropertyIsReserveSocEnabled"] == 1;

                                controllerFrmGrp.addControl("emergencyReserveController",
                                    this.formBuilder.group({
                                        controllerId: new FormControl(controller["id"]),
                                        isReserveSocEnabled: new FormControl(isReserveSocEnabled),
                                        reserveSoc: new FormControl(reserveSoc),
                                    }),
                                );
                            } else if (controller.factoryId == "Controller.Ess.PrepareBatteryExtension") {

                                const isRunning = currentData.channel[controller.id + "/_PropertyIsRunning"] == 1;

                                // Because of ionic segment buttons only accepting a string value, i needed to convert it
                                const targetTimeSpecified = (currentData.channel[controller.id + "/_PropertyTargetTimeSpecified"] == 1).toString();
                                let targetTime = currentData.channel[controller.id + "/_PropertyTargetTime"];
                                const targetSoc = currentData.channel[controller.id + "/_PropertyTargetSoc"];
                                const targetTimeBuffer = currentData.channel[controller.id + "/_PropertyTargetTimeBuffer"];
                                const epochSeconds = currentData.channel[controller.id + "/ExpectedStartEpochSeconds"];

                                const expectedStartOfPreparation = new Date(0);
                                expectedStartOfPreparation.setUTCSeconds(epochSeconds ?? 0);

                                // If targetTime not set, not equals 0 or targetTime is no valid time,
                                // then set targetTime to null
                                if (!targetTime || targetTime == 0 || isNaN(Date.parse(targetTime))) {
                                    targetTime = null;
                                }

                                // Channel "ExpectedStartEpochSeconds" is not set
                                if ((epochSeconds == null
                                    || epochSeconds == 0)) {
                                    this.isTargetTimeInValid.set(essId, true);
                                } else if

                                    // If expected expectedStartOfpreparation is after targetTime
                                    //  Guarantee, that the TargetSoc should be reached after the preparation to reach that Soc started
                                    (isBefore(new Date(targetTime), expectedStartOfPreparation)
                                    || isBefore(new Date(targetTime), new Date())) {
                                    this.isTargetTimeInValid.set(essId, true);
                                } else {
                                    this.isTargetTimeInValid.set(essId, false);
                                }

                                controllerFrmGrp.addControl("prepareBatteryExtensionController",
                                    this.formBuilder.group({
                                        controllerId: new FormControl(controller.id),
                                        isRunning: new FormControl(isRunning),
                                        targetTime: new FormControl(targetTime),
                                        targetTimeSpecified: new FormControl(targetTimeSpecified),
                                        targetSoc: new FormControl(targetSoc),
                                        targetTimeBuffer: new FormControl(targetTimeBuffer),
                                        expectedStartOfPreparation: new FormControl(expectedStartOfPreparation),
                                    }),
                                );
                            }
                        }
                        controls.addControl(essId, controllerFrmGrp);
                    }

                    if (!this.formGroup.dirty) {
                        this.formGroup = controls;
                    }
                });
        },
        );

    }

    async applyChanges() {
        if (this.edge == null) {
            return;
        }
        const updateArray: Map<string, Array<Map<string, any>>> = new Map();
        if (this.hasRequiredEdgeVersion) {
            const metaFormGroup = (this.formGroup.get("_meta") as FormGroup)?.controls ?? [];
            for (const prop of Object.keys(metaFormGroup)) {
                if (metaFormGroup[prop].dirty) {
                    if (updateArray.get("_meta")) {
                        updateArray.get("_meta").push(new Map().set(prop, metaFormGroup[prop].value));
                    } else {
                        updateArray.set("_meta", [new Map().set(prop, metaFormGroup[prop].value)]);
                    }
                }
            }
        }

        for (const essId in this.formGroup.controls) {
            const essGroups = this.formGroup.controls[essId];

            const emergencyReserveController = (essGroups.get("emergencyReserveController") as FormGroup)?.controls ?? {};
            for (const essGroup of Object.keys(emergencyReserveController)) {
                if (emergencyReserveController[essGroup].dirty) {
                    if (updateArray.get(emergencyReserveController["controllerId"].value)) {
                        updateArray.get(emergencyReserveController["controllerId"].value).push(new Map().set(essGroup, emergencyReserveController[essGroup].value));
                    } else {
                        updateArray.set(emergencyReserveController["controllerId"].value, [new Map().set(essGroup, emergencyReserveController[essGroup].value)]);
                    }
                }

            }
            const prepareBatteryExtensionController = (essGroups.get("prepareBatteryExtensionController") as FormGroup)?.controls ?? {};
            for (const essGroup of Object.keys(prepareBatteryExtensionController)) {
                if (prepareBatteryExtensionController[essGroup].dirty) {

                    // For simplicity, split targetTimeSpecified in 2 for template formControlName
                    if (updateArray.get(prepareBatteryExtensionController["controllerId"].value)) {
                        updateArray.get(prepareBatteryExtensionController["controllerId"].value).push(new Map().set(essGroup, prepareBatteryExtensionController[essGroup].value));
                    } else {
                        updateArray.set(prepareBatteryExtensionController["controllerId"].value, [new Map().set(essGroup, prepareBatteryExtensionController[essGroup].value)]);
                    }
                }
            }
        }

        for (const controllerId of updateArray.keys()) {
            const controllers = updateArray.get(controllerId);
            const properties: { name: string, value: any }[] = [];
            controllers.forEach((element) => {
                const name = element.keys().next().value;
                const rawValue = element.values().next().value;
                let value = rawValue;

                // Needs to be done to get Datetime string in this format: YYYY-MM-DDTHH:mm:ssTZD
                if (name === "targetTime") {
                    value = DateTimeUtils.formatToISOZonedDateTime(rawValue);
                }

                properties.push({
                    name: name,
                    value: value,
                });
            });

            try {
                if (controllerId === "_meta") {
                    await this.edge.updateAppConfig(this.websocket, controllerId, properties);
                } else {
                    await this.edge.updateComponentConfig(this.websocket, controllerId, properties);
                }
                this.service.toast(this.translate.instant("General.changeAccepted"), "success");
                this.formGroup.markAsPristine();

            } catch (reason) {
                this.service.toast(this.translate.instant("General.changeFailed") + "\n" + reason, "danger");
            }

        }
    }

    ngOnDestroy() {
        this.edge.unsubscribeChannels(this.websocket, "storage");
    }

}
