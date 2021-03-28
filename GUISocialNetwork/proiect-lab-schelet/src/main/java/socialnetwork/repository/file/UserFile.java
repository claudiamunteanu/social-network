package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User> {

    public UserFile(String fileName) {
        super(fileName);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        //TODO: implement method
        User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName() + ";" + entity.getUsername() + ";" + entity.getPassword();
    }

    @Override
    public User delete(Long id) {
        User u = super.delete(id);
        if (u != null) {
            super.findAll().forEach(entity -> entity.removeFriend(id));
        }
        return u;
    }


    /**
     * creates a friendship between two users
     *
     * @param p - the friendship to be created
     *          p must not be null
     */
    public void createFriendship(Friendship p) {
        User user1 = super.findOne(p.getId().getLeft());
        User user2 = super.findOne(p.getId().getRight());
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void removeFriendship(Friendship p) {
        User user1 = super.findOne(p.getId().getLeft());
        User user2 = super.findOne(p.getId().getRight());
        user1.removeFriend(user2.getId());
        user2.removeFriend(user1.getId());
    }

}
