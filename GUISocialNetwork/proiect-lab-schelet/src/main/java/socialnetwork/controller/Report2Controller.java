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
import socialnetwork.domain.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Report2Controller {
    UserService userService;
    MessageService messageService;

        ObservableList<MessageDTO> model = FXCollections.observableArrayList();

    User user;
    User user2;

    List<FriendshipDTO> friends;
    List<MessageDTO> messages;

    LocalDate date1;
    LocalDate date2;

    @FXML
    ComboBox<String> comboBoxUser;

    @FXML
    DatePicker dateFrom;
    @FXML
    DatePicker dateTo;

    @FXML
    Label labelErrors;
    @FXML
    Label labelSave;

    @FXML
    TableView<MessageDTO> tableViewMessages;
    @FXML
    TableColumn<MessageDTO, String> tableColumnFrom;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessage;
    @FXML
    TableColumn<MessageDTO, String> tableColumnDate;

    public void setService(UserService userService, MessageService messageService, User user) {
        this.userService = userService;
        this.messageService = messageService;
        this.user = user;

        dateFrom.setValue(LocalDate.now());
        dateTo.setValue(LocalDate.now());

        ObservableList<String> lista = FXCollections.observableArrayList();
        userService.getAll().forEach(u -> {
            if (!u.getId().equals(user.getId()))
                lista.add(u.getFirstName() + " " + u.getLastName());
        });
        comboBoxUser.setItems(lista);
    }

    public void initialize() {
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("from"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewMessages.setItems(model);
    }

    public void initTableMessages() {
        model.setAll(messages);
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
        try{
            labelSave.setText("");
            labelErrors.setText("");

            String [] s = comboBoxUser.getValue().split(" ");
            user2 = userService.getUser(s[0], s[1]);

            date1 = dateFrom.getValue();
            date2 = dateTo.getValue();

            messages = messageService.getUserMessages(user.getId().toString()).stream()
                    .filter(m -> m.getFrom().equals(user2.getId()) && date1.compareTo(ChronoLocalDate.from(m.getDate())) <= 0 && date2.compareTo(ChronoLocalDate.from(m.getDate())) >= 0)
                    .map(this::createMessageDTO)
                    .collect(Collectors.toList());

            initTableMessages();

        } catch (NullPointerException ex){
            labelErrors.setText("You have to choose an user!");
        }

    }

    public void handleSaveToPDF() throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("./PDFs/Report2.pdf"));
        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 13, BaseColor.BLACK);

        Paragraph p = new Paragraph("REPORT", font);
        p.setAlignment(Element.ALIGN_CENTER);

        document.add(p);

        String s = "User: " + user.getFirstName() + " " + user.getLastName();
        document.add(new Paragraph(s, font));

        s = "Messages from user: " + user2.getFirstName() + " " + user2.getLastName();
        document.add(new Chunk(s, font));

        s = "\nFrom: " + date1.toString() + "\nTo: " + date2.toString();
        document.add(new Chunk(s, font));

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
