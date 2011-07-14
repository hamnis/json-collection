package net.hamnaberg.json;

import java.net.URI;

public class Link implements WithPrompt {
    private final URI uri;
    private final String rel;
    private final String prompt;

    public Link(URI uri, String rel, String prompt) {
        this.uri = uri;
        this.rel = rel;
        this.prompt = prompt;
    }

    public URI getUri() {
        return uri;
    }

    public String getRel() {
        return rel;
    }

    public String getPrompt() {
        return prompt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (prompt != null ? !prompt.equals(link.prompt) : link.prompt != null) return false;
        if (rel != null ? !rel.equals(link.rel) : link.rel != null) return false;
        if (uri != null ? !uri.equals(link.uri) : link.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (rel != null ? rel.hashCode() : 0);
        result = 31 * result + (prompt != null ? prompt.hashCode() : 0);
        return result;
    }
}
