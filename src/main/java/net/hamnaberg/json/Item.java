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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.json.util.ListOps;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public final class Item extends Extended<Item> implements WithHref {

    Item(ObjectNode node) {
        super(node);
    }

    public static Item create(URI href, List<Property> properties, List<Link> links) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("href", href.toString());
        if (!properties.isEmpty()) {
           ArrayNode data = JsonNodeFactory.instance.arrayNode();
            for (Property property : properties) {
                data.add(property.asJson());
            }
            node.put("data", data);
        }
        return new Item(node);
    }

    public URI getHref() {
        return URI.create(delegate.get("href").asText());
    }

    public List<Property> getData() {
        return delegate.has("data") ? Property.fromData(delegate.get("data")) : Collections.<Property>emptyList();
    }

    public ImmutableMap<String, Property> getDataAsMap() {
        ImmutableMap.Builder<String, Property> builder = ImmutableMap.builder();
        for (Property property : getData()) {
            builder.put(property.getName(), property);
        }
        return builder.build();
    }

    public List<Link> getLinks() {
        return delegate.has("links") ? Link.fromArray(delegate.get("links")) : Collections.<Link>emptyList();
    }

    public Template toTemplate() {
        return Template.create(getData());
    }

    public Optional<Link> findLink(Predicate<Link> predicate) {
        return ListOps.find(getLinks(), predicate);
    }

    public List<Link> findLinks(Predicate<Link> predicate) {
        return ListOps.filter(getLinks(), predicate);
    }

    public Optional<Property> findProperty(Predicate<Property> predicate) {
        return ListOps.find(getData(), predicate);
    }

    @Override
    protected Item copy(ObjectNode value) {
        return new Item(value);
    }

    @Override
    public String toString() {
        return String.format("Item with href %s, properties %s and links %s", getHref(), getData(), getLinks());
    }

    static List<Item> fromArray(JsonNode queries) {
        ImmutableList.Builder<Item> builder = ImmutableList.builder();
        for (JsonNode jsonNode : queries) {
            builder.add(new Item((ObjectNode) jsonNode));
        }
        return builder.build();
    }

    public void validate() {

    }
}
