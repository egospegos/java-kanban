package model.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.client.KVTaskClient;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import model.task.TaskType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {

    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(int port) {
        this(port, false);
    }

    public HttpTaskManager(int port, boolean load) {
        super(null);
        gson = Managers.getGson();
        client = new KVTaskClient(port);
        if (load) {
            load();
        }
    }

    protected void addTasks(List<? extends Task> tasks) {
        for (Task task : tasks) {
            final int id = task.getId();
            if (id > generatedId) {
                generatedId = id;
            }
            TaskType type = task.getTaskType();
            if (type == TaskType.TASK) {
                this.tasks.put(id, task);
                this.orderedTasks.add(task);
                //prioritizedTasks.put(task.getStartTime(), task);
            } else if (type == TaskType.SUBTASK) {
                subtasks.put(id, (Subtask) task);
                this.orderedTasks.add(task);
                //prioritizedTasks.put(task.getStartTime(), task);
            } else if (type == TaskType.EPIC) {
                epics.put(id, (Epic) task);
            }
        }
    }

    private void load() {
        try {
            ArrayList<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
            }.getType());
            addTasks(tasks);

            ArrayList<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
            }.getType());
            addTasks(epics);

            ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            addTasks(subtasks);

            List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {
            }.getType());

            for (Integer taskId : history) {
                if (this.tasks.containsKey(taskId)) {
                    Task task = tasks.get(taskId);
                    historyManager.add(task);
                } else if (this.epics.containsKey(taskId)) {
                    Epic epic = epics.get(taskId);
                    historyManager.add(epic);
                } else if (this.subtasks.containsKey(taskId)) {
                    Subtask subtask = subtasks.get(taskId);
                    historyManager.add(subtask);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void save() {
        try {
            String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
            client.put("tasks", jsonTasks);

            String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
            client.put("subtasks", jsonSubtasks);

            String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
            client.put("epics", jsonEpics);

            String jsonHistory = gson.toJson(new ArrayList<>(getHistory()));
            client.put("history", jsonHistory);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

}
