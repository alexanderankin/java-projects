package info.ankin.projects.ds;

import java.util.List;
import java.util.Objects;

public class EntryListMap<K, V> extends EntryCollectionMap<K, V> {
    protected final List<Entry<K, V>> entryList;

    public EntryListMap(List<Entry<K, V>> entryList) {
        super(entryList);
        Objects.requireNonNull(entryList, "entrySet must not be null");
        this.entryList = entryList;
    }

    public List<Entry<K, V>> getEntryList() {
        return entryList;
    }
}
