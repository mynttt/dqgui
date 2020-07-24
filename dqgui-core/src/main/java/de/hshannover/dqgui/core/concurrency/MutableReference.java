package de.hshannover.dqgui.core.concurrency;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MutableReference<T> {
    private AtomicReference<T> value;

    public MutableReference(T value) {
        this.value = new AtomicReference<>(value);
    }

    public MutableReference() {
        this.value = new AtomicReference<>();
    }

    public Optional<T> get() {
        return Optional.ofNullable(value.get());
    }

    public void set(T value) {
        this.value.set(value);
    }

}
