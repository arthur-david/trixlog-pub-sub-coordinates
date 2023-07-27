package com.trixlog.eventprocessor.amqp;

import com.trixlog.eventprocessor.records.CoordinateDataRecord;
import com.trixlog.eventprocessor.services.EventService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CoordinateListener {

    @Autowired
    private EventService eventService;

    @RabbitListener(queues = "trixlog.coordinates.queue")
    public void receiveCoordinate(CoordinateDataRecord coordinate) {
        log.info("New Coordinate received - Vehicle Plate: {} | Coordinate Date: {} | Latitude: {} | Longitude: {}",
                coordinate.vehiclePlate(),
                coordinate.coordinateDate(),
                coordinate.latitude(),
                coordinate.longitude());
    }
}
