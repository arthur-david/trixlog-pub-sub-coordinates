package com.trixlog.eventprocessor.models;

import com.trixlog.eventprocessor.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Event {

    @Id
    private String id;
    private String vehiclePlate;
    private LocalDateTime openedDate;
    private LocalDateTime closedDate;
    private Double latitude;
    private Double longitude;
    private EventType eventType;
    private Double speed;
    private ObjectId openedCoordinateId;
    private ObjectId closedCoordinateId;

    public Event(EventCoordinate eventCoordinate) {
        setVehiclePlate(eventCoordinate.getVehiclePlate());
        setOpenedDate(eventCoordinate.getCoordinateDate());
        setClosedDate(eventCoordinate.getCoordinateDate());
        setLatitude(eventCoordinate.getLatitude());
        setLongitude(eventCoordinate.getLongitude());
        setEventType(eventCoordinate.getEventType());
        setSpeed(eventCoordinate.getSpeed());
        setOpenedCoordinateId(new ObjectId(eventCoordinate.getId()));
        setClosedCoordinateId(new ObjectId(eventCoordinate.getId()));
    }
}

