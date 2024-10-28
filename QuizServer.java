import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    // Initialization of all the main aspects of the code including bonus time and points, array of connected client, organiziation if 
    private static final int PORT = 5555;
    private static final int BONUS_TIME_LIMIT = 5000; 
    private static final int BONUS_POINTS = 5; // the amou
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Map<String, List<Question>> questionsByCategory = new HashMap<>();
    private static Map<String, Integer> scores = new HashMap<>();

    // Initialize question categories and questions
    static {
        questionsByCategory.put("Geography", Arrays.asList(
            new Question("What is the capital of France?", "Paris"),
            new Question("What is the largest desert on Earth?", "Antarctica"),
            new Question("Where is Ontario tech located?", "Oshawa")
        ));
        questionsByCategory.put("Technology", Arrays.asList(
            new Question("Who developed Java?", "James Gosling"),
            new Question("What is the most popular programming language?", "JavaScript"),
            new Question("Who teaches Distribtued Systems?","Anwar")
        ));
        questionsByCategory.put("Math", Arrays.asList(
            new Question("What is 2+2?", "4"),
            new Question("What is the square root of 16?", "4"),
            new Question("What is a second year math course we took?", "Discrete Math")

        ));
    }

    // intialization of the server socket and adding any clients connected 
    public static void main(String[] args) {
        System.out.println("Quiz Server has started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // mehtod to show the synchornized scores using a map to store their username and scores 
    public static synchronized void updateScores(String username, int score) {
        scores.put(username, scores.getOrDefault(username, 0) + score);
        broadcast("Score Update: " + scores);
    }

    public static synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    //  method to capitalize the first letter of a word to prevent any issues of it not be accepted
    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // class for handling client connections
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Asking the client for their username
                out.println("Enter your username: ");
                username = in.readLine();
                System.out.println(username + " has joined the quiz!");

                // Asking the client for their category 
                out.println("Choose a category: Geography, Technology, or Math");
                String category = in.readLine().trim().toLowerCase();  // Convert to lowercase for comparison

                // Loop until a valid category is entered
                while (!questionsByCategory.containsKey(capitalize(category))) {
                    out.println("Invalid category. Please choose Geography, Technology, or Math.");
                    category = in.readLine().trim().toLowerCase();
                }

                // Retrieve the correctly capitalized category key to prevent any invalid choice option
                category = capitalize(category);
                List<Question> chosenQuestions = questionsByCategory.get(category);

                // Quiz loop with chosen questions
                for (Question question : chosenQuestions) {
                    out.println("Question: " + question.getQuestionText());
                    
                    // Capture start time for time calculation which will subtract from the time asnwered
                    long startTime = System.currentTimeMillis();

                    // Read the client's answer
                    String answer = in.readLine();
                    long responseTime = System.currentTimeMillis() - startTime; 

                    if (answer.equalsIgnoreCase(question.getAnswer())) {
                        int score = 10; // score for every correct answer 
                        
                        // checks to see if they annswered within the time limit 
                        if (responseTime <= BONUS_TIME_LIMIT) {
                            score += BONUS_POINTS;
                            out.println("Fast answer! You earned a bonus of " + BONUS_POINTS + " points!");
                        }

                        updateScores(username, score); // Update score
                        out.println("Correct!");
                    } else {
                        out.println("Incorrect. The correct answer was: " + question.getAnswer());
                    }
                }

                // End game message
                out.println("Game Over! Final Scores: " + scores);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}

// Question class to store question text and answer
class Question {
    private String questionText;
    private String answer;

    public Question(String questionText, String answer) {
        this.questionText = questionText;
        this.answer = answer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAnswer() {
        return answer;
    }
}
