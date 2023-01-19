import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.manager.Managers;
import model.manager.TaskManager;
import model.server.HttpTaskServer;
import model.server.KVServer;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    private static TaskManager manager;
    private static Gson gson = Managers.getGson();
    private static Task task1;
    private static Task task2;
    private static Epic epic;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static Subtask subtask3;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

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

        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();

    }


    @AfterEach
    void AfterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void getOrderedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Таски не возвращаются");
        assertEquals(5, actual.size(), "Неверное количество тасок");
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Таски не возвращаются");
        assertEquals(2, actual.size(), "Неверное количество тасок");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);

        assertNotNull(actual, "Таски не возвращаются");
        assertEquals(task2, actual, "Таски не совпадают");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        manager.getTaskById(1);
        manager.getTaskById(0);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type historyType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), historyType);

        assertNotNull(actual, "Таски не возвращаются");
        assertEquals(2, actual.size(), "Неверное количество тасок");
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), subtaskType);

        assertNotNull(actual, "Сабтаски не возвращаются");
        assertEquals(3, actual.size(), "Неверное количество сабтасок");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type subtaskType = new TypeToken<Subtask>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), subtaskType);

        assertNotNull(actual, "Сабтаски не возвращаются");
        assertEquals(subtask1, actual, "Сабтаски не совпадают");
    }

    @Test
    void getSubtasksByEpicId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), subtaskType);

        assertNotNull(actual, "Сабтаски не возвращаются");
        assertEquals(3, actual.size(), "Неверное количество сабтасок");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), epicType);

        assertNotNull(actual, "Эпики не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество эпиков");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<Epic>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), epicType);

        assertNotNull(actual, "Эпик не возвращаются");
        assertEquals(epic, actual, "Эпики не совпадают");
    }

    @Test
    void deleteTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/");

        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 12, 10);
        Duration duration = Duration.ofMinutes(40);
        Task newTask = new Task("Задача", "Описание задачи");
        newTask.setStartTimeAndDuration(dateTime, duration);

        String json = gson.toJson(newTask);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void createSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");

        LocalDateTime dateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 12, 10);
        Duration duration = Duration.ofMinutes(40);
        Subtask newSubtask = new Subtask("Задача", "Описание задачи");
        newSubtask.setStartTimeAndDuration(dateTime, duration);
        newSubtask.setEpicId(epic.getId());

        String json = gson.toJson(newSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    void createEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic/");

        Epic newEpic = new Epic("Задача", "Описание задачи");
        String json = gson.toJson(newEpic);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }


}
