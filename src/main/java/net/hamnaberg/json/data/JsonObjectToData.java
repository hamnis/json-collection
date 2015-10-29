package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;
import net.hamnaberg.json.InternalObjectFactory;
import net.hamnaberg.json.Json;
import net.hamnaberg.json.Property;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonObjectToData implements ToData<Json.JObject> {
    private InternalObjectFactory internalObjectFactory = new InternalObjectFactory() {};

    @Override
    public Data apply(Json.JObject from) {
        List<Property> properties = from.entrySet().stream().map(j -> {
            Map<String, Json.JValue> map = new LinkedHashMap<>();
            map.put("name", Json.jString(j.getKey()));
            j.getValue().foldUnit(
                    js -> map.put("value", js),
                    jb -> map.put("value", jb),
                    jn -> map.put("value", jn),
                    jo -> map.put("object", jo),
                    ja -> map.put("array", ja),
                    () -> map.put("value", Json.jNull())
            );
            return internalObjectFactory.createProperty(Json.jObject(map));
        }).collect(Collectors.toList());
        return new Data(properties);
    }
}
