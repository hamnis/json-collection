package net.hamnaberg.json;

import net.hamnaberg.funclite.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Data implements Iterable<Property> {
    private List<Property> properties;

    public Data(Iterable<Property> props) {
        properties = Collections.unmodifiableList(CollectionOps.newArrayList(Preconditions.checkNotNull(props, "Properties in Data may not be null")));
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Map<String, Property> getDataAsMap() {
        Map<String, Property> builder = MapOps.newHashMap();
        for (Property property : properties) {
            builder.put(property.getName(), property);
        }
        return Collections.unmodifiableMap(builder);
    }

    public Optional<Property> findProperty(Predicate<Property> predicate) {
        return CollectionOps.find(properties, predicate);
    }

    public Optional<Property> propertyByName(final String name) {
        return findProperty(new Predicate<Property>() {
            @Override
            public boolean apply(Property input) {
                return name.equals(input.getName());
            }
        });
    }

    public Optional<Property> get(int index) {
        int count = 0;
        for (Property property : properties) {
            if (index == count) {
                return Optional.some(property);
            }
            count++;
        }
        return Optional.none();
    }

    /**
     * Replaces all properties with the same name as the supplied properties.
     * @param replacement property to replace with
     * @return a new copy of the template
     */
    public Data replace(Iterable<Property> replacement) {
        if (CollectionOps.isEmpty(replacement)) {
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
     * @param property property to replace with
     * @return a new copy of the template, or this if nothing was modified.
     */
    public Data replace(Property property) {
        return replace(Arrays.asList(property));
    }

    /**
     * Adds a property to the data.
     * @param property the property to add
     * @return a new copy of the template.
     */
    public Data add(Property property) {
        return addAll(Arrays.asList(property));
    }

    /**
     * Adds properties to the data.
     * @param toAdd the properties to add
     * @return a new copy of the template.
     */
    public Data addAll(Iterable<Property> toAdd) {
        if (CollectionOps.isEmpty(toAdd)) {
            return this;
        }
        List<Property> props = new ArrayList<Property>(properties);
        CollectionOps.addAll(props, toAdd);
        return new Data(props);
    }

    /**
     * Replaces all properties.
     *
     * @param props the property to add
     * @return a new copy of the template.
     */
    public Data set(Iterable<Property> props) {
        if (CollectionOps.isEmpty(props)) {
            return this;
        }
        return new Data(props);
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
