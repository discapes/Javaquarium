package com.discape.javaquarium.gui.toolbar;

import com.airhacks.afterburner.injection.Injector;
import com.discape.javaquarium.Utils;
import com.discape.javaquarium.gui.AppStarter;
import com.discape.javaquarium.gui.LoginView;
import com.discape.javaquarium.gui.Themes;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;

public class Intro {

    @Inject private Themes themes;

    public void show(Stage stage) {
        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        Button guestBtn = new Button("Guest");
        VBox vBox = new VBox(loginBtn, registerBtn, guestBtn);
        loginBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            Injector.injectMembers(loginView.getClass(), loginView);
            Parent root = loginView.getView();
            Utils.setAnchors(root, 50);
            Scene scene = new Scene(new AnchorPane(root));
            stage.setTitle("Login");
            stage.setScene(scene);
            themes.setTheme(scene);
        });
        registerBtn.setOnAction(e -> {
            System.out.println("register");
            //TODO
        });
        guestBtn.setOnAction(e -> {
            AppStarter appStarter = new AppStarter();
            Injector.injectMembers(appStarter.getClass(), appStarter);
            appStarter.start(stage);
        });
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        Utils.setAnchors(vBox, 50);
        Scene scene = new Scene(new AnchorPane(vBox));
        stage.setTitle("Javaquarium");
        stage.setScene(scene);
        themes.setTheme(scene);
        stage.show();
    }
}
