package net.hamnaberg.json;

import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class URITargetTest {

    @Test
    public void expandShouldKeepEncodingFromBaseURI() {
        URI href = URI.create("https://api.test.com/myapi/customer;issue_id=1337;location=LUND%2FB;enterprise=false");
        URITarget target = new URITarget(href);

        URI expanded = target.expand(new ArrayList<Property>());

        assertEquals("https://api.test.com/myapi/customer;issue_id=1337;location=LUND%2FB;enterprise=false", expanded.toString());
    }

    @Test
    public void expandShouldNotDoubleEncodeProperties() {
        URI href = URI.create("https://api.test.com/myapi");
        URITarget target = new URITarget(href);

        List<Property> properties = Arrays.asList(
                Property.value("first_name", Value.of("Humle/Dumle"))
        );

        URI expanded = target.expand(properties);

        assertEquals("https://api.test.com/myapi?first_name=Humle%2FDumle", expanded.toString());
    }

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
                Property.value("per", Value.of("loser")),
                Property.value("paal", Value.of("loser")),
                Property.value("espen", Value.of("winner"))
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
                Property.value("per", Value.of("loser")),
                Property.value("paal", Value.of("loser")),
                Property.value("espen", Value.of("winner"))
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
                Property.array("per", Arrays.asList(Value.of("loser"), Value.of("big brother"))),
                Property.array("paal", Arrays.asList(Value.of("loser"), Value.of("second big brother"))),
                Property.value("espen", Value.of("winner"))
        );

        URI expanded = target.expand(properties);
        assertEquals("Incorrect Query", expected, expanded.getQuery());
        assertEquals(URI.create("http://example.com/foo?" + expected), expanded);
    }
}
