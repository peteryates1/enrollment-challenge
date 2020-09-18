#!/bin/bash
docker build -t ntt/challenge .
docker create --name challenge -p 8084:8084 ntt/challenge
docker start challenge
