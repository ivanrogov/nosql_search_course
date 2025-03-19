package ua.epam.mishchenko.ticketbooking.model;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

public class EventTotal {
    @Field("_id")
    private String eventId;

    @Field("total")
    private BigDecimal total;

    @Field("name")
    private String name;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public BigDecimal getTotalRevenue() {
        return total;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.total = totalRevenue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
