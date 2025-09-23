import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginScreen extends Application {
    @Override
    public void start(Stage stage) {
        // --- Heading ---
        Label lbl = new Label("Student Management Login");
        lbl.getStyleClass().add("heading");

        // --- Input fields ---
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.getStyleClass().add("input-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("input-field");

        Label msg = new Label();
        msg.getStyleClass().add("error-label");

        // --- Buttons ---
        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("primary-btn");

        loginBtn.setOnAction(e -> {
            String u = userField.getText(), p = passField.getText();
            if (AuthService.authenticate(u, p)) {
                new DashboardScreen().start(stage);
            } else {
                msg.setText("Invalid credentials");
            }
        });

        userField.textProperty().addListener((obs, oldV, newV) -> msg.setText(""));
        passField.textProperty().addListener((obs, oldV, newV) -> msg.setText(""));

        // --- Card Layout ---
        VBox form = new VBox(15, lbl, userField, passField, loginBtn, msg);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.getStyleClass().add("login-card");

        StackPane root = new StackPane(form);
        root.setPadding(new Insets(50));
        root.getStyleClass().add("background");

        Scene scene = new Scene(root, 450, 350);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
