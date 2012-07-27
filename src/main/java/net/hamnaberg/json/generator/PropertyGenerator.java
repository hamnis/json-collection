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

package net.hamnaberg.json.generator;

import net.hamnaberg.json.Property;
import net.hamnaberg.json.Value;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.*;

import java.util.Map;

public class PropertyGenerator extends AbstractGenerator<Property> {
    protected PropertyGenerator(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public JsonNode toNode(Property property) {
        ObjectNode node = mapper.createObjectNode();
        node.put("name", property.getName());
        if (property.getPrompt().isPresent()) {
            node.put("prompt", property.getPrompt().get());
        }
        if (property.getValue().isPresent()) {
            node.put("value", getJsonValue(property.getValue().get()));
        }
        else if (!property.getObject().isEmpty()) {
            ObjectNode object = mapper.createObjectNode();
            for (Map.Entry<String, Value> entry : property.getObject().entrySet()) {
                object.put(entry.getKey(), getJsonValue(entry.getValue()));
            }
            node.put("object", object);
        }
        else if (!property.getArray().isEmpty()) {
            ArrayNode array = mapper.createArrayNode();
            for (Value value : property.getArray()) {
                array.add(getJsonValue(value));
            }
            node.put("array", array);
        }

        return node;
    }

    private JsonNode getJsonValue(Value value) {
        if (value.isNumeric()) {
            return new DoubleNode(value.asNumber().doubleValue());
        }
        else if (value.isString()) {
            return new TextNode(value.asString());
        }
        else if (value.isBoolean()) {
            return BooleanNode.valueOf(value.asBoolean());
        }
        return NullNode.getInstance();
    }
}
