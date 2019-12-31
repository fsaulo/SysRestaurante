package sysRestaurante.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;
import sysRestaurante.model.Authentication;
import sysRestaurante.util.ExceptionHandler;
import sysRestaurante.util.LoggerHandler;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(LoginController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private static final String SIGNATURE_IMAGE = "resources/images/a1c7cfbbf306ef586600fcf2da1d5acd.png";
    private static final String LOGINTEXT_IMAGE = "resources/images/login-text.png";

    private Authentication certs;

    @FXML
    private Label dbStatusLabel;
    @FXML
    private Label statusAccessLabel;
    @FXML
    private Label lastSessionLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private ToggleSwitch isAdminToggle;
    @FXML
    private ImageView signatureImage;
    @FXML
    private ImageView loginTextImage;

    public void initialize() {
         certs = new Authentication();

        if (certs.isDatabaseConnected()) {
            dbStatusLabel.setTextFill(Color.web("Green"));
            dbStatusLabel.setText("Conectado");

        } else {
            dbStatusLabel.setTextFill(Color.web("Red"));
            dbStatusLabel.setText("Desconectado");
        }

        loginPane.setMinHeight(250);
        loginPane.setMinWidth(440);

        statusAccessLabel.setText("");

        startClock();

        try {
            setSignatureImage();
            setLoginTextImage();
        } catch (FileNotFoundException e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Invalid path to image files");
            e.printStackTrace();
        }

        setLastSessionMessage();
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Login pane initialized.");
    }

    public void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Date clockDate = new Date();
            String clockDateFormatted = CLOCK_FORMAT.format(clockDate);
            clockLabel.setText(clockDateFormatted);
        }), new KeyFrame(Duration.millis(1000)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public void setSignatureImage() throws FileNotFoundException {
        signatureImage.setImage(new Image(new FileInputStream(SIGNATURE_IMAGE)));
    }

    public void setLoginTextImage() throws  FileNotFoundException {
        loginTextImage.setImage(new Image(new FileInputStream(LOGINTEXT_IMAGE)));
    }

    public void setLastSessionMessage() {
        String sessionMessage;
        if (certs.getLastSessionDate() == null) {
            sessionMessage = "Não há registro de sessões";
        } else {
            String lastSessionDate = DATE_FORMAT.format(certs.getLastSessionDate());
            sessionMessage = "Última sessão em: " + lastSessionDate;
        }
        lastSessionLabel.setText(sessionMessage);
    }

    public void onAuthenticationAccepted() {
        MainGUI.getMainController().setMainPanePadding(0, 0, 0, 0);
        SceneNavigator.loadScene(SceneNavigator.MENU);
    }

    public void loginRequested() throws SQLException {
        int typeAuthentication = certs.systemAuthentication(usernameField.getText(), passwordField.getText(),
                isAdminToggle.isSelected());

        LOGGER.config("Type of authentication: " + typeAuthentication);

        switch (typeAuthentication) {
            case 0:
                statusAccessLabel.setTextFill(Color.web("Green"));
                statusAccessLabel.setText("Acesso garantido");
                setLastSessionMessage();
                onAuthenticationAccepted();
                break;
            case 1:
                statusAccessLabel.setTextFill(Color.web("Green"));
                statusAccessLabel.setText("Acesso garantido");
                setLastSessionMessage();
                if (!isAdminToggle.isSelected())
                    isAdminToggle.fire();
                onAuthenticationAccepted();
                break;
            case 2:
                statusAccessLabel.setTextFill(Color.web("Red"));
                statusAccessLabel.setText("Acesso não autorizado: usuário ou senha incorretos.");
                break;
            case 3:
                statusAccessLabel.setTextFill(Color.web("Red"));
                statusAccessLabel.setText("Acesso não autorizado: usuário não é administrador.");
                break;
            default:
                statusAccessLabel.setTextFill(Color.web("Red"));
                statusAccessLabel.setText("Acesso negado");
        }
    }
}
