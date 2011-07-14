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

import com.google.common.base.Preconditions;

public class ValueFactory {
    public static Value createValue(Object value) {
        Preconditions.checkArgument(checkValue(value), "Illegal value %s", value);
        return new ValueImpl(value);
    }

    private static boolean checkValue(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return true;
        }
        else if (value instanceof Boolean) {
            return true;
        }
        else if (value instanceof Number) {
            return true;
        }
        return false;
    }
}
