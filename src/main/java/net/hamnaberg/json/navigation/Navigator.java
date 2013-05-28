package net.hamnaberg.json.navigation;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Item;
import net.hamnaberg.json.Template;
import net.hamnaberg.funclite.Optional;

import java.net.URI;

public interface Navigator {
    Optional<Collection> follow(URI href);
    Optional<Collection> create(URI href, Template template);
    boolean update(Item item);
}
