package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {

    public FriendshipFile(String fileName) {
        super(fileName);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        //TODO: implement method
        Tuple<Long, Long> id = new Tuple<>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(attributes.get(2), formatter);
        Status status = Status.valueOf(attributes.get(3));
        Friendship p = new Friendship(dateTime,status);
        p.setId(id);
        return p;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + formatter.format(entity.getDate()) + ";" + entity.getStatus();
    }

}
