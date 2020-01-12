package org.sysRestaurante.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.model.Authentication;
import org.sysRestaurante.util.DateFormatter;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class AppController implements DateFormatter {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(AppController.class.getName());
    private static long timerInMillies;
    private Label sessionTimer;
    private Authentication certs = new Authentication();

    @FXML
    private BorderPane borderPaneHolder;

    public void initialize() throws IOException {
        startChronometer();
        AppFactory.setAppController(this);
        borderPaneHolder.leftProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.MENU_TOOL_BAR)));
        borderPaneHolder.centerProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.DASHBOARD)));
        SceneNavigator.loadScene(borderPaneHolder);
        borderPaneHolder.setAlignment(borderPaneHolder.getCenter(), Pos.CENTER);
        Stage stage = (Stage) borderPaneHolder.getScene().getWindow();
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.centerOnScreen();
    }

    public HBox getFooter() {
        String lastSessionDate = DATE_FORMAT.format(certs.getLastSessionDate());
        Label timeStatusLabel = new Label("Logado em: " + lastSessionDate);
        timeStatusLabel.setStyle("-fx-font: Carlito 14");
        timeStatusLabel.setOpacity(0.6);
        Label copyleftLabel = new Label("(C) 2020 Saulo Felix GNU SysRestaurante");
        copyleftLabel.setStyle("-fx-font: Carlito 14");
        copyleftLabel.setOpacity(0.6);
        Pane growPane = new Pane();
        HBox footer = new HBox();
        footer.setSpacing(3);
        footer.setPadding(new Insets(1, 3, 1, 3));
        footer.setStyle("-fx-border-color: #CBCBCC");
        footer.setAlignment(Pos.CENTER);
        footer.setHgrow(growPane, Priority.ALWAYS);
        footer.getChildren().addAll(timeStatusLabel,
                new Separator(Orientation.VERTICAL),
                sessionTimer,
                new Separator(Orientation.VERTICAL));
        footer.getChildren().addAll(growPane, copyleftLabel);
        return footer;
    }

    public HBox getHeader() {
        Label titleLable = new Label("Bar & Restaurante Frutos do Mar");
        titleLable.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 30));
        HBox header = new HBox();
        header.setPadding(new Insets(1, 1, 1, 1));
        header.getChildren().add(titleLable);
        return header;
    }

    private void startChronometer() {
        LocalDateTime initialTime = LocalDateTime.now();
        timerInMillies = 0L;
        sessionTimer = new Label();
        Timeline chronometer = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long elapsedTimeInSeconds = ChronoUnit.SECONDS.between(initialTime, LocalDateTime.now());
            String elapsedTime = LocalTime.ofSecondOfDay(elapsedTimeInSeconds).toString();
            sessionTimer.setText("" + elapsedTime);
            sessionTimer.setStyle("-fx-font: Carlito 14");
            sessionTimer.setPadding(new Insets(0, 5, 0, 5));
            sessionTimer.setOpacity(0.6);
            timerInMillies += 1L;
        }), new KeyFrame(Duration.millis(1000)));

        chronometer.setCycleCount(Animation.INDEFINITE);
        chronometer.play();
        LOGGER.info("Chronometer initialized normally");
    }

    public long getElapsedSessionTime() {
        return timerInMillies;
    }

    public void loadPage(MouseEvent e, String fxml) {
        try {
            borderPaneHolder.centerProperty()
                    .setValue(FXMLLoader.load(AppController.class.getResource(fxml)));
            e.consume();
        } catch (IOException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Couldn't load " + fxml + " page.");
            ex.printStackTrace();
        }
    }

    public static void showDialog(String fxml) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            AppFactory.getMainController().darkenScreen();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(fxml));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
            AppFactory.getMainController().brightenScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}