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

package net.hamnaberg.json.generator;

import com.google.common.base.Optional;
import net.hamnaberg.json.*;
import net.hamnaberg.json.Error;
import net.hamnaberg.json.parser.CollectionParser;
import net.hamnaberg.json.util.ListOps;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CollectionGeneratorTest {
    private static final URI COLLECTION_URI = URI.create("http://example.com/collection");

    private CollectionGenerator generator;
    private final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    @Before
    public void setUp() throws Exception {
        generator = new CollectionGenerator();
    }

    @Test
    public void minimalCollection() throws Exception {
        JsonNode jsonNode = generator.toNode(new Collection(COLLECTION_URI));
        assertNotNull(jsonNode);
        JsonNode collection = jsonNode.get("collection");
        assertEquals("1.0", collection.get("version").asText());
        assertEquals(COLLECTION_URI.toString(), collection.get("href").asText());
    }

    @Test
    public void errorCollection() throws Exception {
        JsonNode jsonNode = generator.toNode(new Collection.Builder(COLLECTION_URI).withError(new Error("Hello", "Warning", "Hello")).build());
        assertNotNull(jsonNode);
        JsonNode collection = jsonNode.get("collection");
        assertNotNull(collection);

        assertEquals("1.0", collection.get("version").asText());
        assertEquals(COLLECTION_URI.toString(), collection.get("href").asText());
        JsonNode errorNode = collection.get("error");
        ObjectNode node = nodeFactory.objectNode();
        node.put("title", "Hello");
        node.put("code", "Warning");
        node.put("message", "Hello");
        assertEquals(node, errorNode);
    }

    @Test
    public void itemsCollection() throws Exception {
        List<Item> items = new ArrayList<Item>();
        items.add(new Item(COLLECTION_URI.resolve("item/1"), ListOps.<Property>of(Property.value("one", Optional.of("One"), ValueFactory.createValue(1))), Collections.<Link>emptyList()));
        JsonNode jsonNode = generator.toNode(new Collection(COLLECTION_URI, items));
        assertNotNull(jsonNode);
        JsonNode collection = jsonNode.get("collection");
        assertNotNull(collection);
        assertEquals("1.0", collection.get("version").asText());
        assertEquals(COLLECTION_URI.toString(), collection.get("href").asText());
        assertEquals(createItems(), collection.get("items"));
    }

    @Test
    public void templateCollection() throws Exception {
        JsonNode jsonNode = generator.toNode(new Collection.Builder(
                COLLECTION_URI).withTemplate(
                new Template(ListOps.<Property>of(Property.value("one", Optional.of("One"), Optional.<Value>absent())))
        ).build()
        );
        assertNotNull(jsonNode);
        JsonNode collection = jsonNode.get("collection");
        assertNotNull(collection);
        assertEquals("1.0", collection.get("version").asText());
        assertEquals(COLLECTION_URI.toString(), collection.get("href").asText());
        assertEquals(createTemplate(), collection.get("template"));
    }

    @Test
    public void canParseGeneratedTemplate() throws Exception {
        Template template = new Template(ListOps.<Property>of(Property.value("one", Optional.of("One"), Optional.<Value>absent())));
        StringWriter writer = new StringWriter();
        template.writeTo(writer);
        Template parsed = new CollectionParser().parseTemplate(writer.toString());
        assertEquals(template, parsed);
    }

    @Test
    public void canParseGeneratedCollection() throws Exception {
        List<Item> items = new ArrayList<Item>();
        items.add(new Item(COLLECTION_URI.resolve("item/1"), ListOps.<Property>of(Property.value("one", Optional.of("One"), ValueFactory.createValue(1))), Collections.<Link>emptyList()));

        Collection collection = new Collection(COLLECTION_URI, items);
        String generated = collection.toString();
        Collection parsed = new CollectionParser().parse(generated);
        assertEquals(collection.toString(), parsed.toString());
    }

    private ObjectNode createTemplate() {
        ArrayNode arrayNode = nodeFactory.arrayNode();
        ObjectNode property = nodeFactory.objectNode();
        property.put("name", "one");
        property.put("prompt", "One");
        arrayNode.add(property);
        ObjectNode template = nodeFactory.objectNode();
        template.put("data", arrayNode);
        return template;
    }

    private ArrayNode createItems() {
        ArrayNode array = nodeFactory.arrayNode();
        ObjectNode objectNode = nodeFactory.objectNode();
        objectNode.put("href", COLLECTION_URI.resolve("item/1").toString());
        ArrayNode properties = nodeFactory.arrayNode();
        ObjectNode property = nodeFactory.objectNode();
        property.put("name", "one");
        property.put("value", 1.0);
        property.put("prompt", "One");
        properties.add(property);
        objectNode.put("data", properties);
        array.add(objectNode);
        return array;
    }
}
