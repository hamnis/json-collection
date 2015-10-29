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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.hamnaberg.json.extension.Extended;
import net.hamnaberg.json.util.Iterables;

public final class Query extends DataContainer<Query> {

    Query(Json.JObject delegate) {
        super(delegate);
    }

    public static Query create(URI target, String rel, Optional<String> prompt, Iterable<Property> data) {
        return create(new URITarget(target), rel, prompt, Optional.<String>empty(), data);
    }

    public static Query create(Target target, String rel, Optional<String> prompt, Iterable<Property> data) {
        return create(target, rel, prompt, Optional.<String>empty(), data);
    }

    public static Query create(Target target, String rel, Optional<String> prompt, Optional<String> name, Iterable<Property> data) {
        Map<String, Json.JValue> obj = new LinkedHashMap<>();
        obj.put("href", Json.jString(target.toString()));
        if (target.isURITemplate()) {
            obj.put("encoding", Json.jString("uri-template"));
        }
        obj.put("rel", Json.jString(rel));
        prompt.ifPresent(value -> obj.put("prompt", Json.jString(value)));
        name.ifPresent(value -> obj.put("name", Json.jString(value)));
        if (!Iterables.isEmpty(data)) {
            obj.put("data", Json.jArray(StreamSupport.stream(data.spliterator(), false)
                    .map(Extended::asJson)
                    .collect(Collectors.toList())));
        }
        return new Query(Json.jObject(obj));
    }

    public static Query create(Link link) {
        return create(new URITarget(link.getHref()), link.getRel(), link.getPrompt(), link.getName(), Collections.<Property>emptyList());
    }

    @Override
    protected Query copy(Json.JObject value) {
        return new Query(value);
    }

    public Target getHref() {
        String href = delegate.getAsString("href").orElse(null);
        if (delegate.containsKey("encoding") && "uri-template".equals(delegate.getAsString("encoding").orElse(null))) {
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

    static List<Query> fromArray(Json.JArray queries) {
        return Collections.unmodifiableList(
                queries.getListAsObjects()
                .stream()
                .map(Query::new)
                .collect(Collectors.toList())
        );
    }

    public void validate() {
        Optional.ofNullable(getHref()).orElseThrow(() -> new IllegalArgumentException("Href may not be null"));
        Optional.ofNullable(getRel()).orElseThrow(() -> new IllegalArgumentException("Rel may not be null"));
    }
}
