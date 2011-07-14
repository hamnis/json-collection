package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class JsonCollection {
    private final URI href;
    private final Version version;
    private ImmutableList<Link> links;

    public JsonCollection(URI href) {
        this(href, Version.ONE);
    }

    public JsonCollection(URI href, Version version) {
        this(href, version, ImmutableList.<Link>of());
    }

    public JsonCollection(URI href, Version version, ImmutableList<Link> links) {
        this.href = href;
        this.version = version;
        this.links = links;
    }

    public URI getHref() {
        return href;
    }

    public Version getVersion() {
        return version;
    }

    public ImmutableList<Link> getLinks() {
        return links;
    }
}
