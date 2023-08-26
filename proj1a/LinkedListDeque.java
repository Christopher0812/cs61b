
public class LinkedListDeque<T> implements Deque<T> {
    private class StuffNode {
        public T item;
        public StuffNode prev;
        public StuffNode next;

        public StuffNode(T item, StuffNode prev, StuffNode next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private StuffNode headNode;
    private int size;

    public LinkedListDeque() {
        headNode = new StuffNode(null, null, null);
        headNode.prev = headNode;
        headNode.next = headNode;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        StuffNode tempNode = new StuffNode(item, headNode, headNode.next);
        headNode.next.prev = tempNode;
        headNode.next = tempNode;
        size++;
    }

    @Override
    public void addLast(T item) {
        StuffNode tempNode = new StuffNode(item, headNode.prev, headNode);
        headNode.prev.next = tempNode;
        headNode.prev = tempNode;
        size++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            StuffNode tempNode = headNode.next;

            headNode.next = tempNode.next;
            tempNode.next.prev = headNode;
            tempNode.prev = null;
            tempNode.next = null;
            size--;

            return tempNode.item;
        }
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            StuffNode tempNode = headNode.prev;

            headNode.prev = tempNode.prev;
            tempNode.prev.next = headNode;
            tempNode.prev = null;
            tempNode.next = null;
            size--;

            return tempNode.item;
        }
    }

    @Override
    public void printDeque() {
        StuffNode p = headNode.next;
        String str = "";

        while (p.item != null) {
            str += p.item.toString();
            str += " ";
            p = p.next;
        }

        System.out.println(str);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        StuffNode p = headNode.next;

        for (int i = 0; i < index; i++) {
            p = p.next;
        }

        return p.item;
    }
}
