package net.hamnaberg.json;

import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.json.util.ListOps;
import net.hamnaberg.json.util.MapOps;
import net.hamnaberg.json.util.Optional;
import net.hamnaberg.json.util.Predicate;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class PropertyContainer<A> extends Extended<A> {
    protected PropertyContainer(ObjectNode delegate) {
        super(delegate);
    }

    public List<Property> getData() {
        return delegate.has("data") ? Property.fromData(delegate.get("data")) : Collections.<Property>emptyList();
    }

    public Map<String, Property> getDataAsMap() {
        Map<String, Property> builder = MapOps.newHashMap();
        for (Property property : getData()) {
            builder.put(property.getName(), property);
        }
        return Collections.unmodifiableMap(builder);
    }

    public Optional<Property> findProperty(Predicate<Property> predicate) {
        return ListOps.find(getData(), predicate);
    }

    public Optional<Property> propertyByName(final String name) {
        return findProperty(new Predicate<Property>() {
            @Override
            public boolean apply(Property input) {
                return name.equals(input.getName());
            }
        });
    }

    /**
     * Replaces all properties with the same name as the supplied property
     * @param property property to replace with
     * @return a new copy of the template, or this if nothing was modified.
     */
    @SuppressWarnings("unchecked")
    public A replace(Property property) {
        List<Property> data = getData();
        List<Property> props = new ArrayList<Property>(data);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equals(property.getName())) {
                props.set(i, property);
                break;
            }
        }
        if (!props.isEmpty()) {
            ObjectNode copied = copyDelegate();
            copied.put("data", Property.toArrayNode(props));
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
        List<Property> props = new ArrayList<Property>(getData());
        props.add(property);
        ObjectNode copied = copyDelegate();
        copied.put("data", Property.toArrayNode(props));
        return copy(copied);
    }
}
