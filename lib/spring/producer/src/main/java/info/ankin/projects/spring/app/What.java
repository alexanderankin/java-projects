// package info.ankin.projects.spring.app;
//
// import org.springframework.cloud.stream.function.StreamFunctionProperties;
// import reactor.core.publisher.Flux;
//
// public class What {
//     public static void main(String[] args) {
//         StreamFunctionProperties streamFunctionProperties = new StreamFunctionProperties();
//
//         // streamFunctionProperties
//         Flux<String> abc = Flux.just("abc");
//         Flux<String> stringFlux = abc.publish().autoConnect(2);
//         stringFlux.blockLast();
//     }
// }
