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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Template {
    private final List<Property> properties = new ArrayList<Property>();

    public Template() {
        this(Collections.<Property>emptyList());
    }

    public Template(List<Property> properties) {
        if (properties != null) {
            this.properties.addAll(properties);
        }
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        if (properties != null ? !properties.equals(template.properties) : template.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }
}
