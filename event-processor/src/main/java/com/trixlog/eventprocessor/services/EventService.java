package com.trixlog.eventprocessor.services;

import com.trixlog.eventprocessor.enums.EventType;
import com.trixlog.eventprocessor.models.Event;
import com.trixlog.eventprocessor.models.EventCoordinate;
import com.trixlog.eventprocessor.records.CoordinateDataRecord;
import com.trixlog.eventprocessor.repositories.EventCoordinateRepository;
import com.trixlog.eventprocessor.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventCoordinateRepository eventCoordinateRepository;

    public void checkCoordinate(CoordinateDataRecord coordinate) {
        EventType newCoordinateEventType = EventType.fetchEventTypeByCoordinate(coordinate);

        if (isNull(newCoordinateEventType)) return;

        checkEvent(newCoordinateEventType, coordinate);
    }

    public void checkEvent(EventType newCoordinateEventType, CoordinateDataRecord coordinate) {
        EventCoordinate lastEventCoordinate = getLastEventCoordinate();

        Event event;
        if (isNull(lastEventCoordinate) || isNewEventDifferentFromLastEvent(newCoordinateEventType, lastEventCoordinate.getEventType())) {
            event = createEvent(newCoordinateEventType, coordinate);
        } else {
            ObjectId newEventCoordinateObjectId = new ObjectId();
            event = updateEvent(lastEventCoordinate.getObjectId(), newEventCoordinateObjectId, coordinate.coordinateDate());
        }

        createEventCoordinate(event, coordinate);
    }

    public Event createEvent(EventType newCoordinateEventType, CoordinateDataRecord coordinate) {
        Event event = new Event(newCoordinateEventType, coordinate);
        return eventRepository.save(event);
    }

    public void createEventCoordinate(Event event, CoordinateDataRecord coordinate) {
        EventCoordinate eventCoordinate = new EventCoordinate(event.getClosedCoordinateId(), event.getEventType(), coordinate);
        eventCoordinateRepository.save(eventCoordinate);
    }

    public Event updateEvent(ObjectId lastEventCoordinateObjectId, ObjectId newEventCoordinateObjectId, LocalDateTime coordinateDate) {
        Event event = eventRepository.findByClosedCoordinateId(lastEventCoordinateObjectId);
        event.setClosedDate(coordinateDate);
        event.setClosedCoordinateId(newEventCoordinateObjectId);
        return eventRepository.save(event);
    }

    public boolean isNewEventDifferentFromLastEvent(EventType newCoordinateEventType, EventType eventTypeOfLastEventCoordinate) {
        return !newCoordinateEventType.equals(eventTypeOfLastEventCoordinate);
    }

    public EventCoordinate getLastEventCoordinate() {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("coordinateDate").descending());
        Page<EventCoordinate> page = eventCoordinateRepository.findAll(pageRequest);
        if (page.getContent().isEmpty())
            return null;

        return page.getContent().get(0);
    }
}
