package model.manager;

import model.task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void createTask() {
        Task task = new Task("Задача1", "Описание задания");
        manager.createTask(task);

        final Task savedTask = manager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);

        final Epic savedEpic = manager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        assertEquals(savedEpic.getStatus(), Status.NEW, "Статус задаётся неправильно.");

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void createSubtask() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());

        final Subtask savedSubtask = manager.getSubtaskById(subtask.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
        assertEquals(subtask.getEpicId(), epic.getId(), "ID эпика не совпадают.");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Задача1", "Описание задания");
        manager.createTask(task);

        Task newTask = new Task("Task", "Description");
        manager.updateTask(newTask, task.getId(), Status.IN_PROGRESS);

        final Task savedTask = manager.getTaskById(newTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);

        Epic newEpic = new Epic("Epic", "Big epic");
        manager.updateEpic(newEpic, epic.getId(), epic.getStatus());

        final Epic savedEpic = manager.getEpicById(newEpic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic, savedEpic, "Задачи не совпадают.");
        assertEquals(savedEpic.getStatus(), Status.NEW, "Статус задаётся неправильно.");

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(savedEpic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateSubtask() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());

        Subtask newSubtask = new Subtask("Subtask", "Description");
        manager.updateSubtask(newSubtask, subtask.getId(), Status.IN_PROGRESS);

        final Subtask savedSubtask = manager.getSubtaskById(newSubtask.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(newSubtask, savedSubtask, "Задачи не совпадают.");
        assertEquals(newSubtask.getEpicId(), epic.getId(), "ID эпика не совпадают.");
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика не изменился");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(savedSubtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getEpicStatusWithEmptySubtasks() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        assertEquals(Status.NEW, manager.getEpicStatus(epic), "Статусы не совпадают.");
    }

    @Test
    void getEpicStatusWithNewSubtasks() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        assertEquals(Status.NEW, manager.getEpicStatus(epic), "Статусы не совпадают.");
    }

    @Test
    void getEpicStatusWithDoneSubtasks() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        manager.updateSubtask(subtask1, subtask1.getId(), Status.DONE);
        manager.updateSubtask(subtask2, subtask2.getId(), Status.DONE);
        assertEquals(Status.DONE, manager.getEpicStatus(epic), "Статусы не совпадают.");
    }

    @Test
    void getEpicStatusWithInProgressSubtasks() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        manager.updateSubtask(subtask1, subtask1.getId(), Status.IN_PROGRESS);
        manager.updateSubtask(subtask2, subtask2.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpicStatus(epic), "Статусы не совпадают.");
    }

    @Test
    void getEpicStatusWithNewAndDoneSubtasks() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        manager.updateSubtask(subtask1, subtask1.getId(), Status.DONE);
        assertEquals(Status.IN_PROGRESS, manager.getEpicStatus(epic), "Статусы не совпадают.");
    }

    @Test
    void calculateEpicStartAndEndTime() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        Subtask subtask3 = new Subtask("Подзадача3", "something");
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        manager.createSubtask(subtask3, epic.getId());

        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 12, 10);
        Duration duration = Duration.ofMinutes(40);
        subtask1.setStartTimeAndDuration(dateTime, duration);
        subtask2.setStartTimeAndDuration(subtask1.getEndTime(), Duration.ofMinutes(50));
        subtask3.setStartTimeAndDuration(subtask2.getEndTime(), Duration.ofMinutes(50));
        manager.calculateEpicStartAndEndTime(epic);

        assertEquals(LocalDateTime.of(2023, Month.JANUARY, 1, 12, 10), epic.getStartTime());
        assertEquals(LocalDateTime.of(2023, Month.JANUARY, 1, 14, 30), epic.getEndTime());
        assertEquals(Duration.ofMinutes(140), epic.getDuration());

    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task("Задача", "Описание задачи");
        Task task2 = new Task("Задача2", "Описание задачи2");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        Subtask subtask3 = new Subtask("Подзадача3", "something");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        manager.createSubtask(subtask3, epic.getId());

        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 12, 10);
        Duration duration = Duration.ofMinutes(40);
        subtask1.setStartTimeAndDuration(dateTime, duration);
        subtask2.setStartTimeAndDuration(subtask1.getEndTime(), Duration.ofMinutes(50));
        subtask3.setStartTimeAndDuration(subtask2.getEndTime(), Duration.ofMinutes(50));

        LocalDateTime dateTime2 = LocalDateTime.of(2022, Month.DECEMBER, 5, 18, 15);
        Duration duration2 = Duration.ofMinutes(30);
        task1.setStartTimeAndDuration(dateTime2, duration2);

        LocalDateTime dateTime3 = LocalDateTime.of(2023, Month.MARCH, 30, 20, 57);
        Duration duration3 = Duration.ofMinutes(20);
        task2.setStartTimeAndDuration(dateTime3, duration3);

        List<Task> orderedTasks = manager.getPrioritizedTasks();

        System.out.println(orderedTasks);

        manager.checkCrossing();
    }

    @Test
    void checkCrossing() {
        Task task1 = new Task("Задача", "Описание задачи");
        Task task2 = new Task("Задача2", "Описание задачи2");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        Subtask subtask3 = new Subtask("Подзадача3", "something");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        manager.createSubtask(subtask3, epic.getId());

        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 12, 10);
        Duration duration = Duration.ofMinutes(40);
        subtask1.setStartTimeAndDuration(dateTime, duration);
        subtask2.setStartTimeAndDuration(LocalDateTime.of(2023, Month.JANUARY, 1, 12, 30), Duration.ofMinutes(50));
        subtask3.setStartTimeAndDuration(subtask2.getEndTime(), Duration.ofMinutes(50));

        LocalDateTime dateTime2 = LocalDateTime.of(2022, Month.DECEMBER, 5, 18, 15);
        Duration duration2 = Duration.ofMinutes(30);
        task1.setStartTimeAndDuration(dateTime2, duration2);

        LocalDateTime dateTime3 = LocalDateTime.of(2023, Month.MARCH, 30, 20, 57);
        Duration duration3 = Duration.ofMinutes(20);
        task2.setStartTimeAndDuration(dateTime3, duration3);

        manager.getPrioritizedTasks();

        manager.checkCrossing();
    }

    @Test
    void getTasks() {
        Task task = new Task("Задача1", "Описание задания");
        manager.createTask(task);

        final Task savedTask = manager.getTaskById(task.getId());
        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void getEpics() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubtasks() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubtasksByEpicId() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());

        final List<Subtask> subtasks = manager.getSubtasksByEpicId(epic.getId());

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubtasksByIncorrectEpicId() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());

        final List<Subtask> subtasks = manager.getSubtasksByEpicId(epic.getId() + 1);

        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void getTaskById() {
        Task task = new Task("Задача1", "Описание задания");
        manager.createTask(task);

        final Task savedTask = manager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);

        final Epic savedEpic = manager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void getSubtaskById() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());

        final Subtask savedSubtask = manager.getSubtaskById(subtask.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Задача1", "Описание задания");
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        final List<Task> tasks = manager.getTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.deleteEpicById(epic.getId());

        final List<Epic> epics = manager.getEpics();

        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void deleteSubtaskById() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());
        manager.deleteSubtaskById(subtask.getId());

        final List<Subtask> subtasks = manager.getSubtasks();

        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void clearTasks() {
        Task task = new Task("Задача1", "Описание задания");
        manager.createTask(task);
        manager.clearTasks();

        final List<Task> tasks = manager.getTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void clearEpics() {
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.clearEpics();

        final List<Epic> epics = manager.getEpics();

        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void clearSubtasks() {
        Subtask subtask = new Subtask("Подзадача1", "information");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        manager.createEpic(epic);
        manager.createSubtask(subtask, epic.getId());
        manager.clearSubtasks();

        final List<Subtask> subtasks = manager.getSubtasks();

        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void getHistory() {
        Task task = new Task("Задача1", "Описание задания");
        manager.createTask(task);
        manager.getTaskById(task.getId());
        List<Task> history = manager.getHistory();
        assertNotNull(history, "История пустая");
        assertEquals(1, history.size(), "История пустая");
    }
}