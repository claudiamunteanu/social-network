package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.repository.file.RepoAll;

import java.time.LocalDateTime;
import java.util.List;

public class MessageService {
    private RepoAll repo;
    private MessageValidator val;

    public MessageService(RepoAll repo, MessageValidator val) {
        this.repo = repo;
        this.val = val;
    }

    /**
     * @param data - the data from the client
     * @return null- if the given message is saved
     * otherwise returns the message (id already exists)  *
     */
    public Message addMessage(List<String> data) {
        val.validate(data);
        return repo.addMessage(data);
    }

    public List<Message> getUserMessages(String id){
        val.validateID(id);
        return repo.getUserMessages(Long.parseLong(id));
    }

    public List<Message> getUserSentMessages(String id){
        val.validateID(id);
        return repo.getUserSentMessages(Long.parseLong(id));
    }

    /**
     * @return the number of messages
     */
    public int getSize() {
        return repo.getMessagesSize();
    }

    /**
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @return the chat between these two users
     * @throws socialnetwork.domain.validators.ValidationException if the ids are invalid
     */
    public List<Message> getChat(String id1, String id2) {
        val.validateID(id1);
        val.validateID(id2);
        return repo.getChat(Long.parseLong(id1), Long.parseLong(id2));
    }
}
