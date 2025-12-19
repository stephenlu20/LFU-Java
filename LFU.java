import java.util.*;

class FreqNode {
    FreqNode next;
    FreqNode prev;
    int value;
    Set<LFUItem> items;

    public FreqNode() {
        next = this;
        prev = this;
        value = 0;
        items = new LinkedHashSet<>();
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

class LFUItem {
    Object data;
    FreqNode parent;

    public LFUItem(Object data, FreqNode parent) {
        this.data = data;
        this.parent = parent;
    }
}

class LFUCache {
    HashMap<FreqNode, LFUItem> byKey;
    FreqNode head;

    public LFUCache() {
        byKey = new HashMap<>();
        head = new FreqNode();
    }
}

public class LFU {
    LFUCache lfuCache = new LFUCache();
}
