package net.hamnaberg.json.extension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.json.Error;
import net.hamnaberg.json.InternalObjectFactory;

public class Errors {
    private final Map<String, List<Error>> errors = new LinkedHashMap<String, List<Error>>();

    public Errors(Map<String, List<Error>> errors) {
        this.errors.putAll(errors);
    }

    public List<Error> getErrors(String name) {
        List<Error> v = errors.get(name);
        if (v != null) {
            return Collections.unmodifiableList(v);
        }
        return Collections.emptyList();
    }

    private JsonNode asJson() {
        ObjectNode n = JsonNodeFactory.instance.objectNode();
        n.setAll(errors.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> toArrayNode(entry.getValue()))));
        return n;
    }

    private static ArrayNode toArrayNode(List<Error> errors) {
        return errors.stream()
                     .map(Extended::asJson)
                     .collect(JsonNodeFactory.instance::arrayNode, ArrayNode::add, ArrayNode::addAll);
    }

    public static class Builder {
        private Map<String, List<Error>> m = new LinkedHashMap<String, List<Error>>();

        public Builder put(String name, List<Error> errors) {
            m.put(name, errors);
            return this;
        }

        public Builder add(String name, List<Error> errors) {
            List<Error> list = m.get(name);
            if (list == null) {
                return put(name, errors);
            }
            list.addAll(errors);
            return this;
        }

        public Errors build() {
            return new Errors(m);
        }
    }


    public static class Ext extends Extension<Optional<Errors>> {
        private InternalObjectFactory factory = new InternalObjectFactory() {};

        @Override
        public Optional<Errors> extract(ObjectNode node) {
            if (node.has("errors")) {
                //extract stuff
                JsonNode n = node.get("errors");
                Stream<Map.Entry<String, JsonNode>> stream = stream(spliteratorUnknownSize(n.fields(), Spliterator.ORDERED), false);
                Map<String, List<Error>> errors  = stream.map(e -> {
                    List<Error> list = stream(e.getValue().spliterator(), false).map(elem -> factory.createError((ObjectNode) elem)).collect(toList());
                    return new AbstractMap.SimpleImmutableEntry<>(e.getKey(), list);
                }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
                return Optional.of(new Errors(errors));
            }
            return Optional.empty();
        }

        @Override
        public Map<String, JsonNode> apply(Optional<Errors> value) {
            return value.map(Stream::of)
                        .orElse(Stream.<Errors>empty())
                        .collect(Collectors.toMap(erros -> "errors", Errors::asJson));
        }
    }
}


