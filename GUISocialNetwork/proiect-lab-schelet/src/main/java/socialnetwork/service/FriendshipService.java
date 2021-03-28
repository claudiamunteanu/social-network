package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.events.ChangeEventType;
import socialnetwork.events.FriendshipChangeEvent;
import socialnetwork.observer.Observable;
import socialnetwork.observer.Observer;
import socialnetwork.repository.file.RepoAll;
import socialnetwork.repository.file.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FriendshipService implements Observable<FriendshipChangeEvent> {
    private RepoAll repo;
    private FriendshipValidator val;

    public FriendshipService(RepoAll repo, FriendshipValidator val) {
        this.repo = repo;
        this.val = val;
    }

    /**
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @return null- if the given friendship is saved
     * otherwise returns the friendship (id already exists)  *
     */
    public Friendship addFriendship(Long id1, Long id2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime date = LocalDateTime.parse(formatter.format(LocalDateTime.now()),formatter);
        Status status = Status.PENDING;
        Friendship friendship = new Friendship(date, status);
        Tuple<Long, Long> id = new Tuple<Long, Long>(id1, id2);
        friendship.setId(id);

        Friendship f = repo.addFriendship(friendship);
        if (f == null){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD,f));
        }
        return f;
    }

    /**
     * removes the user with the specified id
     *
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @return the removed friendship or null if there is no friendship with the given id

     */

    public Friendship removeFriendship(Long id1, Long id2) {
        Tuple<Long, Long> id = new Tuple<Long, Long>(id1, id2);
        Friendship f = repo.removeFriendship(id);
        if (f != null){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE,f));
        }
        return f;
    }

    /**
     * @return all friendships
     */
    public Iterable<Friendship> getAll() {
        return repo.getAllFriendships();
    }

    /**
     * @return number of friendships
     */
    public int getSize() {
        return repo.getFriendshipsSize();
    }

    /**
     *
     * @param id the id of the user
     * @return the list of the user's friends
     */
    public List<Friendship> getFriends(String id) {
        return repo.getFriends(id);
    }

    /**
     *
     * @param id the id of the user
     * @return the list of the user's friend requests
     */
    public List<Friendship> getFriendRequests(String id) {
        return repo.getFriendRequests(id);
    }

    /**
     *
     * @param requests the list of the friendships which need to be updated
     */
    public void updateFriendships(List<Friendship> requests) {
        repo.updateFriendships(requests);
        requests.forEach(r->notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE,r)));
    }

    /**
     *
     * @param id the id of the user
     * @return the list of the user's sent friend requests
     */
    public List<Friendship> getSentFriendRequests(String id) {
        return repo.getSentFriendRequests(id);
    }

    private List<Observer<FriendshipChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
