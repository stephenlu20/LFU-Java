import java.util.*;

// O(1) algorithm for implementing the LFU cache eviction scheme

// Additional implementation so that items does not need to be an ArrayList
// but is instead a doubly linked list, as originally intended
// since using SET was for simplicity, but not the original described algorithm
class Node<T> {
    T next;
    T prev;
}

class ItemNode extends Node<ItemNode> {
    int key;

    public ItemNode(int key) {
        this.key = key;
        next = null;
        prev = null;
    }
}

class ItemLinkedList {
    ItemNode head;
    ItemNode tail;

    public ItemLinkedList() {
        head = null;
        tail = null;
    }

    // Add a new node to the end of the linked list
    public void add(ItemNode node) {
        node.next = null;
        node.prev = null;

        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    // Remove a node
    public void remove(ItemNode node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.next = null;
        node.prev = null;
    }

    public boolean isEmpty() {
        return head == null;
    }
}

// Frequency is tracked using a doubly linked list
// each node holds a value, which is an int representing the frequency of access
// and a doubly linked list of items, which are the keys that are used to reference the LFUItem hashmap
// ArrayList was originally used instead of a Java Set or a linked listfor simplicity of implementation, since the
// only use for the items set was to do a O(1) reference to the first item

class FreqNode extends Node<FreqNode>{
    int value;
    ItemLinkedList items;

    public FreqNode() {
        next = this;
        prev = this;
        value = 0;
        items = new ItemLinkedList();
    }
}

// Each item in the LFU has its data and a pointer to the frequency node it is under
// data is currently an object to all any data type as "data"
// this may change as the implementation continues
class LFUItem {
    Object data;
    FreqNode parent;
    ItemNode itemNode;

    public LFUItem(Object data, FreqNode parent, ItemNode itemNode) {
        this.data = data;
        this.parent = parent;
        this.itemNode = itemNode;
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
    public Object access(ItemNode node) {
        LFUItem tmp = lfuCache.byKey.get(node.key);
        if (tmp == null) {
            throw new NullPointerException("Key does not exist");
        }

        FreqNode freq = tmp.parent;
        FreqNode next_freq = freq.next;

        if (next_freq == lfuCache.head || next_freq.value != freq.value + 1) {
            next_freq = getNewNode(freq.value + 1, freq, freq.next);
        }
        next_freq.items.add(node);
        tmp.parent = next_freq;

        freq.items.remove(node);
        if (freq.items.head == null && freq.items.tail == null) {
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
        ItemNode node = new ItemNode(key);
        freq.items.add(node);
        lfuCache.byKey.put(key, new LFUItem(value, freq, node));
    }

    // Fetches an item with the least usage count (the least frequently used item) in the cache
    public LFUItem getLfuItem() {
        if (lfuCache.byKey.size() == 0) {
            throw new NullPointerException("The set is empty");
        }

        return lfuCache.byKey.get(lfuCache.head.next.items.head.key);
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
