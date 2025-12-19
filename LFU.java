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
