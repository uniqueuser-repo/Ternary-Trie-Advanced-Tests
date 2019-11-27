import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * The client of the test cases for CS251 Project 5. Allows students to create their own tree and step through
 * the instance with a debugger. If you want to simply insert the words and then view the tree, run this file.
 * Set a breakpoint on line 57 and analyze the tree from there.
 *
 * @author Andrew Orlowski, orlowska@purdue.edu
 * @version 11/27/2019
 *
 */

public class testcasesClient {
    public static final int VersionID = 120;

    private static OutputStreamWriter osw;
    private static BufferedWriter bfw;
    private static ObjectInputStream ois;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to the client. This will allow you to\n" +
                            "manually input words and then will send you the finished trie.\n\n");
        try {
            WordProcessor localWP = new WordProcessor(); // this is YOUR WordProcessor.
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), 31002);
            osw = new OutputStreamWriter(clientSocket.getOutputStream());
            bfw = new BufferedWriter(osw);
            ois = new ObjectInputStream(clientSocket.getInputStream());


            String versionValidator = validateVersion();
            if (versionValidator == null) {
                System.out.println("An error occurred.");
                return;
            } else if (!(versionValidator.equalsIgnoreCase("Passed version validation."))) {
                System.out.println(versionValidator);
                return;
            } else {
                System.out.println(versionValidator);
            }

            System.out.println("Please insert a list of words. Type \"exit\" to stop writing words. After each word, press the enter key.");

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
                localWP.addWord(nextLine);
            }

            Object readObject = ois.readObject();
            WordProcessor solutionWP = (WordProcessor)readObject;

            // If you want to access and analyze the solutionWP, set a break point on line 78 below.
            // From there, run the client in debug mode (bug in the top right) and work as normal.
            // When you exit, you will have a screen at the bottom that shows all of the variables.
            // From there, you can open the "localWP" and traverse the tree.

            System.out.println("Done! Please see line 73 of the code for details.");


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
            Socket clientSocket = new Socket("167.172.238.22", 31002);
            osw = new OutputStreamWriter(clientSocket.getOutputStream());
            bfw = new BufferedWriter(osw);
            ois = new ObjectInputStream(clientSocket.getInputStream());


            String versionValidator = validateVersion();
            if (versionValidator == null) {
                System.out.println("An error occurred.");
                return null;
            } else if (!(versionValidator.equalsIgnoreCase("Passed version validation."))) {
                System.out.println(versionValidator);
                return null;
            }

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

    private static String validateVersion() {
        if (bfw != null && ois != null) {
            try {
                bfw.write(VersionID); //
                bfw.newLine();        // If you don't have the latest version ID, the server will deny you.
                bfw.flush();          //

                bfw.write(testCasesAdvanced.VersionID);
                bfw.newLine();
                bfw.flush();

                String checkValidVersion = (String)ois.readObject();
                if (!checkValidVersion.equalsIgnoreCase("Passed!")) {
                    return checkValidVersion;
                } else {
                    return "Passed version validation.";
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return "IOException when validating Version!";
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                return "ClassNotFoundException when validating Version!";
            }
        }
        return null;
    }
}
