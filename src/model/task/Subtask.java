package model.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "task.Subtask{" +
                "id=" + super.getId() +
                ", epicId=" + epicId +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                '}';
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public static Subtask fromString(String value) {
        String[] split = value.split(",");
        Subtask subtask = new Subtask(split[2], split[4]);
        subtask.setId(Integer.parseInt(split[0]));
        subtask.setStatusFromString(split[3]);
        subtask.setEpicId(Integer.parseInt(split[5]));
        return subtask;
    }
}
