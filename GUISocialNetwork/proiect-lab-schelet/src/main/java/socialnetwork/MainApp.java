package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.MainWindowController;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.file.FriendshipFile;
import socialnetwork.repository.file.MessageFile;
import socialnetwork.repository.file.RepoAll;
import socialnetwork.repository.file.UserFile;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    RepoAll repo;

    @Override
    public void start(Stage primaryStage) throws IOException {
        String fileName1 = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileName2 = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendships");
        String fileName3 = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.messages");

        UserFile userFileRepository = new UserFile(fileName1);
        FriendshipFile friendshipFileRepository = new FriendshipFile(fileName2);
        MessageFile messageFileRepository = new MessageFile(fileName3);
        repo = new RepoAll(userFileRepository, friendshipFileRepository, messageFileRepository);

        User user = initView(primaryStage);
        if (user != null) {
            primaryStage.show();
        } else {
            primaryStage.close();
        }
    }

    private User initView(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/mainWindowView.fxml"));
        AnchorPane root = loader.load();
        primaryStage.setScene(new Scene(root));

        MainWindowController friendshipController = loader.getController();

        UserService userService = new UserService(repo, new UserValidator());
        FriendshipService friendshipService = new FriendshipService(repo, new FriendshipValidator());
        MessageService messageService = new MessageService(repo, new MessageValidator());
        return friendshipController.setService(friendshipService, userService, messageService, primaryStage);
    }
}
