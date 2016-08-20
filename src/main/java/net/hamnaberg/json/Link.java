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

package net.hamnaberg.json;

import javaslang.control.Option;
import net.hamnaberg.json.extension.Extended;

import java.net.URI;
import java.util.*;

public final class Link extends Extended<Link> {
    Link(Json.JObject delegate) {
        super(delegate);
    }

    @Override
    protected Link copy(Json.JObject value) {
        return new Link(value);
    }

    public static Link create(URI href, String rel) {
        return create(href, rel, Option.none(), Option.none(), Option.none());
    }

    public static Link create(URI href, String rel, Option<String> prompt) {
        return create(href, rel, prompt, Option.none(), Option.none());
    }

    public static Link create(URI href, String rel, Option<String> prompt, Option<String> name) {
        return create(href, rel, prompt, name, Option.none());
    }

    public static Link create(URI href, String rel, Option<String> prompt, Option<String> name, Option<Render> render) {
        Map<String, Json.JValue> obj = new LinkedHashMap<>();

        Json.JObject node = Json.jObject(
                Json.entry("href", Json.jString(Option.of(href).getOrElseThrow(() -> new IllegalArgumentException("Href may not be null")).toString())),
                Json.entry("rel", Json.jString(Option.of(rel).getOrElseThrow(() -> new IllegalArgumentException("Relation may not be null"))))
        );
        prompt.forEach(value -> obj.put("prompt", Json.jString(value)));
        render.forEach(value -> obj.put("render", Json.jString(value.getName())));
        name.forEach(value -> obj.put("name", Json.jString(value)));
        return new Link(node.concat(Json.jObject(obj)));
    }

    public URI getHref() {
        return delegate.getAsString("href").map(URI::create).getOrElse((URI)null);
    }

    public Link withHref(URI href) {
        return copy(delegate.put("href", href.toString()));
    }

    public String getRel() {
        return delegate.getAsString("rel").getOrElse((String)null);
    }

    public Link withRel(String rel) {
        return copy(delegate.put("rel", rel));
    }

    public List<String> getParsedRel() {
        return Arrays.asList(getRel().split("\\s"));
    }

    public Option<String> getPrompt() {
        return Option.of(getAsString("prompt"));
    }

    public Link withPrompt(String prompt) {
        return copy(delegate.put("prompt", prompt));
    }

    public Option<String> getName() {
        return Option.of(getAsString("name"));
    }

    public Link withName(String name) {
        return copy(delegate.put("name", name));
    }

    public Render getRender() {
        return delegate.getAsString("render").map(Render::valueOf).getOrElse(Render.Link);
    }

    public Link withRender(Render render) {
        return copy(delegate.put("render", render.getName()));
    }

    @Override
    public String toString() {
        return String.format("Link{href=%s,rel=%s,prompt=%s,name=%s,render=%s}", getHref(), getRel(), getPrompt(), getName(), getRender());
    }

    public void validate() {
        Option.of(getHref()).getOrElseThrow(() -> new IllegalArgumentException("Href was null"));
        Option.of(getRel()).getOrElseThrow(() -> new IllegalArgumentException("Rel was null"));
    }

    static List<Link> fromArray(Json.JArray node) {
        return node.getListAsObjects().map(Link::new).toJavaList();
    }
}
