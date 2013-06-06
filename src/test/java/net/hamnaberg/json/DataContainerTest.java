package net.hamnaberg.json;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DataContainerTest {
    @Test
    public void replaceFooPropertiesInTemplate() throws Exception {
        Template template = Template.create(Arrays.asList(Property.template("foo"), Property.template("bar")));
        Property replacedFooProperty = Property.value("foo", ValueFactory.createValue("Hello"));
        Template replaced = template.replace(replacedFooProperty);
        assertNotSame(template, replaced);
        assertEquals(replacedFooProperty, replaced.getDataAsMap().get("foo"));
    }
}
