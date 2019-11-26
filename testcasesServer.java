import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.HashMap;

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
public class testcasesServer {
    public static HashMap<Socket, String> clientIDs = new HashMap<>();
    public static void main(String[] args) {
        try {
	        InetAddress inetAddress = InetAddress.getLocalHost();
            System.out.println("Server listening on IP 167.172.238.22 running on port 31002....");
            ServerSocket serverSocket = new ServerSocket(31002);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("A client has connected! Client ID: " + client.getRemoteSocketAddress().toString());
                clientIDs.put(client, client.getRemoteSocketAddress().toString());
                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.start();
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
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            BufferedReader bfr = new BufferedReader(isr);
            while (true) {
                String readLine = bfr.readLine();
                if (readLine == null || readLine.equals("exit")) {
                    oos.writeObject(localWP);
                    testcasesServer.clientIDs.remove(client);
                    break;
                }
                System.out.println("Client ID: " + testcasesServer.clientIDs.get(client) + " is adding word " + readLine);
                localWP.addWord(readLine);

            }

            System.out.println("Client ID: " + testcasesServer.clientIDs.get(client) + " is done adding words. Sending back WordProcessor...");
            Object sendingObject = (Object)localWP;
            oos.writeObject(sendingObject);
        } catch (IOException ioe) {
            System.out.println("IOException 2!");
            System.out.println("Client ID: " + testcasesServer.clientIDs.get(client));
            testcasesServer.clientIDs.remove(client);
            ioe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected Exception!");
            System.out.println("Client ID: " + testcasesServer.clientIDs.get(client));
            testcasesServer.clientIDs.remove(client);
            e.printStackTrace();
        }
    }
}
