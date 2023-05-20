package info.ankin.projects.ds;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class EntryCollectionMap<K, V> extends AbstractMap<K, V> {
    private final Set<Entry<K, V>> entrySet;

    public EntryCollectionMap(Collection<Entry<K, V>> entrySet) {
        Objects.requireNonNull(entrySet, "entrySet must not be null");
        this.entrySet = entrySet instanceof Set ? (Set<Entry<K, V>>) entrySet : new CollectionSet<>(entrySet);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }
}
