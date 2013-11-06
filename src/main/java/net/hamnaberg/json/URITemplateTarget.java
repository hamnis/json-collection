package net.hamnaberg.json;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.VariableExpansionException;
import net.hamnaberg.funclite.*;

import java.net.URI;
import java.util.Map;

public final class URITemplateTarget implements Target {

    public URITemplateTarget(String href) {
        try {
            this.href = UriTemplate.fromTemplate(href);
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
            return URI.create(href.expand());
        } catch (VariableExpansionException e) {
            throw new IllegalStateException(e);
        }
    }

    public URI expand(Iterable<Property> properties) {
        Map<String, Object> map = MapOps.newHashMap();
        for (Property property : properties) {
            if (property.hasArray()) {
                map.put(property.getName(), FunctionalList.create(property.getArray()).filter(NOT_NULL_PRED).map(AS_STRING));
            }
            else if (property.hasObject()) {
                map.put(property.getName(), FunctionalMap.create(property.getObject()).filter(VALUE_NOT_NULL_PRED).mapValues(AS_STRING));
            }
            else {
                Optional<Object> value = property.getValue().filter(NOT_NULL_PRED).map(AS_STRING);
                if (value.isSome()) {
                    map.put(property.getName(), value.get());
                }
            }
        }

        try {
            return URI.create(href.expand(map));
        } catch (VariableExpansionException e) {
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
        return href.getTemplate();
    }

    private final Predicate<Map.Entry<String,Value>> VALUE_NOT_NULL_PRED = new Predicate<Map.Entry<String, Value>>() {
        @Override
        public boolean apply(Map.Entry<String, Value> input) {
            return !input.getValue().isNull();
        }
    };
    private UriTemplate href;
    private Predicate<Value> NOT_NULL_PRED = new Predicate<Value>() {
        @Override
        public boolean apply(Value input) {
            return !input.isNull();
        }
    };
    private Function<Value,Object> AS_STRING = new Function<Value, Object>() {
        @Override
        public Object apply(Value input) {
            return input.asString();
        }
    };

}
