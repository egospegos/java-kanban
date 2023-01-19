package model.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.adapter.LocalDateTimeAdapter;
import model.manager.Managers;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;

    private TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);

    }


    private void handleTasks(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    //получение списка отсортированных задач
                    if (Pattern.matches("^/tasks/$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(httpExchange, response);
                        break;
                    }

                    //получение списка истории
                    if (Pattern.matches("^/tasks/history/$", path)) {
                        String response = gson.toJson(taskManager.getHistory());
                        sendText(httpExchange, response);
                        break;
                    }

                    //получение списка или отдельной задачи
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        final String query = httpExchange.getRequestURI().getQuery();
                        if (query == null) { //если в запросе нет id, то получаем список задач
                            String response = gson.toJson(taskManager.getTasks());
                            sendText(httpExchange, response);
                            System.out.println("Получили все задачи");
                            return;
                        }
                        String idParam = query.substring(3); //?id=
                        final int id = Integer.parseInt(idParam);
                        String response = gson.toJson(taskManager.getTaskById(id));
                        sendText(httpExchange, response);
                        System.out.println("Получили задачу id=" + id);
                        break;
                    }

                    //получение списка или отдельной подзадачи
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        final String query = httpExchange.getRequestURI().getQuery();
                        if (query == null) { //если в запросе нет id, то получаем список подзадач
                            String response = gson.toJson(taskManager.getSubtasks());
                            sendText(httpExchange, response);
                            System.out.println("Получили все подзадачи");
                            return;
                        }
                        String idParam = query.substring(3); //?id=
                        final int id = Integer.parseInt(idParam);
                        String response = gson.toJson(taskManager.getSubtaskById(id));
                        sendText(httpExchange, response);
                        System.out.println("Получили подзадачу id=" + id);
                        break;
                    }

                    //получение списка или отдельного эпика
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        final String query = httpExchange.getRequestURI().getQuery();
                        if (query == null) { //если в запросе нет id, то получаем список подзадач
                            String response = gson.toJson(taskManager.getEpics());
                            sendText(httpExchange, response);
                            System.out.println("Получили все эпики");
                            return;
                        }
                        String idParam = query.substring(3); //?id=
                        final int id = Integer.parseInt(idParam);
                        String response = gson.toJson(taskManager.getEpicById(id));
                        sendText(httpExchange, response);
                        System.out.println("Получили эпик id=" + id);
                        break;
                    }

                    //получение сабтасок эпика
                    if (Pattern.matches("^/tasks/subtask/epic/$", path)) {
                        final String query = httpExchange.getRequestURI().getQuery();
                        if (query == null) { //если в запросе нет id
                            System.out.println("Id не передан");
                            httpExchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        String idParam = query.substring(3); //?id=
                        final int id = Integer.parseInt(idParam);
                        String response = gson.toJson(taskManager.getSubtasksByEpicId(id));
                        sendText(httpExchange, response);
                        System.out.println("Получили сабтаски эпика с id=" + id);
                        break;
                    }


                    break;
                }
                case "POST": {
                    //добавление и обновление для тасок
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        String json = readText(httpExchange);
                        if (json.isEmpty()) {
                            System.out.println("Body c задачей  пустой. указывается в теле запроса");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        final Task task = gson.fromJson(json, Task.class);
                        final Integer id = task.getId();
                        if (id != 0) {
                            taskManager.updateTask(task, id);
                            System.out.println("Обновили задачу id=" + id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.createTask(task);
                            System.out.println("Создали задачу id=" + id);
                            final String response = gson.toJson(task);
                            sendText(httpExchange, response);
                        }
                        break;
                    }

                    //добавлени и обновление для сабтасок
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        String json = readText(httpExchange);
                        if (json.isEmpty()) {
                            System.out.println("Body c подзадачей  пустой. указывается в теле запроса");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        final Subtask subtask = gson.fromJson(json, Subtask.class);
                        final Integer id = subtask.getId();
                        if (id != 0) {
                            taskManager.updateSubtask(subtask, id);
                            System.out.println("Обновили подзадачу id=" + id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.createSubtask(subtask, subtask.getEpicId());
                            System.out.println("Создали подзадачу id=" + id);
                            final String response = gson.toJson(subtask);
                            sendText(httpExchange, response);
                        }
                        break;
                    }

                    //добавлени и обновление для сабтасок
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        String json = readText(httpExchange);
                        if (json.isEmpty()) {
                            System.out.println("Body c эпиком  пустой. указывается в теле запроса");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        final Epic epic = gson.fromJson(json, Epic.class);
                        final Integer id = epic.getId();
                        if (id != 0) {
                            taskManager.updateEpic(epic, id);
                            System.out.println("Обновили эпик id=" + id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.createEpic(epic);
                            System.out.println("Создали эпик id=" + id);
                            final String response = gson.toJson(epic);
                            sendText(httpExchange, response);
                        }
                        break;
                    }

                    break;
                }
                case "DELETE": {
                    //удаление задач
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        final String query = httpExchange.getRequestURI().getQuery();
                        if (query == null) { //если в запросе нет id, то удаляем все
                            taskManager.clearTasks();
                            System.out.println("Удалили все задачи");
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        String idParam = query.substring(3); //?id=
                        final int id = Integer.parseInt(idParam);
                        taskManager.deleteTaskById(id);
                        System.out.println("Удалили задачу id=" + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    }

                    //удаление подзадач
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        final String query = httpExchange.getRequestURI().getQuery();
                        if (query == null) { //если в запросе нет id, то удаляем все
                            taskManager.clearSubtasks();
                            System.out.println("Удалили все подзадачи");
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        String idParam = query.substring(3); //?id=
                        final int id = Integer.parseInt(idParam);
                        taskManager.deleteSubtaskById(id);
                        System.out.println("Удалили подзадачу id=" + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    }

                    //удаление эпиков
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        final String query = httpExchange.getRequestURI().getQuery();
                        if (query == null) { //если в запросе нет id, то удаляем все
                            taskManager.clearEpics();
                            System.out.println("Удалили все эпики");
                            httpExchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        String idParam = query.substring(3); //?id=
                        final int id = Integer.parseInt(idParam);
                        taskManager.deleteEpicById(id);
                        System.out.println("Удалили эпик id=" + id);
                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Ждем GET, POST или DELETE запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }

    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили HttpTaskServer на порту" + PORT);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

}
