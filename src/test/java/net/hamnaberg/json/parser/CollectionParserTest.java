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

package net.hamnaberg.json.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Item;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Query;
import net.hamnaberg.json.Template;
import net.hamnaberg.json.Value;
import net.hamnaberg.json.ValueFactory;
import net.hamnaberg.json.Version;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CollectionParserTest {
    private CollectionParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new CollectionParser();
    }

    @Test
    public void parseMinimal() throws IOException {
        Collection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/minimal.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref().orElse(null));
        Assert.assertEquals(Version.ONE, collection.getVersion());
        assertEquals(0, collection.getLinks().size());
    }

    @Test
    public void parseMinimalWithoutVersion() throws IOException {
        Collection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/minimal-without-version.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref().orElse(null));
        assertEquals(Version.ONE, collection.getVersion());
        assertEquals(0, collection.getLinks().size());
    }

    @Test
    public void parseSingleItemCollection() throws IOException {
        Collection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/item.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref().orElse(null));
        assertEquals(3, collection.getLinks().size());
        assertEquals(1, collection.getItems().size());
        Optional<Item> item = collection.getFirstItem();
        assertTrue("Item was null", item.isPresent());
        assertEquals(URI.create("http://example.org/friends/jdoe"), item.get().getHref().orElse(null));
        assertEquals(Property.value("full-name", Optional.of("Full Name"), Optional.of(Value.of("J. Doe"))), item.get().getData().get(0).get());
        assertEquals(2, item.get().getLinks().size());
    }

    @Test
    public void parseErrorCollection() throws IOException {
        Collection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/error.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref().orElse(null));
        assertNotNull("Error was null", collection.getError());
    }

    @Test
    public void parseTemplateCollection() throws IOException {
        Collection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/template.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref().orElse(null));
        assertNotNull("Template was null", collection.getTemplate());
    }

    @Test
    public void parseOnlyTemplate() throws IOException {
        Template template = parser.parseTemplate(new InputStreamReader(getClass().getResourceAsStream("/only-template.json")));
        assertNotNull("Template was null", template);
        Map<String,Property> properties = template.getDataAsMap();
        assertThat(properties.keySet(), CoreMatchers.hasItems("full-name", "email", "blog", "avatar"));
    }

    @Test
    public void parseQueriesCollection() throws IOException {
        Collection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/queries.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref().orElse(null));
        assertEquals(1, collection.getQueries().size());
        Query query = collection.getQueries().get(0);
        assertEquals("search", query.getData().get(0).get().getName());
    }

    @Test
    public void parseValuesExtension() throws IOException {
        Collection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/value-extension.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref().orElse(null));
        assertEquals(1, collection.getItems().size());
        Optional<Item> first = collection.getFirstItem();
        assertTrue(first.isPresent());
        Optional<Property> complex = first.flatMap(input -> input.findProperty(input1 -> "complex".equals(input1.getName())));
        assertTrue(complex.isPresent());
        assertFalse(complex.get().getValue().isPresent());
        Map<String,Value> object = complex.get().getObject();
        assertTrue(object.containsKey("foo"));
        assertEquals(Value.of("bar"), object.get("foo"));
    }
}
