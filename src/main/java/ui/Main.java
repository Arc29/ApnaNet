package ui;

import backend.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

import static backend.Peer.loggedIn;

public class Main extends Application {
    public static Proxy setProxy;
    private Thread servThread;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root;

            root = FXMLLoader.load(getClass().getResource("/fxml/signInForm.fxml"));
            primaryStage.setTitle("Sign In");
            primaryStage.setScene(new Scene(root, 652, 403));

        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        loadLoggedIn();
        loadProxy();
        Client.loadFiles();

        servThread=new Thread(new Server());
        servThread.setDaemon(true);
        servThread.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        servThread.interrupt();


    }

    public static void loadLoggedIn()throws Exception {
        try {
            String path = "./loggedIn.json";

            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Gson gson = new Gson();
            loggedIn = gson.fromJson(bufferedReader, CurrentUser.class);
        }catch(IOException e){loggedIn=null;}
    }

    public static void saveLoggedIn(){
        if(loggedIn!=null){
            Gson gson=new Gson();
            String dir="./loggedIn.json";

            try(BufferedWriter writer=new BufferedWriter(new FileWriter(dir,false))) {
                writer.write(gson.toJson(loggedIn));
            }catch (Exception e){e.printStackTrace();}
        }}

    public void loadProxy(){
        try {
            String path = "proxy.json";

            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Gson gson = new Gson();
            setProxy = gson.fromJson(bufferedReader, Proxy.class);
            System.out.println(setProxy);
        }catch(IOException e){setProxy=null;}
    }
    public static void saveProxy(){
        String dir="proxy.json";

       if(setProxy!=null){
            Gson gson=new Gson();
            try(BufferedWriter writer=new BufferedWriter(new FileWriter(dir,false))) {

            writer.write(gson.toJson(setProxy));
//                writer.write("ABC");
            }catch(Exception e){e.printStackTrace();}
        }
        else {
            try{
                new File(dir).delete();
            }catch (Exception e){e.printStackTrace();}
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
