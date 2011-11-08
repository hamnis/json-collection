package net.hamnaberg.json.util;

/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 11/8/11
 * Time: 10:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface F<A, B> {
    public B apply(A input);
}
