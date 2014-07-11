package net.hamnaberg.json;

import java.util.Optional;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class QueriesTest {

    @Test
    public void findCreatedQuery() throws Exception {
        Query query = Query.create(new URITemplateTarget("http://example.com{?q}"), "filter", Optional.<String>empty(), Collections.singletonList(Property.template("q")));
        Collection collection = Collection.builder().addQuery(query).build();
        Query filter = collection.queryByRel("filter").get();
        assertThat(URI.create("http://example.com"), equalTo(filter.expand()));
        assertThat(URI.create("http://example.com?q=faff"), equalTo(filter.expand(Collections.singletonList(Property.value("q", "faff")))));

    }
}
