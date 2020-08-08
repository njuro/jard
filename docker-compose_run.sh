#!/bin/bash
docker-compose up -d && docker exec -d -u 0 jard-server chown cnb -R /home/cnb/jard-usercontent
