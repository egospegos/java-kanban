package model.manager;

import model.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();

    void add(Task task);
}
