package net.hamnaberg.json.generator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.hamnaberg.json.Property;
import net.hamnaberg.json.Template;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;


/**
 * @author Erlend Hamnaberg<erlend@hamnaberg.net>
 */
public class TemplateGenerator extends AbstractGenerator<Template> {
    private final PropertyGenerator propertyGenerator;
    public TemplateGenerator(ObjectMapper mapper) {
        super(mapper);
        propertyGenerator = new PropertyGenerator(mapper);
    }

    @Override
    public JsonNode toNode(Template object) {
        ObjectNode node = mapper.createObjectNode();
        node.put("data", mapper.createArrayNode().addAll(Lists.transform(object.getProperties(), new Function<Property, JsonNode>() {
            @Override
            public JsonNode apply(Property input) {
                return propertyGenerator.toNode(input);
            }
        })));
        return node;
    }
}
