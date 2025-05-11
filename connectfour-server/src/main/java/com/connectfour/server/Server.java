package com.connectfour.server;

import java.io.*;
import java.net.*;

public class Server {

    private static final int PORT = 12345; // Server will listen on this port
    private static final String USER_FILE = "D:\\eclipse-workspace\\connectfour-server\\src\\main\\java\\com\\connectfour\\users.txt"; // File to store usernames

    public static void main(String[] args) {
        System.out.println("Server started!");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for players to connect...");

            // Accept Player 1
            Socket player1Socket = serverSocket.accept();
            System.out.println("Player 1 connected!");
            DataInputStream player1Input = new DataInputStream(player1Socket.getInputStream());
            DataOutputStream player1Output = new DataOutputStream(player1Socket.getOutputStream());
            String player1Username = registerOrLogin(player1Input, player1Output);
            
            // Accept Player 2
            Socket player2Socket = serverSocket.accept();
            System.out.println("Player 2 connected!");
            DataInputStream player2Input = new DataInputStream(player2Socket.getInputStream());
            DataOutputStream player2Output = new DataOutputStream(player2Socket.getOutputStream());
            String player2Username = registerOrLogin(player2Input, player2Output);

            // Send opponent names
            player1Output.writeUTF(player2Username);
            player2Output.writeUTF(player1Username);

            // Now send color assignments
            player1Output.writeUTF("You are Player 1 (Red).");
            player2Output.writeUTF("You are Player 2 (Yellow).");

            // Inform player 1 to make the first move
            player1Output.writeUTF("Your move.");

            // Basic move forwarding loop
            while (true) {
                // Player 1 move
                String move = player1Input.readUTF();
                if ("WIN".equals(move)) {
                    player2Output.writeUTF("LOSE");
                    break;
                }
                System.out.println("Player 1 move: " + move);
                player2Output.writeUTF(move); // Send move to Player 2
                player2Output.writeUTF("Your move."); // Tell Player 2 it's their turn

                // Player 2 move
                move = player2Input.readUTF();
                if ("WIN".equals(move)) {
                    player1Output.writeUTF("LOSE");
                    break;
                }
                System.out.println("Player 2 move: " + move);
                player1Output.writeUTF(move); // Send move to Player 1
                player1Output.writeUTF("Your move."); // Tell Player 1 it's their turn
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle registration/login logic
    private static String registerOrLogin(DataInputStream input, DataOutputStream output) throws IOException {
        String username = null;
        boolean isUsernameValid = false;

        while (!isUsernameValid) {
            output.writeUTF("Enter your username:");
            username = input.readUTF();

            // Check if username exists in the file
            if (isUsernameTaken(username)) {
                output.writeUTF("Username already taken. Please choose another.");
            } else {
                // Register the new username
                registerUsername(username);
                isUsernameValid = true;
                output.writeUTF("Registration successful.");
            }
        }
        return username;
    }

    // Check if the username already exists in the file
    private static boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Register the username in the file
    private static void registerUsername(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(username);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
