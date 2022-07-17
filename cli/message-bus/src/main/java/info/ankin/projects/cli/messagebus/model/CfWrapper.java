package info.ankin.projects.cli.messagebus.model;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CfWrapper<T> {
    @Getter
    private final CompletableFuture<T> cf = new CompletableFuture<>();

    public BiConsumer<T, Exception> getCallback() {
        return (t, e) -> {
            if (e == null) cf.complete(t);
            else cf.completeExceptionally(e);
        };
    }
}
