package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;
import net.hamnaberg.json.Json;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class JsonObjectFromData implements FromData<Json.JObject> {

    @Override
    public Json.JObject apply(Data data) {
        Map<String, Json.JValue> map = new LinkedHashMap<>();
        for (Property property : data) {
            Json.JValue value = property.fold(
                    Value::asJson,
                    v -> Json.jArray(
                            v.stream().map(Value::asJson).collect(Collectors.toList())
                    ),
                    v -> Json.jObject(property.getObject()
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().asJson()))),
                    Json::jNull
            );
            map.put(property.getName(), value);
        }
        return Json.jObject(map);
    }
}
