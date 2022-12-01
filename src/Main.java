import model.manager.Managers;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Status;
import model.task.Subtask;
import model.task.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Задача1", "Описание задания");
        Task task2 = new Task("Задача2", "Описание дела");
        Epic epic1 = new Epic("Эпик1", "Большой эпик");
        Epic epic2 = new Epic("Эпик2", "Малый эпик");
        Subtask subtask1 = new Subtask("Подзадача1", "information");
        Subtask subtask2 = new Subtask("Подзадача2", "information");
        Subtask subtask3 = new Subtask("Подзадача3", "information");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());
        manager.createSubtask(subtask3, epic1.getId());
        System.out.println("Первоначальные данные");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println("Проверка работы историй:");
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        System.out.println(manager.getHistory());

        manager.getEpicById(epic2.getId());
        manager.getEpicById(epic1.getId());
        System.out.println(manager.getHistory());

        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());
        System.out.println(manager.getHistory());

        manager.getTaskById(task1.getId());
        System.out.println(manager.getHistory());

        System.out.println("Удаляем таск1, сабтаск1 и эпик1");
        manager.deleteTaskById(task1.getId());
        manager.deleteSubtaskById(subtask1.getId());
        System.out.println(manager.getHistory());

        manager.clearSubtasks();
        System.out.println(manager.getHistory());
    }
}
