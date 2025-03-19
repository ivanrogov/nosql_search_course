package ua.epam.mishchenko.ticketbooking.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.epam.mishchenko.ticketbooking.model.EventTotal;
import ua.epam.mishchenko.ticketbooking.service.AggregationService;

import java.util.List;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    @Autowired
    private AggregationService aggregationService;

    @GetMapping("/totals")
    public ResponseEntity<List<EventTotal>> getTotalsByEvent() {
        return ResponseEntity.ok(aggregationService.getTotalsByEvent());
    }


}
