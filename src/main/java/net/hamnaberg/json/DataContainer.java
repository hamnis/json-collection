package net.hamnaberg.json;

import javaslang.control.Option;
import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.json.util.Iterables;

import java.util.*;
import java.util.function.Predicate;

public abstract class DataContainer<A extends DataContainer> extends Extended<A> {
    protected DataContainer(Json.JObject delegate) {
        super(delegate);
    }

    public Data getData() {
        return new Data(Property.fromData(delegate.getAsArrayOrEmpty("data")));
    }

    public Map<String, Property> getDataAsMap() {
       return getData().getDataAsMap();
    }

    public Option<Property> findProperty(Predicate<Property> predicate) {
        return getData().findProperty(predicate);
    }

    public Option<Property> propertyByName(final String name) {
        return getData().propertyByName(name);
    }

    /**
     * Replaces all properties with the same name as the supplied property
     * @param property property to replace with
     * @return a new copy of the template, or this if nothing was modified.
     */
    @SuppressWarnings("unchecked")
    public A replace(Property property) {
        Data data = getData();
        Data replaced = data.replace(property);
        if (!replaced.isEmpty()) {
            return copy(delegate.put("data", Property.toArrayNode(replaced)));
        }
        return (A)this;
    }

    /**
     * Adds a property to the data.
     * @param property the property to add
     * @return a new copy of the template.
     */
    public A add(Property property) {
        return addAll(Arrays.asList(property));
    }

    /**
     * Adds properties to the data.
     * @param toAdd the properties to add
     * @return a new copy of the template.
     */
    @SuppressWarnings("unchecked")
    public A addAll(Iterable<Property> toAdd) {
        Data data = getData();
        Data modified = data.addAll(toAdd);
        if (data == modified) {
            return (A)this;
        }

        return copy(delegate.put("data", Property.toArrayNode(data)));
    }

    /**
     * Replaces all properties.
     *
     * @param props the property to add
     * @return a new copy of the template.
     */
    @SuppressWarnings("unchecked")
    public A set(Iterable<Property> props) {
        if (Iterables.isEmpty(props)) {
            return (A) this;
        }
        return copy(delegate.put("data", Property.toArrayNode(props)));
    }
}
