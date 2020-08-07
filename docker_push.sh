#!/bin/bash
# script for pushing jard server Docker image to public repository
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker tag jard-server "$DOCKER_USERNAME"/jard-server
docker push "$DOCKER_USERNAME"/jard-server
