package model.manager;

import model.task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;
    private static Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Задача1", "Описание задания");
    }

    @Test
    void getHistory() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая");
        assertEquals(1, history.size(), "История пустая");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая");
        assertEquals(1, history.size(), "История пустая");
    }

    @Test
    void remove() {
        historyManager.add(task);
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая");
    }
}