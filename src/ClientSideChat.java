

import java.io.*;
import java.net.*;

public class ClientSideChat {
    private static PrintWriter out;

    public static void main(String[] args)
    {

        String serverAddress = "localhost"; // Change if needed
        try (Socket socket = new Socket(serverAddress, 12345))
        {
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Login process
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String response;

            while (true)
            {
                response = in.readLine();
                System.out.println(response); // Prompt for username or password

                if (response.equals("Enter username:"))

                {
                    String username = userInput.readLine();
                    out.println(username);
                }
                else if (response.startsWith("Enter password for"))
                {
                    String password = userInput.readLine();
                    out.println(password);
                }
                else if (response.equals("Username not found. Try again."))
                {
                    // Continue to prompt for username
                    continue;
                } else if (response.equals("Incorrect password. Please try again."))
                {
                    // Continue to prompt for password
                    continue;
                }
                else
                {
                    // Login successful
                    break;
                }
            }



            // Create a thread to listen for incoming messages
            new Thread(() -> {
                try
                {
                    String message;
                    while ((message = in.readLine()) != null)
                    {
                        System.out.println(message);
                    }
                }
                catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                }
            }).start();

            // Read user input and send messages
            String userMessage;

            while (true)
            {
                userMessage = userInput.readLine();
                if (userMessage != null)
                {
                    out.println(userMessage);
                    if (userMessage.equalsIgnoreCase("bye")) {
                        break; // Exit loop if user types "bye"
                    }
                }
            }

        }
        catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
        }

    }

}

