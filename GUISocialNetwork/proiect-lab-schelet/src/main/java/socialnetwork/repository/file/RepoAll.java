package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RepoAll {
    private FriendshipFile friendshipFile;
    private UserFile userFile;
    private MessageFile messageFile;

    public RepoAll(UserFile userFile, FriendshipFile friendshipFile, MessageFile messageFile) {
        this.friendshipFile = friendshipFile;
        this.userFile = userFile;
        this.messageFile = messageFile;
        createFriendships();
    }

    /**
     * for each friendship, the users are added to each other's friends list
     */
    private void createFriendships() {
        friendshipFile.findAll().forEach(f-> {
            if(f.getStatus().equals(Status.APPROVED)){
                userFile.createFriendship(f);
            }
        });
    }

    /**
     * @param user user must be not null
     * @return null- if the given user is saved
     * otherwise returns the user (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    public User addUser(User user) {
        return userFile.save(user);
    }

    /**
     * removes the user with the specified id
     *
     * @param id id must be not null
     * @return the removed user or null if there is no user with the given id
     * @throws IllegalArgumentException if the given id is null.
     */
    public User removeUser(Long id) {
        User u = userFile.delete(id);
        if (u != null) {
            Iterable<Friendship> friendships = friendshipFile.findAll();
            List<Friendship> toRemove = new ArrayList<>();
            friendships.forEach(friendship -> {
                if (friendship.getId().getLeft().equals(id) || friendship.getId().getRight().equals(id)) {
                    toRemove.add(friendship);
                }
            });
            toRemove.forEach(p -> friendshipFile.delete(p.getId()));

            Iterable<Message> messages = messageFile.findAll();
            List<Message> toRemove2 = new ArrayList<>();
            messages.forEach(message -> {
                List<Long> to = message.getTo();
                if (message.getFrom().equals(id) || (to.contains(id) && to.size() == 1)) {
                    toRemove2.add(message);
                } else if (to.contains(id)) {
                    to.remove(id);
                    message.setTo(to);
                }
            });
            toRemove2.forEach(p -> messageFile.delete(p.getId()));
        }
        return u;
    }

    /**
     * @return the user with the specified id
     */
    public User getUser(Long id) {
        return userFile.findOne(id);
    }

    public User getUser(String firstName, String lastName) {
        final User[] user = {null};
        StreamSupport.stream(userFile.findAll().spliterator(), false)
                .forEach(u -> {
                    if (u.getFirstName().equals(firstName) && u.getLastName().equals(lastName)) {
                        user[0] = u;
                    }
                });
        if (user[0] == null)
            throw new RepositoryException("There is no user with this name!");
        return user[0];
    }

    /**
     * @return all users
     */
    public Iterable<User> getAllUsers() {
        return userFile.findAll();
    }

    /**
     * @return number of users
     */
    public int getUsersSize() {
        return userFile.size();
    }

    /**
     * @param friendship friendship must be not null
     * @return null- if the given friendship is saved
     * otherwise returns the friendship (id already exists)
     * @throws ValidationException      if the friendship is not valid
     * @throws RepositoryException      if there are no users with these ids
     * @throws IllegalArgumentException if the given friendship is null.     *
     */
    public Friendship addFriendship(Friendship friendship) {
        if (userFile.findOne(friendship.getId().getLeft()) == null || userFile.findOne(friendship.getId().getRight()) == null) {
            throw new RepositoryException("The friendship cannot be created, one or both users with these ids do not exist");
        }
        Friendship f = new Friendship(friendship.getDate(), friendship.getStatus());
        f.setId(new Tuple<>(friendship.getId().getRight(), friendship.getId().getLeft()));
        if(friendshipFile.save(f) != null){
            throw new RepositoryException("This friendship already exists!");
        } else {
            friendshipFile.delete(f.getId());
        }
        f = friendshipFile.save(friendship);
        if (f == null) {
            if (friendship.getStatus() == Status.APPROVED) {
                userFile.createFriendship(friendship);
            }
        } else
            throw new RepositoryException("This friendship already exists!");
        return f;
    }

    /**
     * removes the friendship with the specified id
     *
     * @param id id must be not null
     * @return the removed friendship or null if there is no friendship with the given id
     * @throws IllegalArgumentException if the given id is null
     * @throws RepositoryException      if there are no users with these ids
     */
    public Friendship removeFriendship(Tuple<Long, Long> id) {
        if (userFile.findOne(id.getLeft()) == null || userFile.findOne(id.getRight()) == null) {
            throw new RepositoryException("The friendship cannot be removed, one or both users with these ids do not exist");
        }
        Friendship f = friendshipFile.delete(id);
        if (f == null) {
            Long aux;
            aux = id.getRight();
            id.setRight(id.getLeft());
            id.setLeft(aux);
            f = friendshipFile.delete(id);
        }
        if (f != null) {
            userFile.removeFriendship(f);
        }
        return f;
    }

    /**
     * @param id - the id of the user
     * @return the list of the user's friends
     * @throws ValidationException if the id is invalid
     */
    public List<Friendship> getFriends(String id) {
        if (id.equals("")) {
            throw new ValidationException("The ID cannot be empty!");
        } else if (!id.matches("[0-9]+")) {
            throw new ValidationException("The ID cannot contain letters or symbols!");
        }

        List<Friendship> friends = new ArrayList<>();
        friendshipFile.findAll().forEach(friendship -> {
            if ((friendship.getId().getLeft().equals(Long.parseLong(id)) || friendship.getId().getRight().equals(Long.parseLong(id))) && friendship.getStatus().equals(Status.APPROVED)) {
                friends.add(friendship);
            }
        });

        return friends;
    }

    /**
     * @param id - the id of the user
     * @return the list of the user's friend requests
     * @throws ValidationException if the id is invalid
     */
    public List<Friendship> getFriendRequests(String id) {
        if (id.equals("")) {
            throw new ValidationException("The ID cannot be empty!");
        } else if (!id.matches("[0-9]+")) {
            throw new ValidationException("The ID cannot contain letters or symbols!");
        }

        List<Friendship> friendRequests = new ArrayList<>();
        friendshipFile.findAll().forEach(friendship -> {
            if (friendship.getId().getRight().equals(Long.parseLong(id)) && friendship.getStatus().equals(Status.PENDING)) {
                friendRequests.add(friendship);
            }
        });

        return friendRequests;
    }

    /**
     * @param id - the id of the user
     * @return the list of the user's sent friend requests
     * @throws ValidationException if the id is invalid
     */
    public List<Friendship> getSentFriendRequests(String id) {
        if (id.equals("")) {
            throw new ValidationException("The ID cannot be empty!");
        } else if (!id.matches("[0-9]+")) {
            throw new ValidationException("The ID cannot contain letters or symbols!");
        }

        List<Friendship> friendRequests = new ArrayList<>();
        friendshipFile.findAll().forEach(friendship -> {
            if (friendship.getId().getLeft().equals(Long.parseLong(id)) && friendship.getStatus().equals(Status.PENDING)) {
                friendRequests.add(friendship);
            }
        });

        return friendRequests;
    }

    /**
     * @param requests - the list of the friendships which need to be updated
     */
    public void updateFriendships(List<Friendship> requests) {
        requests.forEach(request -> {
            if (request.getStatus().equals(Status.APPROVED)) {
                friendshipFile.update(request);
                userFile.createFriendship(request);
            } else if (request.getStatus().equals(Status.REJECTED) || request.getStatus().equals(Status.DELETED)) {
                friendshipFile.delete(request.getId());
            }
        });
    }

    /**
     * @return all friendships
     */
    public Iterable<Friendship> getAllFriendships() {
        return friendshipFile.findAll();
    }

    /**
     * @return number of friendships
     */
    public int getFriendshipsSize() {
        return friendshipFile.size();
    }

    /**
     * @param id - the id of the user
     * @return the list of his friends and the date when they became friends
     */

    public List<String> userFriendships(Long id) {
        if (userFile.findOne(id) == null)
            throw new RepositoryException("There is no user with this id");
        List<String> friends = new ArrayList<>();
        StreamSupport.stream(friendshipFile.findAll().spliterator(), false)
                .filter(friendship -> friendship.toString().contains("id='" + id.toString()) && friendship.getStatus().equals(Status.APPROVED))
                .forEach(friendship -> {
                    Long id2;
                    if (friendship.getId().getLeft().equals(id))
                        id2 = friendship.getId().getRight();
                    else
                        id2 = friendship.getId().getLeft();
                    User user = userFile.findOne(id2);
                    String s = user.getFirstName() + "|" + user.getLastName() + "|" + friendship.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
                    friends.add(s);
                });
        return friends;
    }

    /**
     * @param id - the id of the user and the month
     * @return the list of his friends which where added in the specific month
     * and the date when they became friends
     */

    public List<String> userFriendshipsMonth(Long id, int month) {
        List<String> friends = userFriendships(id);
        String m = "";
        if (month < 10)
            m += "0";
        m += month;
        String finalM = m;
        return friends.stream()
                .filter(friendship -> friendship.contains("-" + finalM + "-"))
                .collect(Collectors.toList());
    }

    /**
     * @param data the message's data
     * @return null- if the given message is saved
     * otherwise returns the message (id already exists)
     * @throws RepositoryException      if there are no users with these ids
     * @throws IllegalArgumentException if the given message is null.     *
     */
    public Message addMessage(List<String> data) {
        Message m = messageFile.extractEntity(data);
        if (userFile.findOne(m.getFrom()) == null)
            throw new RepositoryException("There is no user with the id " + m.getFrom());
        m.getTo().forEach(to -> {
            if (userFile.findOne(to) == null) {
                throw new RepositoryException("There is no user with the id " + to);
            }
        });
        return messageFile.save(m);
    }

    /**
     * @param id the id of the user
     * @return the user's received messages
     */
    public List<Message> getUserMessages(Long id) {
        return StreamSupport.stream(messageFile.findAll().spliterator(), false)
                .filter(message -> message.getTo().contains(id))
                .collect(Collectors.toList());
    }

    /**
     * @param id the id of the user
     * @return the user's received messages
     */
    public List<Message> getUserSentMessages(Long id) {
        return StreamSupport.stream(messageFile.findAll().spliterator(), false)
                .filter(message -> message.getFrom().equals(id))
                .collect(Collectors.toList());
    }

    /**
     * @return the number of messages
     */
    public int getMessagesSize() {
        return messageFile.size();
    }

    /**
     * @param id1 - the first id
     * @param id2 - the second id
     * @return the chat between these two users
     * @throws RepositoryException if there are no users with these ids or if they are identical
     */
    public List<Message> getChat(Long id1, Long id2) {
        List<Message> chat = new ArrayList<>();
        if (userFile.findOne(id1) == null)
            throw new RepositoryException("There is no user with the id " + id1 + "!");
        if (userFile.findOne(id2) == null)
            throw new RepositoryException("There is no user with the id " + id2 + "!");
        if (id1.equals(id2))
            throw new RepositoryException("The IDs cannot be identical!");

        messageFile.findAll().forEach(message -> {
            if ((message.getFrom().equals(id1) && message.getTo().get(0).equals(id2) && message.getTo().size() == 1) ||
                    message.getFrom().equals(id2) && message.getTo().get(0).equals(id1) && message.getTo().size() == 1) {
                chat.add(message);
            }
        });

        return chat;
    }

    public User login(List<String> data){
            if(data.contains(""))
                throw new RepositoryException("All fields must pe filled in!");
            User user = null;
            Iterable<User> allUsers = userFile.findAll();
            for(User u:allUsers){
                if(u.getUsername().equals(data.get(0)) && u.getPassword().equals(data.get(1)))
                    user = u;
            }
            if(user == null)
                throw new RepositoryException("Username or password incorrect!");
            return user;
    }
}
