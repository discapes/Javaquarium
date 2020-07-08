package com.discape.javaquarium.frontend.views;

import com.discape.javaquarium.Utils;
import com.discape.javaquarium.backend.AccountManager;
import com.discape.javaquarium.frontend.persistent.IMainView;
import com.discape.javaquarium.frontend.persistent.IViewSetter;
import com.discape.javaquarium.frontend.views.app.AppView;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.inject.Inject;

public class StartView implements IMainView {

    @Inject private IViewSetter viewSetter;
    @Inject private AccountManager accountManager;

    private final Parent root;

    public StartView() {
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> viewSetter.applyView(LoginView.class));

        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> viewSetter.applyView(RegisterView.class));

        Button guestBtn = new Button("Guest");
        guestBtn.setOnAction(e -> {
            accountManager.loginAsGuest();
            System.out.println("logged");
            viewSetter.applyView(AppView.class);
        });

        VBox vBox = new VBox(loginBtn, registerBtn, guestBtn);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        Utils.setAnchors(vBox, 50);
        root = new AnchorPane(vBox);
    }

    @Override public Parent getRoot() {
        return root;
    }

    @Override public void modifyStage(Stage stage) {
        stage.setTitle("Javaquarium");
        stage.centerOnScreen();
        stage.setOnCloseRequest(e -> {});
    }
}
