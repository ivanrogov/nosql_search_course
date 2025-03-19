package com.epam.example;

import com.epam.example.config.MongoConnection;
import com.epam.example.repository.Repository;
import com.epam.example.model.Subtask;
import com.epam.example.model.Task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleTask {

    private static final MongoConnection DB_CONNECTION = new MongoConnection();
    private static final Repository TASK_REPO = new Repository(DB_CONNECTION);
    private static final Scanner INPUT_READER = new Scanner(System.in);

    public static void main(String[] args) {
        boolean isRunning = true;

        while (isRunning) {
            displayMenu();
            int action = getUserChoice();

            switch (action) {
                case 1 -> createNewTask();
                case 2 -> updateExistingTask();
                case 3 -> deleteExistingTask();
                case 4 -> displayAllTasks();
                case 5 -> displayOverdueTasks();
                case 6 -> displayTasksByCategory();
                case 7 -> addSubtaskToTask();
                case 8 -> modifySubtask();
                case 9 -> deleteSubtask();
                case 10 -> listSubtasksByCategory();
                case 11 -> searchTaskByDescription();
                case 12 -> searchSubtaskByName();
                case 0 -> isRunning = false;
                default -> System.err.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Task Management Menu ===");
        System.out.println("1. Add Task");
        System.out.println("2. Update Task");
        System.out.println("3. Delete Task");
        System.out.println("4. View All Tasks");
        System.out.println("5. View Overdue Tasks");
        System.out.println("6. View Tasks by Category");
        System.out.println("7. Add Subtask");
        System.out.println("8. Update Subtask");
        System.out.println("9. Remove Subtask");
        System.out.println("10. View Subtasks by Category");
        System.out.println("11. Search Task by Description");
        System.out.println("12. Search Subtask by Name");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(INPUT_READER.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input! Please enter a number.");
            return -1;
        }
    }

    private static void createNewTask() {
        System.out.print("Task Name: ");
        String taskName = INPUT_READER.nextLine();

        System.out.print("Task Description: ");
        String taskDesc = INPUT_READER.nextLine();

        System.out.print("Deadline (yyyy-MM-dd): ");
        String deadlineInput = INPUT_READER.nextLine();
        LocalDate deadlineDate;
        try {
            deadlineDate = LocalDate.parse(deadlineInput);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Task creation failed.");
            return;
        }

        System.out.print("Category: ");
        String category = INPUT_READER.nextLine();

        Task task = new Task(taskName, taskDesc, LocalDate.now(), deadlineDate, category, new ArrayList<>());
        TASK_REPO.insertTask(task);
        System.out.println("Task successfully created!");
    }

    private static void updateExistingTask() {
        System.out.print("Enter Task ID to Update: ");
        String taskId = INPUT_READER.nextLine();

        System.out.print("New Task Name: ");
        String taskName = INPUT_READER.nextLine();

        System.out.print("New Task Description: ");
        String taskDesc = INPUT_READER.nextLine();

        System.out.print("Creation Date (yyyy-MM-dd): ");
        LocalDate createDate = tryParseDate(INPUT_READER.nextLine());
        if (createDate == null) return;

        System.out.print("New Deadline (yyyy-MM-dd): ");
        LocalDate deadlineDate = tryParseDate(INPUT_READER.nextLine());
        if (deadlineDate == null) return;

        System.out.print("New Category: ");
        String category = INPUT_READER.nextLine();

        Task updatedTask = new Task(taskId, createDate, deadlineDate, taskName, taskDesc, category);
        TASK_REPO.updateTask(updatedTask);
        System.out.println("Task updated successfully!");
    }

    private static void deleteExistingTask() {
        System.out.print("Enter Task Name to Delete: ");
        String taskName = INPUT_READER.nextLine();
        TASK_REPO.deleteTask(taskName);
        System.out.println("Task successfully removed.");
    }

    private static void displayAllTasks() {
        List<Task> allTasks = TASK_REPO.getAllTasks();
        allTasks.forEach(System.out::println);
    }

    private static void displayOverdueTasks() {
        List<Task> overdueTasks = TASK_REPO.getOverdueTasks();
        overdueTasks.forEach(System.out::println);
    }

    private static void displayTasksByCategory() {
        System.out.print("Specify Category: ");
        String category = INPUT_READER.nextLine();
        List<Task> tasksByCategory = TASK_REPO.getTasksByCategory(category);
        tasksByCategory.forEach(System.out::println);
    }

    private static void addSubtaskToTask() {
        System.out.print("Parent Task ID: ");
        String taskId = INPUT_READER.nextLine();

        System.out.print("Subtask Name: ");
        String subName = INPUT_READER.nextLine();

        System.out.print("Subtask Description: ");
        String subDesc = INPUT_READER.nextLine();

        TASK_REPO.insertSubTask(taskId, new Subtask(subName, subDesc));
        System.out.println("Subtask added successfully!");
    }

    private static void modifySubtask() {
        System.out.print("Parent Task ID: ");
        String taskId = INPUT_READER.nextLine();

        System.out.print("Subtask Name to Modify: ");
        String subName = INPUT_READER.nextLine();

        System.out.print("Updated Subtask Description: ");
        String subDesc = INPUT_READER.nextLine();

        TASK_REPO.updateSubtask(taskId, subName, subDesc);
        System.out.println("Subtask updated successfully!");
    }

    private static void deleteSubtask() {
        System.out.print("Parent Task ID: ");
        String taskId = INPUT_READER.nextLine();

        System.out.print("Subtask Name to Delete: ");
        String subName = INPUT_READER.nextLine();

        TASK_REPO.removeSubtask(taskId, subName);
        System.out.println("Subtask removed successfully.");
    }

    private static void listSubtasksByCategory() {
        System.out.print("Specify Category: ");
        String category = INPUT_READER.nextLine();
        TASK_REPO.getSubtasksByCategory(category).forEach(System.out::println);
    }

    private static void searchTaskByDescription() {
        System.out.print("Enter Keyword to Search in Task Description: ");
        String keyword = INPUT_READER.nextLine();
        TASK_REPO.searchTasksByDescription(keyword).forEach(System.out::println);
    }

    private static void searchSubtaskByName() {
        System.out.print("Enter Keyword to Search Subtask by Name: ");
        String keyword = INPUT_READER.nextLine();
        TASK_REPO.searchSubTasksByName(keyword).forEach(System.out::println);
    }

    private static LocalDate tryParseDate(String input) {
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format.");
            return null;
        }
    }
}