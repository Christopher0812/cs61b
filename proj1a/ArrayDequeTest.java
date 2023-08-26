import static org.junit.Assert.*;

import org.junit.Test;

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
}
