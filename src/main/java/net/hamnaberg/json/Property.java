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
import net.hamnaberg.json.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.util.*;

import static net.hamnaberg.json.util.StringUtils.*;

public final class Property extends Extended<Property> {
    Property(ObjectNode delegate) {
        super(delegate);
    }

    static ArrayNode toArrayNode(Iterable<Property> data) {
        ArrayNode arr = JsonNodeFactory.instance.arrayNode();
        for (Property property : data) {
            arr.add(property.asJson());
        }
        return arr;
    }

    public String getName() {
        return delegate.get("name").asText();
    }

    public Optional<Value> getValue() {
        return ValueFactory.createOptionalValue(delegate.get("value"));
    }

    public Optional<String> getPrompt() {
        return delegate.has("prompt") ? Optional.some(delegate.get("prompt").asText()) : Optional.<String>none();
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
        JsonNode array = delegate.get("array");
        List<Value> builder = ListOps.newArrayList();
        if (array != null && array.isArray()) {
            for (JsonNode n : array) {
                builder.add(ValueFactory.createValue(n));
            }
        }
        return Collections.unmodifiableList(builder);
    }

    public Map<String, Value> getObject() {
        Map<String, Value> builder = MapOps.newHashMap();
        JsonNode object = delegate.get("object");
        if (object != null && object.isObject()) {
            Iterator<Map.Entry<String,JsonNode>> fields = object.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                Value opt = ValueFactory.createValue(next.getValue());
                builder.put(next.getKey(), opt);
            }
        }
        return Collections.unmodifiableMap(builder);
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
        dlg.put(name, node);
        dlg.remove(Arrays.asList(toRemove));
        return copy(dlg);
    }

    public boolean isEmpty() {
        return getValue().isNone() && getArray().isEmpty() && getObject().isEmpty();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Property template with name " + getName();
        }
        return String.format("Property with name %s, value %s, array %s, object %s, prompt %s", getName(), getValue().orNull(), getArray(), getObject(), getPrompt());
    }

    public static Property template(String name) {
        return value(name, Optional.some(capitalize(name)), Value.NONE);
    }

    public static Property template(String name, Optional<String> prompt) {
        return value(name, prompt, Value.NONE);
    }

    public static Property value(String name, Optional<String> prompt, Optional<Value> value) {
        ObjectNode node = makeObject(name, prompt);
        if (value.isSome()) {
            node.put("value", value.get().asJson());
        }
        return new Property(node);
    }

    public static Property value(String name, Optional<String> prompt, Value value) {
        return value(name, prompt, Optional.some(value));
    }

    public static Property value(String name, Optional<String> prompt, Object value) {
        return value(name, prompt, ValueFactory.createOptionalValue(value));
    }

    public static Property value(String name, Value value) {
        return value(name,
                Optional.fromNullable(value)
        );
    }

    public static Property value(String name, Object value) {
        return value(name,
                ValueFactory.createOptionalValue(value)
        );
    }

    public static Property value(String name, Optional<Value> value) {
        return value(name,
                Optional.some(capitalize(name)),
                value
        );
    }

    public static Property array(String name, List<Value> list) {
        return array(name, Optional.some(capitalize(name)), list);
    }

    public static Property array(String name, Optional<String> prompt, List<Value> list) {
        ObjectNode node = makeObject(name, prompt);
        node.put("array", toArray(list));
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
        return arrayObject(name, Optional.some(capitalize(name)), list);
    }

    public static Property arrayObject(String name, Optional<String> prompt, List<Object> list) {
        return array(name, prompt, ListOps.flatMap(list, new Function<Object, Iterable<Value>>() {
            @Override
            public Iterable<Value> apply(Object input) {
                return ValueFactory.createOptionalValue(input);
            }
        }));
    }

    public static Property object(String name, Map<String, Value> object) {
        return object(name, Optional.some(capitalize(name)), object);
    }

    public static Property object(String name, Optional<String> prompt, Map<String, Value> object) {
        ObjectNode node = makeObject(name, prompt);
        node.put("object", toObject(object));
        return new Property(node);
    }

    private static ObjectNode toObject(Map<String, Value> object) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<String, Value> entry : object.entrySet()) {
            objectNode.put(entry.getKey(), entry.getValue().asJson());
        }
        return objectNode;
    }

    public static Property objectMap(String name, Map<String, Object> object) {
        return objectMap(name, Optional.some(capitalize(name)), object);
    }

    public static Property objectMap(String name, Optional<String> prompt, Map<String, Object> object) {
        return object(name, prompt, MapOps.mapValues(object, new Function<Object, Value>() {
            @Override
            public Value apply(Object input) {
                return ValueFactory.createValue(input);
            }
        }));
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
        if (prompt.isSome()) {
            node.put("prompt", prompt.get());
        }
        return node;
    }

    public static List<Property> fromData(JsonNode data) {
        List<Property> builder = ListOps.newArrayList();
        for (JsonNode jsonNode : data) {
            builder.add(new Property((ObjectNode) jsonNode));
        }
        return Collections.unmodifiableList(builder);
    }
}
