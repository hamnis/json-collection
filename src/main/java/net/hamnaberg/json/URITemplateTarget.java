package net.hamnaberg.json;

import com.damnhandy.uri.template.UriTemplate;

import java.net.URI;
import java.util.List;

public class URITemplateTarget implements Target {
    private UriTemplate href;

    public URITemplateTarget(String href) {
        this.href = UriTemplate.fromTemplate(href);
    }

    @Override
    public boolean isURITemplate() {
        return true;
    }

    public URI toURI() {
        return URI.create(href.expand());
    }

    public URI expand(List<Property> properties) {
        return URI.create(href.expand(Property.toMap(properties)));
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
}
