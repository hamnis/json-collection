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
import net.hamnaberg.json.util.ListOps;
import net.hamnaberg.json.util.Optional;
import net.hamnaberg.json.util.Predicate;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Collection extends Extended<Collection> implements Writable {
    Collection(ObjectNode value) {
        super(value);
    }

    @Override
    protected Collection copy(ObjectNode value) {
        return new Collection(value);
    }

    public static Collection create(URI href, List<Link> links, List<Item> items, List<Query> queries, Template template, Error error) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.put("version", Version.ONE.getIdentifier());
        obj.put("href", href.toString());
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
        if (template != null) {
            obj.put("template", template.asJson());
        }
        if (error != null) {
            obj.put("error", error.asJson());
        }
        Collection coll = new Collection(obj);
        coll.validate();
        return coll;
    }

    public Version getVersion() {
        return Version.ONE;
    }

    public URI getHref() {
        return delegate.has("href") ? URI.create(delegate.get("href").asText()) : null;
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

    public Template getTemplate() {
        return hasTemplate() ? new Template((ObjectNode) delegate.get("template")) : null;
    }

    public boolean hasError() {
        return delegate.has("error");
    }


    public Error getError() {
        return hasError() ? new Error((ObjectNode) delegate.get("error")) : null;
    }

    public Optional<Link> findLink(Predicate<Link> predicate) {
        return ListOps.find(getLinks(), predicate);
    }

    public List<Link> filterLinks(Predicate<Link> predicate) {
        return ListOps.filter(getLinks(), predicate);
    }

    public Optional<Item> findItem(Predicate<Item> predicate) {
        return ListOps.find(getItems(), predicate);
    }

    public List<Item> filterItems(Predicate<Item> predicate) {
        return ListOps.filter(getItems(), predicate);
    }

    public Optional<Query> findQuery(Predicate<Query> predicate) {
        return ListOps.find(getQueries(), predicate);
    }

    public List<Query> filterQueries(Predicate<Query> predicate) {
        return ListOps.filter(getQueries(), predicate);
    }

    public Optional<Item> getFirstItem() {
        return ListOps.headOption(getItems());
    }

    public Builder toBuilder() {
        Builder builder = builder(getHref());
        builder.addItems(getItems());
        builder.addLinks(getLinks());
        builder.addQueries(getQueries());
        builder.withTemplate(getTemplate());
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

        if (hasTemplate()){
            getTemplate().validate();
        }
        if (hasError()) {
            getError().validate();
        }
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
            return Collection.create(href, linkBuilder, itemBuilder, queryBuilder, template, error);
        }
    }
}
