package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class TypeToData<A> implements ToData<A> {
    private ObjectMapper mapper;

    public TypeToData() {
        this(new ObjectMapper());
    }

    public TypeToData(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    //Not the most efficient implementation
    @Override
    public Data apply(A from) {
        try {
            String string = mapper.writer().writeValueAsString(from);
            JsonNode node = mapper.readTree(string);
            if (node.isObject()) {
                return new JsonObjectToData().apply((ObjectNode) node);
            }
            throw new IllegalArgumentException(String.format("Unable to serialize %s to Data", from.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
