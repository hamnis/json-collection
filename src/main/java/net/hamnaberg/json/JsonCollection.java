package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class JsonCollection implements WithHref {
    private final URI href;
    private final Version version;
    private final ImmutableList<Link> links;
    private final ImmutableList<Item> items;

    public JsonCollection(URI href) {
        this(href, Version.ONE);
    }

    public JsonCollection(URI href, Version version) {
        this(href, version, ImmutableList.<Link>of(), ImmutableList.<Item>of());
    }

    public JsonCollection(URI href, Version version, ImmutableList<Link> links, ImmutableList<Item> items) {
        this.href = href;
        this.version = version;
        this.links = links;
        this.items = items;
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

    public ImmutableList<Item> getItems() {
        return items;
    }
}
