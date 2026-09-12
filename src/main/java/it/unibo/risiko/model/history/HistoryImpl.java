package it.unibo.risiko.model.history;

import java.util.List;

import it.unibo.risiko.model.event.Event;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Implementation of History.
 */
public class HistoryImpl implements History {

    private final ObservableList<Event> history = FXCollections.observableArrayList();

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
    public HistoryImpl(final List<Event> history) {
        this.restoreHistory(history);
    }

    /**
     * This constructor return an history containig the passed events.
     * 
     * @param events variable number events to be added
     */
    public HistoryImpl(final Event... events) {
        this.restoreHistory(List.of(events));
    }

    @Override
    public final List<Event> getAllEvents() {
        return List.copyOf(this.history);
    }

    @Override
    public final List<Event> getLastNEvents(final int n) {
        return this.history.subList(this.history.size() - n, n);
    }

    @Override
    public final int getTotalEvents() {
        return this.history.size();
    }

    @Override
    public final void addEvent(final Event event) {
        this.history.add(event);
    }

    @Override
    public final void restoreHistory(final List<Event> newHistory) {
        this.history.clear();
        this.history.addAll(newHistory);
    }

    @Override
    public final void addListener(final ListChangeListener<Event> listener) {
        this.history.addListener(listener);
    }
}
