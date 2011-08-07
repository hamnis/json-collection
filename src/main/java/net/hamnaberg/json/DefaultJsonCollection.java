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

public class DefaultJsonCollection extends AbstractJsonCollection {
    private final ImmutableList<Link> links;
    private final ImmutableList<Item> items;
    private final ImmutableList<Query> queries;
    private final Template template;

    public DefaultJsonCollection(URI href) {
        this(href, Version.ONE);
    }

    public DefaultJsonCollection(URI href, Version version) {
        this(href, version, ImmutableList.<Link>of(), ImmutableList.<Item>of(), ImmutableList.<Query>of(), null);
    }

    public DefaultJsonCollection(URI href, Version version, ImmutableList<Link> links, ImmutableList<Item> items, ImmutableList<Query> queries, Template template) {
        super(href, version);
        this.links = links;
        this.items = items;
        this.queries = queries;
        this.template = template;
    }

    @Override
    public ImmutableList<Link> getLinks() {
        return links;
    }

    @Override
    public ImmutableList<Item> getItems() {
        return items;
    }

    @Override
    public ImmutableList<Query> getQueries() {
        return queries;
    }

    public Item getFirst() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public ErrorMessage getError() {
        throw new UnsupportedOperationException("Incorrect Collection type.");
    }
}
