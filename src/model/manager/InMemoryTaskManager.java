package model.manager;

import model.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int generatedId = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        task.setId(generatedId++);
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generatedId++);
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        subtask.setId(generatedId++);
        subtask.setEpicId(epicId);
        subtask.setStatus(Status.NEW);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).getSubtasksId().add(subtask.getId()); //добавили ИД подзадачи в нужный эпик
    }

    @Override
    public void updateTask(Task task, int id, Status status) {
        task.setId(id);
        task.setStatus(status);
        tasks.put(id, task);
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        ArrayList<Integer> subtasksId = epics.get(id).getSubtasksId();
        epic.setSubtasksId(subtasksId); // перенос подзадач эпика
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void updateSubtask(Subtask subtask, int id, Status status) {
        subtask.setId(id);
        int epicId = subtasks.get(id).getEpicId();
        subtask.setEpicId(epicId);
        subtask.setStatus(status);
        subtasks.put(id, subtask);

        //обновление статуса эпика
        Status newStatus = getEpicStatus(epics.get(epicId));
        epics.get(epicId).setStatus(newStatus);
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>(tasks.values());
        return taskList;
    }

    @Override
    public Status getEpicStatus(Epic epic) {
        Status status = Status.NEW;
        int counterDone = 0;
        int counterNew = 0;
        if (epic.getSubtasksId().size() == 0) return Status.NEW;
        for (int i = 0; i < epic.getSubtasksId().size(); i++) {
            int subtaskId = epic.getSubtasksId().get(i);
            status = subtasks.get(subtaskId).getStatus();
            if (status.equals("IN_PROGRESS")) {
                return Status.IN_PROGRESS;
            }
            if (status.equals("NEW")) {
                counterNew++;
            }
            if (status.equals("DONE")) {
                counterDone++;
            }
        }
        if (counterNew != 0 && counterNew == epic.getSubtasksId().size()) return Status.NEW;
        if (counterDone != 0 && counterDone == epic.getSubtasksId().size()) return Status.DONE;

        return Status.IN_PROGRESS;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>(epics.values());
        return epicList;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskList = new ArrayList<>(subtasks.values());
        return subtaskList;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer i : subtasks.keySet()) {
            if (subtasks.get(i).getEpicId() == epicId) subtasksOfEpic.add(subtasks.get(i));
        }
        return subtasksOfEpic;
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        epics.remove(id);
        //удалить связанные с эпиком сабтаски
        for (Integer i : subtasks.keySet()) {
            if (subtasks.get(i).getEpicId() == id) subtasks.remove(i);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        //удалить ИД сабтаска из списка ИД сабтасков связанного эпика
        int epicId = subtasks.get(id).getEpicId();
        for (int i = 0; i < epics.get(epicId).getSubtasksId().size(); i++) {
            if (epics.get(epicId).getSubtasksId().get(i) == id) {
                epics.get(epicId).getSubtasksId().remove(i);
                break;
            }
        }
        //удалить саму сабтаску
        subtasks.remove(id);
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear(); //при удалении всех эпиков удаляются все сабтаски
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        epics.clear(); //при удалении всех сабтасков удаляются все эпики
    }

    @Override
    public LinkedList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
