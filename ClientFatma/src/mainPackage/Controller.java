package mainPackage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Controller {
    BufferedReader inputFromSocket;
    PrintWriter outputToSocket;
    @FXML
    private Button sendMessageBtn;

    @FXML
    private TextArea serverMessagesTA;

    @FXML
    private TextField sendMessagesTF;

    boolean isAlive;
    StringBuilder serverMessages;
    Socket socket;
    public  void ini(){
        isAlive=true;
        serverMessages=new StringBuilder(10000);
        serverMessagesTA.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                serverMessagesTA.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });

        try {
             socket=new Socket("127.0.0.1",5000);
            outputToSocket = new PrintWriter(socket.getOutputStream(), true);
             inputFromSocket =new BufferedReader(new InputStreamReader(socket.getInputStream()));
             outputToSocket.flush();
            //address of the host we want to connect to and the port number

            runMessageListener();

        }
        catch (IOException e)
        {
            System.out.println("Client Error "+e.getMessage() );
        }

    }
    public void runMessageListener()
    {
        new Thread(() -> {
            String input;
            while (isAlive&&!socket.isClosed())
            {
                try {
                    input=inputFromSocket.readLine();
                    if(input.equals("quit"))
                        break;
                    serverMessages.append(input).append('\n');
                    javafx.application.Platform.runLater(() ->
                    {
                        serverMessagesTA.setText(serverMessages.toString());
                        serverMessagesTA.appendText("");
                    });
                } catch (IOException e) {
                    break;
                }

            }
            if(!socket.isClosed()) {
                try {
                    outputToSocket.close();
                    inputFromSocket.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sendMessageBtn.setDisable(true);
            sendMessagesTF.setDisable(true);


        }).start();

    }

    @FXML
    void sendMessage(ActionEvent event) {
            if(!socket.isClosed()){
                outputToSocket.println(sendMessagesTF.getText());
                sendMessagesTF.clear();
            }


    }

}
