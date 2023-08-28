
public class Palindrome {
    /**
     * A method to return a { Deque } of the { String } taken in,
     * where characters appear in the same order.
     *
     * @param word The word that needs to be transformed into a { Deque }.
     * @return The deque of the { String } taken in.
     */
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> dequeToReturn = new LinkedListDeque<>();

        for (int i = 0; i < word.length(); i++) {
            dequeToReturn.addLast(word.charAt(i));
        }
        return dequeToReturn;
    }

    /**
     * A method to check that whether a { String } of word is a palindrome.
     *
     * @param word The word that needs to be checked.
     * @return true, if the given word is a palindrome.
     */
    public boolean isPalindrome(String word) {
        return isPalindrome(wordToDeque(word));
    }

    /**
     * Helper method of the { isPalindrome(String word) } method,
     * which uses a recursion way to achieve.
     *
     * @param dequeOfWord The deque of the word that needs to be checked.
     * @return true, if the given word is a palindrome.
     */
    private boolean isPalindrome(Deque<Character> dequeOfWord) {
        if (dequeOfWord.size() == 0 || dequeOfWord.size() == 1) {
            /* If there are only 0 or 1 character left, return true. */
            return true;
        } else if (dequeOfWord.removeFirst() != dequeOfWord.removeLast()) {
            /* If the first character doesn't match the last character, return false. */
            return false;
        } else {
            /* Continue recursion. */
            return isPalindrome(dequeOfWord);
        }
    }

    /**
     * The method will return true if the word is a palindrome,
     * according to the character comparison test,
     * provided by the CharacterComparator passed in as argument cc.
     *
     * @param word The word that needs to be checked.
     * @param cc   { CharacterComparator } passed in as argument.
     * @return true, if the given word is an off-by-one palindrome .
     */
    public boolean isPalindrome(String word, CharacterComparator cc) {
        return isPalindrome(wordToDeque(word), cc);
    }

    /**
     * Helper method of the { isPalindrome(String word, CharacterComparator cc) } method,
     * which uses a recursion way to achieve.
     *
     * @param dequeOfWord The deque of the word that needs to be checked.
     * @param cc          { CharacterComparator } passed in as argument.
     * @return true, if the given word is a palindrome that meets the rules of cc.
     */
    private boolean isPalindrome(Deque<Character> dequeOfWord, CharacterComparator cc) {
        if (dequeOfWord.size() == 0 || dequeOfWord.size() == 1) {
            /* If there are only 0 or 1 character left, return true. */
            return true;
        } else if (!cc.equalChars(dequeOfWord.removeFirst(), dequeOfWord.removeLast())) {
            /* If the first character and the last character is NOT off-by-one, return false. */
            return false;
        } else {
            /* Continue recursion. */
            return isPalindrome(dequeOfWord, cc);
        }
    }
}
