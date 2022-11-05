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
        Task task1 = new Task("Задание", "Описание задания");
        Task task2 = new Task("Дело", "Описание дела");
        Epic epic1 = new Epic("Эпик BIG", "Первый эпик");
        Epic epic2 = new Epic("Эпик SMALL", "Второй эпик");
        Subtask subtask1 = new Subtask("Подзадача", "Подзадача первого эпика");
        Subtask subtask2 = new Subtask("task.Subtask", "task.Subtask of first epic");
        Subtask subtask3 = new Subtask("Подзадача", "Подзадача второго эпика");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());
        manager.createSubtask(subtask3, epic2.getId());
        System.out.println("Первоначальные данные");
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println("Обновлённые данные");
        Subtask updatedSubtask3 = new Subtask("Изменённая подзадача", "Новое описание");
        manager.updateSubtask(updatedSubtask3, subtask3.getId(), Status.IN_PROGRESS);
        Task updatedTask = new Task("Изменённая задача", "Новое описание");
        manager.updateTask(updatedTask, task1.getId(), Status.DONE);
        Subtask updatedSubtask2 = new Subtask("Updated subtask", "It was IN_PROGRESS");
        manager.updateSubtask(updatedSubtask2, subtask2.getId(), Status.DONE);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        System.out.println("Получаем список подзадач эпика1");
        List<Subtask> subtasksOfEpic = manager.getSubtasksByEpicId(epic1.getId());
        System.out.println(subtasksOfEpic);

        System.out.println("Проверка работы историй:");
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
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

        System.out.println("Удаляем таск1, сабтаск1 и эпик2");
        manager.deleteTaskById(task1.getId());
        manager.deleteSubtaskById(subtask1.getId());
        manager.deleteEpicById(epic2.getId());
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
    }
}
