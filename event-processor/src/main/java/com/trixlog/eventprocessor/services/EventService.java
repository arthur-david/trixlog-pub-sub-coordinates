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
        EventCoordinate newEventCoordinate = createEventCoordinate(newCoordinateEventType, coordinate);

        if (isNull(lastEventCoordinate) || isNewEventDifferentFromLastEvent(newCoordinateEventType, lastEventCoordinate.getEventType())) {
            createEvent(newEventCoordinate);
        } else {
            updateEvent(lastEventCoordinate, newEventCoordinate);
        }
    }

    public EventCoordinate createEventCoordinate(EventType eventType, CoordinateDataRecord coordinate) {
        EventCoordinate eventCoordinate = new EventCoordinate(eventType, coordinate);
        return eventCoordinateRepository.save(eventCoordinate);
    }

    public void createEvent(EventCoordinate eventCoordinate) {
        Event event = new Event(eventCoordinate);
        eventRepository.save(event);
    }

    public void updateEvent(EventCoordinate lastEventCoordinate, EventCoordinate newEventCoordinate) {
        Event event = eventRepository.findByClosedCoordinateId(lastEventCoordinate.getId());

        if (isNull(event))
            throw new IllegalArgumentException("Erro ao buscar event anterior");

        event.setClosedDate(newEventCoordinate.getCoordinateDate());
        event.setClosedCoordinateId(new ObjectId(newEventCoordinate.getId()));
        eventRepository.save(event);
    }

    public boolean isNewEventDifferentFromLastEvent(EventType newCoordinateEventType, EventType eventTypeOfLastEventCoordinate) {
        return !newCoordinateEventType.equals(eventTypeOfLastEventCoordinate);
    }

    public EventCoordinate getLastEventCoordinate() {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("createdAt").descending());
        Page<EventCoordinate> page = eventCoordinateRepository.findAll(pageRequest);
        if (page.getContent().isEmpty())
            return null;

        return page.getContent().get(0);
    }
}
