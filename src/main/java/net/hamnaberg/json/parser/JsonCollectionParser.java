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

import com.google.common.collect.ImmutableList;
import net.hamnaberg.json.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

/**
 * Parser for a vnd.collection+json document.
 *
 * 
 */
public class JsonCollectionParser {
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

    private JsonCollection parse(JsonNode node) throws IOException {
        JsonNode collectionNode = node.get("collection");
        return parseCollection(collectionNode);
    }

    private JsonCollection parseCollection(JsonNode collectionNode) {
        URI href = createURI(collectionNode);
        Version version = getVersion(collectionNode);
        ErrorMessage error = parseError(collectionNode);

        if (error != null) {
            return new ErrorJsonCollection(href, version, error);
        }

        ImmutableList<Link> links = parseLinks(collectionNode);
        ImmutableList<Item> items = parseItems(collectionNode);

        return new DefaultJsonCollection(href, version, links, items, new Template());
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

    private ImmutableList<Item> parseItems(JsonNode collectionNode) {
        ImmutableList.Builder<Item> builder = ImmutableList.builder();
        JsonNode items = collectionNode.get("items");
        if (items != null) {
            for (JsonNode node : items) {
                URI uri = createURI(node);
                builder.add(new Item(uri, parseData(node.get("data"))));
            }
        }
        return builder.build();
    }

    private ImmutableList<Property> parseData(JsonNode data) {
        ImmutableList.Builder<Property> builder = ImmutableList.builder();
        for (JsonNode node : data) {
            builder.add(toProperty(node));
        }
        return builder.build();
    }

    private Property toProperty(JsonNode node) {
        return new Property(node.get("name").getTextValue(), ValueFactory.createValue(node.get("value")), getStringValue(node.get("prompt")));
    }

    private URI createURI(JsonNode node) {
        return URI.create(node.get("href").getTextValue());
    }

    private Version getVersion(JsonNode collectionNode) {
        JsonNode version = collectionNode.get("version");
        return Version.getVersion(version != null ? version.getTextValue() : null);
    }

    private ImmutableList<Link> parseLinks(JsonNode collectionNode) {
        JsonNode linkCollection = collectionNode.get("links");
        ImmutableList.Builder<Link> linkBuilder = ImmutableList.builder();
        if (linkCollection != null) {
            for (JsonNode linkNode : linkCollection) {
                JsonNode prompt = linkNode.get("prompt");
                Link link = new Link(
                        createURI(linkNode),
                        linkNode.get("rel").getTextValue(),
                        prompt != null ?  prompt.getTextValue() : null
                );
                linkBuilder.add(link);
            }
        }
        return linkBuilder.build();
    }
}
