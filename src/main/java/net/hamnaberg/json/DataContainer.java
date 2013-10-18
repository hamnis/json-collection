package net.hamnaberg.json;

import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.funclite.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public abstract class DataContainer<A extends DataContainer> extends Extended<A> {
    protected DataContainer(ObjectNode delegate) {
        super(delegate);
    }

    public Data getData() {
        return new Data(delegate.has("data") ? Property.fromData(delegate.get("data")) : Collections.<Property>emptyList());
    }

    public Map<String, Property> getDataAsMap() {
       return getData().getDataAsMap();
    }

    public Optional<Property> findProperty(Predicate<Property> predicate) {
        return getData().findProperty(predicate);
    }

    public Optional<Property> propertyByName(final String name) {
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
            ObjectNode copied = copyDelegate();
            copied.put("data", Property.toArrayNode(replaced));
            return copy(copied);
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

        ObjectNode copied = copyDelegate();
        copied.put("data", Property.toArrayNode(data));
        return copy(copied);
    }

    /**
     * Replaces all properties.
     *
     * @param props the property to add
     * @return a new copy of the template.
     */
    @SuppressWarnings("unchecked")
    public A set(Iterable<Property> props) {
        if (CollectionOps.isEmpty(props)) {
            return (A) this;
        }
        ObjectNode copied = copyDelegate();
        copied.put("data", Property.toArrayNode(props));
        return copy(copied);
    }
}
