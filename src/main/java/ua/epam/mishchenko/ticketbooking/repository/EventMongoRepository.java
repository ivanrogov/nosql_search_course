package ua.epam.mishchenko.ticketbooking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ua.epam.mishchenko.ticketbooking.model.EventMongo;

@Repository
public interface EventMongoRepository extends MongoRepository<EventMongo, String> {


}