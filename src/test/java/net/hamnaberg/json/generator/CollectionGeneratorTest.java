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

import net.hamnaberg.json.*;
import net.hamnaberg.json.Error;
import net.hamnaberg.json.parser.CollectionParser;
import net.hamnaberg.funclite.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CollectionGeneratorTest {
    private static final URI COLLECTION_URI = URI.create("http://example.com/collection");

    private final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    @Test
    public void minimalCollection() throws Exception {
        JsonNode collection = Collection.builder(COLLECTION_URI).build().asJson();
        assertNotNull(collection);
        assertEquals("1.0", collection.get("version").asText());
        assertEquals(COLLECTION_URI.toString(), collection.get("href").asText());
    }

    @Test
    public void errorCollection() throws Exception {
        JsonNode collection = new Collection.Builder(COLLECTION_URI).withError(Error.create("Hello", "Warning", "Hello")).build().asJson();
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
        items.add(Item.create(COLLECTION_URI.resolve("item/1"), CollectionOps.<Property>of(Property.value("one", Optional.some("One"), ValueFactory.createOptionalValue(1))), Collections.<Link>emptyList()));
        JsonNode collection = Collection.builder(COLLECTION_URI).addItems(items).build().asJson();
        assertNotNull(collection);
        assertEquals("1.0", collection.get("version").asText());
        assertEquals(COLLECTION_URI.toString(), collection.get("href").asText());
        assertEquals(createItems(), collection.get("items"));
    }

    @Test
    public void templateCollection() throws Exception {
        JsonNode collection = new Collection.Builder(
                COLLECTION_URI).withTemplate(
                Template.create(CollectionOps.<Property>of(Property.value("one", Optional.some("One"), Optional.<Value>none())))
        ).build().asJson();

        assertNotNull(collection);
        assertEquals("1.0", collection.get("version").asText());
        assertEquals(COLLECTION_URI.toString(), collection.get("href").asText());
        assertEquals(createTemplate(), collection.get("template"));
    }

    @Test
    public void canParseGeneratedTemplate() throws Exception {
        Template template = Template.create(CollectionOps.<Property>of(Property.value("one", Optional.some("One"), Optional.<Value>none())));
        StringWriter writer = new StringWriter();
        template.writeTo(writer);
        Template parsed = new CollectionParser().parseTemplate(writer.toString());
        assertEquals(template, parsed);
    }

    @Test
    public void canParseGeneratedCollection() throws Exception {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.create(COLLECTION_URI.resolve("item/1"), CollectionOps.<Property>of(Property.value("one", Optional.some("One"), ValueFactory.createOptionalValue(1))), Collections.<Link>emptyList()));

        Collection collection = Collection.builder(COLLECTION_URI).addItems(items).build();
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
        property.put("prompt", "One");
        property.put("value", new BigDecimal(1));
        properties.add(property);
        objectNode.put("data", properties);
        array.add(objectNode);
        return array;
    }
}
