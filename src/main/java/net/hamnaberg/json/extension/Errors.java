package net.hamnaberg.json.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.funclite.Function;
import net.hamnaberg.funclite.FunctionalMap;
import net.hamnaberg.funclite.Optional;
import net.hamnaberg.json.InternalObjectFactory;
import net.hamnaberg.json.Error;

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
        n.putAll(FunctionalMap.create(errors).mapValues(new Function<List<Error>, JsonNode>() {
            @Override
            public JsonNode apply(List<Error> errors) {
                ArrayNode n = JsonNodeFactory.instance.arrayNode();
                for (Error error : errors) {
                    n.add(error.asJson());
                }
                return n;
            }
        }));
        return n;
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
                Map<String, List<Error>> errors = new LinkedHashMap<String, List<Error>>();
                JsonNode n = node.get("errors");
                Iterator<Map.Entry<String,JsonNode>> fields = n.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> next = fields.next();
                    List<Error> list = new ArrayList<Error>();
                    for (JsonNode e : next.getValue()) {
                        Error error = factory.createError((ObjectNode) e);
                        list.add(error);
                    }
                    errors.put(next.getKey(), list);
                }
                return Optional.some(new Errors(errors));
            }
            return Optional.none();
        }

        @Override
        public Map<String, JsonNode> apply(Optional<Errors> value) {
            Map<String, JsonNode> m = new HashMap<String, JsonNode>();
            for (Errors e : value) {
                m.put("errors", e.asJson());
            }
            return m;
        }
    }
}


