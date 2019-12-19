package ui;

import backend.*;
import com.google.gson.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static backend.Peer.loggedIn;
import static ui.Main.saveLoggedIn;
import static ui.Main.saveProxy;

public class MainPage implements Initializable {
    @FXML
    private JFXButton browseBtn,dlBtn,shareBtn,logOutBtn,fShareBtn,playBtn,pauseBtn,searchButton,refreshBtn;
    @FXML
    private JFXTextField searchTxt;
    @FXML
    private JFXListView<File> list1,list3;
    @FXML
    private JFXListView<Client> list2;
    private ObservableList<File> fileObservableList1,fileObservableList3;
    private  Stage progress=null;
    public static ArrayList<CurrentUser> peers;
    public MainPage(){
        fileObservableList1 = FXCollections.observableArrayList();
        fileObservableList3=FXCollections.observableArrayList();

        Future<HttpResponse> files=File.getFiles();
        Task<HttpResponse> task=new Task<HttpResponse>() {
            @Override
            protected HttpResponse call() throws Exception {
                while (!files.isDone()) ;
                return files.get();
            }
        };
        try{
            task.setOnSucceeded(workerStateEvent -> {
                try{
                    if(task.getValue().getStatusLine().getStatusCode()==200){
                        Gson gson=new Gson();
                        String json = EntityUtils.toString(task.getValue().getEntity(), StandardCharsets.UTF_8);
                        System.out.println(json);
                        JsonElement jsonElement = new JsonParser().parse(json);
                        JsonArray arr = jsonElement.getAsJsonArray();
                        System.out.println(arr);
                        System.out.println(arr.size());
                        for(int i=0;i<arr.size();i++) {
                            File t=gson.fromJson(arr.get(i), File.class);
                            System.out.println(t.getSharedBy());
//                            if(!t.getSharedBy().equals(loggedIn.getNodeID()))
//                                fileObservableList1.add(t);
//                            else
//                                fileObservableList3.add(t);
                            fileObservableList1.add(t);
                        }



                    }}catch (IOException e){e.printStackTrace();}

            });
        }catch (Exception e){e.printStackTrace();}
        new Thread(task).start();
    }
    public void refresh(){

        fileObservableList1=FXCollections.observableArrayList();
        fileObservableList3=FXCollections.observableArrayList();
        Future<HttpResponse> files=File.getFiles();
        Task<HttpResponse> task=new Task<HttpResponse>() {
            @Override
            protected HttpResponse call() throws Exception {
                while (!files.isDone()) ;
                return files.get();
            }
        };
        try{
            task.setOnSucceeded(workerStateEvent -> {
                try{
                    if(task.getValue().getStatusLine().getStatusCode()==200){
                        Gson gson=new Gson();
                        String json = EntityUtils.toString(task.getValue().getEntity(), StandardCharsets.UTF_8);
                        System.out.println(json);
                        JsonElement jsonElement = new JsonParser().parse(json);
                        JsonArray arr = jsonElement.getAsJsonArray();
                        System.out.println(arr);
                        System.out.println(arr.size());
                        for(int i=0;i<arr.size();i++) {
                            File t=gson.fromJson(arr.get(i), File.class);
                            System.out.println(t.getSharedBy());
                            System.out.println(loggedIn.getNodeID());
                            System.out.println(t.getSharedBy().equals(loggedIn.getNodeID()));
                            if(!t.getSharedBy().equals(loggedIn.getNodeID())){
                            fileObservableList1.add(t);System.out.println(1);}
                            else{
                                fileObservableList3.add(t);System.out.println(2);}
//                            fileObservableList1.add(t);
                        }



                    }}catch (IOException e){e.printStackTrace();}

            });
        }catch (Exception e){e.printStackTrace();}
        new Thread(task).start();


            list1.setItems(fileObservableList1);
            list1.setCellFactory(fileListView -> new FileListCell());

            list3.setItems(fileObservableList3);
            list3.setCellFactory(fileListView -> new FileListCell1());
            list2.setItems(Client.currentFiles());
            list2.setCellFactory(dlListView -> new DownloadListCell());
        }


    public void loadAllPeers(){
        CloseableHttpAsyncClient client = Proxy.clientBuild();
        client.start();
        HttpGet request = new HttpGet("https://apnanet-central.herokuapp.com/");

        Future<HttpResponse> future = client.execute(request, null);
        Task<HttpResponse> t=new Task<HttpResponse>() {
            @Override
            protected HttpResponse call() throws Exception {
                while (!future.isDone());
                    return future.get();
            }
        };
        t.setOnSucceeded(workerStateEvent -> {
            try {
                peers=new ArrayList<>();
                if(t.getValue().getStatusLine().getStatusCode()==200){
//                    refresh();
                    Gson gson=new Gson();
                    String json = EntityUtils.toString(t.getValue().getEntity(), StandardCharsets.UTF_8);
                    JsonElement jsonElement = new JsonParser().parse(json);
                    JsonArray arr = jsonElement.getAsJsonArray();
                    for(int i=0;i<arr.size();i++)
                        peers.add(gson.fromJson(arr.get(i), CurrentUser.class));
                }
            }catch (Exception e){e.printStackTrace();}
        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        peers=new ArrayList<>();
        loadAllPeers();
        list1.setItems(fileObservableList1);
        list1.setCellFactory(fileListView -> new FileListCell());
        playBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/drawable/play.png"))));
        pauseBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/drawable/pause.png"))));

        Future<HttpResponse> user=Peer.getUser(Peer.loggedIn.getNodeID());

        Future<HttpResponse> active = setActive();
        Task<HttpResponse> task=new Task<HttpResponse>() {
            @Override
            protected HttpResponse call() throws Exception {
                while(!active.isDone());
                return active.get();
            }
        },t=new Task<HttpResponse>() {
            @Override
            protected HttpResponse call() throws Exception {
                while (!user.isDone());
                return user.get();
            }
        };

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/progress.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            progress = new Stage(StageStyle.UNDECORATED);
            progress.initModality(Modality.WINDOW_MODAL);
            progress.setScene(new Scene(root1));
            progress.setAlwaysOnTop(true);
            progress.show();



            task.setOnSucceeded(workerStateEvent -> {
                System.out.println("Task 1 done");
                if(task.getValue().getStatusLine().getStatusCode()==400){
                    Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Already logged in");
                    dialog.setContentText("Please log out from other system");
                    dialog.showAndWait();
                    Peer.loggedIn=null;
                    Platform.exit();
                }
            });

            t.setOnSucceeded(workerStateEvent -> {
                System.out.println("123");
                progress.close();
                try{
                if(t.getValue().getStatusLine().getStatusCode()==200){
                    Gson gson=new Gson();
                    Peer.loggedIn=gson.fromJson(EntityUtils.toString(t.getValue().getEntity(), StandardCharsets.UTF_8), CurrentUser.class);
                    System.out.println(Peer.loggedIn);
                }}catch (IOException e){e.printStackTrace();}
            });
        }catch (Exception e){
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setContentText("Something went wrong: "+e);
            dialog.showAndWait();
        }
        new Thread(t).start();
        new Thread(task).start();
        list2.setVisible(false);
        list3.setVisible(false);


    }
    public Future<HttpResponse> setActive(){
        CloseableHttpAsyncClient client = Proxy.clientBuild();
        client.start();
        HttpPut request = new HttpPut("https://apnanet-central.herokuapp.com/signin/"+ Peer.loggedIn.getNodeID());
        Future<HttpResponse> future = client.execute(request, null);
        return future;
    }
    @FXML
    public void onLogout() {
        saveLoggedIn();
        saveProxy();
        Client.saveFiles();
        if (loggedIn != null) {
            System.out.println("Logged out successfully");

            CloseableHttpAsyncClient client = Proxy.clientBuild();
            client.start();
            HttpDelete request = new HttpDelete("https://apnanet-central.herokuapp.com/signin/" + loggedIn.getNodeID());
            loggedIn = null;
            Future<HttpResponse> future = client.execute(request, null);

            try {
                Stage progress;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/progress.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                progress = new Stage(StageStyle.UNDECORATED);
                progress.initModality(Modality.WINDOW_MODAL);
                progress.setScene(new Scene(root1));
                progress.setAlwaysOnTop(true);
                progress.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Task<HttpResponse> task = new Task<HttpResponse>() {
                @Override
                protected HttpResponse call() throws Exception {
                    while (!future.isDone()) ;
                    return future.get();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
//                    flag=false;
                progress.close();
                Platform.exit();
            });
            Thread thread = new Thread(task);
            thread.start();

        }
    }
    @FXML
    public void onFileSelect(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/fileSelect.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void but1(){
        list1.setVisible(true);
        list2.setVisible(false);
        list3.setVisible(false);
    }
    @FXML
    public void but2(){
        list1.setVisible(false);
        list2.setVisible(true);
        list3.setVisible(false);
    }
    @FXML
    public void but3(){
        list1.setVisible(false);
        list2.setVisible(false);
        list3.setVisible(true);
    }
    @FXML
    public void play(){
        if(list2.isVisible()){
            Client c=list2.getSelectionModel().getSelectedItem();
            c.download();
        }
        else if(list1.isVisible()){
            File f=list1.getSelectionModel().getSelectedItem();
            Client c =new Client(f.getRootHash(),f.getChunkHashes(),f.getSize(),f.getName());

            CloseableHttpAsyncClient client = Proxy.clientBuild();
            client.start();
            HttpPut request = new HttpPut("https://apnanet-central.herokuapp.com/files/?nodeID=" + loggedIn.getNodeID()+"&rootHash="+f.getRootHash());
            Future<HttpResponse> future = client.execute(request, null);

            try {
                Stage progress;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/progress.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                progress = new Stage(StageStyle.UNDECORATED);
                progress.initModality(Modality.WINDOW_MODAL);
                progress.setScene(new Scene(root1));
                progress.setAlwaysOnTop(true);
                progress.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Task<HttpResponse> task = new Task<HttpResponse>() {
                @Override
                protected HttpResponse call() throws Exception {
                    while (!future.isDone()) ;
                    return future.get();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {

                progress.close();
                if(task.getValue().getStatusLine().getStatusCode()==200){
                    System.out.println("Successfully added to downloads");
                    Client.addToFiles(c);
                    refresh();
                    but2();
                    c.download();
                }
            });
            Thread thread = new Thread(task);
            thread.start();

        }
    }
    @FXML
    public void pause(){
        if(list2.isVisible()){
            Client c=list2.getSelectionModel().getSelectedItem();
            c.stop(false);
        }
    }
    @FXML
    public void searchChanged(ActionEvent ae){
        String s=searchTxt.getText();
        if(list1.isVisible()) {

            fileObservableList1.sort(new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return sumOfLevenshtein(s,o1)-sumOfLevenshtein(s,o2);
                }
            });
        }
        else if(list3.isVisible()) {

            fileObservableList3.sort(new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return sumOfLevenshtein(s,o1)-sumOfLevenshtein(s,o2);
                }
            });
        }

    }
    public int sumOfLevenshtein(String txt,File f){
        int sum=0;
        LevenshteinDistance ld=new LevenshteinDistance();
        for(int i=0;i<f.getTags().length;i++){
            sum+=ld.apply(txt,f.getTags()[i]);
        }
        sum+=ld.apply(txt,f.getCategory());
        sum+=ld.apply(txt,f.getName());
        return sum;
    }
}
