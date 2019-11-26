import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class testcasesServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(31002);

            while (true) {
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.start();
            }
        } catch (IOException ioe) {
            System.out.println("IOException!");
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
        InputStreamReader isr = new InputStreamReader(client.getInputStream());
        OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
        while () {

        }
    }
}
