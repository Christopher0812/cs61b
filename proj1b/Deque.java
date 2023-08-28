public interface Deque<T> {
    void addFirst(T item);

    void addLast(T item);

    T removeFirst();

    T removeLast();

    boolean isEmpty();

    int size();

    void printDeque();

    T get(int index);
}
