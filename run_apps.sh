#!/bin/bash

docker compose up -d

sleep 5

cd gateway-api
./run_gateway_api.sh

cd ..

cd event-processor
./run_event_processor.sh
