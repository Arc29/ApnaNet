package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import backend.Peer;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.concurrent.Future;

public class Controller {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passField;
    @FXML
    private Button registerBtn;

    public Controller(){}
    public void buttonClicked()throws Exception {
        String name=nameField.getText(),email=emailField.getText(),password=passField.getText();
        Peer peer=new Peer(name,email,password);
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        Future<HttpResponse> f=peer.addUser();
        while(!f.isDone());
        if(f.isDone()){
            if(f.get().getStatusLine().getStatusCode()==200){
                dialog.setTitle("Success");
                dialog.setContentText("Registration successful");

            }
            else{
                dialog.setAlertType(Alert.AlertType.ERROR);
                dialog.setTitle("Error");
                dialog.setContentText("Registration failed");
            }
            dialog.showAndWait();
        }
    }
    public void selectFile(){
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

}
