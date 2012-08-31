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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class Property implements WithPrompt, Nameable {
    private final String name;
    private final Optional<Value> value;
    private final List<Value> array;
    private final Map<String, Value> object;
    private final Optional<String> prompt;

    public Property(String name, Optional<String> prompt, Optional<Value> value) {
        this.name = name;
        this.value = value;
        this.array = ImmutableList.of();
        this.object = ImmutableMap.of();
        this.prompt = prompt;
    }

    public Property(String name, Optional<String> prompt, List<Value> array) {
        this.name = name;
        this.value = Optional.absent();
        this.array = ImmutableList.copyOf(array);
        this.object = ImmutableMap.of();
        this.prompt = prompt;
    }

    public Property(String name, Optional<String> prompt, Map<String, Value> object) {
        this.name = name;
        this.value = Optional.absent();
        this.array = ImmutableList.of();
        this.object = ImmutableMap.copyOf(object);
        this.prompt = prompt;
    }

    public static Property value(String name, Optional<Value> value) {
        return new Property(name, Optional.<String>absent(), value);
    }

    public static Property array(String name, List<Value> array) {
        return new Property(name, Optional.<String>absent(), array);
    }

    public static Property object(String name, Map<String, Value> object) {
        return new Property(name, Optional.<String>absent(), object);
    }

    public String getName() {
        return name;
    }

    public Optional<Value> getValue() {
        return value;
    }

    public Optional<String> getPrompt() {
        return prompt;
    }

    public List<Value> getArray() {
        return array;
    }

    public Map<String, Value> getObject() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (array != null ? !array.equals(property.array) : property.array != null) return false;
        if (name != null ? !name.equals(property.name) : property.name != null) return false;
        if (object != null ? !object.equals(property.object) : property.object != null) return false;
        if (prompt != null ? !prompt.equals(property.prompt) : property.prompt != null) return false;
        if (value != null ? !value.equals(property.value) : property.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (array != null ? array.hashCode() : 0);
        result = 31 * result + (object != null ? object.hashCode() : 0);
        result = 31 * result + (prompt != null ? prompt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Property with name %s, value %s, array %s, object %s, prompt %s", name, value.orNull(), array, object, prompt);
    }
}
