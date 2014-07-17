package net.hamnaberg.json.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.hamnaberg.json.Data;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;

public final class JsonObjectFromData implements FromData<ObjectNode> {

    @Override
    public ObjectNode apply(Data data) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode node = factory.objectNode();
        for (Property property : data) {
            if (property.hasArray()) {
                List<Value> arrValue = property.getArray();
                ArrayNode arr = factory.arrayNode();
                arr.addAll(arrValue.stream().map(Value::asJson).collect(Collectors.toList()));
                node.set(property.getName(), arr);
            }
            else if (property.hasObject()) {
                ObjectNode object = factory.objectNode();
                object.setAll(property.getObject()
                                      .entrySet()
                                      .stream()
                                      .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().asJson())));
                node.set(property.getName(), object);
            }
            else {
                Optional<Value> value = property.getValue();
                value.ifPresent(v -> node.set(property.getName(), v.asJson()));
            }
        }
        return node;
    }

}
