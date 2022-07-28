// package info.ankin.projects.spring.app;
//
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
// import java.util.List;
// import java.util.function.Function;
//
// public class What {
//     public static void main(String[] args) {
//         Flux<String> just = Flux.just("abc", "def", "aaa", "cat", "bat", "color");
//
//         Function<String, Mono<Boolean>> asyncTest = s -> Mono.just(s.contains("a"));
//         Function<String, Mono<String>> filterFunction = getFilterFunction(asyncTest);
//
//         List<String> block = just.flatMap(filterFunction).collectList().block();
//         System.out.println(block);
//     }
//
//     static <T> Function<T, Mono<T>> getFilterFunction(Function<T, Mono<Boolean>> test) {
//         return t -> test.apply(t).mapNotNull(v -> v ? t : null);
//     }
// }
