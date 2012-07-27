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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.net.URI;

public class Link implements WithPrompt, WithHref {
    private final URI href;
    private final String rel;
    private final Optional<String> prompt;

    public Link(URI href, String rel, Optional<String> prompt) {
        this.href = href;
        this.rel = Preconditions.checkNotNull(rel, "Relation may not be null");
        this.prompt = prompt;
    }

    public URI getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public Optional<String> getPrompt() {
        return prompt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (prompt != null ? !prompt.equals(link.prompt) : link.prompt != null) return false;
        if (rel != null ? !rel.equals(link.rel) : link.rel != null) return false;
        if (href != null ? !href.equals(link.href) : link.href != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (rel != null ? rel.hashCode() : 0);
        result = 31 * result + (prompt != null ? prompt.hashCode() : 0);
        return result;
    }
}
