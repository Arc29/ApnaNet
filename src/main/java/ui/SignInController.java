package ui;

import backend.BCrypt;
import backend.Peer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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

    public void onSignIn() throws Exception {
        JFXSnackbar snackbar = new JFXSnackbar(rootPane);
        if (emailTxt.getText().indexOf('@') == -1)
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Invalid Email")));
        else {
            Future<HttpResponse> res = Peer.login(emailTxt.getText());
            while (!res.isDone()) ;
            if (res.isDone()) {
                String json = EntityUtils.toString(res.get().getEntity(), StandardCharsets.UTF_8);
                if (res.get().getStatusLine().getStatusCode() == 200) {

                    JsonElement jsonElement = new JsonParser().parse(json);
                    JsonObject object = jsonElement.getAsJsonObject();
                    String passHash = object.get("passHash").getAsString();
                    if (BCrypt.checkpw(passwordTxt.getText(), passHash)) {
                        //STUB
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Sign in successful")));
                    } else
                        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Invalid password")));
                } else {

                    JsonElement jsonElement = new JsonParser().parse(json);
                    JsonObject object = jsonElement.getAsJsonObject();
                    String msg = object.get("msg").getAsString();
                    snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout(msg)));
                }

            }


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
}
