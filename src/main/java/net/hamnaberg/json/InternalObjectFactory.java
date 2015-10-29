package net.hamnaberg.json;


public abstract class InternalObjectFactory {
    public Collection createCollection(Json.JObject node) {
        return new Collection(node);
    }

    public Error createError(Json.JObject node) {
        return new Error(node);
    }

    public Link createLink(Json.JObject node) {
        return new Link(node);
    }

    public Property createProperty(Json.JObject node) {
        return new Property(node);
    }

    public Query createQuery(Json.JObject node) {
        return new Query(node);
    }

    public Template createTemplate(Json.JObject node) {
        return new Template(node);
    }
}
