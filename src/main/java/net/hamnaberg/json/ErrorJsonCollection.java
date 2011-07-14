package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class ErrorJsonCollection extends AbstractJsonCollection {
    private ErrorMessage error;

    public ErrorJsonCollection(URI href, Version version, ErrorMessage error) {
        super(href, version);
        this.error = error;
    }

    @Override
    public Template getTemplate() {
        throw new UnsupportedOperationException("Incorrect Collection type");
    }

    @Override
    public ImmutableList<Link> getLinks() {
        throw new UnsupportedOperationException("Incorrect Collection type");
    }

    @Override
    public ImmutableList<Item> getItems() {
        throw new UnsupportedOperationException("Incorrect Collection type");
    }

    @Override
    public ErrorMessage getError() {
        return error;
    }
}
