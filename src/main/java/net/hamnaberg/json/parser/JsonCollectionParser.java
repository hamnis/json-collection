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

import com.google.common.base.Preconditions;
import net.hamnaberg.json.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for a vnd.collection+json document.
 */
public class JsonCollectionParser {
    private Charset UTF_8 = Charset.forName("UTF-8");
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonCollection parse(Reader reader) throws IOException {
        try {
            return parse(mapper.readTree(reader));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Parses a JsonCollection from the given stream.
     * The stream is wrapped in a BufferedReader.
     *
     * The stream is expected to be UTF-8 encoded.
     * @param stream the stream
     * @return a jsonCollection
     * @throws IOException
     */
    public JsonCollection parse(InputStream stream) throws IOException {
        return parse(new BufferedReader(new InputStreamReader(stream, UTF_8)));
    }

    private JsonCollection parse(JsonNode node) throws IOException {
        JsonNode collectionNode = node.get("collection");
        return parseCollection(collectionNode);
    }

    private JsonCollection parseCollection(JsonNode collectionNode) {
        URI href = createURI(collectionNode);
        Version version = getVersion(collectionNode);
        Preconditions.checkArgument(version == Version.ONE, "Version was %s, may only be %s", version.getIdentifier(), Version.ONE.getIdentifier());
        ErrorMessage error = parseError(collectionNode);

        if (error != null) {
            return new ErrorJsonCollection(href, error);
        }

        List<Link> links = parseLinks(collectionNode);
        List<Item> items = parseItems(collectionNode);
        
        List<Query> queries = parseQueries(collectionNode);
        Template template = parseTemplate(collectionNode);

        return new DefaultJsonCollection(href, links, items, queries, template);
    }

    private ErrorMessage parseError(JsonNode collectionNode) {
        JsonNode errorNode = collectionNode.get("error");
        if (errorNode != null) {
            String title = getStringValue(errorNode.get("title"));
            String code = getStringValue(errorNode.get("code"));
            String message = getStringValue(errorNode.get("message"));
            if (isEmpty(title) && isEmpty(code) && isEmpty(message)) {
                return ErrorMessage.EMPTY;
            }
            return new ErrorMessage(title, code, message);
        }
        return null;
    }

    private boolean isEmpty(String title) {
        return title == null || title.trim().isEmpty();
    }

    private String getStringValue(JsonNode node) {
        return node == null ? null : node.getTextValue();
    }

    private List<Item> parseItems(JsonNode collectionNode) {
        List<Item> builder = new ArrayList<Item>();
        JsonNode items = collectionNode.get("items");
        if (items != null) {
            for (JsonNode node : items) {
                URI uri = createURI(node);
                builder.add(new Item(uri, parseData(node), parseLinks(node)));
            }
        }
        return builder;
    }

    private List<Query> parseQueries(JsonNode collectionNode) {
        List<Query> builder = new ArrayList<Query>();
        JsonNode queriesNode = collectionNode.get("queries");
        if (queriesNode != null) {
            for (JsonNode node : queriesNode) {
                Link link = toLink(node);
                List<Property> properties = parseData(node);
                builder.add(new Query(link, properties));
            }
        }
        return builder;
    }

    private Template parseTemplate(JsonNode collectionNode) {
        JsonNode node = collectionNode.get("template");
        if (node != null) {
            return new Template(parseData(node));
        }
        return null;
    }

    private List<Property> parseData(JsonNode node) {
        JsonNode data = node.get("data");
        List<Property> builder = new ArrayList<Property>();
        if (data != null) {
            for (JsonNode property : data) {
                builder.add(toProperty(property));
            }
        }
        return builder;
    }

    private Property toProperty(JsonNode node) {
        return new Property(getStringValue(node.get("name")), ValueFactory.createValue(node.get("value")), getStringValue(node.get("prompt")));
    }

    private URI createURI(JsonNode node) {
        return URI.create(node.get("href").getTextValue());
    }

    private Version getVersion(JsonNode collectionNode) {
        JsonNode version = collectionNode.get("version");
        return Version.getVersion(version != null ? version.getTextValue() : null);
    }

    private List<Link> parseLinks(JsonNode collectionNode) {
        JsonNode linkCollection = collectionNode.get("links");
        List<Link> linkBuilder = new ArrayList<Link>();
        if (linkCollection != null) {
            for (JsonNode linkNode : linkCollection) {
                linkBuilder.add(toLink(linkNode));
            }
        }
        return linkBuilder;
    }

    private Link toLink(JsonNode linkNode) {
        JsonNode prompt = linkNode.get("prompt");
        return new Link(
                createURI(linkNode),
                linkNode.get("rel").getTextValue(),
                prompt != null ? prompt.getTextValue() : null
        );
    }
}
