package com.epam.example.util;

import org.bson.Document;
import com.epam.example.model.Subtask;
import com.epam.example.model.Task;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TaskUtil {
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Document toDocument(Task task) {
        return new Document("createdDate", TaskUtil.toDate(task.getCreatedDate()))
                .append("deadline", TaskUtil.toDate(task.getDeadline()))
                .append("name", task.getName())
                .append("description", task.getDescription())
                .append("category", task.getCategory().toString())
                .append("subtasks", task.getSubtasks().stream().map(TaskUtil::toDocument).toList());
    }

    public static Task fromDocument(Document doc) {
        Task task = new Task();
        task.setId(doc.getObjectId("_id").toString());
        task.setCreatedDate(TaskUtil.toLocalDate(doc.getDate("createdDate")));
        task.setDeadline(TaskUtil.toLocalDate(doc.getDate("deadline")));
        task.setName(doc.getString("name"));
        task.setDescription(doc.getString("description"));
        task.setCategory(doc.getString("category"));
        task.setSubtasks(doc.getList("subtasks", Document.class).stream()
                .map(TaskUtil::subTaskFromDocument)
                .toList());
        return task;

    }

    public static Document toDocument(Subtask subtask) {
        return new Document("name", subtask.getName())
                .append("description", subtask.getDescription());
    }

    public static Subtask subTaskFromDocument(Document doc) {
        return new Subtask(doc.getString("name"), doc.getString("description"));
    }
}
