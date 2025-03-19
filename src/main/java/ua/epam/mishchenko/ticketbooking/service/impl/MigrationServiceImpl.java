package ua.epam.mishchenko.ticketbooking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.model.*;
import ua.epam.mishchenko.ticketbooking.repository.*;
import ua.epam.mishchenko.ticketbooking.service.MigrationService;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MigrationServiceImpl implements MigrationService {

    @Autowired
    private UserRepository pgUserRepository;

    @Autowired
    private TicketRepository pgTicketRepository;

    @Autowired
    private UserAccountRepository pgUserAccountRepository;

    @Autowired
    private EventMongoRepository eventRepository;

    @Autowired
    private UserMongoRepository userRepository;

    @Autowired
    private TicketMongoRepository ticketRepository;


    @Autowired
    private EventRepository pgEventRepository;

    private static TimeZone tz = TimeZone.getTimeZone("UTC");
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        df.setTimeZone(tz);
    }


    @Override
    @Transactional
    public void executeMigration() {
        // 1. Initialize mapping structures to track ID transformations
        Map<Long, String> postgresToMongoUserIdMap = new HashMap<>();
        Map<Long, String> postgresToMongoEventIdMap = new HashMap<>();

        // 2. Fetch data from PostgreSQL
        List<Event> postgresEvents = fetchEventsFromPostgres();
        List<User> postgresUsers = fetchUsersFromPostgres();
        List<UserAccount> postgresUserAccounts = fetchUserAccountsFromPostgres();
        List<Ticket> postgresTickets = fetchTicketsFromPostgres();

        // 3. Migrate Events
        for (Event postgresEvent : postgresEvents) {
            EventMongo mongoEvent = new EventMongo();
            mongoEvent.setTitle(postgresEvent.getTitle());
            mongoEvent.setTicketPrice(postgresEvent.getTicketPrice());
            mongoEvent.setDate(df.format(postgresEvent.getDate()));
            mongoEvent = eventRepository.save(mongoEvent);
            postgresToMongoEventIdMap.put(postgresEvent.getId(), mongoEvent.getId());
        }

        // 4. Migrate Users
        for (User postgresUser : postgresUsers) {
            UserMongo mongoUser = new UserMongo();
            mongoUser.setName(postgresUser.getName());
            mongoUser.setEmail(postgresUser.getEmail());

            mongoUser = userRepository.save(mongoUser); // Save to MongoDB and get MongoDB's new _id
            postgresToMongoUserIdMap.put(postgresUser.getId(), mongoUser.getId());
        }

        // 5. Migrate UserAccounts
        for (UserAccount postgresUserAccount : postgresUserAccounts) {
            String mongoUserId = postgresToMongoUserIdMap.get(postgresUserAccount.getUser().getId());
            UserMongo mongoUser = userRepository.findById(mongoUserId).get();
            mongoUser.setMoney(postgresUserAccount.getMoney());
            userRepository.save(mongoUser);
        }

        // 6. Migrate Tickets
        for (Ticket postgresTicket : postgresTickets) {
            TicketMongo mongoTicket = new TicketMongo();
            String mongoUserId = postgresToMongoUserIdMap.get(postgresTicket.getUser().getId());
            String mongoEventId = postgresToMongoEventIdMap.get(postgresTicket.getEvent().getId());

            UserMongo mongoUser = userRepository.findById(mongoUserId).get(); // Get MongoDB User reference
            EventMongo mongoEvent = eventRepository.findById(mongoEventId).get(); // Get MongoDB Event reference

            mongoTicket.setUser(mongoUser);
            mongoTicket.setEvent(mongoEvent);
            mongoTicket.setPlace(postgresTicket.getPlace());
            mongoTicket.setCategory(postgresTicket.getCategory());

            ticketRepository.save(mongoTicket);
        }

        System.out.println("Migration completed successfully!");

    }

    // Placeholder methods to fetch data from PostgreSQL
    private List<Event> fetchEventsFromPostgres() {
        return StreamSupport.stream(pgEventRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<User> fetchUsersFromPostgres() {
        return StreamSupport.stream(pgUserRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<UserAccount> fetchUserAccountsFromPostgres() {
        return StreamSupport.stream(pgUserAccountRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<Ticket> fetchTicketsFromPostgres() {
        return StreamSupport.stream(pgTicketRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
