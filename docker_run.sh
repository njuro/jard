#!/bin/bash
# script for running jard server as docker container
# don't forget to set enviroment variables in .env file first
docker run -itd --network host --env-file .env --name jard-server njuro/jard-server:latest