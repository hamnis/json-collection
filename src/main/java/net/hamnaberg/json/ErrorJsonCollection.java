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

import com.google.common.collect.ImmutableList;

import java.net.URI;

public class ErrorJsonCollection extends AbstractJsonCollection {
    private ErrorMessage error;

    public ErrorJsonCollection(URI href, Version version, ErrorMessage error) {
        super(href, version);
        this.error = error;
    }

    @Override
    public Template getTemplate() {
        throw new UnsupportedOperationException("Incorrect Collection type");
    }

    @Override
    public ImmutableList<Link> getLinks() {
        throw new UnsupportedOperationException("Incorrect Collection type");
    }

    @Override
    public ImmutableList<Item> getItems() {
        throw new UnsupportedOperationException("Incorrect Collection type");
    }

    @Override
    public ErrorMessage getError() {
        return error;
    }
}
