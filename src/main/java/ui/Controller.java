package ui;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import backend.Peer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
        Stage progress;
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
                while(!f.isDone());
                return f.get();
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
//                    flag=false;
                progress.close();
            if(task.getValue().getStatusLine().getStatusCode()==200){
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
    });
        new Thread(task).start();
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
