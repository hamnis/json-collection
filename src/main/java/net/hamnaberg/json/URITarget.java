package net.hamnaberg.json;


import javaslang.control.Option;

import net.hamnaberg.json.util.Iterables;
import net.hamnaberg.json.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

public final class URITarget implements Target {
    private URI href;

    public URITarget(String href) {
        this(URI.create(href));
    }

    public URITarget(URI href) {
        this.href = href;
    }

    @Override
    public boolean isURITemplate() {
        return false;
    }

    public URI toURI() {
        return href;
    }

    public URI expand(Iterable<Property> properties) {
        if (Iterables.isEmpty(properties)) {
            return href;
        }
        final String query = href.getQuery();
        StringBuilder fromProperties = buildQuery(properties);
        if (StringUtils.isNotBlank(query)) {
            String actual = query;
            if (fromProperties.length() > 0) {
                actual = query + "&";
            }
            fromProperties.insert(0, actual);
        }
        String str = href.toString();
        int queryPart = str.indexOf('?');
        if (queryPart > 0) {
            str = str.substring(0, queryPart);
        }
        String createdQuery = fromProperties.length() == 0 ? "" : ( "?" + fromProperties.toString());
        return URI.create(str + createdQuery);
    }

    private StringBuilder buildQuery(Iterable<Property> properties) {
        StringBuilder sb = new StringBuilder();
        for (Property property : properties) {
            if (property.hasObject()) {
                throw new IllegalArgumentException("Expanding Cj Property object is undefined in the spec.");
            }
            else {
                if (property.hasArray()) {
                    List<Value> prop = property.getArray();
                    for (Value value : prop) {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }
                        sb.append(property.getName()).append("=").append(encode(value.asString()));
                    }
                }
                else {
                    Option<Value> value = property.getValue();
                    if (value.isDefined()) {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }
                        sb.append(property.getName()).append("=").append(encode(value.get().asString()));
                    }
                }
            }
        }
        return sb;
    }

    private String encode(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException("UTF-8");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URITarget uriTarget = (URITarget) o;

        if (href != null ? !href.equals(uriTarget.href) : uriTarget.href != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return href != null ? href.hashCode() : 0;
    }

    @Override
    public String toString() {
        return href.toString();
    }
}
