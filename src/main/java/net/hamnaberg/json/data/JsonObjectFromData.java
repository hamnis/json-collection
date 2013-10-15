package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;
import net.hamnaberg.json.util.Function;
import net.hamnaberg.json.util.FunctionalList;
import net.hamnaberg.json.util.FunctionalMap;
import net.hamnaberg.json.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public final class JsonObjectFromData implements FromData<ObjectNode> {

    @Override
    public ObjectNode apply(Data data) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode node = factory.objectNode();
        for (Property property : data) {
            if (property.hasArray()) {
                List<Value> arrValue = property.getArray();
                ArrayNode arr = factory.arrayNode();
                arr.addAll(FunctionalList.create(arrValue).map(toJSON));
                node.put(property.getName(), arr);
            }
            else if (property.hasObject()) {
                ObjectNode object = factory.objectNode();
                object.putAll(FunctionalMap.create(property.getObject()).mapValues(toJSON));
                node.put(property.getName(), object);
            }
            else {
                Optional<Value> value = property.getValue();
                for (Value v : value) {
                    node.put(property.getName(), v.asJson());
                }
            }
        }
        return node;
    }


    private Function<Value,JsonNode> toJSON = new Function<Value, JsonNode>() {
        @Override
        public JsonNode apply(Value input) {
            return input.asJson();
        }
    };

}
