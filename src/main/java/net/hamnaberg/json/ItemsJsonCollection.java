package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class ItemsJsonCollection implements JsonCollection {
    private final URI href;
    private final Version version;
    private final ImmutableList<Link> links;
    private final ImmutableList<Item> items;

    public ItemsJsonCollection(URI href) {
        this(href, Version.ONE);
    }

    public ItemsJsonCollection(URI href, Version version) {
        this(href, version, ImmutableList.<Link>of(), ImmutableList.<Item>of());
    }

    public ItemsJsonCollection(URI href, Version version, ImmutableList<Link> links, ImmutableList<Item> items) {
        this.href = href;
        this.version = version;
        this.links = links;
        this.items = items;
    }

    public URI getHref() {
        return href;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public ImmutableList<Link> getLinks() {
        return links;
    }

    @Override
    public ImmutableList<Item> getItems() {
        return items;
    }

    @Override
    public ErrorMessage getError() {
        throw new UnsupportedOperationException("Incorrect Collection type.");
    }
}
