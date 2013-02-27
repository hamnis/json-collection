package net.hamnaberg.json;

import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class URITargetTest {

    @Test
    public void construction() {
        URITarget target = new URITarget(URI.create("http://example.com/foo?per=loser&paal=loser&espen=winner"));
        String expected = "per=loser&paal=loser&espen=winner";
        assertEquals("Incorrect Query", expected, target.toURI().getQuery());
    }

    @Test
    public void expandIsCorrect() {
        URITarget target = new URITarget(URI.create("http://example.com/foo"));
        String expected = "per=loser&paal=loser&espen=winner";
        List<Property> properties = Arrays.asList(
                Property.value("per", ValueFactory.createValue("loser")),
                Property.value("paal", ValueFactory.createValue("loser")),
                Property.value("espen", ValueFactory.createValue("winner"))
        );

        URI expanded = target.expand(properties);
        assertEquals("Incorrect Query", expected, expanded.getQuery());
        assertEquals(URI.create("http://example.com/foo?" + expected), expanded);
    }
    @Test
    public void expandIsCorrectWithExistingUntouchedQuery() {
        URITarget target = new URITarget(URI.create("http://example.com/foo?baa=foo"));
        String expected = "baa=foo&per=loser&paal=loser&espen=winner";
        List<Property> properties = Arrays.asList(
                Property.value("per", ValueFactory.createValue("loser")),
                Property.value("paal", ValueFactory.createValue("loser")),
                Property.value("espen", ValueFactory.createValue("winner"))
        );

        URI expanded = target.expand(properties);
        assertEquals("Incorrect Query", expected, expanded.getQuery());
        assertEquals(URI.create("http://example.com/foo?" + expected), expanded);
    }

    @Test
    public void expandIsCorrectWithArrays() {
        URITarget target = new URITarget(URI.create("http://example.com/foo"));
        String expected = "per=loser&per=big+brother&paal=loser&paal=second+big+brother&espen=winner";
        List<Property> properties = Arrays.asList(
                Property.array("per", Arrays.asList(ValueFactory.createValue("loser").get(), ValueFactory.createValue("big brother").get())),
                Property.array("paal", Arrays.asList(ValueFactory.createValue("loser").get(), ValueFactory.createValue("second big brother").get())),
                Property.value("espen", ValueFactory.createValue("winner").get())
        );

        URI expanded = target.expand(properties);
        assertEquals("Incorrect Query", expected, expanded.getQuery());
        assertEquals(URI.create("http://example.com/foo?" + expected), expanded);
    }
}
