import model.manager.Managers;
import model.manager.TaskManager;
import model.task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager manager;
    private static Task task1;
    private static Task task2;
    private static Epic epic;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static Subtask subtask3;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
        task1 = new Task("Задача", "Описание задачи");
        task2 = new Task("Задача2", "Описание задачи2");
        epic = new Epic("Эпик1", "Большой эпик");
        subtask1 = new Subtask("Подзадача1", "information");
        subtask2 = new Subtask("Подзадача2", "description");
        subtask3 = new Subtask("Подзадача3", "something");

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

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(subtask1, epic.getId());
        manager.createSubtask(subtask2, epic.getId());
        manager.createSubtask(subtask3, epic.getId());

        manager.calculateEpicStartAndEndTime(epic);
    }

    @Test
    void createTask() {
        final Task savedTask = manager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
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
        final Subtask savedSubtask = manager.getSubtaskById(subtask1.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");
        assertEquals(subtask1.getEpicId(), epic.getId(), "ID эпика не совпадают.");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task newTask = new Task("Task", "Description");
        manager.updateTask(newTask, task1.getId(), Status.IN_PROGRESS);

        final Task savedTask = manager.getTaskById(newTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpic() {
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
        Subtask newSubtask = new Subtask("Subtask", "Description");
        manager.updateSubtask(newSubtask, subtask1.getId(), Status.IN_PROGRESS);

        final Subtask savedSubtask = manager.getSubtaskById(newSubtask.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(newSubtask, savedSubtask, "Задачи не совпадают.");
        assertEquals(newSubtask.getEpicId(), epic.getId(), "ID эпика не совпадают.");
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус эпика не изменился");

        final List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество задач.");
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
        assertEquals(LocalDateTime.of(2023, Month.JANUARY, 1, 12, 10), epic.getStartTime());
        assertEquals(LocalDateTime.of(2023, Month.JANUARY, 1, 14, 30), epic.getEndTime());
        assertEquals(Duration.ofMinutes(140), epic.getDuration());
    }

    @Test
    void getPrioritizedTasks() {
        List<Task> orderedTasks = manager.getPrioritizedTasks();
        System.out.println(orderedTasks);
    }

    @Test
    void getPrioritizedTasksWithDeleteEpic() {
        manager.deleteEpicById(epic.getId());
        List<Task> orderedTasks = manager.getPrioritizedTasks();
        System.out.println(orderedTasks);
    }

    @Test
    void getPrioritizedTasksWithClearSubtasks() {
        manager.clearSubtasks();
        List<Task> orderedTasks = manager.getPrioritizedTasks();
        System.out.println(orderedTasks);
    }

    @Test
    void checkCrossing() {
        manager.getPrioritizedTasks();
        manager.checkCrossing();
    }

    @Test
    void getTasks() {
        final Task savedTask = manager.getTaskById(task1.getId());
        final List<Task> tasks = manager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getEpics() {
        final List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubtasks() {
        final List<Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubtasksByEpicId() {
        final List<Subtask> subtasks = manager.getSubtasksByEpicId(epic.getId());
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubtasksByIncorrectEpicId() {
        final List<Subtask> subtasks = manager.getSubtasksByEpicId(epic.getId() + 1);
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void getTaskById() {
        final Task savedTask = manager.getTaskById(task1.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
    }

    @Test
    void getEpicById() {
        final Epic savedEpic = manager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void getSubtaskById() {
        final Subtask savedSubtask = manager.getSubtaskById(subtask1.getId());
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void deleteTaskById() {
        manager.deleteTaskById(task1.getId());
        final List<Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteEpicById() {
        manager.deleteEpicById(epic.getId());
        final List<Epic> epics = manager.getEpics();
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void deleteSubtaskById() {
        manager.deleteSubtaskById(subtask1.getId());
        final List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void clearTasks() {
        manager.clearTasks();
        final List<Task> tasks = manager.getTasks();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void clearEpics() {
        manager.clearEpics();
        final List<Epic> epics = manager.getEpics();
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    void clearSubtasks() {
        manager.clearSubtasks();
        final List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    void getHistory() {
        manager.getTaskById(task1.getId());
        List<Task> history = manager.getHistory();
        assertNotNull(history, "История пустая");
        assertEquals(1, history.size(), "История пустая");
    }
}