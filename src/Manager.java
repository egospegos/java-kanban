import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int generatedId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void createTask(Task task) {
        task.setId(generatedId++);
        task.setStatus("NEW");
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
        subtask.setStatus("NEW");
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).getSubtasksId().add(subtask.getId()); //добавили ИД подзадачи в нужный эпик
    }

    public void updateTask(Task task, int id, String status) {
        task.setId(id);
        task.setStatus(status);
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic, int id) {
        ArrayList<Integer> subtasksId = epics.get(id).getSubtasksId();
        epic.setSubtasksId(subtasksId); // перенос подзадач эпика
        epic.setId(id);
        epics.put(id, epic);
    }

    public void updateSubtask(Subtask subtask, int id, String status) {
        subtask.setId(id);
        int epicId = subtasks.get(id).getEpicId();
        subtask.setEpicId(epicId);
        subtask.setStatus(status);
        subtasks.put(id, subtask);

        //обновление статуса эпика
        String newStatus = getEpicStatus(epics.get(epicId));
        epics.get(epicId).setStatus(newStatus);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList =  new ArrayList<>(tasks.values());
        return taskList;
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

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList =  new ArrayList<>(epics.values());
        return epicList;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskList =  new ArrayList<>(subtasks.values());
        return subtaskList;
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
        //удалить связанные с эпиком сабтаски
        for (Integer i : subtasks.keySet()) {
            if (subtasks.get(i).getEpicId() == id) subtasks.remove(i);
        }
    }

    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear(); //при удалении всех эпиков удаляются все сабтаски
    }

    public void clearSubtasks() {
        subtasks.clear();
        epics.clear(); //при удалении всех сабтасков удаляются все эпики
    }
}
