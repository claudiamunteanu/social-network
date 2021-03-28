package socialnetwork.domain;

import java.util.*;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private List<Long> friends;
    private String username;
    private String password;

    public User(String firstName, String lastName, String username, String parola) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = parola;
        friends = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Long> getFriends() {
        return friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addFriend(Long userID) {
        this.friends.add(userID);
        Collections.sort(this.friends);
    }

    public void removeFriend(Long userID) {
        this.friends.remove(userID);
    }


    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friends + '\'' +
                ", parola=" + password +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getFirstName().equals(user.getFirstName()) &&
                getLastName().equals(user.getLastName()) &&
                getFriends().equals(user.getFriends()) &&
                getUsername().equals(user.getUsername()) &&
                getPassword().equals(user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends(), getUsername(), getPassword());
    }
}