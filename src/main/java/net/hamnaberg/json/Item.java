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

import javaslang.control.Option;
import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.json.util.Iterables;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static javaslang.control.Option.of;

public final class Item extends DataContainer<Item> {

    Item(Json.JObject node) {
        super(node);
    }

    public static Item create(URI href, Iterable<Property> properties) {
        return create(of(href), properties, Collections.<Link>emptyList());
    }

    public static Item create(URI href, Iterable<Property> properties, List<Link> links) {
        return create(of(href), properties, links);
    }

    public static Item create(Option<URI> href, Iterable<Property> properties, List<Link> links) {
        Map<String, Json.JValue> map = new LinkedHashMap<>();

        href.forEach(uri -> map.put("href", Json.jString(uri.toString())));
        if (!Iterables.isEmpty(properties)) {
            map.put("data", Json.jArray(StreamSupport.stream(properties.spliterator(), false)
                    .map(Extended::asJson)
                    .collect(Collectors.toList())));
        }
        if (!Iterables.isEmpty(links)) {
            map.put("links", Json.jArray(StreamSupport.stream(links.spliterator(), false)
                    .map(Extended::asJson)
                    .collect(Collectors.toList())));
        }
        return new Item(Json.jObject(map));
    }

    public static Item create(Option<URI> href, Iterable<Property> properties) {
        return create(href, properties, Collections.<Link>emptyList());
    }

    public static Item create(Option<URI> href) {
        return create(href, Collections.<Property>emptyList(), Collections.<Link>emptyList());
    }

    public static Item create() {
        return create(Option.none());
    }

    public Option<URI> getHref() {
        return delegate.getAsString("href").map(URI::create);
    }

    public List<Link> getLinks() {
        return Link.fromArray(delegate.getAsArrayOrEmpty("links"));
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

    public Option<Link> linkByRel(final String rel) {
        return findLink(input -> rel.equals(input.getRel()));
    }

    public Option<Link> linkByName(final String name) {
        return findLink(input -> of(name).equals(input.getName()));
    }

    public Option<Link> linkByRelAndName(final String rel, final String name) {
        return findLink(input -> rel.equals(input.getRel()) && of(name).equals(input.getName()));
    }

    public Option<Link> findLink(Predicate<Link> predicate) {
        return Option.ofOptional(getLinks().stream().filter(predicate).findFirst());
    }

    public List<Link> findLinks(Predicate<Link> predicate) {
        return getLinks().stream().filter(predicate).collect(Collectors.<Link>toList());
    }

    @Override
    protected Item copy(Json.JObject value) {
        return new Item(value);
    }

    @Override
    public String toString() {
        return String.format("Item with href %s, properties %s and links %s", getHref().getOrElse((URI)null), getData(), getLinks());
    }

    public Collection toCollection() {
        return new Collection.Builder(getHref()).addItem(this).build();
    }

    public Builder toBuilder() {
        Builder builder = new Builder(getHref());
        return builder.addProperties(getData()).addLinks(getLinks());
    }

    static List<Item> fromArray(Json.JArray items) {
        return Collections.unmodifiableList(
                items.getListAsObjects()
                        .map(Item::new)
                        .toJavaList()
        );
    }

    public void validate() {

    }

    public static Builder builder(URI href) {
        return new Builder(of(href));
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Mutable not thread-safe builder.
     */
    public static class Builder {
        private Option<URI> href;
        private List<Property> props = new ArrayList<Property>();
        private List<Link> links = new ArrayList<Link>();

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

        public Builder addProperty(Property prop) {
            props.add(prop);
            return this;
        }

        public Builder addProperties(Iterable<Property> properties) {
            properties.forEach(props::add);
            return this;
        }

        public Builder addLink(Link link) {
            links.add(link);
            return this;
        }

        public Builder addLinks(Iterable<Link> links) {
            links.forEach(this.links::add);
            return this;
        }

        public Item build() {
            return Item.create(href, props, links);
        }
    }
}
