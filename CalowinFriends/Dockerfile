FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y wget unzip jq && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build-vars.env .

RUN --mount=type=secret,id=GITHUB_TOKEN \
    GITHUB_TOKEN=$(cat /run/secrets/GITHUB_TOKEN) && \
    export $(grep -v '^#' build-vars.env | xargs) && \
    echo "Fetching artifact for service: ${SERVICE_NAME} from run: ${GITHUB_RUN_ID}" && \
    ARTIFACT_JSON=$(curl -s -H "Authorization: token $GITHUB_TOKEN" \
                        "https://api.github.com/repos/${GITHUB_REPO}/actions/runs/${GITHUB_RUN_ID}/artifacts") && \
    JAR_URL=$(echo "$ARTIFACT_JSON" | jq -r ".artifacts[] | select(.name==\"${SERVICE_NAME}-jar\") | .archive_download_url") && \
    if [ -z "$JAR_URL" ]; then echo "Artifact not found!"; exit 1; fi && \
    echo "Found artifact URL: $JAR_URL" && \
    curl -L --header "Authorization: token $GITHUB_TOKEN" --remote-header-name --remote-name "$JAR_URL" && \
    unzip -o *.zip && rm *.zip && ls -lh

EXPOSE 8080

CMD ["sh", "-c", "exec java -jar /app/*.jar"]
