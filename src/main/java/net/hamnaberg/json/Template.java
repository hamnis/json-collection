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

import net.hamnaberg.json.io.JacksonStreamingSerializer;
import net.hamnaberg.json.util.Charsets;

import java.io.*;
import java.net.URI;
import java.util.Collections;

public final class Template extends DataContainer<Template> implements Writable {
    Template(Json.JObject delegate) {
        super(delegate);
    }

    @Override
    protected Template copy(Json.JObject value) {
        return new Template(value);
    }

    public static Template create() {
        return create(Collections.<Property>emptyList());
    }

    public static Template create(Iterable<Property> data) {
        return new Template(Json.jObject("data", Property.toArrayNode(data)));
    }


    public Collection toCollection(URI href) {
        return Collection.builder(href).withTemplate(this).build();
    }

    /*
     * Writes to the supplied outputstream.
     * Note: Does NOT close the stream.
     */
    public void writeTo(OutputStream stream) throws IOException {
        writeTo(new OutputStreamWriter(stream, Charsets.UTF_8));
    }

    /*
     * Writes to the supplied Writer.
     * Note: Does NOT close the writer.
     */
    public void writeTo(Writer writer) throws IOException {
        new JacksonStreamingSerializer().write(Json.jObject("template", asJson()), writer);
    }

    @Override
    public String toString() {
        return new JacksonStreamingSerializer().writeToString(Json.jObject("template", asJson()));
    }

    public void validate() {

    }
}
