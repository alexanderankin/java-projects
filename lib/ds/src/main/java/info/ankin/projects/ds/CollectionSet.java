package info.ankin.projects.ds;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * a set which is optimized for iterating over, and creation from other collections
 *
 * @param <T> the type of elements maintained by this set
 */
public class CollectionSet<T> extends AbstractSet<T> {
    private final Collection<T> entries;

    public CollectionSet(Collection<T> entries) {
        this.entries = entries;
        Objects.requireNonNull(entries);
    }

    @Override
    public Iterator<T> iterator() {
        return entries.iterator();
    }

    @Override
    public int size() {
        return entries.size();
    }
}
