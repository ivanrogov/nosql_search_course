package ua.epam.mishchenko.ticketbooking.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ua.epam.mishchenko.ticketbooking.model.EventTotal;
import ua.epam.mishchenko.ticketbooking.model.TicketMongo;

import java.util.List;

public interface TicketMongoRepository extends MongoRepository<TicketMongo, String> {

    @Aggregation(pipeline = {
            "{ $addFields: {'eventIdObject': '$event.$id'} }",
            // Stage 1: Lookup event information
            "{ $lookup: { from: 'events', localField: 'eventIdObject', foreignField: '_id', as: 'eventDetails' } }",

            // Stage 2: Unwind the eventDetails array (each ticket should have one associated event)
            "{ $unwind: { path: '$eventDetails' } }",

            // Stage 3: Group tickets by event ID and sum the ticket prices (eventId and title come from the eventDetails document)
            "{ $group: { _id: '$eventDetails._id', total: { $sum: { $toDouble: '$eventDetails.ticketPrice' }}, name: { $first: '$eventDetails.title' } } }"
    })
    List<EventTotal> getTotalsByEvent();
}
