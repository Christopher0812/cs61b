import javax.lang.model.type.MirroredTypeException;

public class ArrayDeque<T> implements Deque<T> {
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

    public void resize() {
        T[] temp = (T[]) new Object[items.length * 2];
        for (int i = 0; i < size; i++) {
            temp[i + 1] =  get(i);
        }
        items = temp;

        head = 0;
        tail = size + 1;
    }

    public void flushPointers() {
        if (head == -1) {
            head = items.length - 1;
        }

        tail = tail % items.length;
    }

    @Override
    public void addFirst(T item) {
        flushPointers();

        if (head == tail) {
            resize();
        }

        items[head] = item;
        head--;
        size++;
    }

    @Override
    public void addLast(T item) {
        flushPointers();

        if (head == tail) {
            resize();
        }

        items[tail] = item;
        tail++;
        size++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            flushPointers();
            head++;
            size--;
            flushPointers();

            T ret = items[head];
            items[head] = null;
            return ret;
        }
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            flushPointers();
            tail--;
            size--;
            flushPointers();

            T ret = items[tail];
            items[tail] = null;
            return ret;
        }
    }

    @Override
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
        int pos = (head + index + 1) % items.length;
        return items[pos];
    }
}
