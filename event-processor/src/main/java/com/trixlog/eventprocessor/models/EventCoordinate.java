package com.trixlog.eventprocessor.models;

import com.trixlog.eventprocessor.enums.EventType;
import com.trixlog.eventprocessor.records.CoordinateDataRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class EventCoordinate {

    @Id
    private String id;
    private EventType eventType;
    private LocalDateTime coordinateDate;
    private String vehiclePlate;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double acceleration;
    private Boolean ignition;
    private Long hodometer;
    private Long rpm;
    private Long transmissionReasonId;
    private Long packetCounter;
    private Double batteryVoltage;
    private Double temperature;
    private LocalDateTime createdAt;

    public EventCoordinate(EventType eventType, CoordinateDataRecord coordinate) {
        setEventType(eventType);
        setCoordinateDate(coordinate.coordinateDate());
        setVehiclePlate(coordinate.vehiclePlate());
        setLatitude(coordinate.latitude());
        setLongitude(coordinate.longitude());
        setSpeed(coordinate.speed());
        setAcceleration(coordinate.acceleration());
        setIgnition(coordinate.ignition());
        setHodometer(coordinate.hodometer());
        setRpm(coordinate.rpm());
        setTransmissionReasonId(coordinate.transmissionReasonId());
        setPacketCounter(coordinate.packetCounter());
        setBatteryVoltage(coordinate.batteryVoltage());
        setTemperature(coordinate.temperature());
        setCreatedAt(LocalDateTime.now());
    }
}
