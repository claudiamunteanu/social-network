package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.FriendshipDTO;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.events.FriendshipChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.repository.file.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainWindowController implements Observer<FriendshipChangeEvent> {
    FriendshipService friendshipService;
    UserService userService;
    MessageService messageService;
    Stage dialogStage;
    User user = null; //current user

    ObservableList<FriendshipDTO> model1 = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> model2 = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> model3 = FXCollections.observableArrayList();

    List<Button> buttons1;
    List<Button> buttons2;
    List<Button> buttons3;

    List<FriendshipDTO> friends;
    List<FriendshipDTO> friendRequests;
    List<FriendshipDTO> sentFriendRequests;

    @FXML
    Label labelUser;

    @FXML
    TabPane tabPane;
    @FXML
    Tab tabSentFR;

    @FXML
    ComboBox<String> usersList;

    @FXML
    TableView<FriendshipDTO> tableViewFriends;
    @FXML
    TableView<FriendshipDTO> tableViewFriendRequests;
    @FXML
    TableView<FriendshipDTO> tableViewSentFriendRequests;

    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFirstNameF;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnLastNameF;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnDateF;
    @FXML
    TableColumn<FriendshipDTO, Button> tableColumnButtonF;

    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFirstNameFR;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnLastNameFR;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnStatusFR;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnDateFR;
    @FXML
    TableColumn<FriendshipDTO, Button> tableColumnButtonAcceptFR;
    @FXML
    TableColumn<FriendshipDTO, Button> tableColumnButtonRejectFR;

    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFirstNameSFR;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnLastNameSFR;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnStatusSFR;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnDateSFR;
    @FXML
    TableColumn<FriendshipDTO, Button> tableColumnButtonDeleteSFR;

    public User setService(FriendshipService friendshipService, UserService userService, MessageService messageService, Stage dialogStage) {
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.messageService = messageService;
        this.dialogStage = dialogStage;
        friendshipService.addObserver(this);
        user = showLogin();

        dialogStage.getScene().getAccelerators().put(
                KeyCombination.keyCombination("CTRL+TAB"),
                () -> {
                    if(tabPane.getSelectionModel().getSelectedItem().equals(tabSentFR)){
                        tabPane.getSelectionModel().selectFirst();
                    } else{
                        tabPane.getSelectionModel().selectNext();
                    }
                }
        );

        if (user != null) {
            labelUser.setText("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
        } else {
            return null;
        }


        initComboBox();

        buttons1 = new ArrayList<>();
        buttons2 = new ArrayList<>();
        buttons3 = new ArrayList<>();

        initTables();

        return user;
    }

    @FXML
    public void initialize() {
        tableColumnFirstNameF.setCellValueFactory(new PropertyValueFactory<>("user2FirstName"));
        tableColumnLastNameF.setCellValueFactory(new PropertyValueFactory<>("user2LastName"));
        tableColumnDateF.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnButtonF.setCellValueFactory(new PropertyValueFactory<>("buttonReject"));
        tableViewFriends.setItems(model1);

        tableColumnFirstNameFR.setCellValueFactory(new PropertyValueFactory<>("user1FirstName"));
        tableColumnLastNameFR.setCellValueFactory(new PropertyValueFactory<>("user1LastName"));
        tableColumnStatusFR.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDateFR.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnButtonAcceptFR.setCellValueFactory(new PropertyValueFactory<>("buttonAccept"));
        tableColumnButtonRejectFR.setCellValueFactory(new PropertyValueFactory<>("buttonReject"));
        tableViewFriendRequests.setItems(model2);

        tableColumnFirstNameSFR.setCellValueFactory(new PropertyValueFactory<>("user2FirstName"));
        tableColumnLastNameSFR.setCellValueFactory(new PropertyValueFactory<>("user2LastName"));
        tableColumnStatusSFR.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDateSFR.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnButtonDeleteSFR.setCellValueFactory(new PropertyValueFactory<>("buttonReject"));
        tableViewSentFriendRequests.setItems(model3);
    }

    public void initComboBox(){
        ObservableList<String> lista = FXCollections.observableArrayList();
        userService.getAll().forEach(u -> {
            if (!u.getId().equals(user.getId()))
                lista.add(u.getFirstName() + " " + u.getLastName());
        });
        List<String> users = new ArrayList<>();
        friendshipService.getAll().forEach(f -> {
            User u = null;
            if (f.getId().getLeft().equals(user.getId()))
                u = userService.getUser(f.getId().getRight());
            else if (f.getId().getRight().equals(user.getId()))
                u = userService.getUser(f.getId().getLeft());
            if (u != null) {
                users.add(u.getFirstName() + " " + u.getLastName());
            }
        });
        lista.removeAll(users);
        usersList.setItems(lista);
    }

    public void initFriends() {
        if (user != null) {
            friends = getFriendsList(user.getId());
            model1.setAll(friends);
        }
    }

    private void updateFriendRequests(FriendshipDTO f, Status s) {
        friendRequests.forEach(fr -> {
            if (f.getUser1ID().equals(fr.getUser1ID()))
                fr.setStatus(s.toString());
        });
        model2.setAll(friendRequests);
    }

    private void updateSentFriendRequests(FriendshipDTO f) {
        sentFriendRequests.forEach(fr -> {
            if (fr.getUser2ID().equals(f.getUser2ID())) {
                fr.setStatus(Status.DELETED.toString());
            }
        });
        model3.setAll(sentFriendRequests);
    }

    public void initFriendRequests() {
        if (user != null) {
            friendRequests = getFriendRequestsList(user.getId());
            model2.setAll(friendRequests);
        }
    }

    public void initSentFriendRequests() {
        if (user != null) {
            sentFriendRequests = getSentFriendRequestsList(user.getId());
            model3.setAll(sentFriendRequests);
        }
    }

    public void initTables() {
        if (tabSentFR != null) {
            tabSentFR.setStyle("-fx-border-radius: 0px;");
        }
        initFriends();
        initFriendRequests();
        initSentFriendRequests();
    }


    private List<FriendshipDTO> getFriendsList(Long id) {
        Iterable<Friendship> friendships = friendshipService.getFriends(id.toString());
        return StreamSupport.stream(friendships.spliterator(), false)
                .map(f -> {
                    User u1 = userService.getUser(id);
                    User u2;
                    if (f.getId().getLeft().equals(id))
                        u2 = userService.getUser(f.getId().getRight());
                    else
                        u2 = userService.getUser(f.getId().getLeft());
                    //we create the FriendshipDTO
                    Button button = new Button("Delete friend");
                    button.setId("button" + u2.getId());
                    buttons1.add(button);
                    button.setOnAction(this::handleDeleteFriend);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String formattedDate = f.getDate().format(formatter);
                    FriendshipDTO friendship = new FriendshipDTO(
                            u1.getFirstName(), u1.getLastName(),
                            u2.getFirstName(), u2.getLastName(),
                            formattedDate, f.getStatus().toString(),
                            null, button);
                    friendship.setUser1ID(id);
                    friendship.setUser2ID(u2.getId());
                    return friendship;
                })
                .collect(Collectors.toList());
    }


    private List<FriendshipDTO> getFriendRequestsList(Long id) {
        Iterable<Friendship> friendRequests;
        friendRequests = friendshipService.getFriendRequests(id.toString());
        return StreamSupport.stream(friendRequests.spliterator(), false)
                .map(f -> {
                    User u1 = userService.getUser(f.getId().getLeft());
                    User u2 = userService.getUser(f.getId().getRight());
                    Button buttonAccept = new Button("Accept");
                    buttonAccept.setId("buttonAccept" + u1.getId());
                    buttonAccept.setOnAction(this::handleFriendRequest);
                    buttons2.add(buttonAccept);
                    Button buttonReject = new Button("Reject");
                    buttonReject.setId("buttonReject" + u1.getId());
                    buttonReject.setOnAction(this::handleFriendRequest);
                    buttons2.add(buttonReject);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    FriendshipDTO fr = new FriendshipDTO(
                            u1.getFirstName(), u1.getLastName(),
                            u2.getFirstName(), u2.getLastName(),
                            f.getDate().format(formatter), f.getStatus().toString(),
                            buttonAccept, buttonReject);
                    fr.setUser1ID(u1.getId());
                    fr.setUser2ID(u2.getId());
                    return fr;
                })
                .collect(Collectors.toList());
    }

    private List<FriendshipDTO> getSentFriendRequestsList(Long id) {
        Iterable<Friendship> friendRequests;
        friendRequests = friendshipService.getSentFriendRequests(id.toString());
        return StreamSupport.stream(friendRequests.spliterator(), false)
                .map(f -> {
                    User u1 = userService.getUser(f.getId().getLeft());
                    User u2 = userService.getUser(f.getId().getRight());
                    Button buttonDelete = new Button("Delete");
                    buttonDelete.setId("buttonDelete" + u2.getId());
                    buttonDelete.setOnAction(this::handleDeleteFriend);
                    buttons3.add(buttonDelete);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    FriendshipDTO fr = new FriendshipDTO(
                            u1.getFirstName(), u1.getLastName(),
                            u2.getFirstName(), u2.getLastName(),
                            f.getDate().format(formatter), f.getStatus().toString(),
                            null, buttonDelete);
                    fr.setUser1ID(u1.getId());
                    fr.setUser2ID(u2.getId());
                    return fr;
                })
                .collect(Collectors.toList());
    }

    @FXML
    public void handleAddFriend() {
        String value = usersList.getValue();
        try {
            String[] name = value.split(" ");
            User newUser = userService.getUser(name[0], name[1]);
            Friendship friendship = friendshipService.addFriendship(user.getId(), newUser.getId());
            if (friendship == null) { //if the friend request was send succesfully
                initSentFriendRequests();
                initComboBox();
                tabSentFR.setStyle("-fx-border-radius: 1px; -fx-border-color: #FFE7D6;");
            }
        } catch (NullPointerException ex) {
            usersList.setPromptText("Choose an user!");
        }
    }

    @FXML
    public void handleDeleteFriend(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        TableCell tableCell = (TableCell) button.getParent();
        FriendshipDTO fr = (FriendshipDTO) tableCell.getTableView().getItems().get(tableCell.getIndex());
        Friendship deleted = friendshipService.removeFriendship(fr.getUser1ID(), fr.getUser2ID());
        if (deleted != null) {
            buttons1.remove(button);
            initFriends();
            initComboBox();
            updateSentFriendRequests(fr);
        }
    }

    public void handleFriendRequest(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        TableCell tableCell = (TableCell) button.getParent();
        FriendshipDTO fr = (FriendshipDTO) tableCell.getTableView().getItems().get(tableCell.getIndex());
        String btnID = button.getId();
        Status status;
        if (btnID.contains("Accept")) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Friendship f = new Friendship(LocalDateTime.parse(fr.getDate(), formatter), status);
        f.setId(new Tuple<>(fr.getUser1ID(), user.getId()));
        List<Friendship> friendRequest = Collections.singletonList(f);
        friendshipService.updateFriendships(friendRequest);
        List<Button> remove = new ArrayList<>();
        buttons2.forEach(b -> {
            if (b.getId().charAt(b.getId().length() - 1) == btnID.charAt(btnID.length() - 1)) {
                remove.add(b);
            }
        });
        buttons2.removeAll(remove);
        updateFriendRequests(fr, status);
        initFriends();
        initComboBox();
    }

    public void showInbox() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/inboxView.fxml"));
            AnchorPane root = loader.load();

            //Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Inbox");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            InboxController inboxController = loader.getController();
            inboxController.setService(messageService, userService, user);
            dialogStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/welcomeView.fxml"));
            AnchorPane root = loader.load();

            //Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Welcome!");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            WelcomeController welcomeController = loader.getController();
            welcomeController.setService(userService, dialogStage);
            dialogStage.showAndWait();
            user = welcomeController.getUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @FXML
    public void handleLogout() {
        dialogStage.hide();
        user = showLogin();
        labelUser.setText("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
        initTables();
        initComboBox();
        dialogStage.show();
    }

    @FXML
    public void handleReport1(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/report1View.fxml"));
            AnchorPane root = loader.load();

            //Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Report 1");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            Report1Controller report1Controller = loader.getController();
            report1Controller.setService(userService, friendshipService, messageService, user);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleReport2(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/report2View.fxml"));
            AnchorPane root = loader.load();

            //Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Report 2");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            Report2Controller report2Controller = loader.getController();
            report2Controller.setService(userService, messageService, user);
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initTables();
    }
}
