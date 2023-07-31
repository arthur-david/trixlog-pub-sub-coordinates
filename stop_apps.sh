#!/bin/bash

docker compose down

cd gateway-api
./stop_gateway_api.sh

cd ..

cd event-processor
./stop_event_processor.sh