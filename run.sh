#!/bin/zsh

# Build the Docker image
docker build -t harbor.minipc.local/library/game-deals:latest .

# Push the Docker image to the local Harbor registry
docker push harbor.minipc.local/library/game-deals:latest
