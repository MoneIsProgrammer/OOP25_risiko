package it.unibo.risiko.model.history;

import java.util.List;

import it.unibo.risiko.model.event.Event;
import it.unibo.risiko.view.EventStringVisitor;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Implementation of History.
 */
public class HistoryImpl implements History {

    private final ObservableList<String> history = FXCollections.observableArrayList();
    private final EventStringVisitor visitor = new EventStringVisitor();

    /**
     * This constructor returns an empty hystory.
     */
    public HistoryImpl() {
        //this is for removing a javadoc error
    }

    /**
     * This constructor returns an history already containing the events in the list.
     * 
     * @param history the events 
     */
    public HistoryImpl(final List<String> history) {
        this.restoreHistory(history);
    }

    /**
     * This constructor return an history containig the passed events.
     * 
     * @param events variable number events to be added
     */
    public HistoryImpl(final Event... events) {
        for (Event event : events) {
            this.addEvent(event);
        }
    }

    @Override
    public final List<String> getAllEvents() {
        return List.copyOf(this.history);
    }

    @Override
    public final List<String> getLastNEvents(final int n) {
        if (n > this.history.size()) {
            return List.copyOf(this.history);
        }
        if (n < 0) {
            throw new IllegalArgumentException("cannot request negative elemtents");
        }
        return this.history.stream().skip(this.history.size() - n).toList();
    }

    @Override
    public final int getTotalEvents() {
        return this.history.size();
    }

    @Override
    public final void addEvent(final Event event) {
        var list = event.accept(this.visitor);
        for (String string : list) {
            this.history.add(string);
        }
    }

    @Override
    public final void restoreHistory(final List<String> newHistory) {
        this.history.clear();
        this.history.addAll(newHistory);
    }

    @Override
    public final void addListener(final ListChangeListener<String> listener) {
        this.history.addListener(listener);
    }

    @Override
    public void addCustomEvent(String event) {
        this.history.add(event);
    }
}
