package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class DefaultJsonCollection extends AbstractJsonCollection {
    private final ImmutableList<Link> links;
    private final ImmutableList<Item> items;
    private final Template template;

    public DefaultJsonCollection(URI href) {
        this(href, Version.ONE);
    }

    public DefaultJsonCollection(URI href, Version version) {
        this(href, version, ImmutableList.<Link>of(), ImmutableList.<Item>of(), new Template());
    }

    public DefaultJsonCollection(URI href, Version version, ImmutableList<Link> links, ImmutableList<Item> items, Template template) {
        super(href, version);
        this.links = links;
        this.items = items;
        this.template = template;
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
    public Template getTemplate() {
        return template;
    }

    @Override
    public ErrorMessage getError() {
        throw new UnsupportedOperationException("Incorrect Collection type.");
    }
}
