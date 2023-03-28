#!/bin/bash

JAR_PATH=$(ls -t ~/build/*.jar | head -n 1)

JAR_NAME=$(basename $JAR_PATH)

RESOURCES_PATH=/home/ubuntu/resources

cp /home/ubuntu/build/$JAR_NAME /home/ubuntu/deploy/

echo "[Deploy] : Running new application"

nohup java -jar -Dspring.profiles.active=prod \
        -Dspring.config.additional-location=$RESOURCES_PATH/application-production.yml \
        /home/ubuntu/deploy/$JAR_NAME > /home/ubuntu/deploy/nohup.out 2>&1 &
