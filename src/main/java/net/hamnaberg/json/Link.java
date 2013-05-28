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
import net.hamnaberg.funclite.CollectionOps;
import net.hamnaberg.funclite.Optional;
import net.hamnaberg.funclite.Preconditions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.json.navigation.Navigator;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Link extends Extended<Link> {
    Link(ObjectNode delegate) {
        super(delegate);
    }

    @Override
    protected Link copy(ObjectNode value) {
        return new Link(value);
    }

    public static Link create(URI href, String rel) {
        return create(href, rel, Optional.<String>none(), Optional.<String>none(), Optional.<Render>none());
    }

    public static Link create(URI href, String rel, Optional<String> prompt) {
        return create(href, rel, prompt, Optional.<String>none(), Optional.<Render>none());
    }

    public static Link create(URI href, String rel, Optional<String> prompt, Optional<String> name) {
        return create(href, rel, prompt, name, Optional.<Render>none());
    }

    public static Link create(URI href, String rel, Optional<String> prompt, Optional<String> name, Optional<Render> render) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("href", Preconditions.checkNotNull(href, "Href may not be null").toString());
        node.put("rel", Preconditions.checkNotNull(rel, "Relation may not be null"));
        if (prompt.isSome()) {
            node.put("prompt", prompt.get());
        }
        if (render.isSome()) {
            node.put("render", render.get().getName());
        }
        if (name.isSome()) {
            node.put("name", name.get());
        }
        return new Link(node);
    }

    public URI getHref() {
        return delegate.has("href") ? URI.create(delegate.get("href").asText()) : null;
    }

    public Link withHref(URI href) {
        ObjectNode node = copyDelegate();
        node.put("href", href.toString());
        return copy(node);
    }

    public String getRel() {
        return delegate.get("rel").asText();
    }

    public Link withRel(String rel) {
        ObjectNode node = copyDelegate();
        node.put("rel", rel);
        return copy(node);
    }

    public List<String> getParsedRel() {
        return Arrays.asList(getRel().split("\\s"));
    }

    public Optional<String> getPrompt() {
        return Optional.fromNullable(getAsString("prompt"));
    }

    public Link withPrompt(String prompt) {
        ObjectNode node = copyDelegate();
        node.put("prompt", prompt);
        return copy(node);
    }

    public Optional<String> getName() {
        return Optional.fromNullable(getAsString("name"));
    }

    public Link withName(String name) {
        ObjectNode node = copyDelegate();
        node.put("name", name);
        return copy(node);
    }

    public Render getRender() {
        return delegate.has("render") ? Render.valueOf(delegate.get("render").asText()) : Render.Link;
    }

    public Link withRender(Render render) {
        ObjectNode node = copyDelegate();
        node.put("render", render.getName());
        return copy(node);
    }

    public Optional<Collection> follow(Navigator navigator) {
        return navigator.follow(getHref());
    }

    @Override
    public String toString() {
        return String.format("Link{href=%s,rel=%s,prompt=%s,name=%s,render=%s}", getHref(), getRel(), getPrompt(), getName(), getRender());
    }

    public void validate() {
        Preconditions.checkArgument(getHref() != null, "Href was null");
        Preconditions.checkArgument(getRel() != null, "Rel was null");
    }

    static List<Link> fromArray(JsonNode node) {
        List<Link> links = CollectionOps.newArrayList();
        for (JsonNode jsonNode : node) {
            links.add(new Link((ObjectNode) jsonNode));
        }
        return Collections.unmodifiableList(links);
    }
}
