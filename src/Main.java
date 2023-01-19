import model.manager.FileBackedTaskManager;
import model.manager.Managers;
import model.manager.TaskManager;
import model.server.HttpTaskServer;
import model.server.KVServer;
import model.task.Epic;
import model.task.Status;
import model.task.Subtask;
import model.task.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {


        new KVServer().start();

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача", "Описание задачи");
        Task task2 = new Task("Задача2", "Описание задачи2");
        Epic epic = new Epic("Эпик1", "Большой эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "description");
        Subtask subtask3 = new Subtask("Подзадача3", "something");

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

        System.out.println(manager.getPrioritizedTasks());


        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();


    }
}
