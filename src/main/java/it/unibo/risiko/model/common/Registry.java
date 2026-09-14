package it.unibo.risiko.model.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Elements you can look up by id, kept in the order they were added.
 * Territories and continents needed exactly the same thing, so it's generic and the
 * roster uses it for the players too.
 *
 * @param <T> the type of the elements, has to be {@link Identifiable}
 */
public final class Registry<T extends Identifiable> implements Iterable<T> {

    private final Map<String, T> elements = new LinkedHashMap<>();
    private final String elementName;

    /**
     * Creates an empty registry.
     *
     * @param elementName what the elements are called, only used in error messages
     */
    public Registry(final String elementName) {
        this.elementName = elementName;
    }

    /**
     * Adds an element.
     *
     * @param element the element to add
     * @throws IllegalArgumentException if there is already an element with that id
     */
    public void add(final T element) {
        if (this.elements.containsKey(element.getId())) {
            throw new IllegalArgumentException(this.elementName + " already present: " + element.getId());
        }
        this.elements.put(element.getId(), element);
    }

    /**
     * Finds an element by id, and fails if it's not there.
     *
     * @param id the id to search
     * @return the element with that id
     * @throws IllegalArgumentException if the id does not exist
     */
    public T get(final String id) {
        final T element = this.elements.get(id);
        if (element == null) {
            throw new IllegalArgumentException(this.elementName + " does not exist: " + id);
        }
        return element;
    }

    /**
     * Like {@link #get(String)}, but for when a missing id is normal.
     *
     * @param id the id to search
     * @return the element or {@link Optional#empty()} if it is not there
     */
    public Optional<T> find(final String id) {
        return Optional.ofNullable(this.elements.get(id));
    }

    /**
     * Tells if an id is registered.
     *
     * @param id the id to search
     * @return true if there is an element with that id
     */
    public boolean contains(final String id) {
        return this.elements.containsKey(id);
    }

    /**
     * All the elements in insertion order.
     *
     * @return the elements in an unmodifiable collection
     */
    public Collection<T> values() {
        return Collections.unmodifiableCollection(this.elements.values());
    }

    /**
     * How many elements are in the registry.
     *
     * @return the number of elements
     */
    public int size() {
        return this.elements.size();
    }

    /**
     * A stream of the elements in insertion order.
     *
     * @return a stream of the elements
     */
    public Stream<T> stream() {
        return this.elements.values().stream();
    }

    @Override
    public Iterator<T> iterator() {
        return values().iterator();
    }
}
