package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;


public class WelcomeController {
    UserService userService;
    User user;
    Stage dialogStage;

    public void setService(UserService userService, Stage dialogStage){
        this.userService = userService;
        this.dialogStage = dialogStage;
    }

    public User getUser(){
        return user;
    }

    @FXML
    public void initialize() {
    }

    @FXML
    public void handleLogin(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/loginView.fxml"));
            AnchorPane root = loader.load();

            //Stage
            Stage stage = new Stage();
            stage.setTitle("Login!");
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);

            LoginController loginController = loader.getController();
            loginController.setService(userService, stage);
            stage.showAndWait();
            user = loginController.getUser();
            if(user != null)
                dialogStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNewUser(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/newUserView.fxml"));
            AnchorPane root = loader.load();

            //Stage
            Stage stage = new Stage();
            stage.setTitle("Create account");
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);

            NewUserController newUserController = loader.getController();
            newUserController.setService(userService, stage);
            stage.showAndWait();
            user = newUserController.getUser();
            if(user != null)
                dialogStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
