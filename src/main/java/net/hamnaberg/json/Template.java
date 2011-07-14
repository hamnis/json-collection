package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

public class Template {
    private final ImmutableList<Property> properties;

    public Template() {
        this(ImmutableList.<Property>of());
    }

    public Template(ImmutableList<Property> properties) {
        this.properties = properties;
    }

    public ImmutableList<Property> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        if (properties != null ? !properties.equals(template.properties) : template.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }
}
