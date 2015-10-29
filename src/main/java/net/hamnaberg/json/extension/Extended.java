package net.hamnaberg.json.extension;

import net.hamnaberg.json.Json;

public abstract class Extended<T> {
    protected final Json.JObject delegate;

    protected Extended(Json.JObject delegate) {
        this.delegate = delegate;
    }

    protected abstract T copy(Json.JObject value);

    public <A> A getExtension(Extension<A> extension) {
        return extension.extract(delegate);
    }

    @SuppressWarnings("unchecked")
    public <A> T apply(A value, Extension<A> extension) {
        Json.JObject applied = extension.apply(value);
        if (applied == null || applied.isEmpty()) {
            return (T)this;
        }
        return copy(delegate.merge(applied));
    }

    public Json.JObject asJson() {
        return delegate;
    }

    protected String getAsString(String name) {
        return delegate.getAsString(name).orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Extended extended = (Extended) o;

        if (delegate != null ? !delegate.equals(extended.delegate) : extended.delegate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return delegate != null ? delegate.hashCode() : 0;
    }

    public abstract void validate();
}
