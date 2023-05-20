package info.ankin.projects.ds;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionSetTest {

    @Test
    void test_size() {
        CollectionSet<String> collectionSet = new CollectionSet<>(Arrays.asList("abc", "def"));
        assertTrue(collectionSet.contains("abc"));
        assertTrue(collectionSet.contains("def"));
    }

    @Test
    void test_membershipCheck() {
        CollectionSet<String> collectionSet = new CollectionSet<>(Arrays.asList("abc", "def"));
        assertEquals(2, collectionSet.size());
    }

    @Test
    void test_iterationLength() {
        CollectionSet<String> collectionSet = new CollectionSet<>(Arrays.asList("abc", "def"));

        int actual = 0;
        for (var i = collectionSet.iterator(); i.hasNext(); i.next()) actual++;
        assertEquals(2, actual);
    }

    @Test
    void test_iteration() {
        CollectionSet<String> collectionSet = new CollectionSet<>(Arrays.asList("abc", "def"));
        assertEquals("abc,def", String.join(",", collectionSet));
    }

}
