package net.hamnaberg.json;

import net.hamnaberg.json.parser.JsonCollectionParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonCollectionTest {
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
        assertEquals(Version.ONE, collection.getVersion());
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
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/item.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        assertEquals(3, collection.getLinks().size());
    }

    @Test
    public void parseErrorCollection() throws IOException {
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/error.json")));
        assertNotNull(collection);
        assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
        assertNotNull("Error was null", collection.getError());
    }
}
