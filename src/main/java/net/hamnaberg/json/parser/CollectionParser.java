/*
 * Copyright 2012 Erlend Hamnaberg
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

package net.hamnaberg.json.parser;

import net.hamnaberg.json.*;
import net.hamnaberg.json.Collection;
import net.hamnaberg.json.jackson.JacksonStreamingParser;
import net.hamnaberg.json.util.Charsets;

import java.io.*;
import javaslang.control.Option;

/**
 * Parser for a vnd.collection+json document.
 */
public class CollectionParser {

    private final JacksonStreamingParser parser = new JacksonStreamingParser();

    public Collection parse(Reader reader) throws IOException {
        try {
            return parse(parser.parse(reader));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Parses a Collection from the given stream.
     * The stream is wrapped in a BufferedReader.
     * <p>
     * The stream is expected to be UTF-8 encoded.
     *
     * @param stream the stream
     * @return a Collection
     * @throws IOException
     */
    public Collection parse(InputStream stream) throws IOException {
        return parse(new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8)));
    }

    /**
     * Parses a Collection from the given String.
     *
     * @param input the string to parse
     * @return a Collection
     * @throws IOException
     */
    public Collection parse(String input) throws IOException {
        return parse(new StringReader(input));
    }

    public Template parseTemplate(Reader reader) throws IOException {
        try {
            return parseTemplate(parser.parse(reader).asJsonObjectOrEmpty());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Parses a JsonCollection from the given stream.
     * The stream is wrapped in a BufferedReader.
     * <p>
     * The stream is expected to be UTF-8 encoded.
     *
     * @param stream the stream
     * @return a jsonCollection
     * @throws IOException
     */
    public Template parseTemplate(InputStream stream) throws IOException {
        return parseTemplate(new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8)));
    }

    public Template parseTemplate(String input) throws IOException {
        return parseTemplate(new StringReader(input));
    }

    private Collection parse(Json.JValue node) throws ParseException {
        Option<Json.JObject> collectionNode = node.asJsonObject().flatMap(obj -> obj.getAsObject("collection"));
        if (collectionNode.isDefined()) {
            return parseCollection(collectionNode.get());
        }
        throw new ParseException("Missing \"collection\" property");
    }

    private Collection parseCollection(Json.JObject collectionNode) {
        Collection c = objectFactory.createCollection(collectionNode);
        c.validate();
        return c;
    }

    private Template parseTemplate(Json.JObject object) throws ParseException {
        Option<Json.JObject> node = object.getAsObject("template");
        if (node.isDefined()) {
            return objectFactory.createTemplate(node.get());
        }
        throw new ParseException("Missing \"template\" property");
    }

    private static InternalObjectFactory objectFactory = new InternalObjectFactory() {
    };
}
