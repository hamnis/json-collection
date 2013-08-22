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
import net.hamnaberg.json.util.Preconditions;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Query extends DataContainer<Query> {

    Query(ObjectNode delegate) {
        super(delegate);
    }

    public static Query create(URI target, String rel, Optional<String> prompt, Iterable<Property> data) {
        return create(new URITarget(target), rel, prompt, Optional.<String>none(), data);
    }

    public static Query create(Target target, String rel, Optional<String> prompt, Iterable<Property> data) {
        return create(target, rel, prompt, Optional.<String>none(), data);
    }

    public static Query create(Target target, String rel, Optional<String> prompt, Optional<String> name, Iterable<Property> data) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.put("href", target.toString());
        if (target.isURITemplate()) {
            obj.put("encoding", "uri-template");
        }
        obj.put("rel", rel);
        if (prompt.isSome()) {
            obj.put("prompt", prompt.get());
        }
        if (name.isSome()) {
            obj.put("name", name.get());
        }
        if (!ListOps.isEmpty(data)) {
            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            for (Property property : data) {
                arr.add(property.asJson());
            }
            obj.put("data", arr);
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
        return Optional.fromNullable(getAsString("name"));
    }

    @Override
    public String toString() {
        return String.format("Query with href %s, properties %s", getHref(), getData());
    }

    public List<String> getParsedRel() {
        return Arrays.asList(getRel().split("\\s"));
    }

    public Optional<String> getPrompt() {
        return Optional.fromNullable(getAsString("prompt"));
    }

    static List<Query> fromArray(JsonNode queries) {
        List<Query> builder = ListOps.newArrayList();
        for (JsonNode jsonNode : queries) {
            builder.add(new Query((ObjectNode) jsonNode));
        }
        return Collections.unmodifiableList(builder);
    }

    public void validate() {
        Preconditions.checkArgument(getHref() != null, "Href may not be null");
        Preconditions.checkArgument(getRel() != null, "Rel may not be null");
    }
}
