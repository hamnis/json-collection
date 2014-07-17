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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.json.extension.Extended;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static net.hamnaberg.json.util.StringUtils.capitalize;

public final class Property extends Extended<Property> {
    Property(ObjectNode delegate) {
        super(delegate);
    }

    static ArrayNode toArrayNode(Iterable<Property> data) {
        return stream(data.spliterator(), false)
                .map(Extended::asJson)
                .collect(JsonNodeFactory.instance::arrayNode, ArrayNode::add, ArrayNode::addAll);
    }

    public String getName() {
        return delegate.get("name").asText();
    }

    public Optional<Value> getValue() {
        return ValueFactory.createOptionalValue(delegate.get("value"));
    }

    public Optional<String> getPrompt() {
        return delegate.has("prompt") ? Optional.of(delegate.get("prompt").asText()) : Optional.<String>empty();
    }

    public boolean hasValue() {
        return delegate.has("value");
    }

    public boolean hasArray() {
        return delegate.has("array");
    }

    public boolean hasObject() {
        return delegate.has("object");
    }

    public List<Value> getArray() {
        return Optional.ofNullable(delegate.get("array"))
                       .map(array -> array.isArray()
                                     ? unmodifiableList(stream(array.spliterator(), false)
                                                                .map(ValueFactory::createValue)
                                                                .collect(Collectors.toList()))
                                     : Collections.<Value>emptyList())
                       .orElse(Collections.<Value>emptyList());
    }

    public Map<String, Value> getObject() {
        return unmodifiableMap(Optional.ofNullable(delegate.get("object"))
                       .filter(JsonNode::isObject).map(object ->
                                stream(spliteratorUnknownSize(object.fields(), Spliterator.ORDERED), false)
                                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> ValueFactory.createValue(entry.getValue())))
                ).orElse(Collections.<String, Value>emptyMap()));
    }

    public Property withValue(Value value) {
        return withDataValue("value", value.asJson(), "array", "object");
    }

    public Property withArray(List<Value> values) {
        return withDataValue("array", toArray(values), "value", "object");
    }

    public Property withObject(Map<String, Value> values) {
        return withDataValue("object", toObject(values), "value", "array");
    }

    private Property withDataValue(String name, JsonNode node, String... toRemove) {
        ObjectNode dlg = copyDelegate();
        dlg.set(name, node);
        dlg.remove(Arrays.asList(toRemove));
        return copy(dlg);
    }

    public boolean isEmpty() {
        return !getValue().isPresent() && getArray().isEmpty() && getObject().isEmpty();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Property template with name " + getName();
        }
        return String.format("Property with name %s, value %s, array %s, object %s, prompt %s", getName(), getValue().orElse(null), getArray(), getObject(), getPrompt());
    }

    public static Property template(String name) {
        return value(name, Optional.of(capitalize(name)), Value.NONE);
    }

    public static Property template(String name, Optional<String> prompt) {
        return value(name, prompt, Value.NONE);
    }

    public static Property value(String name, Optional<String> prompt, Optional<Value> value) {
        ObjectNode node = makeObject(name, prompt);
        value.ifPresent(val -> node.set("value", val.asJson()));
        return new Property(node);
    }

    public static Property value(String name, Optional<String> prompt, Value value) {
        return value(name, prompt, Optional.of(value));
    }

    public static Property value(String name, Optional<String> prompt, Object value) {
        return value(name, prompt, ValueFactory.createOptionalValue(value));
    }

    public static Property value(String name, Value value) {
        return value(name,
                Optional.ofNullable(value)
        );
    }

    public static Property value(String name, Object value) {
        return value(name,
                ValueFactory.createOptionalValue(value)
        );
    }

    public static Property value(String name, Optional<Value> value) {
        return value(name,
                Optional.of(capitalize(name)),
                value
        );
    }

    public static Property array(String name, List<Value> list) {
        return array(name, Optional.of(capitalize(name)), list);
    }

    public static Property array(String name, Optional<String> prompt, List<Value> list) {
        ObjectNode node = makeObject(name, prompt);
        node.set("array", toArray(list));
        return new Property(node);
    }

    private static ArrayNode toArray(List<Value> list) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (Value value : list) {
            array.add(value.asJson());
        }
        return array;
    }

    public static Property arrayObject(String name, List<Object> list) {
        return arrayObject(name, Optional.of(capitalize(name)), list);
    }

    public static Property arrayObject(String name, Optional<String> prompt, List<Object> list) {
        return array(name, prompt, list.stream()
                                       .map(ValueFactory::createOptionalValue)
                                       .flatMap(optionalValue -> optionalValue.map(Stream::of).orElseGet(Stream::empty))
                                       .collect(Collectors.toList()));
    }

    public static Property object(String name, Map<String, Value> object) {
        return object(name, Optional.of(capitalize(name)), object);
    }

    public static Property object(String name, Optional<String> prompt, Map<String, Value> object) {
        ObjectNode node = makeObject(name, prompt);
        node.set("object", toObject(object));
        return new Property(node);
    }

    private static ObjectNode toObject(Map<String, Value> object) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<String, Value> entry : object.entrySet()) {
            objectNode.set(entry.getKey(), entry.getValue().asJson());
        }
        return objectNode;
    }

    public static Property objectMap(String name, Map<String, Object> object) {
        return objectMap(name, Optional.of(capitalize(name)), object);
    }

    public static Property objectMap(String name, Optional<String> prompt, Map<String, Object> object) {
        return object(name, prompt, object.entrySet()
                                          .stream()
                                          .collect(toMap(Map.Entry::getKey, entry -> ValueFactory.createValue(entry.getValue()))));
    }

    @Override
    protected Property copy(ObjectNode value) {
        return new Property(value);
    }

    @Override
    public void validate() {

    }

    private static ObjectNode makeObject(String name, Optional<String> prompt) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("name", name);
        prompt.ifPresent(value -> node.put("prompt", value));
        return node;
    }

    public static List<Property> fromData(JsonNode data) {
        return unmodifiableList(stream(data.spliterator(), false)
                                        .map(jsonNode -> new Property((ObjectNode) jsonNode))
                                        .collect(Collectors.toList()));
    }
}
