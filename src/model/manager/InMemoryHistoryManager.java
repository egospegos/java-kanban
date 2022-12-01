package model.manager;

import model.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    public static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        // При добавлении удалить узел из nodeMap
        // Добавить в конец связного списка
        // Добавить в nodeMap
        removeNode(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    private List<Task> getTasks() {
        //итерация по списку и создание ArrayList
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node x = first; x != null; x = x.next) {
            tasks.add(x.task);
        }
        return tasks;
    }

    private void linkLast(Task task) {
        //Добавление в конец
        final Node l = last;
        final Node newNode = new Node(task, l, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;

        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(int id) {
        final Node node = nodeMap.remove(id);
        // Удаление узла
        if (node == null) {
            return;
        } else {
            final Node next = node.next;
            final Node prev = node.prev;

            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.task = null;
        }
    }

}
