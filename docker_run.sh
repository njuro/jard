#!/bin/bash
# script for running jard server as docker container
# don't forget to set enviroment variables in .env file first
docker run -itd --network host --restart unless-stopped --env-file .env --mount 'type=volume,src=jard-usercontent,dst=/home/cnb/jard-usercontent' --name jard-server njuro/jard-server:latest &&
  docker exec -d -u 0 jard-server chown cnb -R /home/cnb/jard-usercontent
