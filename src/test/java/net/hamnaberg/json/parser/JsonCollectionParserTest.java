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

package net.hamnaberg.json.parser;

import com.google.common.base.Optional;
import net.hamnaberg.json.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import static org.junit.Assert.*;

public class JsonCollectionParserTest {
    private JsonCollectionParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new JsonCollectionParser();
    }

    @Test
    public void parseMinimal() throws IOException {
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/minimal.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        Assert.assertEquals(Version.ONE, collection.getVersion());
        assertEquals(0, collection.getLinks().size());
    }

    @Test
    public void parseMinimalWithoutVersion() throws IOException {
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/minimal-without-version.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        assertEquals(Version.ONE, collection.getVersion());
        assertEquals(0, collection.getLinks().size());
    }

    @Test
    public void parseSingleItemCollection() throws IOException {
        DefaultJsonCollection collection = (DefaultJsonCollection) parser.parse(new InputStreamReader(getClass().getResourceAsStream("/item.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        assertEquals(3, collection.getLinks().size());
        assertEquals(1, collection.getItems().size());
        Optional<Item> item = collection.getFirst();
        assertTrue("Item was null", item.isPresent());
        assertEquals(URI.create("http://example.org/friends/jdoe"), item.get().getHref());
        assertEquals(new Property("full-name", ValueFactory.createValue("J. Doe"), "Full Name"), item.get().getProperties().get(0));
        assertEquals(2, item.get().getLinks().size());
    }

    @Test
    public void parseErrorCollection() throws IOException {
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/error.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        assertNotNull("Error was null", collection.getError());
    }

    @Test
    public void parseTemplateCollection() throws IOException {
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/template.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        assertNotNull("Template was null", collection.getTemplate());
    }

    @Test
    public void parseQueriesCollection() throws IOException {
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/queries.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        assertEquals(1, collection.getQueries().size());
        Query query = collection.getQueries().get(0);
        assertEquals("search", query.getProperties().get(0).getName());
    }
}
