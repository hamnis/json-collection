package net.hamnaberg.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.hamnaberg.json.util.Iterables;

public final class Data implements Iterable<Property> {

    private List<Property> properties;

    public Data(Iterable<Property> props) {
        properties = Optional.ofNullable(props)
                             .map(x -> StreamSupport.stream(x.spliterator(), false).collect(Collectors.toList()))
                             .orElseThrow(() -> new IllegalArgumentException("Properties in Data may not be null"));
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Map<String, Property> getDataAsMap() {
        return Collections.unmodifiableMap(properties.stream().collect(Collectors.toMap(Property::getName, Function.<Property>identity())));
    }

    public Optional<Property> findProperty(Predicate<Property> predicate) {
        return properties.stream().filter(predicate).findFirst();
    }

    public Optional<Property> propertyByName(final String name) {
        return findProperty(input -> name.equals(input.getName()));
    }

    public Optional<Property> get(int index) {
        int count = 0;
        for (Property property : properties) {
            if (index == count) {
                return Optional.of(property);
            }
            count++;
        }
        return Optional.empty();
    }

    /**
     * Replaces all properties with the same name as the supplied properties.
     *
     * @param replacement property to replace with
     * @return a new copy of the template
     */
    public Data replace(Iterable<Property> replacement) {
        if (Iterables.isEmpty(replacement)) {
            return this;
        }
        Map<String, Property> map = new Data(replacement).getDataAsMap();
        List<Property> props = new ArrayList<Property>(this.properties.size());
        for (Property current : this.properties) {
            Property property = map.get(current.getName());
            if (property != null) {
                props.add(property);
            } else {
                props.add(current);
            }
        }
        return new Data(props);
    }

    /**
     * Replaces all properties with the same name as the supplied property
     *
     * @param property property to replace with
     * @return a new copy of the template, or this if nothing was modified.
     */
    public Data replace(Property property) {
        return replace(Arrays.asList(property));
    }

    /**
     * Adds a property to the data.
     *
     * @param property the property to add
     * @return a new copy of the template.
     */
    public Data add(Property property) {
        return addAll(Arrays.asList(property));
    }

    /**
     * Adds properties to the data.
     *
     * @param toAdd the properties to add
     * @return a new copy of the template.
     */
    public Data addAll(Iterable<Property> toAdd) {
        return !Iterables.isEmpty(toAdd)
               ? new Data(Stream.concat(properties.stream(), StreamSupport.stream(toAdd.spliterator(), false)).collect(Collectors.toList()))
               : this;
    }

    /**
     * Replaces all properties.
     *
     * @param props the property to add
     * @return a new copy of the template.
     */
    public Data set(Iterable<Property> props) {
        return !Iterables.isEmpty(props)
               ? new Data(props)
               : this;
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    @Override
    public Iterator<Property> iterator() {
        return properties.iterator();
    }
}
