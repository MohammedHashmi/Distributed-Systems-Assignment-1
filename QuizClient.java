import java.io.*;
import java.net.*;

//intialization
public class QuizClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5555;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the Quiz Server");

            // Read server's prompt for the username
            String serverMessage = in.readLine();
            System.out.println(serverMessage); // asks user to enter usnamer 

            // Sends username to server to be stored
            String username = consoleInput.readLine();
            out.println(username);

            // Read server's prompt for the category
            serverMessage = in.readLine();
            System.out.println("Server: " + serverMessage); // Displays "Choose a category..."

            // Send  category to server to ask the question from that category 
            System.out.print("Choose a category: ");
            String category = consoleInput.readLine();
            out.println(category);

            // this loops the quizzes questions plus the clients answer 
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Server: " + serverMessage);

                if (serverMessage.contains("Question")) {
                    System.out.print("Your Answer: ");
                    String answer = consoleInput.readLine();
                    out.println(answer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}