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
        ImmutableList<Link> links = parseLinks(collectionNode);
        ImmutableList<Item> items = parseItems(collectionNode);
        ErrorMessage error = parseError(collectionNode);

        Version version = getVersion(collectionNode);
        return new JsonCollection(createURI(collectionNode), version, links, items);
    }

    private ErrorMessage parseError(JsonNode collectionNode) {
        return null;
    }

    private ImmutableList<Item> parseItems(JsonNode collectionNode) {
        return ImmutableList.of();
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
