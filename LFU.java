import java.util.*;

// O(1) algorithm for implementing the LFU cache eviction scheme


// Frequency is tracked using a doubly linked list
// each node holds a value, which is an int representing the frequency of access
// and a ArrayList of items, which are the keys that are used to reference the LFUItem hashmap
class FreqNode {
    FreqNode next;
    FreqNode prev;
    int value;
    ArrayList<Integer> items;

    public FreqNode() {
        next = this;
        prev = this;
        value = 0;
        items = new ArrayList<>();
    }
}

// Each item in the LFU has its data and a pointer to the frequency node it is under
// data is currently an object to all any data type as "data"
// this may change as the implementation continues
class LFUItem {
    Object data;
    FreqNode parent;

    public LFUItem(Object data, FreqNode parent) {
        this.data = data;
        this.parent = parent;
    }
}

// The actual cache class.
// holds the head of the doubly linked list
// and the hashmap that holds the LFUItems
// referenced by key
class LFUCache {
    HashMap<Integer, LFUItem> byKey;
    FreqNode head;

    public LFUCache() {
        byKey = new HashMap<>();
        head = new FreqNode();
    }
}

// class that holds all the methods and an instance of
// the cache
public class LFU {
    LFUCache lfuCache = new LFUCache();

    // Access (fetch) an element from the LFU cache, simultaneously incrementing its
    // usage count
    public Object access(Integer key) {
        LFUItem tmp = lfuCache.byKey.get(key);
        if (tmp == null) {
            throw new NullPointerException("Key does not exist");
        }

        FreqNode freq = tmp.parent;
        FreqNode next_freq = freq.next;

        if (next_freq == lfuCache.head || next_freq.value != freq.value + 1) {
            next_freq = getNewNode(freq.value + 1, freq, freq.next);
        }
        next_freq.items.add(key);
        tmp.parent = next_freq;

        freq.items.remove(key);
        if (freq.items.size() == 0) {
            deleteNode(freq);
        }

        return tmp.data;
    }

    // Insert a new element into the LFU cache
    public void insert(int key, int value) {
        if (lfuCache.byKey.containsKey(key)) {
            throw new IllegalArgumentException ("Key already exists");
        }

        FreqNode freq = lfuCache.head.next;
        if (freq.value != 1) {
            freq = getNewNode(1, lfuCache.head, freq);
        }
        freq.items.add(key);
        lfuCache.byKey.put(key, new LFUItem(value, freq));
    }

    // Fetches an item with the least usage count (the least frequently used item) in the cache
    public LFUItem getLfuItem() {
        if (lfuCache.byKey.size() == 0) {
            throw new NullPointerException("The set is empty");
        }

        return lfuCache.byKey.get(lfuCache.head.next.items.get(0));
    }

    // Helper functions

    public void deleteNode(FreqNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public FreqNode getNewNode(int value, FreqNode prev, FreqNode next) {
        FreqNode newNode = new FreqNode();
        newNode.value = value;
        newNode.prev = prev;
        newNode.next = next;
        prev.next = newNode;
        next.prev = newNode;

        return newNode;
    }
}
