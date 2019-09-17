package ui;

import backend.Peer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;

import java.io.File;
import java.util.concurrent.Future;

public class FileController {
    @FXML
    private Button fchooseBtn,shareBtn;
    @FXML
    private TextField fchooseTxt,tagsTxt,nameTxt;
    @FXML
    private ComboBox<String> catBox;
    @FXML
    private AnchorPane mainPane;
    private FileChooser fileChooser;
    private File selectedFile;

    public FileController(){}

    @FXML
    public void initialize() {
        catBox.getItems().removeAll(catBox.getItems());
        catBox.getItems().addAll("Movie","Song","Music Video","Document","Image","Book","Game","Software");
        catBox.getSelectionModel().select("Movie");
        fileChooser = new FileChooser();

    }
    @FXML
    public void fChoose(ActionEvent ae){
        selectedFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        fchooseTxt.setText(selectedFile.getAbsolutePath());

    }
    @FXML
    public void share(ActionEvent ae)throws Exception{
        String name=nameTxt.getText(),tags=tagsTxt.getText(),category=catBox.getSelectionModel().getSelectedItem();
        String[] tagArr=tags.split(";");
        for(String s:tagArr)
            s.strip();
        backend.File file=new backend.File(selectedFile,name,category,tagArr,Peer.loggedIn);
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        Future<HttpResponse> f=file.addFile();
        while(!f.isDone());
        if(f.isDone()){
            if(f.get().getStatusLine().getStatusCode()==200){
                dialog.setTitle("Success");
                dialog.setContentText("File added successfully");

            }
            else{
                dialog.setAlertType(Alert.AlertType.ERROR);
                dialog.setTitle("Error");
                dialog.setContentText("Failed");
            }
            dialog.showAndWait();
        }
    }
}
