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
import net.hamnaberg.json.util.MapOps;
import net.hamnaberg.json.util.Optional;
import net.hamnaberg.json.util.Predicate;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URI;
import java.util.*;

public final class Item extends Extended<Item> implements WithHref {

    Item(ObjectNode node) {
        super(node);
    }

    public static Item create(URI href, List<Property> properties, List<Link> links) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("href", href.toString());
        if (!properties.isEmpty()) {
           ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            for (Property property : properties) {
                arr.add(property.asJson());
            }
            node.put("data", arr);
        }
        if (!links.isEmpty()) {
            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            for (Link link : links) {
                arr.add(link.asJson());
            }
            node.put("links", arr);
        }
        return new Item(node);
    }

    public URI getHref() {
        return URI.create(delegate.get("href").asText());
    }

    public List<Property> getData() {
        return delegate.has("data") ? Property.fromData(delegate.get("data")) : Collections.<Property>emptyList();
    }

    public Map<String, Property> getDataAsMap() {
        Map<String, Property> builder = MapOps.newHashMap();
        for (Property property : getData()) {
            builder.put(property.getName(), property);
        }
        return Collections.unmodifiableMap(builder);
    }

    public List<Link> getLinks() {
        return delegate.has("links") ? Link.fromArray(delegate.get("links")) : Collections.<Link>emptyList();
    }

    public Template toTemplate() {
        return Template.create(getData());
    }

    public Template toTemplate(Template input) {
        Map<String, Property> dataFromTemplate = input.getDataAsMap();
        Map<String, Property> ourData = getDataAsMap();
        Set<String> propsNotInOur = new HashSet<String>();
        List<Property> templateProps = new ArrayList<Property>();
        for (String key : dataFromTemplate.keySet()) {
            Property property = ourData.get(key);
            if (property != null) {
                templateProps.add(property);
            }
            else {
                propsNotInOur.add(key);
            }
        }
        if (propsNotInOur.isEmpty()) {
            return Template.create(templateProps);
        }
        else {
            throw new IllegalArgumentException("There are some properties that cannot be found in the template:\n" + propsNotInOur);
        }

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

    public Optional<Property> propertyByName(final String name) {
        return findProperty(new Predicate<Property>() {
            @Override
            public boolean apply(Property input) {
                return name.equals(input.getName());
            }
        });
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
        List<Item> builder = ListOps.newArrayList();
        for (JsonNode jsonNode : queries) {
            builder.add(new Item((ObjectNode) jsonNode));
        }
        return Collections.unmodifiableList(builder);
    }

    public void validate() {

    }
}
