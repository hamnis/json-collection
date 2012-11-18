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


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import net.hamnaberg.json.generator.TemplateGenerator;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class Template {
    private final List<Property> properties = new ArrayList<Property>();

    public Template() {
        this(Collections.<Property>emptyList());
    }

    public Template(List<Property> properties) {
        if (properties != null) {
            this.properties.addAll(properties);
        }
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public ImmutableMap<String, Property> getPropertiesAsMap() {
        ImmutableMap.Builder<String, Property> builder = ImmutableMap.builder();
        for (Property property : properties) {
            builder.put(property.getName(), property);
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        if (properties != null ? !properties.equals(template.properties) : template.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
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
        template.put("template", new TemplateGenerator().toNode(this));
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
}
