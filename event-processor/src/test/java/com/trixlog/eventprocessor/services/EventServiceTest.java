package com.trixlog.eventprocessor.services;

import com.trixlog.eventprocessor.enums.EventType;
import com.trixlog.eventprocessor.models.Event;
import com.trixlog.eventprocessor.models.EventCoordinate;
import com.trixlog.eventprocessor.records.CoordinateDataRecord;
import com.trixlog.eventprocessor.repositories.EventCoordinateRepository;
import com.trixlog.eventprocessor.repositories.EventRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@AutoConfigureDataMongo
@SpringBootTest
class EventServiceTest {

    public static final Double SPEED = 50.0;
    public static final Double ACCELERATION = 0.22;
    public static final Long TRANSMISSION_REASON_ID = 101L;
    public static final Double BATTERY_VOLTAGE = 23.0;
    public static final Double TEMPERATURE = 90.0;

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventCoordinateRepository eventCoordinateRepository;

    @Test
    void shouldReturnNoEventType() {
        EventType eventType = assertDoesNotThrow(() -> EventType.fetchEventTypeByCoordinate(startCoordinateDataRecord()));

        assertNull(eventType);
    }

    @Test
    void shouldReturnEventTypeHardAcceleration() {
        CoordinateDataRecord coordinateDataRecord = startCoordinateDataRecord(SPEED, 0.23, 104L, BATTERY_VOLTAGE, TEMPERATURE);
        EventType eventType = assertDoesNotThrow(() -> EventType.fetchEventTypeByCoordinate(coordinateDataRecord));

        assertNotNull(eventType);
        assertEquals(EventType.HARD_ACCELERATION, eventType);
    }

    @Test
    void shouldReturnEventTypeHardBraking() {
        CoordinateDataRecord coordinateDataRecord = startCoordinateDataRecord(SPEED, 0.23, 105L, BATTERY_VOLTAGE, TEMPERATURE);
        EventType eventType = assertDoesNotThrow(() -> EventType.fetchEventTypeByCoordinate(coordinateDataRecord));

        assertNotNull(eventType);
        assertEquals(EventType.HARD_BRAKING, eventType);
    }

    @Test
    void shouldReturnEventTypeHardTurning() {
        CoordinateDataRecord coordinateDataRecord = startCoordinateDataRecord(SPEED, 0.40, 106L, BATTERY_VOLTAGE, TEMPERATURE);
        EventType eventType = assertDoesNotThrow(() -> EventType.fetchEventTypeByCoordinate(coordinateDataRecord));

        assertNotNull(eventType);
        assertEquals(EventType.HARD_TURNING, eventType);
    }

    @Test
    void shouldReturnEventTypeSpeeding() {
        CoordinateDataRecord coordinateDataRecord = startCoordinateDataRecord(100.0, ACCELERATION, TRANSMISSION_REASON_ID, BATTERY_VOLTAGE, TEMPERATURE);
        EventType eventType = assertDoesNotThrow(() -> EventType.fetchEventTypeByCoordinate(coordinateDataRecord));

        assertNotNull(eventType);
        assertEquals(EventType.SPEEDING, eventType);
    }

    @Test
    void shouldReturnEventTypeOverTemperature() {
        CoordinateDataRecord coordinateDataRecord = startCoordinateDataRecord(SPEED, ACCELERATION, TRANSMISSION_REASON_ID, BATTERY_VOLTAGE, 200.0);
        EventType eventType = assertDoesNotThrow(() -> EventType.fetchEventTypeByCoordinate(coordinateDataRecord));

        assertNotNull(eventType);
        assertEquals(EventType.OVER_TEMPERATURE, eventType);
    }

    @Test
    void shouldReturnEventTypeLowBattery() {
        CoordinateDataRecord coordinateDataRecord = startCoordinateDataRecord(SPEED, ACCELERATION, TRANSMISSION_REASON_ID, 19.0, TEMPERATURE);
        EventType eventType = assertDoesNotThrow(() -> EventType.fetchEventTypeByCoordinate(coordinateDataRecord));

        assertNotNull(eventType);
        assertEquals(EventType.LOW_BATTERY, eventType);
    }

    @Test
    void shouldCheckCoordinateAndCreateEvent() {
        EventCoordinate lastEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.SPEEDING, startCoordinateDataRecord());
        EventCoordinate newEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.LOW_BATTERY, startCoordinateDataRecord());

        Page<EventCoordinate> pageResult = new PageImpl<>(List.of(lastEventCoordinate));

        when(eventCoordinateRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(eventCoordinateRepository.save(any())).thenReturn(newEventCoordinate);

        assertDoesNotThrow(() -> eventService.checkCoordinate(startCoordinateDataRecord(SPEED, ACCELERATION, TRANSMISSION_REASON_ID, 20.0, TEMPERATURE)));

        verify(eventCoordinateRepository).findAll(any(Pageable.class));
        verify(eventCoordinateRepository).save(any(EventCoordinate.class));
        verify(eventRepository).save(any(Event.class));
        verify(eventRepository, times(0)).findByClosedCoordinateId(anyString());
    }

    @Test
    void shouldCheckCoordinateAndUpdateEvent() {
        EventCoordinate lastEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.LOW_BATTERY, startCoordinateDataRecord());
        EventCoordinate newEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.LOW_BATTERY, startCoordinateDataRecord());
        Event event = startEvent(lastEventCoordinate);

        Page<EventCoordinate> pageResult = new PageImpl<>(List.of(lastEventCoordinate));

        when(eventCoordinateRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(eventCoordinateRepository.save(any())).thenReturn(newEventCoordinate);
        when(eventRepository.findByClosedCoordinateId(eq(lastEventCoordinate.getId()))).thenReturn(event);

        assertDoesNotThrow(() -> eventService.checkCoordinate(startCoordinateDataRecord(SPEED, ACCELERATION, TRANSMISSION_REASON_ID, 20.0, TEMPERATURE)));

        verify(eventCoordinateRepository).findAll(any(Pageable.class));
        verify(eventCoordinateRepository).save(any(EventCoordinate.class));
        verify(eventRepository).save(any(Event.class));
        verify(eventRepository).findByClosedCoordinateId(anyString());
    }

    @Test
    void shouldCheckCoordinateAndDoNothing() {
        assertDoesNotThrow(() -> eventService.checkCoordinate(startCoordinateDataRecord()));

        verify(eventCoordinateRepository, times(0)).findAll(any(Pageable.class));
        verify(eventCoordinateRepository, times(0)).save(any(EventCoordinate.class));
        verify(eventRepository, times(0)).save(any(Event.class));
        verify(eventRepository, times(0)).findByClosedCoordinateId(anyString());
    }

    @Test
    void shouldCheckEventAndCreateNewEventWhenAreOfEventTypeDifferent() {
        EventCoordinate lastEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.SPEEDING, startCoordinateDataRecord());
        EventCoordinate newEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.LOW_BATTERY, startCoordinateDataRecord());

        Page<EventCoordinate> pageResult = new PageImpl<>(List.of(lastEventCoordinate));
        when(eventCoordinateRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(eventCoordinateRepository.save(any())).thenReturn(newEventCoordinate);

        assertDoesNotThrow(() -> eventService.checkEvent(EventType.LOW_BATTERY, startCoordinateDataRecord()));

        verify(eventCoordinateRepository).findAll(any(Pageable.class));
        verify(eventCoordinateRepository).save(any(EventCoordinate.class));
        verify(eventRepository).save(any(Event.class));
        verify(eventRepository, times(0)).findByClosedCoordinateId(anyString());
    }

    @Test
    void shouldCheckEventAndCreateNewEventWhenNoPreviousEvent() {
        EventCoordinate newEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.LOW_BATTERY, startCoordinateDataRecord());

        Page<EventCoordinate> pageResult = new PageImpl<>(List.of());
        when(eventCoordinateRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(eventCoordinateRepository.save(any())).thenReturn(newEventCoordinate);

        assertDoesNotThrow(() -> eventService.checkEvent(EventType.LOW_BATTERY, startCoordinateDataRecord()));

        verify(eventCoordinateRepository).findAll(any(Pageable.class));
        verify(eventCoordinateRepository).save(any(EventCoordinate.class));
        verify(eventRepository).save(any(Event.class));
        verify(eventRepository, times(0)).findByClosedCoordinateId(anyString());
    }

    @Test
    void shouldCheckEventAndUpdateEventWhenOfSameEventType() {
        EventCoordinate lastEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.SPEEDING, startCoordinateDataRecord());
        EventCoordinate newEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.SPEEDING, startCoordinateDataRecord());
        Event event = startEvent(lastEventCoordinate);

        Page<EventCoordinate> pageResult = new PageImpl<>(List.of(lastEventCoordinate));
        when(eventCoordinateRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        when(eventCoordinateRepository.save(any())).thenReturn(newEventCoordinate);
        when(eventRepository.findByClosedCoordinateId(eq(lastEventCoordinate.getId()))).thenReturn(event);

        assertDoesNotThrow(() -> eventService.checkEvent(EventType.SPEEDING, startCoordinateDataRecord()));

        verify(eventCoordinateRepository).findAll(any(Pageable.class));
        verify(eventCoordinateRepository).save(any(EventCoordinate.class));
        verify(eventRepository).save(any(Event.class));
        verify(eventRepository).findByClosedCoordinateId(anyString());
    }

    @Test
    void shouldCreateEventCoordinate() {
        assertDoesNotThrow(() -> eventService.createEventCoordinate(EventType.HARD_TURNING, startCoordinateDataRecord()));

        verify(eventCoordinateRepository).save(any(EventCoordinate.class));
    }

    @Test
    void shouldDontCreateEventCoordinateWhenParametersIsNull() {
        assertThrows(Exception.class, () -> eventService.createEventCoordinate(null, null));

        verify(eventCoordinateRepository, times(0)).save(any(EventCoordinate.class));
    }

    @Test
    void shouldCreateEvent() {
        assertDoesNotThrow(() -> eventService.createEvent(startEventCoordinate(ObjectId.get().toHexString(), EventType.HARD_TURNING, startCoordinateDataRecord())));

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldDontCreateEventWhenEventCoordinateIsNull() {
        assertThrows(Exception.class, () -> eventService.createEvent(null));

        verify(eventRepository, times(0)).save(any(Event.class));
    }

    @Test
    void shouldUpdateEvent() {
        EventCoordinate lastEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.HARD_ACCELERATION, startCoordinateDataRecord());
        EventCoordinate newEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.HARD_ACCELERATION, startCoordinateDataRecord());
        Event event = startEvent(lastEventCoordinate);
        when(eventRepository.findByClosedCoordinateId(eq(lastEventCoordinate.getId()))).thenReturn(event);

        assertDoesNotThrow(()-> eventService.updateEvent(lastEventCoordinate, newEventCoordinate));

        verify(eventRepository).findByClosedCoordinateId(anyString());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldDontUpdateEventWhenNotFoundedByClosedCoordinateId() {
        EventCoordinate lastEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.HARD_ACCELERATION, startCoordinateDataRecord());
        EventCoordinate newEventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.HARD_ACCELERATION, startCoordinateDataRecord());
        when(eventRepository.findByClosedCoordinateId(eq(lastEventCoordinate.getId()))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, ()-> eventService.updateEvent(lastEventCoordinate, newEventCoordinate));

        verify(eventRepository).findByClosedCoordinateId(anyString());
        verify(eventRepository, times(0)).save(any(Event.class));
    }

    @Test
    void shouldReturnTrueWhenIsNewEventDifferentFromLastEvent() {
        Boolean result = assertDoesNotThrow(() -> eventService.isNewEventDifferentFromLastEvent(EventType.HARD_ACCELERATION, EventType.LOW_BATTERY));

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenIsNewEventDifferentFromLastEvent() {
        Boolean result = assertDoesNotThrow(() -> eventService.isNewEventDifferentFromLastEvent(EventType.HARD_ACCELERATION, EventType.HARD_ACCELERATION));

        assertFalse(result);
    }

    @Test
    void shouldGetLastEventCoordinate() {
        CoordinateDataRecord coordinateDataRecord = startCoordinateDataRecord(SPEED, 0.24, 104L, BATTERY_VOLTAGE, TEMPERATURE);
        EventCoordinate eventCoordinate = startEventCoordinate(ObjectId.get().toHexString(), EventType.HARD_ACCELERATION, coordinateDataRecord);
        Page<EventCoordinate> pageResult = new PageImpl<>(List.of(eventCoordinate));
        when(eventCoordinateRepository.findAll(any(Pageable.class))).thenReturn(pageResult);

        EventCoordinate result = assertDoesNotThrow(() -> eventService.getLastEventCoordinate());

        assertNotNull(result);

        verify(eventCoordinateRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldDontGetLastEventCoordinateWhenNotFounded() {
        Page<EventCoordinate> pageResult = new PageImpl<>(List.of());
        when(eventCoordinateRepository.findAll(any(Pageable.class))).thenReturn(pageResult);

        EventCoordinate result = assertDoesNotThrow(() -> eventService.getLastEventCoordinate());

        assertNull(result);

        verify(eventCoordinateRepository).findAll(any(Pageable.class));
    }

    private Event startEvent(EventCoordinate eventCoordinate) {
        return new Event(eventCoordinate);
    }

    private EventCoordinate startEventCoordinate(String id,
                                                 EventType eventType,
                                                 CoordinateDataRecord coordinateDataRecord) {
        EventCoordinate eventCoordinate = new EventCoordinate(eventType, coordinateDataRecord);
        eventCoordinate.setId(id);
        return eventCoordinate;
    }

    private CoordinateDataRecord startCoordinateDataRecord() {
        return startCoordinateDataRecord(SPEED, ACCELERATION, TRANSMISSION_REASON_ID, BATTERY_VOLTAGE, TEMPERATURE);
    }

    private CoordinateDataRecord startCoordinateDataRecord(double speed,
                                           double acceleration,
                                           long transmissionReasonId,
                                           double batteryVoltage,
                                           double temperature) {
        return new CoordinateDataRecord(
                LocalDateTime.now(),
                "HVL2794",
                -26.991158,
                -48.6459632,
                speed,
                acceleration,
                true,
                41528921L,
                807L,
                transmissionReasonId,
                191L,
                batteryVoltage,
                temperature
        );
    }
}