package net.hamnaberg.json.parser;

import com.google.common.collect.ImmutableList;
import net.hamnaberg.json.JsonCollection;
import net.hamnaberg.json.Link;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

/**jsonParser
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
        JsonNode linkCollection = collectionNode.get("links");
        ImmutableList.Builder<Link> linkBuilder = ImmutableList.builder();
        
        for (JsonNode linkNode : linkCollection) {
            JsonNode prompt = linkNode.get("prompt");
            Link link = new Link(
                    createURI(linkNode),
                    linkNode.get("rel").getTextValue(),
                    prompt != null ?  prompt.getTextValue() : null
            );
            linkBuilder.add(link);
        }


        return new JsonCollection(createURI(collectionNode));
    }

    private URI createURI(JsonNode node) {
        return URI.create(node.get("href").getValueAsText());
    }
}
