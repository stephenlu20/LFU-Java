public class LFUTest {

    public static void main(String[] args) {
        LFUTest test = new LFUTest();

        test.testInsertAndAccessSingleItem();
        test.testFrequencyIncrement();
        test.testMultipleItemsDifferentFrequencies();
        test.testLFUReturnsLeastFrequentlyUsed();
        test.testDeleteFrequencyNodeWhenEmpty();
        test.testAccessNonExistentKey();
        test.testInsertDuplicateKey();
        test.testLFUOnEmptyCache();

        System.out.println("\nALL TESTS COMPLETED");
    }

    private void testInsertAndAccessSingleItem() {
        System.out.println("Running testInsertAndAccessSingleItem");

        LFU lfu = new LFU();
        lfu.insert(1, 100);

        Object value = lfu.access(1);
        if (!value.equals(100)) {
            throw new RuntimeException("Expected value 100, got " + value);
        }

        System.out.println("PASSED");
    }

    private void testFrequencyIncrement() {
        System.out.println("Running testFrequencyIncrement");

        LFU lfu = new LFU();
        lfu.insert(1, 100);

        lfu.access(1);
        lfu.access(1);

        LFUItem item = lfu.lfuCache.byKey.get(1);
        if (item.parent.value != 3) {
            throw new RuntimeException(
                "Expected frequency 3, got " + item.parent.value
            );
        }

        System.out.println("PASSED");
    }

    private void testMultipleItemsDifferentFrequencies() {
        System.out.println("Running testMultipleItemsDifferentFrequencies");

        LFU lfu = new LFU();
        lfu.insert(1, 100);
        lfu.insert(2, 200);
        lfu.insert(3, 300);

        lfu.access(1); // increment frequency to 2
        lfu.access(1); // increment frequency to 3
        lfu.access(2); // increment frequency to 2

        LFUItem item1 = lfu.lfuCache.byKey.get(1);
        LFUItem item2 = lfu.lfuCache.byKey.get(2);
        LFUItem item3 = lfu.lfuCache.byKey.get(3);

        if (item1.parent.value != 3) {
            throw new RuntimeException("Item 1 expected freq 3");
        }
        if (item2.parent.value != 2) {
            throw new RuntimeException("Item 2 expected freq 2");
        }
        if (item3.parent.value != 1) {
            throw new RuntimeException("Item 3 expected freq 1");
        }

        System.out.println("PASSED");
    }

    private void testLFUReturnsLeastFrequentlyUsed() {
        System.out.println("Running testLFUReturnsLeastFrequentlyUsed");

        LFU lfu = new LFU();
        lfu.insert(1, 100);
        lfu.insert(2, 200);
        lfu.insert(3, 300);

        lfu.access(1);
        lfu.access(1);
        lfu.access(2);

        LFUItem lfuItem = lfu.getLfuItem();
        if (!lfuItem.data.equals(300)) {
            throw new RuntimeException(
                "Expected LFU item data 300, got " + lfuItem.data
            );
        }

        System.out.println("PASSED");
    }

    private void testDeleteFrequencyNodeWhenEmpty() {
        System.out.println("Running testDeleteFrequencyNodeWhenEmpty");

        LFU lfu = new LFU();
        lfu.insert(1, 100);

        // Move item from freq 1 to freq 2
        lfu.access(1);

        // After access, freq 1 node should be deleted
        if (lfu.lfuCache.head.next.value == 1) {
            throw new RuntimeException("Frequency 1 node was not deleted");
        }

        System.out.println("PASSED");
    }

    private void testAccessNonExistentKey() {
        System.out.println("Running testAccessNonExistentKey");

        LFU lfu = new LFU();
        boolean threw = false;

        try {
            lfu.access(99);
        } catch (NullPointerException e) {
            threw = true;
        }

        if (!threw) {
            throw new RuntimeException("Expected NullPointerException");
        }

        System.out.println("PASSED");
    }

    private void testInsertDuplicateKey() {
        System.out.println("Running testInsertDuplicateKey");

        LFU lfu = new LFU();
        lfu.insert(1, 100);

        boolean threw = false;
        try {
            lfu.insert(1, 200);
        } catch (IllegalArgumentException e) {
            threw = true;
        }

        if (!threw) {
            throw new RuntimeException("Expected IllegalArgumentException");
        }

        System.out.println("PASSED");
    }

    private void testLFUOnEmptyCache() {
        System.out.println("Running testLFUOnEmptyCache");

        LFU lfu = new LFU();
        boolean threw = false;

        try {
            lfu.getLfuItem();
        } catch (NullPointerException e) {
            threw = true;
        }

        if (!threw) {
            throw new RuntimeException("Expected NullPointerException");
        }

        System.out.println("PASSED");
    }
}
