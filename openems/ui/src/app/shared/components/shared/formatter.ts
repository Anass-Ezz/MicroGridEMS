import { formatNumber } from "@angular/common";
import { Currency } from "../../shared";
import { Language } from "../../type/language";

export namespace Formatter {

  // Changes the number format based on the language selected.
  const locale: string = (Language.getByKey(localStorage.LANGUAGE) ?? Language.DEFAULT).i18nLocaleKey;

  export const FORMAT_WATT = (value: number) => {
    return formatNumber(value, locale, "1.0-0") + " W";
  };

  export const FORMAT_KILO_WATT = (value: number) => {
    return formatNumber(value, locale, "1.0-2") + " kW";
  };

  export const FORMAT_KILO_WATT_HOURS = (value: number) => {
    return formatNumber(value, locale, "1.0-0") + " kWh";
  };

  export const FORMAT_VOLT = (value: number) => {
    return formatNumber(value, locale, "1.0-0") + " V";
  };

  export const FORMAT_AMPERE = (value: number) => {
    return formatNumber(value, locale, "1.1-1") + " A";
  };

  export const FORMAT_CELSIUS = (value: number) => {
    return formatNumber(value, locale, "1.0-0") + " °C";
  };

  export const FORMAT_PERCENT = (value: number) => {
    return formatNumber(value, locale, "1.0-0") + " %";
  };

  export const FORMAT_BAR = (value: number) => {
    return formatNumber(value, locale, "1.1-1") + " mbar";
  };

  export const FORMAT_CURRENCY_PER_KWH = (value: number | string, currency: string = Currency.Unit.CENT) => {
    return formatNumber(parseInt(value.toString()), locale, "1.0-2") + " " + Currency.getCurrencyLabelByCurrency(currency);
  };

  export const formatSafely = (value: number | string | null, format: string) => {

    if (value == null) {
      return value;
    }

    return formatNumber(parseFloat(value.toString()), locale, format);
  };
}

