package it.unibo.risiko;

import it.unibo.risiko.view.MainMenu;
import javafx.application.Application;

public final class RisikoApp {

    private RisikoApp() {

    }
    public static void main(final String... args) {
        Application.launch(MainMenu.class);
    }
}
