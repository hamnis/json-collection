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
import com.google.common.base.Preconditions;
import org.codehaus.jackson.JsonNode;

public class ValueFactory {
    public static Optional<Value> createValue(JsonNode node) {
        if (node == null) {
            return Optional.absent();
        }
        else if (node.isNumber()) {
            return Optional.<Value>of(new ValueImpl(node.getDoubleValue()));
        }
        else if (node.isBoolean()) {
            return Optional.<Value>of(new ValueImpl(node.getBooleanValue()));
        }
        else if (node.isTextual()) {
            return Optional.<Value>of(new ValueImpl(node.getTextValue()));
        }
        else if (node.isNull()) {
            return Optional.absent();
        }
        throw new IllegalArgumentException("Illegal value " + node);
    }

    public static Optional<Value> createValue(Object value) {
        if (value == null) {
            return Optional.absent();
        }
        Preconditions.checkArgument(checkValue(value), "Illegal value %s", value);
        return Optional.<Value>of(new ValueImpl(value));
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
