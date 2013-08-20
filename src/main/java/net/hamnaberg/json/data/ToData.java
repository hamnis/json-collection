package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;

public interface ToData<A> {
    Data apply(A from);
}
