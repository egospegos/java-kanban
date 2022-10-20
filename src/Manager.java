import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int generatedId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void createTask(Task task) {
        task.setId(generatedId++);
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(generatedId++);
        epic.setStatus("NEW");
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask, int epicId) {
        subtask.setId(generatedId++);
        subtask.setEpicId(epicId);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).getSubtasksId().add(subtask.getId()); //добавили ИД подзадачи в нужный эпик
    }

    public void updateTask(Task task, int id) {
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic, int id) {
        ArrayList<Integer> subtasksId = epics.get(id).getSubtasksId();
        epic.setSubtasksId(subtasksId); // перенос подзадач эпика
        epics.put(id, epic);
    }

    public void updateSubtask(Subtask subtask, int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtask.setEpicId(epicId);
        subtasks.put(id, subtask);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    private String getEpicStatus(Epic epic) {
        String status = "";
        int counterDone = 0;
        int counterNew = 0;
        if (epic.getSubtasksId().size() == 0) return "NEW";
        for (int i = 0; i < epic.getSubtasksId().size(); i++) {
            int subtaskId = epic.getSubtasksId().get(i);
            status = subtasks.get(subtaskId).getStatus();
            if (status.equals("IN_PROGRESS")) {
                return "IN_PROGRESS";
            }
            if (status.equals("NEW")) {
                counterNew++;
            }
            if (status.equals("DONE")) {
                counterDone++;
            }
        }
        if (counterNew != 0 && counterNew == epic.getSubtasksId().size()) return "NEW";
        if (counterDone != 0 && counterDone == epic.getSubtasksId().size()) return "DONE";

        return "IN_PROGRESS";
    }

    public HashMap<Integer, Epic> getEpics() {
        for (Integer i : epics.keySet()) {
            String status = getEpicStatus(epics.get(i));
            epics.get(i).setStatus(status);
        }
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer i : subtasks.keySet()) {
            if (subtasks.get(i).getEpicId() == epicId) subtasksOfEpic.add(subtasks.get(i));
        }
        return subtasksOfEpic;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }
}
