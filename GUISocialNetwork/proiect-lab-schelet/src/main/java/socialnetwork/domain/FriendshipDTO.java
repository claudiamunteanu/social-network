package socialnetwork.domain;

import javafx.scene.control.Button;
import socialnetwork.repository.file.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FriendshipDTO {
    Long user1ID;
    Long user2ID;
    String user1FirstName;
    String user1LastName;
    String user2FirstName;
    String user2LastName;
    String status;
    String date;
    Button buttonAccept;
    Button buttonReject;

    public FriendshipDTO(String user1FirstName, String user1LastName, String user2FirstName, String user2LastName, String date, String status, Button btnAccept, Button btnReject) {
        this.user1FirstName = user1FirstName;
        this.user1LastName = user1LastName;
        this.user2FirstName = user2FirstName;
        this.user2LastName = user2LastName;
        this.status = status;
        this.date = date;
        this.buttonAccept = btnAccept;
        this.buttonReject = btnReject;
    }

    public Long getUser1ID() {
        return user1ID;
    }

    public void setUser1ID(Long user1ID) {
        this.user1ID = user1ID;
    }

    public Long getUser2ID() {
        return user2ID;
    }

    public void setUser2ID(Long user2ID) {
        this.user2ID = user2ID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser1FirstName() {
        return user1FirstName;
    }

    public void setUser1FirstName(String user1FirstName) {
        this.user1FirstName = user1FirstName;
    }

    public String getUser1LastName() {
        return user1LastName;
    }

    public void setUser1LastName(String user1LastName) {
        this.user1LastName = user1LastName;
    }

    public String getUser2FirstName() {
        return user2FirstName;
    }

    public void setUser2FirstName(String user2FirstName) {
        this.user2FirstName = user2FirstName;
    }

    public String getUser2LastName() {
        return user2LastName;
    }

    public void setUser2LastName(String user2LastName) {
        this.user2LastName = user2LastName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Button getButtonAccept() {
        return buttonAccept;
    }

    public void setButtonAccept(Button buttonAccept) {
        this.buttonAccept = buttonAccept;
    }

    public Button getButtonReject() {
        return buttonReject;
    }

    public void setButtonReject(Button buttonReject) {
        this.buttonReject = buttonReject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendshipDTO)) return false;
        FriendshipDTO that = (FriendshipDTO) o;
        return getUser1ID().equals(that.getUser1ID()) &&
                getUser2ID().equals(that.getUser2ID()) &&
                getUser1FirstName().equals(that.getUser1FirstName()) &&
                getUser1LastName().equals(that.getUser1LastName()) &&
                getUser2FirstName().equals(that.getUser2FirstName()) &&
                getUser2LastName().equals(that.getUser2LastName()) &&
                getStatus().equals(that.getStatus()) &&
                getDate().equals(that.getDate()) &&
                Objects.equals(getButtonAccept(), that.getButtonAccept()) &&
                Objects.equals(getButtonReject(), that.getButtonReject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser1ID(), getUser2ID(), getUser1FirstName(), getUser1LastName(), getUser2FirstName(), getUser2LastName(), getStatus(), getDate(), getButtonAccept(), getButtonReject());
    }

    @Override
    public String toString() {
        return "FriendshipDTO{" +
                "user1ID=" + user1ID +
                ", user2ID=" + user2ID +
                ", user1FirstName='" + user1FirstName + '\'' +
                ", user1LastName='" + user1LastName + '\'' +
                ", user2FirstName='" + user2FirstName + '\'' +
                ", user2LastName='" + user2LastName + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}
