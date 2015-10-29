package net.hamnaberg.json;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;

public final class URITemplateTarget implements Target {
    private String href;

    public URITemplateTarget(String href) {
        try {
            UriTemplate.fromTemplate(href);
            this.href = href;
        } catch (MalformedUriTemplateException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isURITemplate() {
        return true;
    }

    public URI toURI() {
        try {
            return URI.create(UriTemplate.fromTemplate(href).expand());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public URI expand(Iterable<Property> properties) {
        Map<String, Object> map = new HashMap<>();
        for (Property property : properties) {
            if (property.hasArray()) {
                map.put(property.getName(), property.getArray().stream().filter(NOT_NULL_PRED).map(AS_STRING).collect(Collectors.toList()));
            }
            else if (property.hasObject()) {
                map.put(property.getName(),
                        property.getObject()
                                .entrySet()
                                .stream()
                                .filter(VALUE_NOT_NULL_PRED)
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().asString())));
            }
            else {
                Optional<Object> value = property.getValue().filter(NOT_NULL_PRED).map(AS_STRING);
                value.ifPresent(val -> map.put(property.getName(), val));
            }
        }

        try {
            return URI.create(UriTemplate.fromTemplate(href).expand(map));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URITemplateTarget uriTarget = (URITemplateTarget) o;

        if (href != null ? !href.equals(uriTarget.href) : uriTarget.href != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return href != null ? href.hashCode() : 0;
    }

    @Override
    public String toString() {
        return href;
    }

    private final Predicate<Map.Entry<String,Value>> VALUE_NOT_NULL_PRED = input -> input.getValue() != Value.NULL;

    private Predicate<Value> NOT_NULL_PRED = input -> input != Value.NULL;
    private Function<Value,Object> AS_STRING = Value::asString;

}
