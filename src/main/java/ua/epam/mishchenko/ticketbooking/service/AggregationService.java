package ua.epam.mishchenko.ticketbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.model.EventTotal;
import ua.epam.mishchenko.ticketbooking.repository.TicketMongoRepository;

import java.util.List;

@Service
public class AggregationService {

    @Autowired
    private TicketMongoRepository ticketRepository;


    public List<EventTotal> getTotalsByEvent() {
        return ticketRepository.getTotalsByEvent();
    }


}
