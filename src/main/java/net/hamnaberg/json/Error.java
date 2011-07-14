package net.hamnaberg.json;

public class Error {
    private final String title;
    private final String code;
    private final String message;

    public Error(String title, String code, String message) {
        this.title = title;
        this.code = code;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
