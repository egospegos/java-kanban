package model.manager;

import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import model.task.Status;

import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask, int epicId);

    void updateTask(Task task, int id, Status status);

    void updateEpic(Epic epic, int id);

    void updateSubtask(Subtask subtask, int id, Status status);

    List<Task> getTasks();

    Status getEpicStatus(Epic epic);

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasksByEpicId(int epicId);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    List<Task> getHistory();

}
