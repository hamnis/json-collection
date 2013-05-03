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
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.net.URI;

public class Error extends Extended<Error> {
    public static final Error EMPTY = Error.create(null, null, null);

    Error(ObjectNode delegate) {
        super(delegate);
    }

    @Override
    protected Error copy(ObjectNode value) {
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
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        obj.put("title", title);
        obj.put("code", code);
        obj.put("message", message);
        return new Error(obj);
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
