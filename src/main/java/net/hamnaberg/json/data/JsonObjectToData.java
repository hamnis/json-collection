package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;
import net.hamnaberg.json.ValueFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonObjectToData implements ToData<ObjectNode> {
    @Override
    public Data apply(ObjectNode from) {
        List<Property> properties = new ArrayList<Property>();
        Iterator<Map.Entry<String,JsonNode>> fields = from.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            String key = next.getKey();
            JsonNode value = next.getValue();
            if (value.isArray()) {
                List<Value> values = new ArrayList<Value>();
                for (JsonNode node : value) {
                    values.add(ValueFactory.createValue(node));
                }
                properties.add(Property.array(key, values));
            }
            else if (value.isObject()) {
                Iterator<Map.Entry<String,JsonNode>> objFields = value.fields();
                Map<String, Value> map = new LinkedHashMap<String, Value>();
                while (objFields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = objFields.next();
                    map.put(entry.getKey(), ValueFactory.createValue(entry.getValue()));
                }
                properties.add(Property.object(key, map));
            }
            else {
                Value v = ValueFactory.createValue(value);
                properties.add(Property.value(key, v));
            }
        }
        return new Data(properties);
    }
}
