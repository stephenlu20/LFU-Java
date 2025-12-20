import java.util.*;

// O(1) algorithm for implementing the LFU cache eviction scheme

// My understanding and why/how I've implemented this paper's algorithm:

// initially, the psuedo code describes using a SET() to hold the node list
// under the frequency node for ease of implmentation.
// the paper describes an O(1) reference to the first element of the SET()
// so I used Java's ArrayList to be similar in that regard
// the rest of the implementation is very straight forward when following the
// psuedo code

// the harder part came when trying to avoid using ArrayList and use a doubly
// linked list instead, since the paper in section 5 describes it as a linked list
// for the diagram. My understanding is that we hold keys in cache, aka
// we know what key belongs to which LFUItem. In that case, when trying to use
// a doubly linked list as well as retain O(1) run time, each LFUItem needs to
// hold a reference to the location of its respective item node on the node list
// With this, as well as knowing we have cached keys, we can use the key
// to grab the node and quickly remove it from a node list instead of searching
// when we want to change the access frequency of a particular LFUItem

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
// ArrayList was originally used instead of a Java Set or a linked list for simplicity of implementation, since the
// only use for the items set was to do a O(1) reference to the first element

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
// as well a reference to its respective item node
// data is currently an object to allow any data type as "data"

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

// public class that holds all the methods and an instance of the cache

public class LFU {
    LFUCache lfuCache = new LFUCache();

    // Access (fetch) an element from the LFU cache, 
    // simultaneously incrementing its usage count
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
        freq.items.remove(node);
        
        next_freq.items.add(node);
        tmp.parent = next_freq;

        if (freq.items.isEmpty()) {
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
