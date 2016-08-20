package net.hamnaberg.json.extension;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

import javaslang.control.Option;
import net.hamnaberg.json.Error;
import net.hamnaberg.json.InternalObjectFactory;
import net.hamnaberg.json.Json;

public class Errors {
    private final Map<String, List<Error>> errors = new LinkedHashMap<String, List<Error>>();

    public Errors(Map<String, List<Error>> errors) {
        this.errors.putAll(errors);
    }

    public List<Error> getErrors(String name) {
        List<Error> v = errors.get(name);
        if (v != null) {
            return Collections.unmodifiableList(v);
        }
        return Collections.emptyList();
    }

    private Json.JObject asJson() {
        return Json.jObject(
                errors.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> toArrayNode(entry.getValue())))
        );
    }

    private static Json.JArray toArrayNode(List<Error> errors) {
        return Json.jArray(errors.stream()
                .map(Extended::asJson)
                .collect(Collectors.toList()));
    }

    public static class Builder {
        private Map<String, List<Error>> m = new LinkedHashMap<String, List<Error>>();

        public Builder put(String name, List<Error> errors) {
            m.put(name, errors);
            return this;
        }

        public Builder add(String name, List<Error> errors) {
            List<Error> list = m.get(name);
            if (list == null) {
                return put(name, errors);
            }
            list.addAll(errors);
            return this;
        }

        public Errors build() {
            return new Errors(m);
        }
    }


    public static class Ext extends Extension<Option<Errors>> {
        private InternalObjectFactory factory = new InternalObjectFactory() {};

        @Override
        public Option<Errors> extract(Json.JObject node) {
            Function<Json.JArray, List<Error>> toErrors = arr -> arr.
                    getListAsObjects().
                    map(factory::createError).
                    toJavaList();

            Option<Json.JObject> errors = node.getAsObject("errors");
            return errors.map(j -> j.entrySet().stream().map(
                    e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), toErrors.apply(e.getValue().asJsonArrayOrEmpty()))).
                    collect(toMap(Map.Entry::getKey, Map.Entry::getValue))).map(Errors::new);
        }

        @Override
        public Json.JObject apply(Option<Errors> value) {
            return Json.jObject(value.map(Stream::of)
                    .getOrElse(Stream.<Errors>empty())
                    .collect(Collectors.toMap(errors -> "errors", Errors::asJson)));
        }
    }
}


