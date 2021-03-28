package socialnetwork.domain;

import javafx.scene.control.Button;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class MessageDTO {
    Long id;
    Long fromID;
    String from;
    List<Long> toIDs;
    String to;
    String message;
    String date;
    Button btnReply;
    Long reply;

    public MessageDTO(Long id, Long fromID, String from, List<Long> toIDs, String to, String message, String date, Button btnReply, Long reply) {
        this.id = id;
        this.fromID = fromID;
        this.from = from;
        this.toIDs = toIDs;
        this.to = to;
        this.message = message;
        this.date = date;
        this.btnReply = btnReply;
        this.reply = reply;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getFromID() {
        return fromID;
    }

    public void setFromID(Long fromID) {
        this.fromID = fromID;
    }

    public List<Long> getToIDs() {
        return toIDs;
    }

    public void setToIDs(List<Long> toIDs) {
        this.toIDs = toIDs;
    }

    public Button getBtnReply() {
        return btnReply;
    }

    public void setBtnReply(Button btnReply) {
        this.btnReply = btnReply;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageDTO)) return false;
        MessageDTO that = (MessageDTO) o;
        return getId().equals(that.getId()) &&
                getFromID().equals(that.getFromID()) &&
                getFrom().equals(that.getFrom()) &&
                getToIDs().equals(that.getToIDs()) &&
                getTo().equals(that.getTo()) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                getDate().equals(that.getDate()) &&
                Objects.equals(getBtnReply(), that.getBtnReply()) &&
                getReply().equals(that.getReply());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFromID(), getFrom(), getToIDs(), getTo(), getMessage(), getDate(), getBtnReply(), getReply());
    }
}
