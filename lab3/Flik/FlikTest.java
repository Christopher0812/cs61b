import org.junit.Test;

import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void testIsSameNumber() {
        int i = 0;
        for (int j = 0; i < 500; ++i, ++j) {
            assertTrue(Flik.isSameNumber(i, j));
        }
    }
}
