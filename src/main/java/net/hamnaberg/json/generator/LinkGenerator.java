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

import net.hamnaberg.json.Link;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class LinkGenerator extends AbstractGenerator<Link> {
    public LinkGenerator(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public JsonNode toNode(Link object) {
        ObjectNode node = mapper.createObjectNode();
        node.put("href", object.getHref().toString());
        node.put("rel", object.getRel());
        if (object.getPrompt().isPresent()) {
            node.put("prompt", object.getPrompt().get());
        }
        return node;
    }
}
