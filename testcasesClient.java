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
                    bfw.newLine();
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

            // If you want to access and analyze the solutionWP, set a break point on line 55 below.
            // From there, run the client in debug mode (bug in the top right) and work as normal.
            // When you exit, you will have a screen at the bottom that shows all of the variables.
            // From there, you can open the "localWP" and traverse the tree.

            //System.out.println();


        } catch (IOException ioe) {
            System.out.println("IOException!");
            ioe.printStackTrace();
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

    public static WordProcessor headlessClient(String[] wordsToAdd) throws IOException{
        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), 31002);
            OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
            BufferedWriter bfw = new BufferedWriter(osw);
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());


            for (int i = 0; i < wordsToAdd.length; i++) {
                bfw.write(wordsToAdd[i]);
                bfw.newLine();
                bfw.flush();
            }

            bfw.write("exit");
            bfw.newLine();
            bfw.flush();

            Object readObject = ois.readObject();
            return (WordProcessor)readObject;

        } catch (ClassCastException cce) {
            System.out.println("For some reason, the returned Object is not a WordProcessor Object!");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("For some reason, the class was not found when reading the Object!");
        }
        return null;
    }
}
