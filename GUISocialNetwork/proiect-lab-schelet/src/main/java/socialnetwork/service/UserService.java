package socialnetwork.service;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.file.RepoAll;
import sun.security.validator.ValidatorException;

import java.util.List;

public class UserService {
    private RepoAll repo;
    private UserValidator val;

    public UserService(RepoAll repo, UserValidator val) {
        this.repo = repo;
        this.val = val;
    }

    /**
     * @param data - the data from the client
     * @return null- if the given user is saved
     * otherwise returns the user (id already exists)  *
     */
    public User addUser(List<String> data) {
        val.validate(data);
        User user = new User(data.get(0), data.get(1), data.get(2), data.get(3));
        user.setId((long) (repo.getUsersSize()+1));
        return repo.addUser(user);
    }

    /**
     * removes the user with the specified id
     *
     * @param data - the data from the client
     * @return the removed user or null if there is no user with the given id
     */
    public User removeUser(List<String> data) {
        val.validate(data);
        return repo.removeUser(Long.parseLong(data.get(0)));
    }

    /**
     * @return all users
     */
    public Iterable<User> getAll() {
        return repo.getAllUsers();
    }

    /**
     * @return number of users
     */
    public int getSize() {
        return repo.getUsersSize();
    }

    /**
     * @param data, month - the id of the user and the month
     * @return the list of his friends which where added in the specific month
     * and the date when they became friends
     */
    public List<String> userFriendshipsMonth(List<String> data, int month) {
        val.validate(data);
        return repo.userFriendshipsMonth(Long.parseLong(data.get(0)), month);
    }

    /**
     *
     * @param id the user's id
     * @return the user with the given id
     */
    public User getUser(Long id) {
        return repo.getUser(id);
    }

    /**
     *
     * @param firstName
     * @param lastName
     * @return
     */
    public User getUser(String firstName, String lastName) {
        val.validateName(firstName,lastName);
        return repo.getUser(firstName,lastName);
    }

    public User login(List<String> data){
        return repo.login(data);
    }
}
