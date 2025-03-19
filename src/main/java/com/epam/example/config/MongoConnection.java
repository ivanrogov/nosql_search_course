package com.epam.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {

    private MongoDatabase database;
    private static String URI = "mongodb://mongo:mongo@localhost:27017/tasks?authSource=admin";


    public MongoConnection() {
        MongoClient client = MongoClients.create(URI);
        this.database = client.getDatabase("tasks");
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
