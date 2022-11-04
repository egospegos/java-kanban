package model.manager;

import model.task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList<Task> taskHistory = new LinkedList<>();
    private static final int HISTORY_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }

    @Override
    public void add(Task task) {
        if (taskHistory.size() < HISTORY_SIZE) {
            taskHistory.add(task);
        } else {
            taskHistory.removeFirst();
            taskHistory.add(task);
        }
    }

}
