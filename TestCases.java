import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Test cases for Fall'19 CS251 Project 5.
 *
 * These test cases aren't rigorous, and really not that complex. If you have easily found issues
 * with your AddWord() method, this should spot those for you. But you may pass these and have more
 * intricate issues.
 *
 * Failing any of these test cases guarantees that you will fail test
 * cases on Vocareum. The converse is not true.
 *
 * @author Andrew Orlowski, orlowska@purdue.edu
 * @version 11/25/2019
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCases {

    WordProcessor wordProcessor = new WordProcessor();
    int counter = 0;

    public void tree_counter(WordProcessor.Node node) {
        if (node == null) {
            return;
        }

        counter++;
        tree_counter(node.left);
        tree_counter(node.equal);
        tree_counter(node.right);
    }

    @Rule
    public Timeout globalTimeout = new Timeout(2, TimeUnit.SECONDS);

    @Test
    public void Test_A_clear() {
        wordProcessor.addWord("testing");
        wordProcessor.clear();
        assertEquals("Ensure that your clear() method properly clears the tree.", null, wordProcessor.getWordTrie());
    }


    @Test
    public void test_B_AddWord_Simple() {
        wordProcessor.addWord("ABCDE");
        WordProcessor.Node traversalNode = wordProcessor.getWordTrie();

        if (traversalNode == null) {
            fail("Ensure that you are initializing the root after a word is added!");
        }

        tree_counter(traversalNode);
        assertEquals("Ensure that you have inserted the correct number of nodes!", 5, counter);
        counter = 0;


        try {
            for (int i = 65; i < 70; i++) {

                if (i != 69) {
                    assertEquals("Ensure that you aren't incorrectly flagging nodes as isEnd!", false, traversalNode.isEnd);
                } else {
                    assertEquals("Ensure that you are correctly flagging nodes as isEnd!", true, traversalNode.isEnd);
                }

                assertEquals("Ensure that you are correctly inserting characters!", (char)i, traversalNode.c);
                traversalNode = traversalNode.equal;
            }
        } catch (NullPointerException npe) {
            fail("Your program threw a null pointer exception! Ensure that you have the correct amount of nodes..");
        }
    }

    @Test
    public void test_C_AddWord_Intermediate() {
        test_B_AddWord_Simple();
        // Note: This works in conjunction with test_B_AddWord_Simple(). If you are failing that, you will also fail this.
        WordProcessor.Node traversalNode = wordProcessor.getWordTrie();

        wordProcessor.addAllWords(new String[] {"ABC", "Jay", "Jeff"});


        tree_counter(traversalNode);
        assertEquals("Ensure that you have inserted the correct number of nodes!", 11, counter);
        counter = 0;


        assertEquals("Ensure that you are correctly inserting nodes!", 'J', traversalNode.right.c);
        assertEquals("Ensure that you are correctly inserting nodes!", 'a', traversalNode.right.equal.c);
        assertEquals("Ensure that you are correctly inserting nodes!", 'e', traversalNode.right.equal.right.c);
        assertEquals("Ensure that you are correctly inserting nodes!", 'f', traversalNode.right.equal.right.equal.c);
        assertEquals("Ensure that you are correctly inserting nodes!", 'f', traversalNode.right.equal.right.equal.equal.c);
        assertEquals("Ensure that you are correctly inserting nodes!", true, traversalNode.right.equal.right.equal.equal.isEnd);
        assertEquals("Ensure that you are correctly inserting nodes!", 'f', traversalNode.right.equal.right.equal.c);
        assertEquals("Ensure that you are correctly inserting nodes!", 'y', traversalNode.right.equal.equal.c);
        assertEquals("Ensure that you are correctly inserting nodes!", true, traversalNode.right.equal.equal.isEnd);
        assertEquals("Ensure that you correctly update the isEnd flag after a word that is a prefix of another word was added!", true, traversalNode.equal.equal.isEnd);
    }

    @Test
    public void test_D_AddWord_Expert() {
        // Because it's hard for me to go manually check everything, I just didn't include that part.
        // This will only count the number of nodes and verifies that the number of nodes is correct.
        // If it's incorrect, the best way to figure out where is likely stepping through the tree with a debugger.

        wordProcessor.addAllWords(new String[] {"ABCDE", "ABC", "JAY", "JEFF", "JOHN", "JOE", "Jeremy", "Jeremiah", "Mongo", "Mango", "mousepad", "monitor", "tab",
                                                "Word", "box", "Navigation", "Emphasis", "Intense"});

        tree_counter(wordProcessor.getWordTrie());
        assertEquals("Ensure that the number of nodes is correct after inserting many words!", 80, counter);
    }

    @Test
    public void test_Y_SearchWord() {
        test_B_AddWord_Simple();
        test_C_AddWord_Intermediate();

        // Note: This will fail if the AddWord test cases are failing.

        assertEquals("Ensure that 'true' is returned if the word exists in the tree! (1)", true, wordProcessor.wordSearch("ABCDE"));
        assertEquals("Ensure that 'true' is returned if the word exists in the tree! (2)", true, wordProcessor.wordSearch("ABC"));
        assertEquals("Ensure that 'true' is returned if the word exists in the tree! (3)", true, wordProcessor.wordSearch("Jay"));
        assertEquals("Ensure that 'true' is returned if the word exists in the tree! (4)", true, wordProcessor.wordSearch("Jeff"));

        assertEquals("Ensure that 'false' is returned if the word does NOT exist in the tree! (1)", false, wordProcessor.wordSearch("abc"));
        assertEquals("Ensure that 'false' is returned if the word does NOT exist in the tree! (2)", false, wordProcessor.wordSearch("jay"));
        assertEquals("Ensure that 'false' is returned if the word does NOT exist in the tree! (3)", false, wordProcessor.wordSearch("Ja"));
        assertEquals("Ensure that 'false' is returned if the word does NOT exist in the tree! (4)", false, wordProcessor.wordSearch("A"));
        assertEquals("Ensure that 'false' is returned if the word does NOT exist in the tree! (5)", false, wordProcessor.wordSearch("Qua"));
        assertEquals("Ensure that 'false' is returned if the word does NOT exist in the tree! (6)", false, wordProcessor.wordSearch("Shoelace"));
    }

    @Test
    public void test_Z_autoCompleteOptions() {
        test_B_AddWord_Simple();
        test_C_AddWord_Intermediate();

        // Note: This will fail if the AddWord test cases are failing.

        assertEquals("Ensure that the number of autocomplete suggestions is correct! (1)", 2, wordProcessor.autoCompleteOptions("A").size());
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"A\"!", true, wordProcessor.autoCompleteOptions("A").contains("ABC"));
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"A\"!", true, wordProcessor.autoCompleteOptions("A").contains("ABCDE"));

        assertEquals("Ensure that the number of autocomplete suggestions is correct! (2)", 2, wordProcessor.autoCompleteOptions("AB").size());
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"AB\"!", true, wordProcessor.autoCompleteOptions("AB").contains("ABC"));
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"AB\"!", true, wordProcessor.autoCompleteOptions("AB").contains("ABCDE"));

        assertEquals("Ensure that the number of autocomplete suggestions is correct! (4)", 1, wordProcessor.autoCompleteOptions("ABCD").size());
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"ABCD\"!", true, wordProcessor.autoCompleteOptions("ABCD").contains("ABCDE"));

        assertEquals("Ensure that the number of autocomplete suggestions is correct! (5)", 2, wordProcessor.autoCompleteOptions("J").size());
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"J\"!", true, wordProcessor.autoCompleteOptions("J").contains("Jay"));
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"J\"!", true, wordProcessor.autoCompleteOptions("J").contains("Jeff"));

        assertEquals("Ensure that the number of autocomplete suggestions is correct! (6)", 1, wordProcessor.autoCompleteOptions("Ja").size());
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"Ja\"!", true, wordProcessor.autoCompleteOptions("Ja").contains("Jay"));

        assertEquals("Ensure that the number of autocomplete suggestions is correct! (7)", 1, wordProcessor.autoCompleteOptions("Je").size());
        assertEquals("Ensure that the autocomplete options are correct when given prefix \"Je\"!", true, wordProcessor.autoCompleteOptions("Je").contains("Jeff"));

        assertEquals("Ensure that the number of autocomplete suggestions is correct when the word is not in the tree! (1)", 0, wordProcessor.autoCompleteOptions("ogjreogj").size());
        assertEquals("Ensure that the number of autocomplete suggestions is correct when the word is not in the tree! (2)", 0, wordProcessor.autoCompleteOptions("Hello").size());
        assertEquals("Ensure that the number of autocomplete suggestions is correct when the word is not in the tree! (3)", 0, wordProcessor.autoCompleteOptions(";qwr-").size());
        assertEquals("Ensure that the number of autocomplete suggestions is correct when the word is not in the tree! (4)", 0, wordProcessor.autoCompleteOptions("Vaie").size());
        assertEquals("Ensure that the number of autocomplete suggestions is correct when the word is not in the tree! (5)", 0, wordProcessor.autoCompleteOptions("NotInTree").size());

        assertEquals("Ensure that the number of autocomplete suggestions is correct when the prefix exists in the tree already! (1)", 0, wordProcessor.autoCompleteOptions("ABC").size());
        assertEquals("Ensure that the number of autocomplete suggestions is correct when the prefix exists in the tree already! (2)", 0, wordProcessor.autoCompleteOptions("ABCDE").size());
        assertEquals("Ensure that the number of autocomplete suggestions is correct when the prefix exists in the tree already! (3)", 0, wordProcessor.autoCompleteOptions("Jay").size());
        assertEquals("Ensure that the number of autocomplete suggestions is correct when the prefix exists in the tree already! (4)", 0, wordProcessor.autoCompleteOptions("Jeff").size());
    }

}
