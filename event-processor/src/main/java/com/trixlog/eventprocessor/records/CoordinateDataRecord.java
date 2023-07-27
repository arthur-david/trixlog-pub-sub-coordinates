package com.trixlog.eventprocessor.records;

import java.time.LocalDateTime;


public record CoordinateDataRecord(
        LocalDateTime coordinateDate,
        String vehiclePlate,
        Double latitude,
        Double longitude,
        Double speed,
        Double acceleration,
        Boolean ignition,
        Long hodometer,
        Long rpm,
        Long transmissionReasonId,
        Long packetCounter,
        Double batteryVoltage,
        Double temperature
) { }
