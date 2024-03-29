import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.time.*;
import java.util.List;

/**
 *  The server for the test cases of CS251 Fall'19 Project 5. It accepts input from clients (students)
 *  as to what words should be inputted in the ternary trie. After the students are content with their
 *  added words, the server will send the complete Object to the student. From there, they can step
 *  through the solution trie with a debugger to see exactly how the solution trie should look.
 *
 *  This is particularly helpful for when students are failing a test case, but they don't
 *  know exactly how their version differs. It allows them to walk through the trie and spot
 *  the differences between their trie and the solution trie.
 *
 *  This is important because I am obviously not allowed to share code. Generally, in test cases
 *  for academic assignments like these, I would have an instance of the solution trie available to me
 *  freely. When creating test cases here, I can't have that, because I can't attach the solution code
 *  along with the test cases. Doing this allows an instance to be passed without revealing the source code.
 *
 * @author Andrew Orlowski, orlowska@purdue.edu
 * @version 11/26/2019
 *
 */
public class TestCasesServer {
    public static final int ClientVersionID = 200;
    public static final int TestCasesVersionID = 2675;
    public static HashMap<Socket, String> clientIDs = new HashMap<>();
    public static HashSet<String> uniqueClientIDs = new HashSet<>();

    public static String stripString(Socket clientSocket) {
        String strippedString = clientSocket.getRemoteSocketAddress().toString();
        strippedString = strippedString.substring(1);
        strippedString = strippedString.substring(0, strippedString.indexOf(':'));
        return strippedString;
    }

    public static void main(String[] args) {
        try {
            Runtime.getRuntime().addShutdownHook(
                    new Thread("app-shutdown-hook") {
                        @Override
                        public void run() {
                            System.out.println("Number of unique clients: " + uniqueClientIDs.size()); // prints # of unique client IDs that connected
                            for (String s: uniqueClientIDs) {                                          // when process gets killed in the server
                                System.out.println(s);
                            }
                        }
                    });
	        InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("Server listening on IP 167.172.238.22 running on port 31002....");
            ServerSocket serverSocket = new ServerSocket(31002);

            while (true) {
                Socket client = serverSocket.accept();
                client.setSoTimeout(600*1000); // set a socket timeout of 600 seconds, aka 600 * 1000 milliseconds.
                LocalDateTime timeObject = LocalDateTime.now();
                System.out.println(timeObject + ": A client has connected! Client ID: " + client.getRemoteSocketAddress().toString());
                clientIDs.put(client, client.getRemoteSocketAddress().toString());
                String strippedString = stripString(client);                     // strips to JUST the IP address, nothing else.
                if (!uniqueClientIDs.contains(strippedString)) {                 // adds the IP Address if it's not already in the HashSet
                    uniqueClientIDs.add(strippedString);
                }
                ClientHandler clientHandler = new ClientHandler(client);         // create a thread to handle the client
                clientHandler.start();                                           // run the thread
            }
        } catch (IOException ioe) {
            System.out.println("IOException 1!");
            ioe.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    public void run() {
        WordProcessor localWP = new WordProcessor();
        try {
            int wordCounter = 0;
            boolean tripped = false;
            LocalDateTime timeObject = LocalDateTime.now();
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            String clientType = "";


            int clientVersion = ois.read();
            Integer testcasesVersion = (Integer)ois.readObject();

            if (clientVersion != TestCasesServer.ClientVersionID || testcasesVersion != TestCasesServer.TestCasesVersionID) {
                oos.writeObject("You have failed the version check. Please update to the latest version on the Piazza post. @1120\n Latest version of test cases: "
                        + TestCasesServer.TestCasesVersionID + "\n Latest version of Client: " + TestCasesServer.ClientVersionID + "\n");
                oos.flush();
                timeObject = LocalDateTime.now();
                System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " has failed the version check. Closing.");
                client.close();
                return;
            } else {
                oos.writeObject("Passed!");
                oos.flush();
                timeObject = LocalDateTime.now();
                System.out.print(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " has passed the version check. Running in mode ");
                clientType = (String)ois.readObject();
                int clientMode = ois.read();
                if (clientMode == 0) {
                    System.out.println("manual.");
                } else {
                    System.out.println("automatic.");
                }
            }

            ArrayList<String> addedWords = new ArrayList<>();


            while (true) {
                String readLine = (String)ois.readObject() ;
                if (readLine == null || readLine.equals("exit")) {
                    if (clientType.equals("addword")) { // if their query is of type addword
                        timeObject = LocalDateTime.now();
                        System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " is done adding words. Sending back WordProcessor...");
                        Object sendingObject = localWP;
                        oos.writeObject(sendingObject);
                        System.out.println("Closing socket.");
                        client.close();
                        return;
                    } else {                            // if their query is of type autocomplete
                        Object sendingObject = localWP;
                        oos.writeObject(sendingObject);
                        break;
                    }
                }
                if (wordCounter >= 170) {
                    timeObject = LocalDateTime.now();
                    System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " is adding word " + readLine);
                    System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " stopped printing words to console. Exceeded 170 words.");
                    tripped = true;
                }
                if (tripped == false) {
                    timeObject = LocalDateTime.now();
                    System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " is adding word " + readLine);
                }

                wordCounter++;
                localWP.addWord(readLine);
                addedWords.add(readLine);

            }

            // this portion ONLY runs if the user is in the autocomplete test case
            timeObject = LocalDateTime.now();
            System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " is now checking prefixes.");

            while (true) {
                String prefix = (String)ois.readObject();
                if (prefix == null) {
                    timeObject = LocalDateTime.now();
                    System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " is done checking prefixes.");
                    System.out.println("Closing socket.");
                    client.close();
                    return;
                } else if (prefix.equals("uniqueIdentifier-allprefixes-5203592350")) { // if the passed prefix is the unique identifier for the set of all combinations of prefixes
                    HashMap<String, List<String>> word_autocomplete_pair = new HashMap<>();
                    //TODO: Multithread this so that it's faster. Currently takes 150ms locally, 10seconds on server.
                    //TODO: Problem: Once server reaches this point, CPU usage drastically drops, presumably because
                    //TODO: It's working on only one thread and that thread only goes so fast.
                    for (int i = 0; i < addedWords.size(); i++) { // for each word in the array
                        for (int j = 0; j < addedWords.get(i).length(); j++) { // for each prefix of that word
                            String prefixString = addedWords.get(i).substring(0, j + 1);
                            word_autocomplete_pair.put(prefixString, localWP.autoCompleteOptions(prefixString));
                        }
                    }
                    oos.writeObject(word_autocomplete_pair);
                    oos.flush();
                } else {
                    oos.writeObject(localWP.autoCompleteOptions(prefix));
                    oos.flush();
                }

            }
        } catch (SocketTimeoutException ste) {
            LocalDateTime timeObject = LocalDateTime.now();
            System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " timed out.");
            TestCasesServer.clientIDs.remove(client);
        } catch (SocketException se) {
            LocalDateTime timeObject = LocalDateTime.now();
            System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client) + " abruptly closed connection.");
            TestCasesServer.clientIDs.remove(client);
        } catch (IOException ioe) {
            LocalDateTime timeObject = LocalDateTime.now();
            System.out.println("IOException 2!");
            System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client));
            TestCasesServer.clientIDs.remove(client);
            ioe.printStackTrace();
        } catch (Exception e) {
            LocalDateTime timeObject = LocalDateTime.now();
            System.out.println("Unexpected Exception!");
            System.out.println(timeObject + ": Client ID: " + TestCasesServer.clientIDs.get(client));
            TestCasesServer.clientIDs.remove(client);
            e.printStackTrace();
        }
    }
}
