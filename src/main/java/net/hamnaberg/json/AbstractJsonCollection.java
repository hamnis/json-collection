package net.hamnaberg.json;

import java.net.URI;

public abstract class AbstractJsonCollection implements JsonCollection {
    private URI href;
    private Version version;

    public AbstractJsonCollection(URI href, Version version) {
        this.href = href;
        this.version = version;
    }

    public URI getHref() {
        return href;
    }

    public Version getVersion() {
        return version;
    }
}
