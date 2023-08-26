public interface Deque<T> {
    void addFirst(T item);

    void addLast(T item);

    T removeFirst();

    T removeLast();

    void printDeque();

    boolean isEmpty();

    int size();

    T get(int index);
}
