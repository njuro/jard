#!/bin/bash
# script for pushing jard server Docker image to public repository
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push njuro/jard-server:latest