package com.trixlog.gatewayapi.services;

import com.trixlog.gatewayapi.records.CoordinateDataRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CoordinateReceiverService {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void send(CoordinateDataRecord coordinateData) {
        rabbitTemplate.convertAndSend("trixlog.coordinates", coordinateData);
    }
}
