import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for Fall'19 CS251 Project 5.
 *
 * Everything is the same as testCases.java with the exception that test_D is now
 * much, much, much more rigorous and you can modify it however you'd like, and it will
 * still pass or fail appropriately. It's also easier to debug.
 *
 * Failing any of these test cases guarantees that you will fail test
 * cases on Vocareum. The converse is not true.
 *
 * @author Andrew Orlowski, orlowska@purdue.edu
 * @version 11/27/2019
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testCasesAdvanced {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final int VersionID = 2675;

    WordProcessor wordProcessor = new WordProcessor();

    public int tree_counter(WordProcessor.Node node) {
        if (node == null) {
            return 0;
        }
        int counter = 1;


        counter += tree_counter(node.left);
        counter += tree_counter(node.equal);
        counter += tree_counter(node.right);
        return counter;
    }

    public WordProcessor.Node[] tree_traverse_compare(WordProcessor.Node localWP, WordProcessor.Node solutionWP) {
        if (localWP == null && solutionWP == null) {
            return null;
        }

        if (localWP == null) {
            return new WordProcessor.Node[] {localWP, solutionWP};
        }

        if (solutionWP == null) {
            return new WordProcessor.Node[] {localWP, solutionWP};
        }

        if (localWP.c != solutionWP.c || localWP.isEnd != solutionWP.isEnd) {
            return new WordProcessor.Node[] {localWP, solutionWP};
        }

        WordProcessor.Node[] left = tree_traverse_compare(localWP.left, solutionWP.left);
        WordProcessor.Node[] equal = tree_traverse_compare(localWP.equal, solutionWP.equal);
        WordProcessor.Node[] right = tree_traverse_compare(localWP.right, solutionWP.right);

        if (left == null && equal == null && right == null) {
            return null;
        }

        if (left != null) {
            return left;
        } else if (equal != null) {
            return equal;
        } else {
            return right;
        }
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @Test(timeout = 2000)
    public void Test_A_clear() {
        wordProcessor.addWord("testing");
        wordProcessor.clear();
        assertEquals("Ensure that your clear() method properly clears the tree.", null, wordProcessor.getWordTrie());
    }


    @Test(timeout = 2000)
    public void test_B_AddWord_Simple() {
        wordProcessor.addWord("ABCDE");
        WordProcessor.Node traversalNode = wordProcessor.getWordTrie();

        if (traversalNode == null) {
            fail("Ensure that you are initializing the root after a word is added!");
        }

        tree_counter(traversalNode);
        assertEquals("Ensure that you have inserted the correct number of nodes!", 5, tree_counter(wordProcessor.getWordTrie()));


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

    @Test(timeout = 2000)
    public void test_C_AddWord_Intermediate() {
        test_B_AddWord_Simple();
        // Note: This works in conjunction with test_B_AddWord_Simple(). If you are failing that, you will also fail this.
        WordProcessor.Node traversalNode = wordProcessor.getWordTrie();

        wordProcessor.addAllWords(new String[] {"ABC", "Jay", "Jeff"});


        tree_counter(traversalNode);
        assertEquals("Ensure that you have inserted the correct number of nodes!", 11,tree_counter(wordProcessor.getWordTrie()));


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

    @Test(timeout = 2000)
    public void test_D_AddWord_Expert() {
        // You can modify this test case yourself if you are on the client-server pairing.
        // It will always return the correct solution trie in the variable solutionWP.
        // All you need to do is change the list of wordsToAdd.

        String[] wordsToAdd = new String[] {"ABCDE", "ABC", "JAY", "JEFF", "JOHN", "JOE", "Jeremy", "Jeremiah", "Mongo", "Mango", "mousepad", "monitor", "tab",
                "Word", "box", "Navigation", "Emphasis", "Intense"};

        wordProcessor.addAllWords(wordsToAdd);
        try {
            WordProcessor solutionWP = testcasesClient.headlessClient(wordsToAdd, "addword");
            if (solutionWP == null) {
                fail("Please update to the latest version of test cases and test client on the Piazza post!");
            }

            tree_counter(wordProcessor.getWordTrie());
            assertEquals("Ensure that the number of nodes is correct after inserting variable words!", tree_counter(solutionWP.getWordTrie()), tree_counter(wordProcessor.getWordTrie()));

            WordProcessor.Node[] firstFail = tree_traverse_compare(wordProcessor.getWordTrie(), solutionWP.getWordTrie());

            // firstFail[0] is the local node, that is your node.
            // firstFail[1] is the solution node, the correct node.
            // If failing this, I recommend setting a break point and walking through the solution trie.


            if (firstFail != null) {
                if (firstFail[0] != null && firstFail[1] != null) {
                    assertEquals("Ensure that you are correctly adding nodes!", firstFail[0].c, firstFail[1].c);
                    assertEquals("Ensure that the flags are correctly set!", firstFail[1].isEnd, firstFail[0].isEnd);
                } else if (firstFail[0] != null && firstFail[1] == null) {
                    fail("Ensure that you are correctly adding nodes! Your tree was non-null in a position that the solution tree had null!");
                } else {
                    fail("Ensure that you are correctly adding nodes! Your tree was null in a position that the solution tree had non-null!");
                }
            }


        } catch (IOException ioe) {
            fail("An IOException occurred! Please contact me. The server may be down. Most likely, you just need to update to the latest version.");
        }
    }

    @Test(timeout = 2000)
    public void test_E_AddWord_God() {
        // You can modify this test case yourself if you are on the client-server pairing.
        // It will always return the correct solution trie in the variable solutionWP.
        // All you need to do is change the list of wordsToAdd.

        String[] wordsToAdd = new String[] {"a", "B", "c", "D", "ABCDE", "ABC", "ABCDFE", "ABCFDOI", "AOHGRSOG", "VSJVIS", "AbcDefGhe", "ABCaka", "ABELINCOLN", "ABRSRG", "ABCDFSDF", "ABfsgf",
                "JAY", "JEFF", "JOHN", "JOE", "Jeremy", "Jeremiah", "Mongo", "Mango", "mousepad", "monitor", "tab", "ABCKEN", "ABKen", "ABCODP", "OFEAPn", "LEAON", "XRGS",
                "Word", "box", "Navigation", "Emphasis", "Intense", "Pen", "Sharpie", "Dollar", "Pill", "Vitamin", "Centrum", "Napkin", "Nappie", "Bottle", "Bot", "Wire",
                "Camera", "Circle", "Square", "Rectangle", "Trapezoid", "Sphere", "Cube", "Pyramid", "Hook", "Mouse", "Charger", "Cabinet", "Bag", "Plastic bag", "Paper bag",
                "Wheat", "Gluten", "Dairy", "Corn", "Soy", "Eggs", "tree nuts", "peanuts", "fish", "you", "can", "modify", "this", "test", "case", "yourself", "if", "are",
                "on", "the", "client", "-", "server", "pairing", "it", "will", "always", "return", "the", "correct", "solution", "trie", "in", "the", "variable", "solutionWP",
                "All", "need", "to", "do", "is", "change", "list", "of", "wordsToAdd", "local", "node", "recommend", "failing", "If", "I", "setting", "a", "break", "point",
                "and", "walking", "through", "Joker. The Young Man", "Joker. The Old Man", "Joker. Funny", "joker, big", "A", "B", "C", "D", "\\V][Lanmp", "][ALeaop-", "\\V][IEO",
                "LE\\OEA\\ee", "[[]][[", "]][[{", "ABCDE", "ABC", "ABCDFE", "\n", "word\n", "\nword", "\nwo\nrd\n"};

        wordProcessor.addAllWords(wordsToAdd);
        try {
            WordProcessor solutionWP = testcasesClient.headlessClient(wordsToAdd, "addword");
            if (solutionWP == null) {
                fail("Please update to the latest version of test cases and test client on the Piazza post!");
            }

            tree_counter(wordProcessor.getWordTrie());
            assertEquals("Ensure that the number of nodes is correct after inserting variable words!", tree_counter(solutionWP.getWordTrie()), tree_counter(wordProcessor.getWordTrie()));

            WordProcessor.Node[] firstFail = tree_traverse_compare(wordProcessor.getWordTrie(), solutionWP.getWordTrie());

            // firstFail[0] is the local node, that is your node.
            // firstFail[1] is the solution node, the correct node.
            // If failing this, I recommend setting a break point and walking through the solution trie.


            if (firstFail != null) {
                if (firstFail[0] != null && firstFail[1] != null) {
                    assertEquals("Ensure that you are correctly adding nodes!", "" + firstFail[1].c, "" + firstFail[0].c);
                    assertEquals("Ensure that the flags are correctly set!", firstFail[1].isEnd, firstFail[0].isEnd);
                } else if (firstFail[0] != null && firstFail[1] == null) {
                    fail("Ensure that you are correctly adding nodes! Your tree was non-null in a position that the solution tree had null!");
                } else {
                    fail("Ensure that you are correctly adding nodes! Your tree was null in a position that the solution tree had non-null!");
                }
            }


        } catch (IOException ioe) {
            fail("An IOException occurred! Please contact me. The server may be down. Most likely, you just need to update to the latest version.");
        }
    }

    @Test(timeout = 2000)
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

    @Test(timeout = 2000)
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

    @Test(timeout = 20000) // 20 second timeout
    public void test_Z_autoCompleteOptions_God() {
        String[] wordsToAdd = new String[] {"a", "B", "c", "D", "ABCDE", "ABC", "ABCDFE", "ABCFDOI", "AOHGRSOG", "VSJVIS", "AbcDefGhe", "ABCaka", "ABELINCOLN", "ABRSRG", "ABCDFSDF", "ABfsgf",
                "JAY", "JEFF", "JOHN", "JOE", "Jeremy", "Jeremiah", "Mongo", "Mango", "mousepad", "monitor", "tab", "ABCKEN", "ABKen", "ABCODP", "OFEAPn", "LEAON", "XRGS",
                "Word", "box", "Navigation", "Emphasis", "Intense", "Pen", "Sharpie", "Dollar", "Pill", "Vitamin", "Centrum", "Napkin", "Nappie", "Bottle", "Bot", "Wire",
                "Camera", "Circle", "Square", "Rectangle", "Trapezoid", "Sphere", "Cube", "Pyramid", "Hook", "Mouse", "Charger", "Cabinet", "Bag", "Plastic bag", "Paper bag",
                "Wheat", "Gluten", "Dairy", "Corn", "Soy", "Eggs", "tree nuts", "peanuts", "fish", "you", "can", "modify", "this", "test", "case", "yourself", "if", "are",
                "on", "the", "client", "-", "server", "pairing", "it", "will", "always", "return", "the", "correct", "solution", "trie", "in", "the", "variable", "solutionWP",
                "All", "need", "to", "do", "is", "change", "list", "of", "wordsToAdd", "local", "node", "recommend", "failing", "If", "I", "setting", "a", "break", "point",
                "and", "walking", "through", "Joker. The Young Man", "Joker. The Old Man", "Joker. Funny", "joker, big", "A", "B", "C", "D", "\\V][Lanmp", "][ALeaop-", "\\V][IEO",
                "LE\\OEA\\ee", "[[]][[", "]][[{", "ABCDE", "ABC", "ABCDFE", "\n", "word\n", "\nword", "\nwo\nrd\n"};

        wordProcessor.addAllWords(wordsToAdd);
        try {
            //Note: If you're failing AddWord, you're probably going to have problems here.
            WordProcessor solutionWP = testcasesClient.headlessClient(wordsToAdd, "autocomplete");

            if (solutionWP == null) {
                fail("Please update to the latest version of test cases and test client on the Piazza post!");
            }

            HashMap<String, List<String>> solution_word_autocomplete_pairs = testcasesClient.retrieveList();
            HashMap<String, List<String>> local_word_autocomplete_pairs = new HashMap<>();

            for (int i = 0; i < wordsToAdd.length; i++) { // for each word in the array
                for (int j = 0; j < wordsToAdd[i].length(); j++) { // for each prefix of that word
                    String prefixString = wordsToAdd[i].substring(0, j + 1);
                    local_word_autocomplete_pairs.put(prefixString, wordProcessor.autoCompleteOptions(prefixString)); // populate hashmap with key: prefix - value: autocomplete list pairs
                }
            }

            Iterator solutionIterator = solution_word_autocomplete_pairs.entrySet().iterator();
            Iterator localIterator = local_word_autocomplete_pairs.entrySet().iterator();

            while (solutionIterator.hasNext() && localIterator.hasNext()) {
                Map.Entry solutionPair = (Map.Entry)solutionIterator.next();
                Map.Entry localPair = (Map.Entry)localIterator.next();
                List<String> solutionAutoCompleteList = (List<String>)solutionPair.getValue();
                List<String> localAutoCompleteList = (List<String>)localPair.getValue();
                HashSet<String> localAutoCompleteHashSet = new HashSet<String>(localAutoCompleteList);
                assertTrue("Peculiar error involving HashMap indices. Please contact me.", ((String)solutionPair.getKey()).equals((String)localPair.getKey()));
                assertEquals("Ensure that the size of your autoCompleteOptions List is correct given prefix " + solutionPair.getKey(), solutionAutoCompleteList.size(), localAutoCompleteList.size());
                for (int k = 0; k < solutionAutoCompleteList.size(); k++) { // for each word in the solution auto complete list
                    assertTrue("Ensure that every word in the solution auto completion list exists in the local auto completion list! " +
                                    "Word missing from local list: " + solutionAutoCompleteList.get(k) + "\nFor prefix: " + solutionPair.getKey(),
                            localAutoCompleteHashSet.contains(solutionAutoCompleteList.get(k)));
                }
            }

            if (solutionIterator.hasNext() && !localIterator.hasNext()) {
                Map.Entry solutionNext = (Map.Entry)solutionIterator.next();
                fail("The solution has a prefix-autocomplete pair that you don't!\n Prefix: " + solutionNext.getKey() + "\n Autocomplete list: " + ((List<String>)solutionNext.getValue()).toString() + "\n");
            } else if (!solutionIterator.hasNext() && localIterator.hasNext()) {
                Map.Entry localNext = (Map.Entry)localIterator.next();
                fail("You have a prefix-autocomplete pair that the solution doesn't!\n Prefix: " + localNext.getKey() + "\n Autocomplete list: " + ((List<String>)localNext.getValue()).toString() + "\n");
            }

            int numRandomStringAutoCompletes = 100; // modify this to test more random string auto completes.
            int lenRandomString = 6;                // modify this to test varying lengths of random strings.
            for (int i = 0; i < numRandomStringAutoCompletes; i++) { // checks autocomplete with random Strings
                String randomString = randomAlphaNumeric(lenRandomString);
                List<String> solutionAutoCompleteList = testcasesClient.retrieveList(randomString);
                List<String> localAutoCompleteList = wordProcessor.autoCompleteOptions(randomString);
                HashSet<String> localAutoCompleteHashSet = new HashSet<String>(localAutoCompleteList);
                assertEquals("Ensure that the size of your autoCompleteOptions List is correct given extension String " + randomString, solutionAutoCompleteList.size(), localAutoCompleteList.size());
                for (int k = 0; k < solutionAutoCompleteList.size(); k++) { // for each word in the solution auto complete list
                    assertTrue("Ensure that every word in the solution auto completion list exists in the local auto completion list! " +
                                    "Word missing from local list: " + solutionAutoCompleteList.get(k) + "\nFor prefix: " + randomString,
                            localAutoCompleteHashSet.contains(solutionAutoCompleteList.get(k)));
                }
            }

            for (int i = 0; i < 5; i++) { // checks autocomplete with extensions of words that already exist
                if (i >= wordsToAdd.length) { // only run 5 times maximum; ensures no crash if wordsToAdd.length < 5
                    break;
                }
                String extension = wordsToAdd[i] + randomAlphaNumeric(lenRandomString);
                List<String> solutionAutoCompleteList = testcasesClient.retrieveList(extension);
                List<String> localAutoCompleteList = wordProcessor.autoCompleteOptions(extension);
                HashSet<String> localAutoCompleteHashSet = new HashSet<String>(localAutoCompleteList);
                assertEquals("Ensure that the size of your autoCompleteOptions List is correct given random String " + extension, solutionAutoCompleteList.size(), localAutoCompleteList.size());
                for (int k = 0; k < solutionAutoCompleteList.size(); k++) { // for each word in the solution auto complete list
                    assertTrue("Ensure that every word in the solution auto completion list exists in the local auto completion list! " +
                                    "Word missing from local list: " + solutionAutoCompleteList.get(k) + "\nFor prefix: " + extension,
                            localAutoCompleteHashSet.contains(solutionAutoCompleteList.get(k)));
                }
            }

            testcasesClient.retrieveList(null); // DO NOT REMOVE THIS LINE!

        } catch (IOException ioe) {
            fail("Outer IOException in test_Z_autoCompleteOptions_God()! Most likely, all you need to do is update.");
        }
    }

}
