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

import net.hamnaberg.json.util.Optional;
import net.hamnaberg.json.util.Preconditions;
import org.codehaus.jackson.JsonNode;

import java.math.BigDecimal;

public class ValueFactory {
    static Value createValue(JsonNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node may not be null");
        }
        else if (node.isNumber()) {
            return new ValueImpl(node.getDecimalValue());
        }
        else if (node.isBoolean()) {
            return new ValueImpl(node.getBooleanValue());
        }
        else if (node.isTextual()) {
            return new ValueImpl(node.getTextValue());
        }
        else if (node.isNull()) {
            return ValueImpl.NULL;
        }
        throw new IllegalArgumentException("Illegal value " + node);
    }

    public static Value createValue(Object value) {
        if (value == null) {
            return ValueImpl.NULL;
        }
        if (value instanceof Number && !(value instanceof BigDecimal)) {
            value = new BigDecimal(value.toString());
        }
        Preconditions.checkArgument(checkValue(value), "Illegal value %s", value);
        return new ValueImpl(value);
    }


    public static Optional<Value> createOptionalValue(Object value) {
        Value v = createValue(value);
        if (v.isNull()) {
            return Optional.none();
        }
        return Optional.some(v);
    }

    static Optional<Value> createOptionalValue(JsonNode value) {
        if (value == null) {
            return Optional.none();
        }
        Value v = createValue(value);
        if (v.isNull()) {
            return Optional.none();
        }
        return Optional.some(v);
    }

    private static boolean checkValue(Object value) {
        if (value instanceof String) {
            return true;
        }
        else if (value instanceof Boolean) {
            return true;
        }
        else if (value instanceof Number) {
            return true;
        }
        return false;
    }
}
