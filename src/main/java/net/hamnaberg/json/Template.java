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



import net.hamnaberg.json.util.Charsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.json.util.Iterables;

import java.io.*;
import java.net.URI;
import java.util.Collections;

public final class Template extends DataContainer<Template> implements Writable {
    Template(ObjectNode delegate) {
        super(delegate);
    }

    @Override
    protected Template copy(ObjectNode value) {
        return new Template(value);
    }

    public static Template create() {
        return create(Collections.<Property>emptyList());
    }

    public static Template create(Iterable<Property> data) {
        ObjectNode obj = JsonNodeFactory.instance.objectNode();
        if (!Iterables.isEmpty(data)) {
            obj.put("data", Property.toArrayNode(data));
        }
        return new Template(obj);
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
        ObjectMapper factory = new ObjectMapper();
        ObjectNode template = JsonNodeFactory.instance.objectNode();
        template.put("template", asJson());
        factory.writeValue(writer, template);
    }

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            writeTo(writer);
        } catch (IOException ignore) {
        }
        return writer.toString();
    }

    public void validate() {

    }
}
