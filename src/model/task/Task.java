package model.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status; // NEW IN_PROGRESS DONE
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTimeAndDuration(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setStatusFromString(String string) {
        switch (string) {
            case "DONE":
                this.status = Status.DONE;
                break;
            case "NEW":
                this.status = Status.NEW;
                break;
            case "IN_PROGRESS":
                this.status = Status.IN_PROGRESS;
                break;
        }
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public static Task fromString(String value) {
        String[] split = value.split(",");
        Task task = new Task(split[2], split[4]);
        task.setId(Integer.parseInt(split[0]));
        task.setStatusFromString(split[3]);
        return task;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }


}
