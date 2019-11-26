import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * The client of the test cases for CS251 Project 5. Allows students to create their own tree and step through
 * the instance with a debugger.
 *
 * @author Andrew Orlowski, orlowska@purdue.edu
 * @version 11/26/2019
 *
 */

public class testcasesClient {
    public static void main(String[] args) {
        System.out.println("Welcome to the client. This will allow you to\n" +
                            "manually input words and then will send you the finished trie.");
        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), 31002);
            OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            System.out.println("IOException when creating client socket in testcasesClient!");
        }
    }

    // A headless version of the test cases client that doesn't specifically
    // need to be executed by the user. This will be used from within the test
    // cases (specifically Test_D_AddWord_Expert) to help you narrow down
    // precisely where you first fail the test case.
    public static void headlessClient(String[] wordsToAdd) {
        //TODO: Everything.
    }
}
