
            //Collaborators                   ID
//        • ABADANAF CHALA …………………..………………1848/14
//        • ABDIBIYA ABADURA……………………………………1875/14
//        • AMANUEL ABATE ………………………………………2122/14
//        • AYANA FILE ………………………………………………2226/14
//        • BANA DAWIT ………………………………………………2245/14
//        . Abubaker Mohammed……………………………2045/14


import java.io.*;
import java.net.*;
import java.util.*;

public class ServerSideChat {

    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, String> userCredentials = new HashMap<>();
    private static final int MAX_CLIENTS = 3;


    public static void main(String[] args)
    {
        // Predefined usernames and passwords
        userCredentials.put("Abadanaf", "1848/14");
        userCredentials.put("Abdi", "1875/14");
        userCredentials.put("Amanuel", "2122/14");
        userCredentials.put("Ayana", "2226/14");
        userCredentials.put("Bana", "2245/14");
        userCredentials.put("Abubaker", "2045/14");

        System.out.println("Server Side Chat Started...");
        System.out.println("WELL COME TO THIS GROUP DISCUSSION");


        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            while (true)
            {
                new ClientHandler(serverSocket.accept()).start();
            }

        }
        catch (IOException e)
        {
            System.out.println("Server exception: " + e.getMessage());
        }

    }

    private static void broadcastMessage(String message, PrintWriter excludeWriter)

    {
        for (PrintWriter writer : clientWriters)
        {

            if (writer != excludeWriter)
            {
                writer.println(message);
            }
        }

    }

    private static class ClientHandler extends Thread

    {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;


        public ClientHandler(Socket socket)

        {
            this.socket = socket;
        }

        public void run()
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Login process

                while (true)
                {
                    out.println("Enter your name:[ Abadanaf, Abdi, Amanuel, Ayana, Bana, Abubaker]");
                    username = in.readLine();

                    // Check for case insensitive username

                    String normalizedUsername = username.trim();

                    if (!userCredentials.containsKey(normalizedUsername))
                    {
                        out.println("Username not found. Try again.");
                        continue;
                    }

                    out.println("Enter password for " + normalizedUsername + ":");
                    String password = in.readLine();


                    if (userCredentials.get(normalizedUsername).equals(password))

                    {
                        break; // Correct username and password
                    }
                    else
                    {
                        out.println("Incorrect password. Please try again.");
                    }

                }

                synchronized (clientWriters)
                {

                    if (clientWriters.size() >= MAX_CLIENTS)

                    {
                        out.println("Chat room is full. Disconnecting...");
                        socket.close();
                        return;

                    }

                    clientWriters.add(out);

                }

                //broadcastMessage(username + " has joined the chat.", null);

                out.println("Welcome to the chat, " + username + "! You have successfully connected to the server.\n You can start the discussion...");

                String message;

                while ((message = in.readLine()) != null)

                {
                    if (message.equalsIgnoreCase("bye"))
                    {
                        break; // Exit on "bye"
                    }
                    System.out.println(username + ": " + message);
                    broadcastMessage(username + ": " + message, out);
                }

                out.println("Goodbye, " + username + "!");
                broadcastMessage(username + " has left the chat.", null);
            }
            catch (IOException e)
            {
                System.out.println("Client handler exception: " + e.getMessage());
            }
            finally
            {
                try
                {
                    socket.close();
                }

                catch (IOException e)
                {
                    System.out.println("Could not close socket: " + e.getMessage());
                }
                synchronized (clientWriters)
                {
                    clientWriters.remove(out);
                }

            }

        }

    }

}