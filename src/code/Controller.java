package code;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;

public class Controller {
    @FXML TextArea serverConsoleLogOutput;
    @FXML Label serverStatusLabel;

    private Questions questions = new Questions();
    private Random rand = new Random();

    //Variable to check if correct answer were made.
    private boolean correctAnswer = false;

    //Sets for holding user and client connections
    private static final HashSet<String> names = new HashSet<>();
    private static final HashSet<PrintStream> clientConnection = new HashSet<>();

    public Controller() {
        startServer(); //Run the server
        new sendQuestionThread().start(); //Start sending questions
    }

    public void startServer(){
        Runnable startServerRunnable = () -> {

            ClientThread clientThread;
            Thread serverInputThread;

            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(1000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                Socket connection;
                try {
                    assert serverSocket != null;
                    connection = serverSocket.accept();

                    clientThread = new ClientThread(connection);
                    serverInputThread = new Thread(clientThread);
                    serverInputThread.start();
                    serverConsoleLogOutput.appendText("Server : New connection \n");

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        };
        new Thread(startServerRunnable).start();
        System.out.println("Server: is running:");
    }

    //Getting a random question and sending to every connection, and server log.
    public class sendQuestionThread extends Thread{
        public void run() {
            System.out.println("Server: send Is running");
            while (true) {
                try {
                    Thread.sleep(10000); //Waiting 10 seconds

                    //Get random question
                    String question = questions.getQuestion(rand.nextInt(5 - 1 + 1) + 1);

                    //Correct answer to false
                    correctAnswer = false;

                    //Print question to log
                    serverConsoleLogOutput.appendText("Server: " + question + "\n");

                    //Sending the question to all clients connected.
                    for(PrintStream writer : clientConnection) {
                        writer.println(question);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Sleep exception: " + e);
                    break;
                }
            }
        }
    }

    //Prompt te user to send a user name and storing it,
    //After that it is listening to incoming text from user
    class ClientThread implements Runnable {
        private String name;
        private int score = 0;
        private PrintStream out;
        private BufferedReader in;

        ClientThread(Socket connection) throws IOException {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = new PrintStream(connection.getOutputStream());
        }

        @Override
        public void run() {

            //Ask the user for a user name, if it is taken it asks again.
            //Otherwise it is stored.
            out.println("Server connected.");
            while (true) {
                out.println("Please enter a username: ");
                try {
                    name = in.readLine();
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        } else {
                            out.println("Username taken.");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("No incoming: " + e);
                    break;
                }
            }

            out.println("Name accepted: \nWelcome " + this.name);
            clientConnection.add(out);
            serverConsoleLogOutput.appendText("Server: New connection name: " + this.name + "\nServer: Client connection: " + clientConnection + "\n");

            //Listening to incoming text from user.
            while (true) {
                String input;
                try {
                    input = in.readLine(); //Store input.

                        //If correct answer haven't ben made.
                        if (!correctAnswer) {

                            //Check input answer with current correct answer.
                            if (input.equalsIgnoreCase(questions.getAnswer())) {
                                serverConsoleLogOutput.appendText("User input: Answer: " + input + " Is Correct! \n");

                                //Add score to player.
                                this.score++;
                                for (PrintStream writer : clientConnection) {
                                    writer.println("Answer: " + input + " from " + this.name + " is correct!\n" + this.name + "s new score is: " + this.score);

                                    //Change correct answer to true.
                                    correctAnswer = true;
                                }

                                //If answer were incorrect, tell the user.
                            } else {
                                serverConsoleLogOutput.appendText("User input: Answer: " + input + " Is WRONG! \n");
                                out.println("Answer: " + input + " Is WRONG!");

                            }

                        //If correct answer were before, tell the user.
                        } else {
                            out.println("To late");
                        }
                } catch (IOException e) {
                    System.out.println("No incoming: " + e);
                    break;
                }
            }
        }
    }
}
