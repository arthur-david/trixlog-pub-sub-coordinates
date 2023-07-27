package com.trixlog.gatewayapi.services;

import com.trixlog.gatewayapi.records.CoordinateDataRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class CoordinateReceiverService {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void send(CoordinateDataRecord coordinate) {
        rabbitTemplate.convertAndSend("trixlog.coordinate.ex", "", coordinate);
        log.info("Sending coordinate to queue - Vehicle Plate: {} | Coordinate Date: {} | Latitude: {} | Longitude: {}",
                coordinate.vehiclePlate(),
                coordinate.coordinateDate(),
                coordinate.latitude(),
                coordinate.longitude());
    }
}
