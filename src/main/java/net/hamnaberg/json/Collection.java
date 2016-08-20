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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javaslang.control.Option;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static javaslang.control.Option.of;

public final class Collection extends Extended<Collection> implements Writable {

    Collection(Json.JObject value) {
        super(value);
    }

    @Override
    protected Collection copy(Json.JObject value) {
        return new Collection(value);
    }

    public static Collection create(URI href, List<Link> links, List<Item> items, List<Query> queries, Template template, Error error) {
        return create(of(href), links, items, queries, of(template), of(error));
    }

    public static Collection create(Option<URI> href,
                                    List<Link> links,
                                    List<Item> items,
                                    List<Query> queries,
                                    Option<Template> template,
                                    Option<Error> error) {

        Map<String, Json.JValue> map = new LinkedHashMap<>();
        map.put("version", Json.jString(Version.ONE.getIdentifier()));
        href.forEach(value -> map.put("href", Json.jString(value.toString())));
        if (!links.isEmpty()) {
            map.put("links", Json.jArray(links.stream()
                    .map(Extended::asJson)
                    .collect(Collectors.toList())));
        }
        if (!items.isEmpty()) {
            map.put("items", Json.jArray(items.stream()
                    .map(Extended::asJson)
                    .collect(Collectors.toList())));
        }
        if (!queries.isEmpty()) {
            map.put("queries", Json.jArray(queries.stream()
                    .map(Extended::asJson)
                    .collect(Collectors.toList())));
        }
        template.forEach(value -> map.put("template", value.asJson()));
        error.forEach(value -> map.put("error", value.asJson()));
        Collection coll = new Collection(Json.jObject(map));
        coll.validate();
        return coll;
    }

    public Version getVersion() {
        return Version.ONE;
    }

    public Option<URI> getHref() {
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

    public Option<Template> getTemplate() {
        return delegate.getAsObject("template").map(Template::new);
    }

    public boolean hasError() {
        return delegate.containsKey("error");
    }

    public Option<Error> getError() {
        return delegate.getAsObject("error").map(Error::new);
    }

    public Option<Link> linkByName(final String name) {
        return findLink(link -> of(name).equals(link.getName()));
    }

    public Option<Link> linkByRelAndName(final String rel, final String name) {
        return findLink(link -> rel.equals(link.getRel()) && of(name).equals(link.getName()));
    }

    public Option<Link> linkByRel(final String rel) {
        return findLink(link -> rel.equals(link.getRel()));
    }

    public List<Link> linksByRel(final String rel) {
        return filterLinks(link -> rel.equals(link.getRel()));
    }

    public Option<Query> queryByRel(final String rel) {
        return findQuery(query -> rel.equals(query.getRel()));
    }

    public Option<Query> queryByName(final String name) {
        return findQuery(query -> of(name).equals(query.getName()));
    }

    public Option<Query> queryByRelAndName(final String rel, final String name) {
        return findQuery(query -> rel.equals(query.getRel()) && of(name).equals(query.getName()));
    }

    public Option<Link> findLink(Predicate<Link> predicate) {
        return Option.ofOptional(getLinks().stream().filter(predicate).findFirst());
    }

    public List<Link> filterLinks(Predicate<Link> predicate) {
        return getLinks().stream().filter(predicate).collect(Collectors.<Link>toList());
    }

    public Option<Item> findItem(Predicate<Item> predicate) {
        return Option.ofOptional(getItems().stream().filter(predicate).findFirst());
    }

    public List<Item> filterItems(Predicate<Item> predicate) {
        return getItems().stream().filter(predicate).collect(Collectors.<Item>toList());
    }

    public List<Item> filterItemsByProfile(final URI profile) {
        return filterItems(item -> item.linkByRel("profile").map(link -> link.getHref().equals(profile)).getOrElse(true));
    }

    public Option<Item> getFirstItem() {
        return Option.ofOptional(getItems().stream().findFirst());
    }

    public Option<Query> findQuery(Predicate<Query> predicate) {
        return Option.ofOptional(getQueries().stream().filter(predicate).findFirst());
    }

    public List<Query> filterQueries(Predicate<Query> predicate) {
        return getQueries().stream().filter(predicate).collect(Collectors.<Query>toList());
    }

    public Builder toBuilder() {
        Builder builder = new Builder(getHref());
        builder.addItems(getItems());
        builder.addLinks(getLinks());
        builder.addQueries(getQueries());
        getTemplate().forEach(builder::withTemplate);
        getError().forEach(builder::withError);
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
        getLinks().forEach(Link::validate);
        getItems().forEach(Item::validate);
        getQueries().forEach(Query::validate);
        getTemplate().forEach(Template::validate);
        getError().forEach(Error::validate);
    }

    public static Builder builder(URI href) {
        return new Builder(of(href));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Option<URI> href;

        private final List<Item> itemBuilder = new ArrayList<Item>();

        private final List<Link> linkBuilder = new ArrayList<Link>();

        private final List<Query> queryBuilder = new ArrayList<Query>();

        private Option<Template> template = Option.none();

        private Option<Error> error = Option.none();

        public Builder() {
            this(Option.none());
        }

        public Builder(URI href) {
            this(of(href));
        }

        public Builder(Option<URI> href) {
            this.href = href;
        }

        public Builder withHref(URI href) {
            this.href = of(href);
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
            this.error = of(error);
            return this;
        }

        public Builder withTemplate(Template template) {
            this.template = of(template);
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
