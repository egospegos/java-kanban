package model.manager;

import model.exception.ManagerSaveException;
import model.task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private static final String FIRST_LINE = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = super.getTasks();
        save();
        return tasks;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epics = super.getEpics();
        save();
        return epics;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = super.getSubtasks();
        save();
        return subtasks;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write(FIRST_LINE + "\n");

            for (Task task : tasks.values()) {
                fileWriter.write(toString(task) + "\n");
            }

            for (Epic epic : epics.values()) {
                fileWriter.write(toString(epic) + "\n");
            }

            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(toString(subtask) + "\n");
            }

            fileWriter.write("\n" + historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager fileBTM = new FileBackedTaskManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            boolean historyLine = false;
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (!line.equals(FIRST_LINE)) {
                    if (historyLine) {
                        for (Integer id : historyFromString(line)) {
                            if (fileBTM.tasks.containsKey(id)) {
                                Task task = fileBTM.tasks.get(id);
                                fileBTM.historyManager.add(task);
                            } else if (fileBTM.epics.containsKey(id)) {
                                Epic epic = fileBTM.epics.get(id);
                                fileBTM.historyManager.add(epic);
                            } else if (fileBTM.subtasks.containsKey(id)) {
                                Subtask subtask = fileBTM.subtasks.get(id);
                                fileBTM.historyManager.add(subtask);
                            }
                        }
                    } else {
                        if (!line.isEmpty()) {
                            switch (getTaskType(line)) {
                                case TASK:
                                    Task task = Task.fromString(line);
                                    fileBTM.tasks.put(task.getId(), task);
                                    break;
                                case EPIC:
                                    Epic epic = Epic.fromString(line);
                                    fileBTM.epics.put(epic.getId(), epic);
                                    break;
                                case SUBTASK:
                                    Subtask subtask = Subtask.fromString(line);
                                    fileBTM.subtasks.put(subtask.getId(), subtask);
                                    fileBTM.epics.get(subtask.getEpicId()).getSubtasksId().add(subtask.getId());
                                    break;
                            }
                        } else {
                            historyLine = true;
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла");
        }
        return fileBTM;
    }

    public String toString(Task task) {
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",";
    }

    public String toString(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription() + ",";
    }

    public String toString(Subtask subtask) {
        return subtask.getId() + "," + TaskType.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus() + "," +
                subtask.getDescription() + "," + subtask.getEpicId();
    }

    public static TaskType getTaskType(String value) {

        String[] split = value.split(",");
        switch (split[1]) {
            case "TASK":
                return TaskType.TASK;
            case "EPIC":
                return TaskType.EPIC;
            case "SUBTASK":
                return TaskType.SUBTASK;
            default:
                return null;
        }
    }

    public static String historyToString(HistoryManager manager) {
        String line = "";
        List<Task> history = manager.getHistory();
        for (int i = 0; i < history.size(); i++) {
            if (i == (history.size() - 1)) {
                line = line + history.get(i).getId();
            } else {
                line = line + history.get(i).getId() + ",";
            }
        }
        return line;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] split = value.split(",");
        for (String number : split) {
            history.add(Integer.valueOf(number));
        }
        return history;
    }

    public static void main(String[] args) {
        String fileName = "info.csv";
        File fileInfo = new File("resources\\" + fileName);
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(fileInfo);

        Task task1 = new Task("Задача1", "Описание задания");
        Task task2 = new Task("Задача2", "Описание дела");
        Epic epic1 = new Epic("Эпик1", "Большой эпик");
        Epic epic2 = new Epic("Эпик2", "Малый эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "information");
        Subtask subtask3 = new Subtask("Подзадача3", "information");

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createEpic(epic2);
        fileBackedTaskManager.createSubtask(subtask1, epic1.getId());
        fileBackedTaskManager.createSubtask(subtask2, epic1.getId());
        fileBackedTaskManager.createSubtask(subtask3, epic1.getId());
        fileBackedTaskManager.getTaskById(task2.getId());
        fileBackedTaskManager.getEpicById(epic2.getId());
        fileBackedTaskManager.getSubtaskById(subtask3.getId());

        System.out.println("Записали в файл");
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());
        System.out.println("История:");
        System.out.println(fileBackedTaskManager.getHistory());


        FileBackedTaskManager fileBTM2 = loadFromFile(fileInfo);
        System.out.println("\nСчитали из файла");
        System.out.println(fileBTM2.getTasks());
        System.out.println(fileBTM2.getEpics());
        System.out.println(fileBTM2.getSubtasks());
        System.out.println("История:");
        System.out.println(fileBTM2.getHistory());
    }
}
