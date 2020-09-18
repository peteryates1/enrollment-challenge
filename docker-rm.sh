#!/bin/bash
docker stop challenge
docker container rm challenge
docker image rm ntt/challenge:latest
