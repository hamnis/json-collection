package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;

public interface FromData<A> {
    A apply(Data data);
}
