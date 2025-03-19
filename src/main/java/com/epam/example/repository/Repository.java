package com.epam.example.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.epam.example.config.MongoConnection;
import com.epam.example.model.Subtask;
import com.epam.example.model.Task;
import com.epam.example.util.TaskUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private final MongoCollection<Document> collection;

    public Repository(MongoConnection mongoDB) {
        this.collection = mongoDB.getDatabase().getCollection("tasks");
    }

    public void insertTask(Task task) {
        collection.insertOne(TaskUtil.toDocument(task));
    }

    public void updateTask(Task task) {
        Document filter = new Document("_id", new ObjectId(task.getId()));

        Document updatedTask = new Document("$set", new Document("createdDate", TaskUtil.toDate(task.getCreatedDate()))
                .append("deadline", TaskUtil.toDate(task.getDeadline()))
                .append("name", task.getName())
                .append("description", task.getDescription())
                .append("category", task.getCategory().toString()));

        collection.updateOne(filter, updatedTask);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Document doc : collection.find()) {
            tasks.add(TaskUtil.fromDocument(doc));
        }
        return tasks;
    }

    public List<Task> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        List<Task> overdueTasks = new ArrayList<>();

        try (MongoCursor<Document> cursor = collection.find(
                        Filters.lt("deadline", TaskUtil.toDate(today)))
                .iterator()) {

            while (cursor.hasNext()) {
                overdueTasks.add(TaskUtil.fromDocument(cursor.next()));
            }
        }
        return overdueTasks;
    }

    public List<Task> getTasksByCategory(String category) {
        List<Task> tasks = new ArrayList<>();
        FindIterable<Document> documents = collection.find(
                Filters.eq("category", category)
        );

        for (Document doc : documents) {
            tasks.add(TaskUtil.fromDocument(doc));
        }

        return tasks;
    }

    public List<Task> searchTasksByDescription(String keyword) {
        Bson filter = Filters.text(keyword);
        return collection.find(filter)
                .map(TaskUtil::fromDocument)
                .into(new ArrayList<>());
    }

    public void deleteTask(String name) {
        collection.deleteOne(Filters.eq("name", name));
    }

    public List<Subtask> getSubtasksByCategory(String category) {
        List<Subtask> subtasks = new ArrayList<>();
        FindIterable<Document> documents = collection.find(Filters.eq(
                "category", category)
        );

        for (Document doc : documents) {
            List<Document> subtaskDocs = (List<Document>) doc.get("subtasks");
            if (subtaskDocs != null) {
                for (Document subtaskDoc : subtaskDocs) {
                    subtasks.add(TaskUtil.subTaskFromDocument(subtaskDoc));
                }
            }
        }

        return subtasks;
    }

    public void insertSubTask(String taskId, Subtask subtask) {
        Document subtaskDoc = new Document("name", subtask.getName())
                .append("description", subtask.getDescription());

        collection.updateOne(Filters.eq("_id", new ObjectId(taskId)),
                Updates.push("subtasks", subtaskDoc));
    }

    public void updateSubtask(String taskId, String subtaskName, String newDescription) {
        collection.updateOne(
                Filters.and(Filters.eq("_id", new ObjectId(taskId)), Filters.eq("subtasks.name", subtaskName)),
                Updates.set("subtasks.$.description", newDescription)
        );
    }

    public List<Task> searchSubTasksByName(String keyword) {
        List<Task> tasks = new ArrayList<>();
        FindIterable<Document> documents = collection.find(Filters.text(keyword));

        for (Document doc : documents) {
            tasks.add(TaskUtil.fromDocument(doc));
        }

        return tasks;
    }

    public void removeSubtask(String taskId, String subtaskName) {
        Document subtaskDoc = new Document("name", subtaskName);

        collection.updateOne(Filters.eq("_id", new ObjectId(taskId)),
                Updates.pull("subtasks", subtaskDoc));
    }

    public void clearSubtasks(String taskId) {
        collection.updateOne(Filters.eq("_id", new ObjectId(taskId)),
                Updates.set("subtasks", new ArrayList<>()));
    }
}
