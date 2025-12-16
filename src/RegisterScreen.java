import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RegisterScreen {

    public void start(Stage stage) {

        Label lbl = new Label("User Registration");
        lbl.getStyleClass().add("heading");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.getStyleClass().add("input-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.getStyleClass().add("input-field");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.getStyleClass().add("input-field");

        Label msg = new Label();
        msg.getStyleClass().add("error-label");

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("primary-btn");

        Hyperlink backLink = new Hyperlink("← Back to Login");

        registerBtn.setOnAction(e -> {
            boolean ok = UserService.register(
                    userField.getText(),
                    passField.getText(),
                    nameField.getText(),
                    "user"
            );

            if (ok) {
                msg.setText("Registration successful!");
                msg.setStyle("-fx-text-fill: green;");
            } else {
                msg.setText("Registration failed");
            }
        });

        backLink.setOnAction(e -> new LoginScreen().start(stage));

        VBox form = new VBox(15,
                lbl,
                userField,
                passField,
                nameField,
                registerBtn,
                backLink,
                msg
        );
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.getStyleClass().add("login-card");

        StackPane root = new StackPane(form);
        root.getStyleClass().add("background");

        Scene scene = new Scene(root, 450, 420);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }
}

