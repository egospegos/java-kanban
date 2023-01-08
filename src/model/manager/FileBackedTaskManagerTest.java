package model.manager;

import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import model.task.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static String fileName;
    private static File fileInfo;
    private static FileBackedTaskManager fileBackedTaskManager;
    private static Task task;

    @BeforeEach
    void beforeEach() {
        fileName = "info.csv";
        fileInfo = new File("resources\\" + fileName);
        fileBackedTaskManager = new FileBackedTaskManager(fileInfo);
        task = new Task("Задача1", "Описание задания");
    }

    @Test
    void save() {
        fileInfo = new File("resources\\" + fileName);
        fileBackedTaskManager.createTask(task);
        FileBackedTaskManager fileBTM2 = FileBackedTaskManager.loadFromFile(fileInfo);

        final List<Task> tasks = fileBTM2.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
    }

    @Test
    void loadFromFile() {
        fileInfo = new File("resources\\" + fileName);
        fileBackedTaskManager.createTask(task);
        FileBackedTaskManager fileBTM2 = FileBackedTaskManager.loadFromFile(fileInfo);

        final List<Task> tasks = fileBTM2.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
    }

    @Test
    void testTaskToString() {
        fileBackedTaskManager.createTask(task);
        assertEquals(task.getId() + "," + TaskType.TASK + "," + task.getName() +
                        "," + task.getStatus() + "," + task.getDescription() + ",",
                fileBackedTaskManager.toString(task));
    }

    @Test
    void testEpicToString() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        fileBackedTaskManager.createEpic(epic);
        assertEquals(epic.getId() + "," + TaskType.EPIC + "," + epic.getName() +
                        "," + epic.getStatus() + "," + epic.getDescription() + ",",
                fileBackedTaskManager.toString(epic));
    }

    @Test
    void testSubtaskToString() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask, epic.getId());
        assertEquals(subtask.getId() + "," + TaskType.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus() + "," +
                        subtask.getDescription() + "," + subtask.getEpicId(),
                fileBackedTaskManager.toString(subtask));
    }

    @Test
    void getTaskType() {
        assertEquals(TaskType.TASK, FileBackedTaskManager.getTaskType("1,TASK"));
    }

    @Test
    void historyToString() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        fileBackedTaskManager.createTask(task);
        Task task2 = new Task("Task2", "description2");
        fileBackedTaskManager.createTask(task2);
        historyManager.add(task);
        historyManager.add(task2);
        assertEquals("0,1", FileBackedTaskManager.historyToString(historyManager));
    }

    @Test
    void historyFromString() {
        List<Integer> history = new ArrayList<>();
        history.add(0);
        history.add(1);
        assertEquals(history, FileBackedTaskManager.historyFromString("0,1"));
    }
}