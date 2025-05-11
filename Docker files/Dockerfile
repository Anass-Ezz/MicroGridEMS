# Use a multi-arch JRE 21 image (Temurin is available for ARM64)
FROM eclipse-temurin:21-jre-alpine

# Set working directory inside the container
WORKDIR /usr/lib/openems

# Copy the built OpenEMS Edge JAR file into the container
COPY openems-edge.jar .

# Expose port 8080 for the Apache Felix Web Console
EXPOSE 8080 8081

# Run the JAR with the configuration directory set to /etc/openems.d
CMD ["java", "-Dfelix.cm.dir=/etc/openems.d", "-jar", "openems-edge.jar"]


