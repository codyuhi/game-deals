#!/bin/zsh

docker build -t game-deals .
docker run -p 80:80 game-deals