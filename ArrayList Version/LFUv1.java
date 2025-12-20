import java.util.*;

// Creating another class to have the
// ArrayList version of the implementation
// the difference isn't severe, but
// this is just for ease of reference

class FreqNode{
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

class LFUItem {
    Object data;
    FreqNode parent;

    public LFUItem(Object data, FreqNode parent) {
        this.data = data;
        this.parent = parent;
    }
}

class LFUCache {
    HashMap<Integer, LFUItem> byKey;
    FreqNode head;

    public LFUCache() {
        byKey = new HashMap<>();
        head = new FreqNode();
    }
}

class LFUv1 {
    LFUCache lfuCache = new LFUCache();

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
