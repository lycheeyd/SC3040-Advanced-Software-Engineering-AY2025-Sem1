FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y wget unzip jq && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build-vars.env .

RUN --mount=type=secret,id=GH_PAT,mode=0444,required=true \
    export GITHUB_TOKEN=$(cat /run/secrets/GH_PAT)

RUN export $(grep -v '^#' build-vars.env | xargs)

RUN echo "Fetching artifact for service: ${SERVICE_NAME} from run: ${GITHUB_RUN_ID}" && \
    wget --header="Authorization: token ${GITHUB_TOKEN}" \
         "https://api.github.com/repos/${GITHUB_REPO}/actions/runs/${GITHUB_RUN_ID}/artifacts" \
         -O artifacts.json && \
    JAR_URL=$(jq -r ".artifacts[] | select(.name==\"${SERVICE_NAME}-jar\") | .archive_download_url" artifacts.json) && \
    if [ -z "$JAR_URL" ]; then echo "Artifact not found!"; exit 1; fi && \
    echo "Found artifact URL: $JAR_URL" && \
    wget --header="Authorization: token ${GITHUB_TOKEN}" "$JAR_URL" -O jar.zip && \
    unzip jar.zip && \
    rm jar.zip artifacts.json && \
    ls -lh

EXPOSE 8080

CMD ["sh", "-c", "exec java -jar /app/*.jar"]