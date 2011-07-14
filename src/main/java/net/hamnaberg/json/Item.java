package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class Item implements WithHref {
    private URI href;
    private ImmutableList<Property> properties;

    public Item(URI href, ImmutableList<Property> properties) {
        this.href = href;
        this.properties = properties;
    }

    public URI getHref() {
        return href;
    }

    public ImmutableList<Property> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (href != null ? !href.equals(item.href) : item.href != null) return false;
        if (properties != null ? !properties.equals(item.properties) : item.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Item with href %s and properties %s", href, properties);
    }
}
