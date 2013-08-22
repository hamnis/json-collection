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

import net.hamnaberg.json.util.ListOps;
import net.hamnaberg.json.util.Optional;
import net.hamnaberg.json.util.Predicate;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URI;
import java.util.*;

public final class Item extends DataContainer<Item> implements WithHref {

    Item(ObjectNode node) {
        super(node);
    }

    public static Item create(URI href, Iterable<Property> properties, List<Link> links) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("href", href.toString());
        if (!ListOps.isEmpty(properties)) {
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

    public static Item create(URI href, Iterable<Property> properties) {
        return create(href, properties, Collections.<Link>emptyList());
    }

    public static Item create(URI href) {
        return create(href, Collections.<Property>emptyList(), Collections.<Link>emptyList());
    }

    public URI getHref() {
        return URI.create(delegate.get("href").asText());
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

    public Optional<Link> linkByRel(final String rel) {
        return findLink(new Predicate<Link>() {
            @Override
            public boolean apply(Link input) {
                return rel.equals(input.getRel());
            }
        });
    }

    public Optional<Link> linkByName(final String name) {
        return findLink(new Predicate<Link>() {
            @Override
            public boolean apply(Link input) {
                return Optional.fromNullable(name).equals(input.getName());
            }
        });
    }

    public Optional<Link> linkByRelAndName(final String rel, final String name) {
        return findLink(new Predicate<Link>() {
            @Override
            public boolean apply(Link input) {
                return rel.equals(input.getRel()) && Optional.fromNullable(name).equals(input.getName());
            }
        });
    }

    public Optional<Link> findLink(Predicate<Link> predicate) {
        return ListOps.find(getLinks(), predicate);
    }

    public List<Link> findLinks(Predicate<Link> predicate) {
        return ListOps.filter(getLinks(), predicate);
    }

    @Override
    protected Item copy(ObjectNode value) {
        return new Item(value);
    }

    @Override
    public String toString() {
        return String.format("Item with href %s, properties %s and links %s", getHref(), getData(), getLinks());
    }

    public Collection toCollection() {
        return Collection.builder(getHref()).addItem(this).build();
    }

    public Builder toBuilder() {
        Builder builder = new Builder(getHref());
        return builder.addProperties(getData()).addLinks(getLinks());
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

    /**
     * Mutable not thread-safe builder.
     */
    public static class Builder {
        private final URI href;
        private List<Property> props = new ArrayList<Property>();
        private List<Link> links = new ArrayList<Link>();

        public Builder(URI href) {
            this.href = href;
        }

        public Builder addProperty(Property prop) {
            props.add(prop);
            return this;
        }

        public Builder addProperties(Iterable<Property> properties) {
            ListOps.addAll(this.props, properties);
            return this;
        }

        public Builder addLink(Link link) {
            links.add(link);
            return this;
        }

        public Builder addLinks(Iterable<Link> links) {
            ListOps.addAll(this.links, links);
            return this;
        }

        public Item build() {
            return Item.create(href, props, links);
        }
    }
}
