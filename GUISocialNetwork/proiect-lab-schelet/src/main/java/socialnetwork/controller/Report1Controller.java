package socialnetwork.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import socialnetwork.domain.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Report1Controller {
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;

    ObservableList<FriendshipDTO> model1 = FXCollections.observableArrayList();
    ObservableList<MessageDTO> model2 = FXCollections.observableArrayList();

    User user;

    List<FriendshipDTO> friends;
    List<MessageDTO> messages;

    LocalDate date1;
    LocalDate date2;

    @FXML
    DatePicker dateFrom;
    @FXML
    DatePicker dateTo;

    @FXML
    Label labelSave;

    @FXML
    TableView<FriendshipDTO> tableViewFriends;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnLastName;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnDate;

    @FXML
    TableView<MessageDTO> tableViewMessages;
    @FXML
    TableColumn<MessageDTO, String> tableColumnFrom;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessage;
    @FXML
    TableColumn<MessageDTO, String> tableColumnDate2;

    public void setService(UserService userService, FriendshipService friendshipService, MessageService messageService, User user) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.user = user;

        dateFrom.setValue(LocalDate.now());
        dateTo.setValue(LocalDate.now());
    }

    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("user2FirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("user2LastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewFriends.setItems(model1);

        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnDate2.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewMessages.setItems(model2);
    }

    public void initTableFriends() {
        model1.setAll(friends);
    }

    public void initTableMessages() {
        model2.setAll(messages);
    }

    public FriendshipDTO createFriendshipDTO(Friendship friendship) {
        User user2;
        if (friendship.getId().getLeft().equals(user.getId()))
            user2 = userService.getUser(friendship.getId().getRight());
        else
            user2 = userService.getUser(friendship.getId().getLeft());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new FriendshipDTO(user.getFirstName(), user.getLastName(),
                user2.getFirstName(), user2.getLastName(),
                friendship.getDate().format(formatter), friendship.getStatus().toString(),
                null, null);
    }

    public MessageDTO createMessageDTO(Message message) {
        User u1 = userService.getUser(message.getFrom());
        String[] to = new String[1];
        to[0] = "";
        message.getTo().forEach(t -> {
            User u = userService.getUser(t);
            to[0] += u.getFirstName() + " " + u.getLastName() + ", ";
        });
        to[0] = to[0].substring(0, to[0].length() - 2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new MessageDTO(message.getId(),
                u1.getId(),
                u1.getFirstName() + " " + u1.getLastName(),
                message.getTo(),
                to[0],
                message.getMessage(),
                message.getDate().format(formatter),
                null, message.getReply());
    }

    public void handleLoad() {
        date1 = dateFrom.getValue();
        date2 = dateTo.getValue();
        friends = friendshipService.getFriends(user.getId().toString()).stream()
                .filter(f -> date1.compareTo(ChronoLocalDate.from(f.getDate())) <= 0 && date2.compareTo(ChronoLocalDate.from(f.getDate())) >= 0)
                .map(this::createFriendshipDTO)
                .collect(Collectors.toList());
        messages = messageService.getUserMessages(user.getId().toString()).stream()
                .filter(m -> date1.compareTo(ChronoLocalDate.from(m.getDate())) <= 0 && date2.compareTo(ChronoLocalDate.from(m.getDate())) >= 0)
                .map(this::createMessageDTO)
                .collect(Collectors.toList());
        initTableFriends();
        initTableMessages();
    }

    public void handleSaveToPDF() throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("./PDFs/Report1.pdf"));
        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 13, BaseColor.BLACK);

        Paragraph p = new Paragraph("REPORT", font);
        p.setAlignment(Element.ALIGN_CENTER);

        document.add(p);

        String s = "User: " + user.getFirstName() + " " + user.getLastName();
        document.add(new Paragraph(s, font));

        s = "From: " + date1.toString() + "\nTo: " + date2.toString();
        document.add(new Chunk(s, font));

        s = "\nFriends:\n";
        document.add(new Chunk(s, font));

        PdfPTable tableFriends = new PdfPTable(3);
        Stream.of("First Name", "Last Name", "Date")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPhrase(new Phrase(columnTitle));
                    tableFriends.addCell(header);
                });

        friends.forEach(f -> {
            tableFriends.addCell(f.getUser2FirstName());
            tableFriends.addCell(f.getUser2LastName());
            tableFriends.addCell(f.getDate());
        });

        document.add(tableFriends);

        s = "\nMessages:\n";
        document.add(new Chunk(s, font));

        PdfPTable tableMessages = new PdfPTable(3);
        Stream.of("From", "Message", "Date")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPhrase(new Phrase(columnTitle));
                    tableMessages.addCell(header);
                });

        messages.forEach(m -> {
            tableMessages.addCell(m.getFrom());
            tableMessages.addCell(m.getMessage());
            tableMessages.addCell(m.getDate());
        });

        document.add(tableMessages);

        document.close();

        labelSave.setText("Report saved successfully!");
    }
}
