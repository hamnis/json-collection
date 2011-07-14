/*
 * Copyright 2011 Erlend Hamnaberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hamnaberg.json;

public class ErrorMessage {
    public static final ErrorMessage EMPTY = new ErrorMessage(null, null, null);

    private final String title;
    private final String code;
    private final String message;

    public ErrorMessage(String title, String code, String message) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorMessage error = (ErrorMessage) o;

        if (code != null ? !code.equals(error.code) : error.code != null) return false;
        if (message != null ? !message.equals(error.message) : error.message != null) return false;
        if (title != null ? !title.equals(error.title) : error.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
