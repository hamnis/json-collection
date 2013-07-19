package net.hamnaberg.json.parser;

import java.io.IOException;

public class ParseException extends IOException {
    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
