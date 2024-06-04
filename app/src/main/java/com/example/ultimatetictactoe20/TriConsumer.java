package com.example.ultimatetictactoe20;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts two input arguments and returns no
 * result.  This is the two-arity specialization of {@link Consumer}.
 * Unlike most other functional interfaces, {@code BiConsumer} is expected
 * to operate via side-effects.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <D> the type of the third argument to the operation
 *
 * @see Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface TriConsumer<T, U, D> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param d the third input argument
     */
    void accept(T t, U u, D d);
}