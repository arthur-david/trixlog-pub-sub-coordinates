package com.trixlog.eventprocessor.repositories;

import com.trixlog.eventprocessor.models.EventCoordinate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCoordinateRepository extends MongoRepository<EventCoordinate, String> {
}
