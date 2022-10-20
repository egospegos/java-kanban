import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("Задание", "Описание задания", "NEW");
        Task task2 = new Task("Дело", "Описание дела", "NEW");
        Epic epic1 = new Epic("Эпик BIG", "Первый эпик");
        Epic epic2 = new Epic("Эпик SMALL", "Второй эпик");
        Subtask subtask1 = new Subtask("Подзадача", "Подзадача первого эпика", "NEW");
        Subtask subtask2 = new Subtask("Subtask", "Subtask of first epic", "IN_PROGRESS");
        Subtask subtask3 = new Subtask("Подзадача", "Подзадача второго эпика", "NEW");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());
        manager.createSubtask(subtask3, epic2.getId());
        System.out.println("Первоначальные данные");
        System.out.println(manager.getTasks().toString());
        System.out.println(manager.getEpics().toString());
        System.out.println(manager.getSubtasks().toString());

        System.out.println("Обновлённые данные");
        Subtask updatedSubtask3 = new Subtask("Изменённая подзадача", "Новое описание", "DONE");
        manager.updateSubtask(updatedSubtask3, subtask3.getId());
        Task updatedTask = new Task("Изменённая задача", "Новое описание", "IN_PROGRESS");
        manager.updateTask(updatedTask, task1.getId());
        Subtask updatedSubtask2 = new Subtask("Updated subtask", "It was IN_PROGRESS", "NEW");
        manager.updateSubtask(updatedSubtask2, subtask2.getId());
        System.out.println(manager.getTasks().toString());
        System.out.println(manager.getEpics().toString());
        System.out.println(manager.getSubtasks().toString());

        System.out.println("Удаляем таск1 и эпик2");
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId());
        System.out.println(manager.getTasks().toString());
        System.out.println(manager.getEpics().toString());
        System.out.println(manager.getSubtasks().toString());

        System.out.println("Получаем список подзадач эпика1");
        ArrayList<Subtask> subtasksOfEpic = manager.getSubtasksByEpicId(epic1.getId());
        System.out.println(subtasksOfEpic);
    }
}
