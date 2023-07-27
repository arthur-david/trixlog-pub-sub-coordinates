package com.trixlog.eventprocessor.enums;

import com.trixlog.eventprocessor.records.CoordinateDataRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum EventType {
    HARD_ACCELERATION(coordinate -> coordinate.transmissionReasonId() == 104 && Double.compare(coordinate.acceleration(), 0.22) > 0),
    HARD_BRAKING(coordinate -> coordinate.transmissionReasonId() == 105 && Double.compare(coordinate.acceleration(), 0.17) > 0),
    HARD_TURNING(coordinate -> coordinate.transmissionReasonId() == 106 && Double.compare(coordinate.acceleration(), 0.30) > 0),
    SPEEDING(coordinate -> Double.compare(coordinate.speed(), 80) > 0),
    OVER_TEMPERATURE(coordinate -> Double.compare(coordinate.temperature(), 115) > 0),
    LOW_BATTERY(coordinate -> Double.compare(coordinate.batteryVoltage(), 21) < 0);

    private final Predicate<CoordinateDataRecord> eventRule;

    public static EventType fetchEventTypeByCoordinate(CoordinateDataRecord coordinate) {
        for (EventType eventType : values()) {
            if (eventType.eventRule.test(coordinate)) {
                return eventType;
            }
        }
        return null;
    }
}
