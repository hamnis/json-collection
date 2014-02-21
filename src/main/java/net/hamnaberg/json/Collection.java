/*
 * Copyright 2012 Erlend Hamnaberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hamnaberg.json;


import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.funclite.CollectionOps;
import net.hamnaberg.funclite.Optional;
import net.hamnaberg.funclite.Predicate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.json.navigation.Navigator;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.hamnaberg.funclite.Optional.fromNullable;
import static net.hamnaberg.funclite.Optional.some;

public final class Collection extends Extended<Collection> implements Writable {
    Collection(ObjectNode value) {
        super(value);
    }

    @Override
    protected Collection copy(ObjectNode value) {
        return new Collection(value);
    }

    public static Collection create(URI href, List<Link> links, List<Item> items, List<Query> queries, Template template, Error error) {
        return create(fromNullable(href), links, items, queries, fromNullable(template), fromNullable(error));
    }

    public static Collection create(Optional<URI> href, List<Link> links, List<Item> items, List<Query> queries, Optional<Template> template, Optional<Error> error) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.put("version", Version.ONE.getIdentifier());
        if (href.isSome()) {
            obj.put("href", href.get().toString());
        }
        if (!links.isEmpty()) {
            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            for (Link link : links) {
                arr.add(link.asJson());
            }
            obj.put("links", arr);
        }
        if (!items.isEmpty()) {
            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            for (Item i : items) {
                arr.add(i.asJson());
            }
            obj.put("items", arr);
        }
        if (!queries.isEmpty()) {
            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            for (Query i : queries) {
                arr.add(i.asJson());
            }
            obj.put("queries", arr);
        }
        if (template.isSome()) {
            obj.put("template", template.get().asJson());
        }
        if (error.isSome()) {
            obj.put("error", error.get().asJson());
        }
        Collection coll = new Collection(obj);
        coll.validate();
        return coll;
    }

    public Version getVersion() {
        return Version.ONE;
    }

    public Optional<URI> getHref() {
        return delegate.has("href") ? some(URI.create(delegate.get("href").asText())) : Optional.<URI>none();
    }

    public List<Link> getLinks() {
        return delegate.has("links") ? Link.fromArray(delegate.get("links")) : Collections.<Link>emptyList();
    }

    public List<Item> getItems() {
        return delegate.has("items") ? Item.fromArray(delegate.get("items")) : Collections.<Item>emptyList();
    }

    public List<Query> getQueries() {
        return delegate.has("queries") ? Query.fromArray(delegate.get("queries")) : Collections.<Query>emptyList();
    }

    public boolean hasTemplate() {
        return delegate.has("template");
    }

    public Optional<Template> getTemplate() {
        return hasTemplate() ? some(new Template((ObjectNode) delegate.get("template"))) : Optional.<Template>none();
    }

    public boolean hasError() {
        return delegate.has("error");
    }

    public Optional<Error> getError() {
        return hasError() ? some(new Error((ObjectNode) delegate.get("error"))) : Optional.<Error>none();
    }

    public Optional<Collection> addItem(Navigator navigator, Template template) {
        Optional<URI> href = getHref();
        if (href.isSome()) {
            throw new UnsupportedOperationException("Collection has no href, unable to POST to remote.");
        }
        return navigator.create(href.get(), template);
    }

    public Optional<Link> linkByName(final String name) {
        return findLink(new Predicate<Link>() {
            @Override
            public boolean apply(Link input) {
                return fromNullable(name).equals(input.getName());
            }
        });
    }

    public Optional<Link> linkByRelAndName(final String rel, final String name) {
        return findLink(new Predicate<Link>() {
            @Override
            public boolean apply(Link input) {
                return rel.equals(input.getRel()) && fromNullable(name).equals(input.getName());
            }
        });
    }

    public Optional<Link> linkByRel(final String rel) {
        return findLink(new Predicate<Link>() {
            @Override
            public boolean apply(Link input) {
                return rel.equals(input.getRel());
            }
        });
    }

    public Optional<Query> queryByRel(final String rel) {
        return findQuery(new Predicate<Query>() {
            @Override
            public boolean apply(Query input) {
                return rel.equals(input.getRel());
            }
        });
    }

    public Optional<Query> queryByName(final String name) {
        return findQuery(new Predicate<Query>() {
            @Override
            public boolean apply(Query input) {
                return fromNullable(name).equals(input.getName());
            }
        });
    }

    public Optional<Query> queryByRelAndName(final String rel, final String name) {
        return findQuery(new Predicate<Query>() {
            @Override
            public boolean apply(Query input) {
                return rel.equals(input.getRel()) && fromNullable(name).equals(input.getName());
            }
        });
    }

    public Optional<Link> findLink(Predicate<Link> predicate) {
        return CollectionOps.find(getLinks(), predicate);
    }

    public List<Link> filterLinks(Predicate<Link> predicate) {
        return CollectionOps.filter(getLinks(), predicate);
    }

    public Optional<Item> findItem(Predicate<Item> predicate) {
        return CollectionOps.find(getItems(), predicate);
    }

    public List<Item> filterItems(Predicate<Item> predicate) {
        return CollectionOps.filter(getItems(), predicate);
    }

    public List<Item> filterItemsByProfile(final URI profile) {
        return filterItems(new Predicate<Item>() {
            @Override
            public boolean apply(Item item) {
                return item.linkByRel("profile").forall(new Predicate<Link>() {
                    @Override
                    public boolean apply(Link link) {
                        return link.getHref().equals(profile);
                    }
                });
            }
        });
    }

    public Optional<Item> getFirstItem() {
        return CollectionOps.headOption(getItems());
    }

    public Optional<Query> findQuery(Predicate<Query> predicate) {
        return CollectionOps.find(getQueries(), predicate);
    }

    public List<Query> filterQueries(Predicate<Query> predicate) {
        return CollectionOps.filter(getQueries(), predicate);
    }

    public Builder toBuilder() {
        Builder builder = new Builder(getHref());
        builder.addItems(getItems());
        builder.addLinks(getLinks());
        builder.addQueries(getQueries());
        for (Template t : getTemplate()) {
            builder.withTemplate(t);
        }
        for (Error e : getError()) {
            builder.withError(e);
        }
        return builder;
    }

    public void writeTo(OutputStream stream) throws IOException {
        writeTo(new OutputStreamWriter(stream, Charset.forName("UTF-8")));
    }

    public void writeTo(Writer writer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode obj = mapper.createObjectNode();
        obj.put("collection", asJson());
        mapper.writeValue(writer, obj);
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            writeTo(writer);
        } catch (IOException ignore) {
        }
        return writer.toString();
    }

    public void validate() {
        for (Link link : getLinks()) {
            link.validate();
        }
        for (Item item : getItems()) {
            item.validate();
        }
        for (Query query : getQueries()) {
            query.validate();
        }
        for (Template t : getTemplate()) {
            t.validate();
        }
        for (Error e : getError()) {
            e.validate();
        }
    }

    public static Builder builder(URI href) {
        return new Builder(fromNullable(href));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Optional<URI> href;
        private final List<Item> itemBuilder = new ArrayList<Item>();
        private final List<Link> linkBuilder = new ArrayList<Link>();
        private final List<Query> queryBuilder = new ArrayList<Query>();
        private Optional<Template> template = Optional.none();
        private Optional<Error> error = Optional.none();

        public Builder() {
            this(Optional.<URI>none());
        }

        public Builder(URI href) {
            this(fromNullable(href));
        }

        public Builder(Optional<URI> href) {
            this.href = href;
        }

        public Builder withHref(URI href) {
            this.href = fromNullable(href);
            return this;
        }
        
        public Builder addItem(Item item) {
            itemBuilder.add(item);
            return this;
        }

        public Builder addItems(Iterable<Item> items) {
            addToList(items, itemBuilder);
            return this;
        }

        public Builder addQuery(Query query) {
            queryBuilder.add(query);
            return this;
        }

        public Builder addQueries(Iterable<Query> queries) {
            addToList(queries, queryBuilder);
            return this;
        }

        public Builder addLink(Link link) {
            linkBuilder.add(link);
            return this;
        }

        public Builder addLinks(Iterable<Link> links) {
            addToList(links, linkBuilder);
            return this;
        }

        public Builder withError(Error error) {
            this.error = fromNullable(error);
            return this;
        }

        public Builder withTemplate(Template template) {
            this.template = fromNullable(template);
            return this;
        }

        private <A> void addToList(Iterable<A> iterable, List<A> list) {
            for (A a : iterable) {
                list.add(a);
            }
        }

        public Collection build() {
            return Collection.create(href, linkBuilder, itemBuilder, queryBuilder, template, error);
        }
    }
}
