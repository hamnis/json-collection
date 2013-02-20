package net.hamnaberg.json;

import java.net.URI;
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

    public URI expand(List<Property> properties) {
        throw new UnsupportedOperationException("Not implemented");
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
