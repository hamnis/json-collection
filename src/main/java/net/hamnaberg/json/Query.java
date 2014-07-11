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

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.json.util.Iterables;

public final class Query extends DataContainer<Query> {

    Query(ObjectNode delegate) {
        super(delegate);
    }

    public static Query create(URI target, String rel, Optional<String> prompt, Iterable<Property> data) {
        return create(new URITarget(target), rel, prompt, Optional.<String>empty(), data);
    }

    public static Query create(Target target, String rel, Optional<String> prompt, Iterable<Property> data) {
        return create(target, rel, prompt, Optional.<String>empty(), data);
    }

    public static Query create(Target target, String rel, Optional<String> prompt, Optional<String> name, Iterable<Property> data) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.put("href", target.toString());
        if (target.isURITemplate()) {
            obj.put("encoding", "uri-template");
        }
        obj.put("rel", rel);
        prompt.ifPresent(value -> obj.put("prompt", value));
        name.ifPresent(value -> obj.put("name", value));
        if (!Iterables.isEmpty(data)) {
            obj.put("data", StreamSupport.stream(data.spliterator(), false)
                                         .map(Extended::asJson)
                                         .collect(JsonNodeFactory.instance::arrayNode, ArrayNode::add, ArrayNode::addAll));
        }
        return new Query(obj);
    }

    public static Query create(Link link) {
        return create(new URITarget(link.getHref()), link.getRel(), link.getPrompt(), link.getName(), Collections.<Property>emptyList());
    }

    @Override
    protected Query copy(ObjectNode value) {
        return new Query(value);
    }

    public Target getHref() {
        String href = delegate.get("href").asText();
        if (delegate.has("encoding") && "uri-template".equals(delegate.get("encoding").asText())) {
            return new URITemplateTarget(href);
        }
        return new URITarget(href);
    }

    public URI expand(Iterable<Property> properties) {
        return getHref().expand(properties);
    }

    public URI expand() {
        return expand(getData());
    }

    public String getRel() {
        return getAsString("rel");
    }

    public Optional<String> getName() {
        return Optional.ofNullable(getAsString("name"));
    }

    @Override
    public String toString() {
        return String.format("Query with href %s, properties %s", getHref(), getData());
    }

    public List<String> getParsedRel() {
        return Arrays.asList(getRel().split("\\s"));
    }

    public Optional<String> getPrompt() {
        return Optional.ofNullable(getAsString("prompt"));
    }

    static List<Query> fromArray(JsonNode queries) {
        return Collections.unmodifiableList(StreamSupport.stream(queries.spliterator(), false)
                                                         .map(jsonNode -> new Query((ObjectNode) jsonNode))
                                                         .collect(Collectors.toList()));
    }

    public void validate() {
        Optional.ofNullable(getHref()).orElseThrow(() -> new IllegalArgumentException("Href may not be null"));
        Optional.ofNullable(getRel()).orElseThrow(() -> new IllegalArgumentException("Rel may not be null"));
    }
}
