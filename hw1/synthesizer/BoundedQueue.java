package synthesizer;

import java.util.Iterator;

public interface BoundedQueue<T> extends Iterable<T>{
    int capacity();     // return size of the buffer
    int fillCount();    // return number of items currently in the buffer
    void enqueue(T x);  // add item x to the end
    T dequeue();        // delete and return item from the front
    T peek();           // return (but do not delete) item from the front

    Iterator<T> iterator();

    /**
     * Check out if the bounded queue is empty.
     *
     * @return True, if the buffer is empty
     */
    default boolean isEmpty()      {
        return fillCount() == 0;
    }

    /**
     * Check out if the bounded queue is full.
     *
     * @return True, if the buffer is full
     */
    default boolean isFull() {
        return fillCount() == capacity();
    }
}
