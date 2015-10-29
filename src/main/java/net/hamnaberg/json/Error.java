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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import java.net.URI;

import static java.util.Optional.ofNullable;

public final class Error extends Extended<Error> {
    public static final Error EMPTY = Error.create(Optional.<String>empty(), Optional.<String>empty(), Optional.<String>empty());

    Error(Json.JObject delegate) {
        super(delegate);
    }

    @Override
    protected Error copy(Json.JObject value) {
        return new Error(value);
    }

    public String getTitle() {
        return getAsString("title");
    }

    public String getCode() {
        return getAsString("code");
    }

    public String getMessage() {
        return getAsString("message");
    }

    @Override
    public void validate() {
    }

    public Collection toCollection(URI href) {
        return Collection.builder(href).withError(this).build();
    }

    public static Error create(String title, String code, String message) {
        return create(ofNullable(title), ofNullable(code), ofNullable(message));
    }

    public static Error create(Optional<String> title, Optional<String> code, Optional<String> message) {
        final Map<String, Json.JValue> obj = new LinkedHashMap<>();
        title.ifPresent(value -> obj.put("title", Json.jString(value)));
        code.ifPresent(value -> obj.put("code", Json.jString(value)));
        message.ifPresent(value -> obj.put("message", Json.jString(value)));
        return new Error(Json.jObject(obj));
    }


    @Override
    public String toString() {
        return "Error{" +
                "title='" + getTitle() + '\'' +
                ", code='" + getCode() + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
