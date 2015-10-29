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

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.StreamSupport.stream;

public final class Property extends Extended<Property> {
    Property(Json.JObject delegate) {
        super(delegate);
    }

    static Json.JArray toArrayNode(Iterable<Property> data) {
        return Json.jArray(stream(data.spliterator(), false)
                .map(Extended::asJson)
                .collect(Collectors.toList()));
    }

    public String getName() {
        return delegate.getAsString("name").orElse(null);
    }

    public Optional<Value> getValue() {
        return ValueFactory.createOptionalValue(delegate.get("value").orElse(Json.jNull()));
    }

    public Optional<String> getPrompt() {
        return delegate.getAsString("prompt");
    }

    public boolean hasValue() {
        return delegate.containsKey("value");
    }

    public boolean hasArray() {
        return delegate.containsKey("array");
    }

    public boolean hasObject() {
        return delegate.containsKey("object");
    }

    public List<Value> getArray() {
        return delegate.getAsArrayOrEmpty("array").mapToList(ValueFactory::createValue);
    }

    public Map<String, Value> getObject() {
        Json.JObject obj = delegate.getAsObjectOrEmpty("object");
        return unmodifiableMap(obj.entrySet().stream().
                map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), ValueFactory.createValue(e.getValue()))).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
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

    private Property withDataValue(String name, Json.JValue node, String first, String second) {
        Json.JObject updated = delegate.put(name, node);
        return copy(updated.remove(first).remove(second));
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

    @Override
    protected Property copy(Json.JObject value) {
        return new Property(value);
    }

    @Override
    public void validate() {
    }

    public <A> A fold(Function<Value, A> fValue, Function<List<Value>, A> fList, Function<Map<String, Value>, A> fObject, Supplier<A> fEmpty) {
        if (hasValue()) {
            return fValue.apply(getValue().orElse(Value.NULL));
        }
        if (hasArray()) {
            return fList.apply(getArray());
        }
        if (hasObject()) {
            return fObject.apply(getObject());
        }
        return fEmpty.get();
    }

    public static Property template(String name) {
        return value(name, Optional.empty(), Optional.empty());
    }

    public static Property template(String name, Optional<String> prompt) {
        return value(name, prompt, Optional.empty());
    }

    public static Property value(String name, Optional<String> prompt, Optional<Value> value) {
        Json.JObject node = makeObject(name, prompt);
        return new Property(value.map(val -> node.put("value", val.asJson())).orElse(node));
    }

    public static Property value(String name, Optional<String> prompt, Value value) {
        return value(name, prompt, Optional.of(value));
    }

    public static Property value(String name, Value value) {
        return value(name, Optional.ofNullable(value));
    }

    public static Property value(String name, Optional<Value> value) {
        return value(name, Optional.empty(), value);
    }

    public static Property array(String name, List<Value> list) {
        return array(name, Optional.empty(), list);
    }

    public static Property array(String name, Optional<String> prompt, List<Value> list) {
        Json.JObject node = makeObject(name, prompt);
        return new Property(node.put("array", toArray(list)));
    }

    private static Json.JArray toArray(List<Value> list) {
        return Json.jArray(list.stream().map(Value::asJson).collect(Collectors.toList()));
    }

    public static Property object(String name, Optional<String> prompt, Map<String, Value> object) {
        Json.JObject node = makeObject(name, prompt);
        return new Property(node.put("object", toObject(object)));
    }


    public static List<Property> fromData(Json.JArray data) {
        return unmodifiableList(data.getListAsObjects().stream()
                .map(Property::new)
                .collect(Collectors.toList()));
    }

    private static Json.JObject toObject(Map<String, Value> object) {
        Map<String, Json.JValue> map = new LinkedHashMap<>();
        object.forEach((k, v) -> map.put(k, v.asJson()));
        return Json.jObject(map);
    }

    private static Json.JObject makeObject(String name, Optional<String> prompt) {
        Map<String, Json.JValue> map = new LinkedHashMap<>();
        map.put("name", Json.jString(name));
        prompt.ifPresent(value -> map.put("prompt", Json.jString(value)));
        return Json.jObject(map);
    }
}
