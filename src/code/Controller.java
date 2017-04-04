package code;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;


public class Controller {
    @FXML Button startServerButton;
    @FXML Button stopServerButton;
    @FXML TextArea serverConsoleLogOutput;

    Questions questions = new Questions();


    Random rand = new Random();

    private static BufferedReader in;
    private static PrintStream out;

    private static final HashSet<String> names = new HashSet<>();
    private static final HashSet<PrintStream> clientConnection = new HashSet<>();

    public Controller() {
        startServer();
        new sendQuestionThread().start();
        }

    public void startServer(){
        Runnable startServerRunnable = () -> {
            System.out.println("Server is running:");

            OutputThread serverOutputThread;
            Thread serverConnectionThread;

            InputThread serverInputThread;
            Thread serverInputThread2;

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
                    serverOutputThread = new OutputThread(connection);
                    serverConnectionThread = new Thread(serverOutputThread);
                    serverConnectionThread.start();

                    serverInputThread = new InputThread(connection);
                    serverInputThread2 = new Thread(serverInputThread);
                    serverInputThread2.start();
                    serverConsoleLogOutput.appendText("New connection \n");

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        };
        new Thread(startServerRunnable).start();
    }

    class OutputThread implements Runnable {
        OutputThread(Socket connection) throws IOException {
            out = new PrintStream(connection.getOutputStream());
            out.println("Server connected.");
        }

        @Override
        public void run() {

        }
    }

    public class sendQuestionThread extends Thread{
        public void run() {
            System.out.println("send Is running");
            while (true) {
                try {
                    Thread.sleep(10000);
                    String question = questions.getQuestion(rand.nextInt(5 - 1 + 1) + 1);
                    serverConsoleLogOutput.appendText(question + "\n");
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

    class InputThread implements Runnable {
        private String name;
        private int score = 0;

        InputThread(Socket connection) throws IOException {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }

        @Override
        public void run() {

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
            serverConsoleLogOutput.appendText("New connection name: " + this.name + "\nClient connection: " + clientConnection + "\n");

            for(PrintStream printStream : clientConnection){
                serverConsoleLogOutput.appendText(String.valueOf(printStream + "\n"));
            }

            while (true) {
                  String input;
                        try {
                            input = in.readLine();
                            if(input.equalsIgnoreCase(questions.getAnswer())){
                                serverConsoleLogOutput.appendText("Answer: " + input + " Is Correct! \n");
                                for(PrintStream writer : clientConnection) {
                                    this.score++;
                                    writer.println("Answer: " + input + " from " + this.name + " is correct!\n Hens new score is: " + this.score);
                                }
                            } else {
                                serverConsoleLogOutput.appendText("Answer: " + input + " Is WRONG! \n");
                                for (PrintStream writer : clientConnection) {
                                    writer.println("Answer: " + input + " from" + name +  " Is WRONG!");
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("No incoming: " + e);
                            break;
                        }
            }
        }
    }
}
// TODO: Klient 1 låser sig när klient 2 kommer in och börjar svara till servern.
// TODO: När klient 2 skickar rätt svar så nämns klient 1 och 2s namn var annan gång.