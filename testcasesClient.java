import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 *
 * The client of the test cases for CS251 Project 5. Allows students to create their own tree and step through
 * the instance with a debugger. If you want to simply insert the words and then view the tree, run this file.
 * Set a breakpoint on line 73 and analyze the tree from there.
 *
 * @author Andrew Orlowski, orlowska@purdue.edu
 * @version 11/27/2019
 *
 */

public class testcasesClient {
    public static final int VersionID = 150;

    private static OutputStreamWriter osw;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to the client. This will allow you to\n" +
                            "manually input words and then will send you the finished trie.\n\n");
        try {
            WordProcessor localWP = new WordProcessor(); // this is YOUR WordProcessor.
            Socket clientSocket = new Socket("167.172.238.22", 31002);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());

            String versionValidator = validateVersion();
            sendType("addword");
            sendMode(0);
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
                    oos.writeObject(nextLine);
                    oos.flush();
                    break;
                }
                oos.writeObject(nextLine);
                oos.flush();
                System.out.println("Sent word!");
                localWP.addWord(nextLine);
            }

            Object readObject = ois.readObject();
            WordProcessor solutionWP = (WordProcessor)readObject;

            // If you want to access and analyze the solutionWP, set a break point on the line below.
            // From there, run the client in debug mode (bug in the top right) and work as normal.
            // When you exit, you will have a screen at the bottom that shows all of the variables.
            // From there, you can open the "localWP" and traverse the tree.

            System.out.println("Done! Please see lines above this portion of the code for details.");


        } catch (IOException ioe) {
            System.out.println("IOException! Most likely, you just need to update to the latest version.");
            ioe.printStackTrace();
        } catch (ClassCastException cce) {
            System.out.println("For some reason, the returned Object is not a WordProcessor Object! Most likely, you just need to update to the latest version.");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("For some reason, the class was not found when reading the Object! Most likely, you just need to update to the latest version.");
        }
    }

    // A headless version of the test cases client that doesn't specifically
    // need to be executed by the user. This will be used from within the test
    // cases (specifically Test_D_AddWord_Expert) to help you narrow down
    // precisely where you first fail the test case.

    public static WordProcessor headlessClient(String[] wordsToAdd, String type) throws IOException{
        try {
            Socket clientSocket = new Socket("167.172.238.22", 31002);
            osw = new OutputStreamWriter(clientSocket.getOutputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());

            String versionValidator = validateVersion();
            sendType(type);
            sendMode(1);
            if (versionValidator == null) {
                System.out.println("An error occurred.");
                return null;
            } else if (!(versionValidator.equalsIgnoreCase("Passed version validation."))) {
                System.out.println(versionValidator);
                return null;
            }

            for (int i = 0; i < wordsToAdd.length; i++) {

                        oos.writeObject(wordsToAdd[i]);
                        oos.flush();
            }

            oos.writeObject("exit");
            oos.flush();

            Object readObject = ois.readObject();
            return (WordProcessor)readObject;

        } catch (ClassCastException cce) {
            System.out.println("For some reason, the returned Object is not a WordProcessor Object! Most likely, you just need to update to the latest version.");
        } catch (ClassNotFoundException cnfe) {
            System.out.println("For some reason, the class was not found when reading the Object! Most likely, you just need to update to the latest version.");
        }
        return null;
    }

    private static String validateVersion() {
        if (oos != null && ois != null) {
            try {
                oos.write(VersionID);
                oos.flush();

                int testcasesVersion = testCasesAdvanced.VersionID;
                oos.writeObject(testcasesVersion);
                oos.flush();

                String checkValidVersion = (String)ois.readObject();
                if (!checkValidVersion.equalsIgnoreCase("Passed!")) {
                    return checkValidVersion;
                } else {
                    return "Passed version validation.";
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return "IOException when validating Version! Most likely, you just need to update to the latest version.";
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                return "ClassNotFoundException when validating Version! Most likely, you just need to update to the latest version.";
            }
        }
        return null;
    }

    public static void sendType(String type) throws IOException{
        if (oos != null && ois != null) {
            oos.writeObject(type);
            oos.flush();
        }
    }

    public static void sendMode(int mode) throws IOException { // sends whether the user is in automatic or manual mode
        if (oos != null && ois != null) {
            oos.write(mode);
            oos.flush();
        }
    }

    public static List<String> retrieveList(String prefix) throws IOException {
        try {
            if (oos != null && ois != null) {
                oos.writeObject(prefix);
                oos.flush();
                try {
                    return (List<String>)ois.readObject();
                } catch (EOFException eof) {
                    return null;
                }
            }
        } catch (ClassNotFoundException cnfe) {
            System.out.println("CNFE in retrieveList!");
            cnfe.printStackTrace();
        }
        return null;
    }
}
