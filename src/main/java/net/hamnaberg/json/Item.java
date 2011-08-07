/*
 * Copyright 2011 Erlend Hamnaberg
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

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class Item implements WithHref {
    private URI href;
    private ImmutableList<Property> properties;
    private final ImmutableList<Link> links;

    public Item(URI href, ImmutableList<Property> properties, ImmutableList<Link> links) {
        this.href = href;
        this.properties = properties;
        this.links = links;
    }

    public URI getHref() {
        return href;
    }

    public ImmutableList<Property> getProperties() {
        return properties;
    }

    public ImmutableList<Link> getLinks() {
        return links;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (href != null ? !href.equals(item.href) : item.href != null) return false;
        if (links != null ? !links.equals(item.links) : item.links != null) return false;
        if (properties != null ? !properties.equals(item.properties) : item.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Item with href %s, properties %s and links %s", href, properties, links);
    }
}
