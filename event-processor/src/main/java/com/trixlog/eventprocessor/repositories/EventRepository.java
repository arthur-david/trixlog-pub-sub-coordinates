package com.trixlog.eventprocessor.repositories;

import com.trixlog.eventprocessor.models.Event;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    @Query("{closedCoordinateId : {$eq:  ObjectId(?0)}}")
    Event findByClosedCoordinateId(String closedCoordinateId);
}
