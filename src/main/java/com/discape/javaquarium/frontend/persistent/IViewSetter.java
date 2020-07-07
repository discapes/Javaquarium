package com.discape.javaquarium.frontend.persistent;

import javafx.scene.Parent;
import javafx.stage.Stage;

public interface IViewSetter {
    void applySecondaryView(Class<? extends IMainView> clazz);

    void closeSecondary();

    void applyView(Class<? extends IMainView> clazz);
    void clearViews();

    Stage quickStage(Parent parent, String title);
}
