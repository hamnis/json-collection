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

import java.util.Optional;

class FromJsonValue {
    public static Value createValue(Json.JValue node) {
        return node.fold(
                j -> Value.of(j.getValue()),
                j -> Value.of(j.isValue()),
                j -> Value.of(j.getValue()),
                j -> {
                    throw new IllegalArgumentException("Illegal value " + node);
                },
                j -> {
                    throw new IllegalArgumentException("Illegal value " + node);
                },
                () -> Value.NULL
        );
    }
    public static Optional<Value> createOptionalValue(Json.JValue value) {
        Value v = createValue(value);
        if (v == Value.NULL) {
            return Optional.empty();
        }
        return Optional.of(v);
    }
}
