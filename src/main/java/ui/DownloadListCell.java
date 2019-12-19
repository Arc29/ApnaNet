package ui;

import backend.Client;
import backend.DownloadManager;
import backend.File;
import com.jfoenix.controls.JFXListCell;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DownloadListCell extends JFXListCell<Client> {
    @FXML
    private Label fname,fsize,percentProg;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ProgressBar pField;
    private FXMLLoader mLLoader;
    @Override
    protected void updateItem(Client item, boolean empty) {
        super.updateItem(item, empty);
        if(empty||item==null){}
        else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/downloadListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            System.out.println(item.getFileName());
            fname.setText(item.getFileName());
            fsize.setText(Long.toString(item.getFileSize()));
            Task<Void> dltask=new DownloadManager(item);
            pField.progressProperty().bind(dltask.progressProperty());
            percentProg.setText((pField.getProgress()*100)+" %");

            setText(null);
            setGraphic(rootPane);
        }
    }

}
