package com.github.abcdra.serialization;

public class Dual<T,V> {
    public final T first;
    public final V second;
    public Dual(T t, V v) {
        this.first = t;
        this.second = v;
    }
}
