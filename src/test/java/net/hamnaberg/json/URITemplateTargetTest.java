package net.hamnaberg.json;

import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class URITemplateTargetTest {

    @Test
    public void construction() {
        URITemplateTarget target = new URITemplateTarget("http://example.com/{foo}");
        String expected = "http://example.com/";
        assertEquals("Incorrect Query", expected, target.toURI().toString());
    }

    @Test
    public void expandIsCorrect() {
        URITemplateTarget target = new URITemplateTarget("http://example.com/{foo}");
        String expected = "http://example.com/124567";
        List<Property> properties = Arrays.asList(
                Property.value("foo", ValueFactory.createOptionalValue(124567))
        );

        URI expanded = target.expand(properties);
        assertEquals("Incorrect Query", expected, expanded.toString());
    }
}
