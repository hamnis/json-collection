package net.hamnaberg.json;

import net.hamnaberg.json.parser.JsonCollectionParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class JsonCollectionTest {
    private JsonCollectionParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new JsonCollectionParser();
    }

    @Test
    public void parseSingleItemCollection() throws IOException {
        JsonCollection collection = parser.parse(new InputStreamReader(getClass().getResourceAsStream("/item.json")));
        Assert.assertNotNull(collection);
        Assert.assertEquals(URI.create("http://example.org/friends/"), collection.getHref());
    }
}
