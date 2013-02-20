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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.hamnaberg.json.extension.Extended;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URI;
import java.util.List;

public final class Link extends Extended<Link> {
    Link(ObjectNode delegate) {
        super(delegate);
    }

    @Override
    protected Link copy(ObjectNode value) {
        return new Link(value);
    }

    public static Link of(URI href, String rel, Optional<String> prompt) {
        return of(href, rel, prompt, Optional.<Render>absent());
    }

    public static Link of(URI href, String rel, Optional<String> prompt, Optional<Render> render) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("href", Preconditions.checkNotNull(href, "Href may not be null").toString());
        node.put("rel", Preconditions.checkNotNull(rel, "Relation may not be null"));
        if (prompt.isPresent()) {
            node.put("prompt", prompt.get());
        }
        if (render.isPresent()) {
            node.put("render", render.get().getName());
        }
        return new Link(node);
    }

    public URI getHref() {
        return delegate.has("href") ? URI.create(delegate.get("href").asText()) : null;
    }

    public String getRel() {
        return delegate.get("rel").asText();
    }

    public Optional<String> getPrompt() {
        return delegate.has("prompt") ? Optional.of(delegate.get("prompt").asText()) : Optional.<String>absent();
    }

    public Render getRender() {
        return delegate.has("render") ? Render.valueOf(delegate.get("render").asText()) : Render.Link;
    }

    public void validate() {
        Preconditions.checkArgument(getHref() != null, "Href was null");
        Preconditions.checkArgument(getRel() != null, "Rel was null");
        Preconditions.checkArgument(getPrompt() != null, "Prompt was null");
        Preconditions.checkArgument(getRender() != null, "Render was null");
    }

    static List<Link> fromArray(JsonNode node) {
        ImmutableList.Builder<Link> links = ImmutableList.builder();
        for (JsonNode jsonNode : node) {
            links.add(new Link((ObjectNode) jsonNode));
        }
        return links.build();
    }
}
