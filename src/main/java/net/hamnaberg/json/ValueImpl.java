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

public class ValueImpl implements Value {
    private final Object value;
    private final Class type;

    ValueImpl(Object value) {
        this.value = value;
        this.type = value == null ? null : value.getClass();
    }

    @Override
    public boolean isBoolean() {
        return !isNull() && Boolean.class == type;
    }

    @Override
    public boolean isString() {
        return !isNull() && String.class == type;
    }

    @Override
    public boolean isNumeric() {
        return !isNull() && Number.class.isAssignableFrom(type);
    }

    @Override
    public boolean isNull() {
        return value == null;
    }

    @Override
    public String asString() {
        if (isNull()) {
            return "null";
        }
        return value.toString();
    }

    @Override
    public boolean asBoolean() {
        if (!isBoolean()) {
            throw new IllegalStateException("Trying to get a boolean when its not; It is a " + getTypeName());
        }
        return (Boolean) value;
    }

    @Override
    public Number asNumber() {
        if (!isNumeric()) {
            throw new IllegalStateException("Trying to get a Number when its not; It is a " + getTypeName());
        }
        return (Number) value;
    }


    private String getTypeName() {
        return (type == null ? "Null" : type.getSimpleName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueImpl value1 = (ValueImpl) o;

        if (type != null ? !type.equals(value1.type) : value1.type != null) return false;
        if (value != null ? !value.equals(value1.value) : value1.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Value is %s of type %s", value, getTypeName());
    }
}
