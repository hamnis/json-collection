package net.hamnaberg.json;

import net.hamnaberg.json.util.Optional;
import net.hamnaberg.json.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

public class URITarget implements Target {
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
        final String query = href.getQuery();
        StringBuilder fromProperties = buildQuery(properties);
        if (StringUtils.isNotBlank(query)) {
            String actual = query;
            if (fromProperties.length() > 0) {
                actual = query + "&";
            }
            fromProperties.insert(0, actual);
        }
        try {
            return new URI(
                    href.getScheme(),
                    href.getUserInfo(),
                    href.getHost(),
                    href.getPort(),
                    href.getPath(),
                    fromProperties.length() == 0 ? null : fromProperties.toString(),
                    href.getFragment()
            );
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private StringBuilder buildQuery(Iterable<Property> properties) {
        StringBuilder sb = new StringBuilder();
        for (Property property : properties) {
            if (property.isObject()) {
                throw new IllegalArgumentException("Expanding Cj Property object is undefined in the spec.");
            }
            else {
                if (property.isArray()) {
                    List<Value> prop = property.getArray();
                    for (Value value : prop) {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }
                        sb.append(property.getName()).append("=").append(encode(value.asString()));
                    }
                }
                else {
                    Optional<Value> value = property.getValue();
                    if (value.isSome()) {
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
