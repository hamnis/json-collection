package net.hamnaberg.json;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javaslang.control.Option;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.hamnaberg.json.util.Iterables;

public final class Data implements Iterable<Property> {

    private final List<Property> properties;

    public static Map<String, Property> getDataAsMap(Iterable<Property> properties) {
        return Collections.unmodifiableMap(
                StreamSupport.stream(properties.spliterator(), false).
                        collect(Collectors.toMap(Property::getName, Function.<Property>identity()))
        );
    }


    public Data(Iterable<Property> props) {
        properties = Option.of(props)
                             .map(x -> StreamSupport.stream(x.spliterator(), false).collect(Collectors.toList()))
                             .getOrElseThrow(() -> new IllegalArgumentException("Properties in Data may not be null"));
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Map<String, Property> getDataAsMap() {
        return getDataAsMap(properties);
    }

    public Option<Property> findProperty(Predicate<Property> predicate) {
        return Option.ofOptional(properties.stream().filter(predicate).findFirst());
    }

    public Option<Property> propertyByName(final String name) {
        return findProperty(input -> name.equals(input.getName()));
    }

    public Option<Property> get(int index) {
        int count = 0;
        for (Property property : properties) {
            if (index == count) {
                return Option.of(property);
            }
            count++;
        }
        return Option.none();
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
        Map<String, Property> map = getDataAsMap(replacement);

        List<Property> props = this.stream().
                map(f -> map.getOrDefault(f.getName(), f)).
                collect(Collectors.toList());

        return new Data(props);
    }

    /**
     * Replaces all properties with the same name as the supplied property
     *
     * @param property property to replace with
     * @return a new copy of the template, or this if nothing was modified.
     */
    public Data replace(Property property) {
        return replace(Collections.singletonList(property));
    }

    /**
     * Adds a property to the data.
     *
     * @param property the property to add
     * @return a new copy of the template.
     */
    public Data add(Property property) {
        return addAll(Collections.singletonList(property));
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

    public Stream<Property> stream() {return StreamSupport.stream(spliterator(), false); }
}
