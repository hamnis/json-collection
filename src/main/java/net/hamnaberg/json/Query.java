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


import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Query implements WithHref, WithPrompt {
    private final Link link;
    private final List<Property> properties = new ArrayList<Property>();

    public Query(Link link, List<Property> properties) {
        this.link = Preconditions.checkNotNull(link, "Null link was passed");
        this.properties.addAll(Preconditions.checkNotNull(properties, "Null properties was passed"));
    }

    public Query(URI uri, String rel, Optional<String> prompt, List<Property> properties) {
        this(new Link(uri, rel, prompt), properties);
    }

    public Query(Link link) {
        this(link, Collections.<Property>emptyList());
    }

    public Link getLink() {
        return link;
    }

    @Override
    public URI getHref() {
        return link.getHref();
    }

    @Override
    public Optional<String> getPrompt() {
        return link.getPrompt();
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        if (link != null ? !link.equals(query.link) : query.link != null) return false;
        if (properties != null ? !properties.equals(query.properties) : query.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = link != null ? link.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
