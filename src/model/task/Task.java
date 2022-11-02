package model.task;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status; // NEW IN_PROGRESS DONE

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "task.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
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
}
