package model.task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", subtasksId=" + subtasksId +
                '}';
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public static Epic fromString(String value) {
        String[] split = value.split(",");
        Epic epic = new Epic(split[2], split[4]);
        epic.setId(Integer.parseInt(split[0]));
        epic.setStatusFromString(split[3]);
        return epic;
    }
}
