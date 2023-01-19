package model.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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
                ", startTime=" + super.getStartTime() + '\'' +
                ", duration=" + super.getDuration() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
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
        subtask.setStartTimeAndDuration(LocalDateTime.parse(split[6]), Duration.parse(split[7]));
        return subtask;
    }
}
