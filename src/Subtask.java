public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, String status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", epicId=" + epicId +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
