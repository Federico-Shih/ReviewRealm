package ar.edu.itba.paw.webapp.controller.datacontainers;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilteredList <T> {

    private final List<T> selected;
    private final List<T> unselected;

    public FilteredList(List<T> all, Predicate<T> filter) {
        this.selected = all.stream().filter(filter).collect(Collectors.toList());
        this.unselected = all.stream().filter(filter.negate()).collect(Collectors.toList());
    }

    public List<T> getSelected() {
        return selected;
    }

    public List<T> getUnselected() {
        return unselected;
    }
}
