package model.manager;

import model.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int generatedId = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> orderedTasks = new TreeSet<>(Comparator.comparing(l -> l.getStartTime()));
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        task.setId(generatedId++);
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
        orderedTasks.add(task);
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
        orderedTasks.add(subtask);
    }

    @Override
    public void updateTask(Task task, int id) {
        task.setId(id);
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
    public void updateSubtask(Subtask subtask, int id) {
        subtask.setId(id);
        int epicId = subtasks.get(id).getEpicId();
        subtask.setEpicId(epicId);
        subtasks.put(id, subtask);

        //обновление статуса эпика
        Status newStatus = getEpicStatus(epics.get(epicId));
        epics.get(epicId).setStatus(newStatus);
    }


    @Override
    public Status getEpicStatus(Epic epic) {
        Status status = Status.NEW;
        int counterDone = 0;
        int counterNew = 0;
        if (epic.getSubtasksId().size() == 0 || epic.getSubtasksId() == null) return Status.NEW;
        for (int i = 0; i < epic.getSubtasksId().size(); i++) {
            int subtaskId = epic.getSubtasksId().get(i);
            status = subtasks.get(subtaskId).getStatus();
            if (status.equals(Status.IN_PROGRESS)) {
                return Status.IN_PROGRESS;
            }
            if (status.equals(Status.NEW)) {
                counterNew++;
            }
            if (status.equals(Status.DONE)) {
                counterDone++;
            }
        }
        if (counterNew != 0 && counterNew == epic.getSubtasksId().size()) return Status.NEW;
        if (counterDone != 0 && counterDone == epic.getSubtasksId().size()) return Status.DONE;

        return Status.IN_PROGRESS;
    }

    @Override
    public void calculateEpicStartAndEndTime(Epic epic) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer i : subtasks.keySet()) {
            if (subtasks.get(i).getEpicId() == epic.getId()) subtasksOfEpic.add(subtasks.get(i));
        }
        //находим начало, конец и продолжительность
        LocalDateTime start = LocalDateTime.of(3000, Month.MAY, 1, 10, 10);
        LocalDateTime end = LocalDateTime.of(1500, Month.MAY, 1, 10, 10);
        long duration = 0;
        for (Subtask subtask : subtasksOfEpic) {
            if (subtask.getStartTime().isBefore(start)) start = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(end)) end = subtask.getEndTime();
            duration += subtask.getDuration().toMinutes();
        }
        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(Duration.ofMinutes(duration));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> orderedTasksList = new ArrayList<>(orderedTasks);
        return orderedTasksList;
    }

    //проверка пересечений
    @Override
    public void checkCrossing() {
        List<Task> orderedTasksList = new ArrayList<>(orderedTasks);
        for (int i = 0; i < orderedTasksList.size() - 1; i++) {
            LocalDateTime first = orderedTasksList.get(i).getEndTime();
            LocalDateTime second = orderedTasksList.get(i+1).getStartTime();
            if (first.isAfter(second)) System.out.println("Есть пересечения");
        }
    }

    @Override
    public List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>(tasks.values());
        return taskList;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epicList = new ArrayList<>(epics.values());
        return epicList;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtaskList = new ArrayList<>(subtasks.values());
        return subtaskList;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> subtasksOfEpic = new ArrayList<>();
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
        orderedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        epics.remove(id);
        historyManager.remove(id);
        //удалить связанные с эпиком сабтаски
        ArrayList<Integer> subtasksIdToDelete = new ArrayList<>();
        for (Integer i : subtasks.keySet()) {
            if (subtasks.get(i).getEpicId() == id) {
                subtasksIdToDelete.add(i); // добавляем ИД сабтасков, которые потом надо удалить
                //subtasks.remove(i);
                historyManager.remove(i);
            }
        }
        for (Integer i : subtasksIdToDelete) {
            orderedTasks.remove(subtasks.get(i));
            subtasks.remove(i);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        orderedTasks.remove(subtasks.get(id));
        //удалить ИД сабтаска из списка ИД сабтасков связанного эпика
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).getSubtasksId().remove(Integer.valueOf(id)); //удалить объект ИД из списка ИД сабтасков
        //удалить саму сабтаску
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearTasks() {
        //удалить из истории все таски и из orderedTasks
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            orderedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        //удалить из истории все эпики и сабтаски
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            orderedTasks.remove(subtasks.get(id));
        }
        epics.clear();
        subtasks.clear(); //при удалении всех эпиков удаляются все сабтаски
    }

    @Override
    public void clearSubtasks() {
        //Очистить у эпиков поле subtasksId
        for (Integer id : epics.keySet()) {
            epics.get(id).getSubtasksId().clear();
        }
        //удалить сабтаски из истории и из orderedTasks
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            orderedTasks.remove(subtasks.get(id));
        }
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
