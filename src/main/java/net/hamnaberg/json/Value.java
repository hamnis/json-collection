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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Value {

    <A> A fold(Function<BooleanValue, A> fBoolean,
               Function<StringValue, A> fString,
               Function<NumberValue, A> fNumber,
               Supplier<A> fNull
    );

    default Json.JValue asJson() {
        return fold(
                b -> Json.jBoolean(b == BooleanValue.TRUE),
                s -> Json.jString(s.value),
                n -> Json.jNumber(n.value),
                Json::jNull
        );
    }

    default String asString() {
        return fold(
                b -> b.name().toLowerCase(),
                s -> s.value,
                n -> n.value.toString(),
                () -> "null"
        );
    }


    static Value of(String value) {
        return new StringValue(value);
    }

    static Value of(boolean value) {
        return value ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    static Value of(BigDecimal bd) {
        return new NumberValue(bd);
    }
    static Value of(int bd) {
        return new NumberValue(new BigDecimal(bd));
    }
    static Value of(long bd) {
        return new NumberValue(new BigDecimal(bd));
    }

    static Value of(double bd) {
        return new NumberValue(new BigDecimal(bd));
    }

    Value NULL = NullValue.INSTANCE;


    enum BooleanValue implements Value {
        TRUE,
        FALSE;

        @Override
        public <A> A fold(Function<BooleanValue, A> fBoolean, Function<StringValue, A> fString, Function<NumberValue, A> fNumber, Supplier<A> fNull) {
            return fBoolean.apply(this);
        }
    }

    final class StringValue implements Value {
        public final String value;

        public StringValue(String value) {
            this.value = value;
        }

        @Override
        public <A> A fold(Function<BooleanValue, A> fBoolean, Function<StringValue, A> fString, Function<NumberValue, A> fNumber, Supplier<A> fNull) {
            return fString.apply(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringValue that = (StringValue) o;

            return Objects.equals(value, that.value);

        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    final class NumberValue implements Value {
        public final BigDecimal value;

        public NumberValue(BigDecimal value) {
            this.value = value;
        }

        @Override
        public <A> A fold(Function<BooleanValue, A> fBoolean, Function<StringValue, A> fString, Function<NumberValue, A> fNumber, Supplier<A> fNull) {
            return fNumber.apply(this);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NumberValue that = (NumberValue) o;

            return Objects.equals(value, that.value);
        }
    }

    enum NullValue implements Value {
        INSTANCE;

        @Override
        public <A> A fold(Function<BooleanValue, A> fBoolean, Function<StringValue, A> fString, Function<NumberValue, A> fNumber, Supplier<A> fNull) {
            return fNull.get();
        }
    }

}
