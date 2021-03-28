package socialnetwork.repository.file;

import socialnetwork.domain.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageFile extends AbstractFileRepository<Long, Message> {

    public MessageFile(String fileName) {
        super(fileName);
    }

    @Override
    public Message extractEntity(List<String> attributes) {
        //TODO: implement method
        long id = Long.parseLong(attributes.get(0));
        long from = Long.parseLong(attributes.get(1));
        List<Long> to = new ArrayList<>();
        List<String> toString = Arrays.asList(attributes.get(2).split(" "));
        toString.forEach(string ->
                to.add(Long.parseLong(string)));
        String message = attributes.get(3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(attributes.get(4), formatter);

        long reply;
        reply = Long.parseLong(attributes.get(5));

        return new Message(id, from, to, message, dateTime, reply);
    }

    @Override
    protected String createEntityAsString(Message entity) {
        final String[] s = new String[1];
        s[0] = entity.getId() + ";" + entity.getFrom() + ";";
        List<Long> to = entity.getTo();
        to.forEach(id ->
                s[0] = s[0] + id + " ");
        s[0] += ";" + entity.getMessage() + ";";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        s[0] += entity.getDate().format(formatter) + ";" + entity.getReply();
        return s[0];
    }


}
