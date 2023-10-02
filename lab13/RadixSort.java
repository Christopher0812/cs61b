import java.util.Arrays;

/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 */
public class RadixSort {
    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     * @return String[] the sorted array
     */
    public static String[] sort(String[] asciis) {
        String[] asciisCopy = asciis.clone();

        int max = Integer.MIN_VALUE;
        for (String str : asciis) {
            max = max > str.length() ? max : str.length();
        }

        for (int i = 0; i < max; i++) {
            sortHelperLSD(asciisCopy, max - i - 1);
        }

        return asciisCopy;
    }

    private static char getCharAt(String str, int index) {
        if (str == null || str.length() <= index) {
            return 0;
        }
        return str.charAt(index);
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     *
     * @param asciis Input array of Strings
     * @param index  The position to sort the Strings on.
     */
    private static void sortHelperLSD(String[] asciis, int index) {
        // gather all the counts for each value
        int[] counts = new int[256];
        for (String i : asciis) {
            counts[getCharAt(i, index)]++;
        }

        int pos = 0;
        for (int i = 0; i < counts.length; i += 1) {
            int temp = counts[i];
            counts[i] = pos;
            pos += temp;
        }

        String[] sorted = new String[asciis.length];
        for (int i = 0; i < asciis.length; i += 1) {
            String item = asciis[i];
            int place = counts[getCharAt(item, index)];
            sorted[place] = item;
            counts[getCharAt(item, index)] += 1;
        }

        System.arraycopy(sorted, 0, asciis, 0, asciis.length);
    }

    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start  int for where to start sorting in this method (includes String at start)
     * @param end    int for where to end sorting in this method (does not include String at end)
     * @param index  the index of the character the method is currently sorting on
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }

    public static void main(String[] args) {
        String[] students = new String[]{"Vanessa", "Alice", "Am", "Ethan"};

        String[] expected = new String[]{"Alice", "Am", "Ethan", "Vanessa"};

        System.out.println(Arrays.toString(expected));
        System.out.println(Arrays.toString(RadixSort.sort(students)));
    }
}
