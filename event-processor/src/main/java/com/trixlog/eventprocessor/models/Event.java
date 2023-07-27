package com.trixlog.eventprocessor.models;

import com.trixlog.eventprocessor.enums.EventType;
import com.trixlog.eventprocessor.records.CoordinateDataRecord;
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

    public Event(EventType eventType, CoordinateDataRecord coordinate) {
        setVehiclePlate(coordinate.vehiclePlate());
        setOpenedDate(coordinate.coordinateDate());
        setClosedDate(coordinate.coordinateDate());
        setLatitude(coordinate.latitude());
        setLongitude(coordinate.longitude());
        setEventType(eventType);
        setSpeed(coordinate.speed());
        setOpenedCoordinateId(new ObjectId());
        setClosedCoordinateId(new ObjectId());
    }
}

