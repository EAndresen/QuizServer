package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;


public class Controller {
    @FXML Button startServerButton;
    @FXML Button stopServerButton;
    @FXML TextArea serverConsoleLogOutput;

    private BufferedReader in;
    private PrintStream out;

    private static final HashSet<String> names = new HashSet<>();
    private static HashSet<PrintStream> clientConnection = new HashSet<>();

    public Controller() {
        startServer();
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
                Socket connection = null;
                try {
                    connection = serverSocket.accept();
                    serverOutputThread = new OutputThread(connection);
                    serverConnectionThread = new Thread(serverOutputThread);
                    serverConnectionThread.start();

                    serverInputThread = new InputThread(connection);
                    serverInputThread2 = new Thread(serverInputThread);
                    serverInputThread2.start();
                    System.out.println("New connection");

                } catch (IOException e) {
                    e.printStackTrace();
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
            while (true) {
                try {
                    Thread.sleep(10000);
                    for(PrintStream writer : clientConnection) {
                        writer.println("What is the solution for everything?");
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

            out.println("Name accepted: \nWelcome " + name);
            clientConnection.add(out);
            System.out.println(names);
            System.out.println(clientConnection);

            while (true) {
                  String input = null;
                        try {
                            input = in.readLine();
                            if(input.equalsIgnoreCase("a")){
                                serverConsoleLogOutput.appendText("Answer: " + input + " Is Correct!");
                                for(PrintStream writer : clientConnection) {
                                    writer.println("Answer: " + input + " from " + name + " is correct!");
                                }
                            } else {
                                serverConsoleLogOutput.appendText("Answer: " + input + " Is WRONG! \n");
                                out.println("Answer: " + input + " Is WRONG!");
                            }
                        } catch (IOException e) {
                            System.out.println("No incoming: " + e);
                            break;
                        }
            }
        }
    }
}