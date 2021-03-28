package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import socialnetwork.domain.Message;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.events.FriendshipChangeEvent;
import socialnetwork.observer.Observer;
import socialnetwork.repository.file.RepositoryException;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InboxController implements Observer<FriendshipChangeEvent> {
    MessageService messageService;
    UserService userService;
    User user = null; //current user

    ObservableList<MessageDTO> model1 = FXCollections.observableArrayList();
    ObservableList<MessageDTO> model2 = FXCollections.observableArrayList();

    List<MessageDTO> inbox;
    List<MessageDTO> sentMessages;

    @FXML
    TextField textTo;
    @FXML
    TextField textFrom;
    @FXML
    TextArea textMessage;
    @FXML
    Label msgID;
    @FXML
    Label reply;
    @FXML
    Label labelErrors;
    @FXML
    Label sent;

    @FXML
    TableView<MessageDTO> tableViewInbox;
    @FXML
    TableView<MessageDTO> tableViewSentMessages;

    @FXML
    TableColumn<MessageDTO, String> tableColumnFrom;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessage;
    @FXML
    TableColumn<MessageDTO, String> tableColumnDate;
    @FXML
    TableColumn<MessageDTO, Button> tableColumnReply;

    @FXML
    TableColumn<MessageDTO, String> tableColumnToS;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessageS;
    @FXML
    TableColumn<MessageDTO, String> tableColumnDateS;

    public void setService(MessageService service, UserService userService, User user) {
        this.messageService = service;
        this.userService = userService;
        this.user = user;

        textFrom.setText(user.getFirstName()+" "+user.getLastName());
        reply.setText("-1");

        initInbox();
        initSentMessages();
    }

    @FXML
    public void initialize() {
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnReply.setCellValueFactory(new PropertyValueFactory<>("btnReply"));
        tableViewInbox.setItems(model1);
        tableViewInbox.setOnMouseClicked(this::loadMessage);

        tableColumnToS.setCellValueFactory(new PropertyValueFactory<>("to"));
        tableColumnMessageS.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnDateS.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewSentMessages.setItems(model2);
        tableViewSentMessages.setOnMouseClicked(this::loadMessage);
    }

    public void initInbox() {
        inbox = getInbox();
        model1.setAll(inbox);
    }

    public void initSentMessages() {
        sentMessages = getSentMessages();
        model2.setAll(sentMessages);
    }

    public MessageDTO createMessageDTO(Message message){
        User u1 = userService.getUser(message.getFrom());
        String[] to = new String[1];
        to[0] = "";
        message.getTo().forEach(t -> {
            User u = userService.getUser(t);
            to[0] += u.getFirstName() + " " + u.getLastName() + ", ";
        });
        to[0] = to[0].substring(0,to[0].length()-2);
        Button btnReply = new Button("Reply");
        btnReply.setOnAction(this::replyMessage);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new MessageDTO(message.getId(),
                u1.getId(),
                u1.getFirstName() + " " + u1.getLastName(),
                message.getTo(),
                to[0],
                message.getMessage(),
                message.getDate().format(formatter),
                btnReply, message.getReply());
    }

    public List<MessageDTO> getInbox() {
        return messageService.getUserMessages(user.getId().toString()).stream()
                .map(this::createMessageDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getSentMessages() {
        return messageService.getUserSentMessages(user.getId().toString()).stream()
                .map(this::createMessageDTO)
                .collect(Collectors.toList());
    }

    private void loadMessage(MouseEvent mouseEvent) {
        TableView tableView = (TableView) mouseEvent.getSource();
        MessageDTO msg = (MessageDTO) tableView.getSelectionModel().getSelectedItem();
        textFrom.setText(msg.getFrom());
        textTo.setText(msg.getTo());
        textMessage.setText(msg.getMessage());
        msgID.setText(msg.getId().toString());
        sent.setText("");
        labelErrors.setText("");
    }

    public void clearFields(){
        textFrom.setText(user.getFirstName()+" "+user.getLastName());
        textTo.clear();
        textMessage.clear();
        msgID.setText("");
        reply.setText("-1");
        sent.setText("");
        labelErrors.setText("");
    }

    public void replyMessage(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();
        TableCell tableCell = (TableCell) button.getParent();
        MessageDTO msg = (MessageDTO) tableCell.getTableView().getItems().get(tableCell.getIndex());
        textFrom.setText(msg.getTo());
        textTo.setText(msg.getFrom());
        textMessage.clear();
        reply.setText(msg.getId().toString());
        sent.setText("");
        labelErrors.setText("");
    }

    public void sendMessage(){
        String to = textTo.getText();
        String message = textMessage.getText();
        try {
            String[] split = to.split(", ");
            String ids = "";
            for (String s : split){
                List<String> name = Arrays.asList(s.split(" "));
                Long id = userService.getUser(name.get(0),name.get(1)).getId();
                ids = ids.concat(id.toString()+" ");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<String> data = Arrays.asList(String.valueOf(messageService.getSize()+1)
                    ,user.getId().toString(),ids,message, formatter.format(LocalDateTime.now()),reply.getText());
            messageService.addMessage(data);
            initSentMessages();
            textTo.clear();
            textMessage.clear();
            sent.setText("Message sent!");
        }catch (ValidationException | RepositoryException ex){
            labelErrors.setText(ex.getMessage());
        }
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
    }
}