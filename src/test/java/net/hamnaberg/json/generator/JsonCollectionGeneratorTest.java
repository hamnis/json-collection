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

package net.hamnaberg.json.generator;

import com.google.common.collect.ImmutableList;
import net.hamnaberg.json.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class JsonCollectionGeneratorTest {
    private static final URI COLLECTION_URI = URI.create("http://example.com/collection");

    private final ObjectMapper mapper = new ObjectMapper();
    private JsonCollectionGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new JsonCollectionGenerator();
    }

    @Test
    public void minimalCollection() throws Exception {
        JsonNode jsonNode = generator.toNode(new DefaultJsonCollection(COLLECTION_URI));
        assertNotNull(jsonNode);
        assertEquals("1.0", jsonNode.get("version").getValueAsText());
        assertEquals(COLLECTION_URI.toString(), jsonNode.get("href").getValueAsText());
    }

    @Test
    public void errorCollection() throws Exception {
        JsonNode jsonNode = generator.toNode(new ErrorJsonCollection(COLLECTION_URI, new ErrorMessage("Hello", "Warning", "Hello")));
        assertNotNull(jsonNode);
        assertEquals("1.0", jsonNode.get("version").getValueAsText());
        assertEquals(COLLECTION_URI.toString(), jsonNode.get("href").getValueAsText());
        JsonNode errorNode = jsonNode.get("error");
        ObjectNode node = mapper.createObjectNode();
        node.put("title", "Hello");
        node.put("code", "Warning");
        node.put("message", "Hello");
        assertEquals(node, errorNode);
    }

    @Test
    public void itemsCollection() throws Exception {
        ImmutableList.Builder<Item> items = ImmutableList.builder();
        items.add(new Item(COLLECTION_URI.resolve("item/1"), ImmutableList.<Property>of(new Property("one", ValueFactory.createValue(1), "One")), ImmutableList.<Link>of()));
        JsonNode jsonNode = generator.toNode(new DefaultJsonCollection(COLLECTION_URI, Version.ONE, ImmutableList.<Link>of(), items.build(), ImmutableList.<Query>of(), null));
        assertNotNull(jsonNode);
        assertEquals("1.0", jsonNode.get("version").getValueAsText());
        assertEquals(COLLECTION_URI.toString(), jsonNode.get("href").getValueAsText());
        assertEquals(createItems(), jsonNode.get("items"));
    }

    private ArrayNode createItems() {
        ArrayNode array = mapper.createArrayNode();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("href", COLLECTION_URI.resolve("item/1").toString());
        ArrayNode properties = mapper.createArrayNode();
        ObjectNode property = mapper.createObjectNode();
        property.put("name", "one");
        property.put("value", 1.0);
        property.put("prompt", "One");
        properties.add(property);
        objectNode.put("data", properties);
        array.add(objectNode);
        return array;
    }
}
