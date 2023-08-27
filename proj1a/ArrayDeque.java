/**
 * A deque based on array.
 * @param <T>
 * @author Filtee
 */

public class ArrayDeque<T> {
    private T[] items;
    private int head;
    private int tail;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[3];
        head = 0;
        tail = 1;
        size = 0;
    }

    /**
     * Resize the array when the array is full, or the size is way less than the array length.
     * @param capacity The new length of the new length of the array.
     */
    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            temp[i + 1] = get(i);
        }
        items = temp;

        head = 0;
        tail = size + 1;
    }

    /**
     * Redirect the pointers in case they go out of bound.
     */
    private void flushPointers() {
        /* When the pointer index is less than 0. */
        if (head < 0) {
            head += items.length;
        }
        if (tail < 0) {
            tail += items.length;
        }

        /* When the pointer index is more than the length of the array. */
        head = head % items.length;
        tail = tail % items.length;
    }

    public void addFirst(T item) {
        flushPointers();

        /* If the array is full, resize it to be bigger. */
        if (head == tail) {
            resize(items.length * 2);
        }

        /* Store the new item. */
        items[head] = item;
        head--;
        size++;
    }

    public void addLast(T item) {
        flushPointers();

        /* If the array is full, resize it to be bigger. */
        if (head == tail) {
            resize(items.length * 2);
        }

        /* Store the new item. */
        items[tail] = item;
        tail++;
        size++;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            /* Remove the item. */
            head++;
            size--;
            flushPointers();

            T ret = items[head];
            items[head] = null;

            /* Checkout if the array needs to be resized. */
            if (size <= items.length / 2) {
                resize(items.length / 2 + 1);
            }
            return ret;
        }
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            /* Remove the item. */
            tail--;
            size--;
            flushPointers();

            T ret = items[tail];
            items[tail] = null;

            /* Checkout if the array needs to be resized. */
            if (size <= items.length / 2) {
                resize(items.length / 2 + 1);
            }
            return ret;
        }
    }

    public void printDeque() {
        flushPointers();

        String str = "";
        int p = head + 1;

        while (p != tail) {
            str += items[p].toString();
            str += " ";
            p = p + 1 % items.length;
        }

        System.out.println(str);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        int pos = (head + index + 1) % items.length;
        return items[pos];
    }
}
