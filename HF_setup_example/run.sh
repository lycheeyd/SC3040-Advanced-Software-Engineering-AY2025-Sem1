#!/bin/bash

# Start Calowin app on port 8081
java -Dserver.port=8080 -jar app.jar &

# Start HF UI app on port 7860
java -Dserver.port=7860 -jar uiapp.jar