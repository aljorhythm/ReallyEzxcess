package ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ezxcess.ReallyEzxcess;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.CredentialsSupplier;

public class ReallyEzxcessApplication extends Application {

    private static class MainMessageBox implements MessagerToUser {

        private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:m:s");
        private TextArea textArea;

        public MainMessageBox(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void sendMessage(String str) {
            javafx.application.Platform.runLater(() -> {
                if (str == null || str.equals("")) {
                    textArea.appendText("\n");
                } else {
                    textArea.setText(textArea.getText() + dateFormat.format(new Date()) + " " + str + "\n");
                }
                textArea.setScrollTop(Double.MAX_VALUE);
            });
        }

    }

    private static class JavaFXCredentialsSupplier implements CredentialsSupplier {

        private TextField userTextField;
        private PasswordField pwBox;

        public JavaFXCredentialsSupplier(TextField userTextField2, PasswordField pwBox) {
            this.userTextField = userTextField2;
            this.pwBox = pwBox;
        }

        @Override
        public String getUsername() {
            return userTextField.getText();
        }

        @Override
        public String getPassword() {
            return pwBox.getText();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ReallyEzxcess");
        Text scenetitle = new Text("Stay signed in to NUSNET through ezxcess!");
        GridPane.setHalignment(scenetitle, HPos.CENTER);
        scenetitle.setTextAlignment(TextAlignment.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        GridPane credentialsGrid = new GridPane();
        credentialsGrid.setAlignment(Pos.TOP_CENTER);
        credentialsGrid.setHgap(10);
        credentialsGrid.setVgap(10);

        Scene scene = new Scene(grid, 700, 700);
        primaryStage.setScene(scene);

        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Username:");
        credentialsGrid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        credentialsGrid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        credentialsGrid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        credentialsGrid.add(pwBox, 1, 2);

        grid.add(credentialsGrid, 0, 2);

        TextArea textArea = new TextArea();
        textArea.autosize();
        textArea.setPrefRowCount(50);
        textArea.setMinWidth(400);
        textArea.setEditable(false);

        Font font = Font.font("Courier", 12);
        textArea.setFont(font);

        grid.add(textArea, 0, 3);

        Button btn = new Button();
        btn.setText("Logout of ezxcess");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(ReallyEzxcess.URLS.LOG_OUT_FORM));
                    } catch (IOException | URISyntaxException e1) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("ReallyEzxcess");
                        alert.setHeaderText("ERROR");
                        alert.setContentText("Cannot open URL " + ReallyEzxcess.URLS.LOG_OUT_FORM);
                        alert.showAndWait();
                    }
                }
            }
        });
        grid.add(btn, 0, 4);
        GridPane.setHalignment(btn, HPos.CENTER);

        primaryStage.show();

        mainFlow(new MainMessageBox(textArea), new JavaFXCredentialsSupplier(userTextField, pwBox));
    }

    private void main(Main mainflow, MessagerToUser messager, CredentialsSupplier credentials) {
        mainflow.ensureConnectedToNUS(credentials.getUsername(), credentials.getPassword());
        messager.sendMessage("");
    }

    public void mainFlow(MessagerToUser messager, CredentialsSupplier credentials) {
        Main main = new Main(messager);
        main(main, messager, credentials);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                main(main, messager, credentials);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
