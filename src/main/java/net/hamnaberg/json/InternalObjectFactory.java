package net.hamnaberg.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class InternalObjectFactory {
    public Collection createCollection(ObjectNode node) {
        return new Collection(node);
    }

    public Error createError(ObjectNode node) {
        return new Error(node);
    }

    public Link createLink(ObjectNode node) {
        return new Link(node);
    }

    public Property createProperty(ObjectNode node) {
        return new Property(node);
    }

    public Query createQuery(ObjectNode node) {
        return new Query(node);
    }

    public Template createTemplate(ObjectNode node) {
        return new Template(node);
    }
}
