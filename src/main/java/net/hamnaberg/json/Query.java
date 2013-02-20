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
import com.google.common.collect.ImmutableMap;
import net.hamnaberg.json.extension.Extended;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Query extends Extended<Query> {

    Query(ObjectNode delegate) {
        super(delegate);
    }

    public static Query create(Target target, String rel, Optional<String> prompt, List<Property> data) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.put("href", target.toString());
        obj.put("rel", rel);
        if (prompt.isPresent()) {
            obj.put("prompt", prompt.get());
        }
        if (!data.isEmpty()) {
            ArrayNode arr = JsonNodeFactory.instance.arrayNode();
            for (Property property : data) {
                arr.add(property.asJson());
            }
            obj.put("data", arr);
        }
        return new Query(obj);
    }

    public static Query create(Link link) {
        return create(new URITarget(link.getHref()), link.getRel(), link.getPrompt(), Collections.<Property>emptyList());
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

    public Optional<String> getPrompt() {
        return delegate.has("prompt") ? Optional.fromNullable(delegate.get("prompt").asText()) : Optional.<String>absent();
    }

    public List<Property> getData() {
        return delegate.has("data") ? Property.fromData(delegate.get("data")) : Collections.<Property>emptyList();
    }

    public ImmutableMap<String, Property> getDataAsMap() {
        ImmutableMap.Builder<String, Property> builder = ImmutableMap.builder();
        for (Property property : getData()) {
            builder.put(property.getName(), property);
        }
        return builder.build();
    }

    static List<Query> fromArray(JsonNode queries) {
        ImmutableList.Builder<Query> builder = ImmutableList.builder();
        for (JsonNode jsonNode : queries) {
            builder.add(new Query((ObjectNode) jsonNode));
        }
        return builder.build();
    }

    public void validate() {

    }
}
