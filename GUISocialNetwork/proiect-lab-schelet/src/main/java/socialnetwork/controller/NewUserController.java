package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.file.RepositoryException;
import socialnetwork.service.UserService;

import java.util.Arrays;

public class NewUserController {
    UserService service;
    Stage dialogStage;
    User user;

    @FXML
    TextField textFirstName;
    @FXML
    TextField textLastName;
    @FXML
    TextField textUsername;
    @FXML
    PasswordField textPassword;
    @FXML
    Label labelErrors;

    public void setService(UserService service, Stage stage) {
        this.service = service;
        dialogStage = stage;
    }

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleNewUser() {
        try {
            String firstName = textFirstName.getText();
            String lastName = textLastName.getText();
            String username = textUsername.getText();
            String password = textPassword.getText();
            service.addUser(Arrays.asList(firstName, lastName, username, password));
            user = service.getUser(firstName, lastName);
            dialogStage.close();
        } catch (ValidationException | RepositoryException ex) {
            labelErrors.setPrefHeight(20 * ex.getMessage().split("\n").length);
            labelErrors.setText(ex.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }

    public User getUser() {
        return user;
    }
}
