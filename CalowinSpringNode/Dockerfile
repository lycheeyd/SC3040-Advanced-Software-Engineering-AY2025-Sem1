FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR into the image
COPY Calowin/CalowinSpringNode.jar /app/app.jar
COPY HF/huggingface-UI.jar /app/uiapp.jar
COPY run.sh /app/run.sh

EXPOSE 7860 8081
ENV JAVA_OPTS="-Xmx1g -Xms512m"
ENV SERVER_PORT=8081

# Make sure the script is executable and has proper line endings
RUN chmod +x /app/run.sh && \
    sed -i 's/\r$//' /app/run.sh

CMD ["/bin/sh", "/app/run.sh"]
