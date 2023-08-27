import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Optional;

public class ArrayDequeTest {
    @Test
    public void addRemoveTest() {
        System.out.println("Running add/remove test.");

        ArrayDeque<String> deque2Test = new ArrayDeque<>();

        deque2Test.addFirst("Middle");
        deque2Test.addFirst("Front");
        deque2Test.addLast("Last");
        assertEquals("Front", deque2Test.get(0));
        assertEquals("Middle", deque2Test.get(1));
        assertEquals("Last", deque2Test.get(2));
    }

    @Test
    public void addRemoveTest1() {
        System.out.println("Running add/remove test.");

        ArrayDeque<String> deque2Test = new ArrayDeque<>();

        deque2Test.addFirst("2");
        deque2Test.addFirst("1");
        deque2Test.addLast("3");
        deque2Test.addFirst("0");
        deque2Test.addLast("4");

        assertEquals("0", deque2Test.get(0));
        assertEquals("1", deque2Test.get(1));
        assertEquals("2", deque2Test.get(2));
        assertEquals("3", deque2Test.get(3));
        assertEquals("4",deque2Test.get(4));
    }
}
