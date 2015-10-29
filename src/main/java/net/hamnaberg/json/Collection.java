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
import net.hamnaberg.json.io.JacksonStreamingSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;

public final class Collection extends Extended<Collection> implements Writable {

    Collection(Json.JObject value) {
        super(value);
    }

    @Override
    protected Collection copy(Json.JObject value) {
        return new Collection(value);
    }

    public static Collection create(URI href, List<Link> links, List<Item> items, List<Query> queries, Template template, Error error) {
        return create(ofNullable(href), links, items, queries, ofNullable(template), ofNullable(error));
    }

    public static Collection create(Optional<URI> href,
                                    List<Link> links,
                                    List<Item> items,
                                    List<Query> queries,
                                    Optional<Template> template,
                                    Optional<Error> error) {
        ArrayList<Map.Entry<String, Json.JValue>> object = new ArrayList<>();
        object.add(Json.entry("version", Json.jString(Version.ONE.getIdentifier())));
        href.ifPresent(value -> object.add(Json.entry("href", Json.jString(value.toString()))));
        if (!links.isEmpty()) {
            object.add(Json.entry("links", Json.jArray(links.stream()
                    .map(Extended::asJson)
                    .collect(Collectors.toList()))));
        }
        if (!items.isEmpty()) {
            object.add(Json.entry("items", Json.jArray(items.stream()
                    .map(Extended::asJson)
                    .collect(Collectors.toList()))));
        }
        if (!queries.isEmpty()) {
            object.add(Json.entry("queries", Json.jArray(queries.stream()
                    .map(Extended::asJson)
                    .collect(Collectors.toList()))));
        }
        template.ifPresent(value -> object.add(Json.entry("template", value.asJson())));
        error.ifPresent(value -> object.add(Json.entry("error", value.asJson())));
        Collection coll = new Collection(Json.jObject(object));
        coll.validate();
        return coll;
    }

    public Version getVersion() {
        return Version.ONE;
    }

    public Optional<URI> getHref() {
        return delegate.getAsString("href").map(URI::create);
    }

    public List<Link> getLinks() {
        return Link.fromArray(delegate.getAsArrayOrEmpty("links"));
    }

    public List<Item> getItems() {
        return Item.fromArray(delegate.getAsArrayOrEmpty("items"));
    }

    public List<Query> getQueries() {
        return Query.fromArray(delegate.getAsArrayOrEmpty("queries"));
    }

    public boolean hasTemplate() {
        return delegate.containsKey("template");
    }

    public Optional<Template> getTemplate() {
        return delegate.getAsObject("template").map(Template::new);
    }

    public boolean hasError() {
        return delegate.containsKey("error");
    }

    public Optional<Error> getError() {
        return delegate.getAsObject("error").map(Error::new);
    }

    public Optional<Link> linkByName(final String name) {
        return findLink(link -> ofNullable(name).equals(link.getName()));
    }

    public Optional<Link> linkByRelAndName(final String rel, final String name) {
        return findLink(link -> rel.equals(link.getRel()) && ofNullable(name).equals(link.getName()));
    }

    public Optional<Link> linkByRel(final String rel) {
        return findLink(link -> rel.equals(link.getRel()));
    }

    public List<Link> linksByRel(final String rel) {
        return filterLinks(link -> rel.equals(link.getRel()));
    }

    public Optional<Query> queryByRel(final String rel) {
        return findQuery(query -> rel.equals(query.getRel()));
    }

    public Optional<Query> queryByName(final String name) {
        return findQuery(query -> ofNullable(name).equals(query.getName()));
    }

    public Optional<Query> queryByRelAndName(final String rel, final String name) {
        return findQuery(query -> rel.equals(query.getRel()) && ofNullable(name).equals(query.getName()));
    }

    public Optional<Link> findLink(Predicate<Link> predicate) {
        return getLinks().stream().filter(predicate).findFirst();
    }

    public List<Link> filterLinks(Predicate<Link> predicate) {
        return getLinks().stream().filter(predicate).collect(Collectors.<Link>toList());
    }

    public Optional<Item> findItem(Predicate<Item> predicate) {
        return getItems().stream().filter(predicate).findFirst();
    }

    public List<Item> filterItems(Predicate<Item> predicate) {
        return getItems().stream().filter(predicate).collect(Collectors.<Item>toList());
    }

    public List<Item> filterItemsByProfile(final URI profile) {
        return filterItems(item -> item.linkByRel("profile").map(link -> link.getHref().equals(profile)).orElse(true));
    }

    public Optional<Item> getFirstItem() {
        return getItems().stream().findFirst();
    }

    public Optional<Query> findQuery(Predicate<Query> predicate) {
        return getQueries().stream().filter(predicate).findFirst();
    }

    public List<Query> filterQueries(Predicate<Query> predicate) {
        return getQueries().stream().filter(predicate).collect(Collectors.<Query>toList());
    }

    public Builder toBuilder() {
        Builder builder = new Builder(getHref());
        builder.addItems(getItems());
        builder.addLinks(getLinks());
        builder.addQueries(getQueries());
        getTemplate().ifPresent(builder::withTemplate);
        getError().ifPresent(builder::withError);
        return builder;
    }

    public void writeTo(OutputStream stream) throws IOException {
        writeTo(new OutputStreamWriter(stream, Charset.forName("UTF-8")));
    }

    public void writeTo(Writer writer) throws IOException {
        new JacksonStreamingSerializer().write(
                Json.jObject("collection", asJson()),
                writer
        );
    }

    @Override
    public String toString() {
        return new JacksonStreamingSerializer().
                writeToString(Json.jObject("collection", asJson()));
    }

    public void validate() {
        getLinks().stream().forEach(Link::validate);
        getItems().stream().forEach(Item::validate);
        getQueries().stream().forEach(Query::validate);
        getTemplate().ifPresent(Template::validate);
        getError().ifPresent(Error::validate);
    }

    public static Builder builder(URI href) {
        return new Builder(ofNullable(href));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Optional<URI> href;

        private final List<Item> itemBuilder = new ArrayList<Item>();

        private final List<Link> linkBuilder = new ArrayList<Link>();

        private final List<Query> queryBuilder = new ArrayList<Query>();

        private Optional<Template> template = Optional.empty();

        private Optional<Error> error = Optional.empty();

        public Builder() {
            this(Optional.<URI>empty());
        }

        public Builder(URI href) {
            this(ofNullable(href));
        }

        public Builder(Optional<URI> href) {
            this.href = href;
        }

        public Builder withHref(URI href) {
            this.href = ofNullable(href);
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
            this.error = ofNullable(error);
            return this;
        }

        public Builder withTemplate(Template template) {
            this.template = ofNullable(template);
            return this;
        }

        private <A> void addToList(Iterable<A> iterable, List<A> list) {
            StreamSupport.stream(iterable.spliterator(), false).forEach(list::add);
        }

        public Collection build() {
            return Collection.create(href, linkBuilder, itemBuilder, queryBuilder, template, error);
        }
    }
}
