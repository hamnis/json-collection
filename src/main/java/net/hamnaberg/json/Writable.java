package net.hamnaberg.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface Writable {
    public void writeTo(OutputStream stream) throws IOException;
    public void writeTo(Writer stream) throws IOException;
    public String toString();
}
