package org.zendesk.client.v2.model.agregator;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import java.util.function.Supplier;

public class Page<T> {
    Supplier<Integer> countSupplier;
    private Integer perPage;
    Iterable<T> result;

    public Page(Integer count, Integer perPage, Iterable<T> result) {
        this(() -> count == null || count < 0 ? -1 : count,
                perPage,
                result);
    }

    public Page(Supplier<Integer> countSupplier, Integer perPage, Iterable<T> result) {
        this.countSupplier = countSupplier;
        this.perPage = perPage;
        this.result = result;
    }

    public Integer getCount() {
        return countSupplier.get();
    }

    public List<T> getItemsOnCurrentPage() {
        return stream(result.spliterator(), false)
                .limit(ofNullable(perPage).orElse(MAX_VALUE))
                .collect(toList());
    }

    public Iterable<T> getAllItemsStartingWithCurrentPage() {
        return result;
    }
}
