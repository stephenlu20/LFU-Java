import java.util.*;

// O(1) algorithm for implementing the LFU cache eviction scheme


// Frequency is tracked using a doubly linked list
// each node holds a value, which is an int representing the frequency of access
// and a Set of items, which are the keys that are used to reference the LFUItem hashmap
class FreqNode {
    FreqNode next;
    FreqNode prev;
    int value;
    Set<Integer> items;

    public FreqNode() {
        next = this;
        prev = this;
        value = 0;
        items = new LinkedHashSet<>();
    }
}

// Each item in the LFU has its data and a pointer to the frequency node it is under
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

    public FreqNode access(Integer key) {
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
    }

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
