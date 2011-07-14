package net.hamnaberg.json;

import com.google.common.collect.ImmutableList;

public interface JsonCollection extends WithHref {
    Version getVersion();

    ImmutableList<Link> getLinks();

    ImmutableList<Item> getItems();

    ErrorMessage getError();
}
