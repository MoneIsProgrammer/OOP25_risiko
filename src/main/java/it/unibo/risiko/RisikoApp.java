package it.unibo.risiko;

import it.unibo.risiko.view.MainMenu;
import javafx.application.Application;

/**
 * Starting point of the application.
 */
public final class RisikoApp {

    /**
     * useless constructor.
     */
    private RisikoApp() {

    }

    /**
     * Launches JavaFx Thread.
     * 
     * @param args useless
     */
    public static void main(final String... args) {
        Application.launch(MainMenu.class);
    }
}
