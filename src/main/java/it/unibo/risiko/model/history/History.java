package it.unibo.risiko.model.history;

import java.util.List;

import it.unibo.risiko.model.event.Event;
import javafx.collections.ListChangeListener;

/**
 * Models the history of the game containing various {@link Event}s.
 */
public interface History {
    /**
     * Used to get all the events that happened in a ordered list from oldest to newest.
     * 
     * @return a List of all Events that happened
     */
    List<String> getAllEvents();

    /**
     * Used to get a portion of the events in a ordered list from oldest to newest.
     * 
     * @param n number of Events in the return list
     * @return the last n Events in the list starting from last-n until last,
     *      if n is greater than the total number of events there will be less than n elements
     */
    List<String> getLastNEvents(int n);

    /**
     * Used to know how many elements are present in the history.
     * 
     * @return the number of events present in History
     */
    int getTotalEvents();

    /**
     * Used to add an event as the newest element of the list.
     * 
     * @param event the event to be added to the History
     */
    void addEvent(Event event);

    /**
     * Used to create a History with various string rapresentation of events from zero.
     * 
     * @param history Overwrites current History with the passed list of String
     */
    void restoreHistory(List<String> history);

    /**
     * Used to add listeners that want to be notified when this class changes.
     * 
     * @param listener listener to be added that will be notified on addition to the history
     */
    void addListener(ListChangeListener<String> listener);

}
