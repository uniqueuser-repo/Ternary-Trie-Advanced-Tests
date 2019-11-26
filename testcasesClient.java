import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

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
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to the client. This will allow you to\n" +
                            "manually input words and then will send you the finished trie.");
        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), 31002);
            OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedWriter bfw = new BufferedWriter(osw);
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Please insert a list of words. Type \"exit\" to stop writing words.");

            while (true) {
                String nextLine = scan.nextLine();
                if (nextLine.equals("exit")) {
                    System.out.println("Retrieving solution WordProcessor from server...");
                    bfw.write(nextLine);
                    bfw.flush();
                    break;
                }
                bfw.write(nextLine);
                bfw.newLine();
                bfw.flush();
                System.out.println("Sent word!");
            }

            Object readObject = ois.readObject();
            WordProcessor solutionWP = (WordProcessor)readObject;
            System.out.println();


        } catch (IOException ioe) {
            System.out.println("IOException when creating client socket in testcasesClient!");
        } catch (ClassCastException cce) {
            System.out.println("For some reason, the returned Object is not a WordProcessor Object!");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("For some reason, the class was not found when reading the Object!");
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
