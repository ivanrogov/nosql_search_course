package ua.epam.mishchenko.ticketbooking.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "tickets")
public class TicketMongo {
    @Id
    private String id;
    @DBRef
    private UserMongo user; // Reference to User
    @DBRef
    private EventMongo event; // Reference to Event
    private int place;
    private Category category;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UserMongo getUser() { return user; }
    public void setUser(UserMongo user) { this.user = user; }
    public EventMongo getEvent() { return event; }
    public void setEvent(EventMongo event) { this.event = event; }
    public int getPlace() { return place; }
    public void setPlace(int place) { this.place = place; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}