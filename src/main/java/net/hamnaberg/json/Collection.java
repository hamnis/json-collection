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


import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import net.hamnaberg.json.generator.CollectionGenerator;
import net.hamnaberg.json.util.ListOps;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Collection {
    private final URI href;
    private final List<Link> links;
    private final List<Item> items;
    private final List<Query> queries;
    private final Optional<Template> template;
    private final Optional<Error> error;

    public Collection(URI href) {
        this(href, Collections.<Link>emptyList(), Collections.<Item>emptyList(), Collections.<Query>emptyList(), null, null);
    }

    public Collection(URI href, List<Item> items) {
        this(href, Collections.<Link>emptyList(), items, Collections.<Query>emptyList(), null, null);
    }

    public Collection(URI href, List<Link> links, List<Item> items, List<Query> queries, Template template, Error error) {
        this.href = href;
        this.links = links == null ? ImmutableList.<Link>of() : ImmutableList.<Link>builder().addAll(links).build();
        this.items = items == null ? ImmutableList.<Item>of() : ImmutableList.<Item>builder().addAll(items).build();
        this.queries = queries == null ? ImmutableList.<Query>of() : ImmutableList.<Query>builder().addAll(queries).build();
        this.template = Optional.fromNullable(template);
        this.error = Optional.fromNullable(error);
    }

    public Version getVersion() {
        return Version.ONE;
    }

    public URI getHref() {
        return href;
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean hasError() {
        return error.isPresent();
    }

    public List<Query> getQueries() {
        return queries;
    }

    public boolean hasTemplate() {
        return template.isPresent();
    }

    public Optional<Link> findLink(Predicate<Link> predicate) {
        return ListOps.find(links, predicate);
    }

    public List<Link> findLinks(Predicate<Link> predicate) {
        return ListOps.filter(links, predicate);
    }

    public Optional<Item> findItem(Predicate<Item> predicate) {
        return ListOps.find(items, predicate);
    }

    public List<Item> findItems(Predicate<Item> predicate) {
        return ListOps.filter(items, predicate);
    }

    public Optional<Query> findQuery(Predicate<Query> predicate) {
        return ListOps.find(queries, predicate);
    }

    public List<Query> findQueries(Predicate<Query> predicate) {
        return ListOps.filter(queries, predicate);
    }

    public Optional<Item> getFirst() {
        return findItem(Predicates.<Item>alwaysTrue());
    }

    public Builder toBuilder() {
        Builder builder = builder(getHref());
        builder.addItems(items);
        builder.addLinks(links);
        builder.addQueries(queries);
        builder.withTemplate(template.orNull());
        return builder;
    }

    public Template getTemplate() {
        return template.orNull();
    }

    public Error getError() {
        return error.orNull();
    }

    public void writeTo(OutputStream stream) throws IOException {
        writeTo(new OutputStreamWriter(stream, Charset.forName("UTF-8")));
    }

    public void writeTo(Writer writer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(writer, new CollectionGenerator().toNode(this));
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;

        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        if (href != null ? !href.equals(that.href) : that.href != null) return false;
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        if (links != null ? !links.equals(that.links) : that.links != null) return false;
        if (queries != null ? !queries.equals(that.queries) : that.queries != null) return false;
        if (template != null ? !template.equals(that.template) : that.template != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (queries != null ? queries.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    public static Builder builder(URI href) {
        return new Builder(href);
    }

    public static class Builder {
        private final URI href;
        private final List<Item> itemBuilder = new ArrayList<Item>();
        private final List<Link> linkBuilder = new ArrayList<Link>();
        private final List<Query> queryBuilder = new ArrayList<Query>();
        private Template template;
        private Error error;

        public Builder(URI href) {
            this.href = href;
        }
        
        public Builder withTemplate(Template template) {
            this.template = template;
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
            this.error = error;
            return this;
        }

        private <A> void addToList(Iterable<A> iterable, List<A> list) {
            for (A a : iterable) {
                list.add(a);
            }
        }

        public Collection build() {
            return new Collection(href, linkBuilder, itemBuilder, queryBuilder, template, error);
        }
    }
}
