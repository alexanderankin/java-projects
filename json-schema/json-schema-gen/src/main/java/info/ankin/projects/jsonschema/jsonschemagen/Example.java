package info.ankin.projects.jsonschema.jsonschemagen;

import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;

public class Example {
    public static void main(String[] args) {
        Klass k;
        sideApproach().doOnNext(System.out::println).blockOptional();
        pureApproach().doOnNext(System.out::println).blockOptional();
    }

    public static Mono<BothValues> pureApproach() {
        Mono<String> fromDb = getFromDb("table1", "col1");
        Mono<String> alsoDb = getFromDb("table2", "col2");

        return Mono.zip(fromDb, alsoDb)
                .map(tuple -> new BothValues()
                        .setOne(tuple.getT1())
                        .setTwo(tuple.getT2()));
    }

    public static Mono<BothValues> sideApproach() {
        Mono<String> fromDb = getFromDb("table1", "col1");
        Mono<String> alsoDb = getFromDb("table2", "col2");

        BothValues bothValues = new BothValues();
        Mono<BothValues> setOne = fromDb.map(bothValues::setOne);
        Mono<BothValues> setTwo = alsoDb.map(bothValues::setTwo);

        return Mono.zip(setOne, setTwo).thenReturn(bothValues);
    }

    private static Mono<String> getFromDb(String tableName, String fieldName) {
        return Mono.just(tableName + fieldName);
    }

    @Accessors(chain = true)
    @Data
    static class BothValues {
        String one;
        String two;
    }
}
