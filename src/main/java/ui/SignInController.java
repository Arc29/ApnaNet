package ui;

import backend.BCrypt;
import backend.CurrentUser;
import backend.Peer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.*;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class SignInController {
    @FXML
    private JFXButton signinBtn,regBtn;
    @FXML
    private JFXTextField emailTxt;
    @FXML
    private JFXPasswordField passwordTxt;
    @FXML
    private JFXCheckBox remchk;
    @FXML
    private AnchorPane rootPane;
    private boolean flag;
    private  Stage progress=null;
    public void onSignIn() throws Exception {
        JFXSnackbar snackbar = new JFXSnackbar(rootPane);
        if (emailTxt.getText().indexOf('@') == -1)
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Invalid Email")));
        else {
            Future<HttpResponse> res = Peer.login(emailTxt.getText());
            flag=true;
            HttpResponse val=null;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/progress.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            progress = new Stage(StageStyle.UNDECORATED);
            progress.initModality(Modality.WINDOW_MODAL);
            progress.setScene(new Scene(root1));
            progress.setAlwaysOnTop(true);
            progress.show();

            Task<HttpResponse> task=new Task<HttpResponse>() {
                @Override
                protected HttpResponse call() throws Exception {
                    while(!res.isDone());
                    return res.get();
                }
            };
            task.setOnSucceeded(workerStateEvent -> {
//                    flag=false;
                progress.close();
                try {
                    String json = EntityUtils.toString(task.getValue().getEntity(), StandardCharsets.UTF_8);
                    if (task.getValue().getStatusLine().getStatusCode() == 200) {

                        JsonElement jsonElement = new JsonParser().parse(json);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String passHash = object.get("passHash").getAsString();
                        if (BCrypt.checkpw(passwordTxt.getText(), passHash)) {
                            Peer.loggedIn=new CurrentUser(object.get("nodeID").getAsString());
                            System.out.println(Peer.loggedIn);
//                                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Sign in successful")));
                            openMainPanel();

                        } else
                            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Invalid password")));

                    } else {

                        JsonElement jsonElement = new JsonParser().parse(json);
                        JsonObject object = jsonElement.getAsJsonObject();
                        String msg = object.get("msg").getAsString();
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout(msg)));
                    }
                }catch (Exception e){Alert dialog = new Alert(Alert.AlertType.ERROR);
                    dialog.setTitle("Error");
                    e.printStackTrace();
                    dialog.setContentText("Something went wrong: "+e);
                    dialog.showAndWait();}
            });
            Thread thread=new Thread(task);
            thread.start();
//            thread.join();


        }
    }
    public void onReg(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/sample.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void setProxy(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/proxySelect.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void openMainPanel(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root1));
            stage.show();
            ((Stage)rootPane.getScene().getWindow()).close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
