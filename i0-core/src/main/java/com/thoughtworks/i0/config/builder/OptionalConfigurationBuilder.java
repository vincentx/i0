package com.thoughtworks.i0.config.builder;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import javax.annotation.Nullable;

public class OptionalConfigurationBuilder<T extends ConfigurationBuilder<R>, R> implements ConfigurationBuilder<Optional<R>> {
    private final T builder;
    private Optional<T> wrapper = Optional.absent();

    public OptionalConfigurationBuilder(T builder) {
        this.builder = builder;
    }

    public T builder() {
        wrapper = Optional.of(builder);
        return builder;
    }

    @Override
    public Optional<R> build() {
        return wrapper.transform(new Function<T, R>() {
            @Nullable
            @Override
            public R apply(@Nullable T input) {
                return input.build();
            }
        });
    }
}
