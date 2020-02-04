package owmii.lib.util.lambda;

import java.util.Objects;
import java.util.function.Consumer;

public interface Checker<T> {
    boolean check(T t);

    default Consumer<T> andThen(Checker<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            check(t);
            after.check(t);
        };
    }
}
