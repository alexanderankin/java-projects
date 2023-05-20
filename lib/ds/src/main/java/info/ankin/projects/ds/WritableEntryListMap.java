package info.ankin.projects.ds;

import java.util.List;
import java.util.Objects;

public class WritableEntryListMap<K, V> extends EntryListMap<K, V> {
    public WritableEntryListMap(List<Entry<K, V>> entryList) {
        super(entryList);
        Objects.requireNonNull(entryList, "entryList must not be null");
    }

    @Override
    public V put(K key, V value) {
        Entry<K, V> entryToAdd = new SimpleEntry<>(key, value);

        for (int i = 0; i < entryList.size(); i++) {
            Entry<K, V> kvEntry = entryList.get(i);
            if (kvEntry.getKey().equals(key)) {
                entryList.set(i, entryToAdd);

                return kvEntry.getValue();
            }
        }

        entryList.add(entryToAdd);
        return null;
    }
}
