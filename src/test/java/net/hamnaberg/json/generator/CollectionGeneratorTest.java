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

import net.hamnaberg.json.Collection;
import net.hamnaberg.json.Error;
import net.hamnaberg.json.*;
import net.hamnaberg.json.parser.CollectionParser;
import org.junit.Test;

import java.io.StringWriter;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CollectionGeneratorTest {
    private static final URI COLLECTION_URI = URI.create("http://example.com/collection");

    @Test
    public void minimalCollection() throws Exception {
        Json.JObject collection = Collection.builder(COLLECTION_URI).build().asJson();
        assertNotNull(collection);
        assertEquals("1.0", collection.getAsString("version").get());
        assertEquals(COLLECTION_URI.toString(), collection.getAsString("href").get());
    }

    @Test
    public void errorCollection() throws Exception {
        Json.JObject collection = new Collection.Builder(COLLECTION_URI).withError(Error.create("Hello", "Warning", "Hello")).build().asJson();
        assertNotNull(collection);

        assertEquals("1.0", collection.getAsString("version").get());
        assertEquals(COLLECTION_URI.toString(), collection.getAsString("href").get());
        Json.JObject errorNode = collection.getAsObjectOrEmpty("error");
        Json.JObject node = Json.jObject(
                Json.entry("title", Json.jString("Hello")),
                Json.entry("code", Json.jString("Warning")),
                Json.entry("message", Json.jString("Hello"))
        );
        assertEquals(node, errorNode);
    }

    @Test
    public void itemsCollection() throws Exception {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.create(COLLECTION_URI.resolve("item/1"), Arrays.asList(Property.value("one", Optional.of("One"), Value.of(1))), Collections.<Link>emptyList()));
        Json.JObject collection = Collection.builder(COLLECTION_URI).addItems(items).build().asJson();
        assertNotNull(collection);
        assertEquals("1.0", collection.getAsString("version").get());
        assertEquals(COLLECTION_URI.toString(), collection.getAsString("href").get());
        assertEquals(createItems(), collection.getAsArrayOrEmpty("items"));
    }

    @Test
    public void templateCollection() throws Exception {
        Json.JObject collection = new Collection.Builder(
                COLLECTION_URI).withTemplate(
                Template.create(Arrays.asList(Property.value("one", Optional.of("One"), Optional.<Value>empty())))
        ).build().asJson();

        assertNotNull(collection);
        assertEquals("1.0", collection.getAsString("version").get());
        assertEquals(COLLECTION_URI.toString(), collection.getAsString("href").get());
        assertEquals(createTemplate(), collection.getAsObjectOrEmpty("template"));
    }

    @Test
    public void canParseGeneratedTemplate() throws Exception {
        Template template = Template.create(Arrays.asList(Property.value("one", Optional.of("One"), Optional.<Value>empty())));
        StringWriter writer = new StringWriter();
        template.writeTo(writer);
        Template parsed = new CollectionParser().parseTemplate(writer.toString());
        assertEquals(template, parsed);
    }

    @Test
    public void canParseGeneratedCollection() throws Exception {
        List<Item> items = new ArrayList<Item>();
        items.add(Item.create(COLLECTION_URI.resolve("item/1"), Arrays.asList(Property.value("one", Optional.of("One"), Value.of(1))), Collections.<Link>emptyList()));

        Collection collection = Collection.builder(COLLECTION_URI).addItems(items).build();
        String generated = collection.toString();
        Collection parsed = new CollectionParser().parse(generated);
        assertEquals(collection.toString(), parsed.toString());
    }

    private Json.JObject createTemplate() {
        return Json.jObject("data", Json.jArray(
                Json.jObject(
                        Json.entry("name", Json.jString("one")),
                        Json.entry("prompt", Json.jString("One"))
                )
        ));
    }

    private Json.JArray createItems() {
        return Json.jArray(
            Json.jObject(
                    Json.entry("href", Json.jString(COLLECTION_URI.resolve("item/1").toString())),
                    Json.entry("data", Json.jArray(
                            Property.value("one", Optional.of("One"), Value.of(1)).asJson()
                    ))
            )
        );
    }
}
