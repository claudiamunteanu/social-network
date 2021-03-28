package socialnetwork.domain;

import socialnetwork.repository.file.Status;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Friendship extends Entity<Tuple<Long, Long>> {

    LocalDateTime date;
    Status status;

    public Friendship(LocalDateTime date, Status status) {
        this.date = date;
        this.status = status;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return date.equals(that.date) &&
                status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, status);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Friendship{" +
                "first id='" + getId().getLeft() + '\'' +
                "second id='" + getId().getRight() + '\'' +
                "date='" + formatter.format(getDate()) + '\'' +
                "status=" + getStatus() +
                '}';
    }
}
