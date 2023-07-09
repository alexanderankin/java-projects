package info.ankin.projects.tfe4j.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.List;

public interface Wrappers {

    @Data
    @Accessors(chain = true)
    class Single<T> {
        Item<T> data;
    }

    @Data
    @Accessors(chain = true)
    class Multiple<T> {
        List<Item<T>> data;
        Links links;
        Meta meta;
    }

    @Data
    @Accessors(chain = true)
    class Item<T> {
        String id;
        String type;
        T attributes;
        LinkedHashMap<String, Multiple<?>> relationships;
        Links links;

        public Single<T> toSingle() {
            throw new UnsupportedOperationException("serializing this item to a single is not implemented: " + this);
        }
    }

    @Data
    @Accessors(chain = true)
    class Links {
        String self;
        String first;
        String prev;
        String next;
        String last;
        String related;
    }

    @Data
    @Accessors(chain = true)
    class Meta {
        Pagination pagination;
    }

    @Data
    @Accessors(chain = true)
    class Pagination {
        @JsonProperty("current-page")
        Integer currentPage;
        @JsonProperty("prev-page")
        Integer prevPage;
        @JsonProperty("next-page")
        Integer nextPage;
        @JsonProperty("total-pages")
        Integer totalPages;
        @JsonProperty("total-count")
        Integer totalCount;
    }

}
