package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    Long id;
    Long from;
    List<Long> to;
    String message;
    LocalDateTime date;
    Long reply;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public Message(long id, Long from, List<Long> to, String message, LocalDateTime data, long reply) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = data;
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return id.equals(message1.id) &&
                from.equals(message1.from) &&
                to.equals(message1.to) &&
                message.equals(message1.message) &&
                date.equals(message1.date) &&
                Objects.equals(reply, message1.reply);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + formatter.format(date) +
                ", reply=" + reply +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, message, date, reply);
    }
}
