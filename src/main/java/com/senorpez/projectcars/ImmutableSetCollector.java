package com.senorpez.projectcars;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;

class ImmutableSetCollector {
    static <T, A extends Set<T>> Collector<T, A, Set<T>> toImmutableSet(Supplier<A> factory) {
        return Collector.of(
                factory,
                Set::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;},
                Collections::unmodifiableSet);
    }

    static <T> Collector<T, Set<T>, Set<T>> toImmutableSet() {
        return toImmutableSet(HashSet::new);
    }
}
