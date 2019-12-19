package ui;

import backend.File;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FileListCell extends JFXListCell<File>  {
    @FXML
    private Label fname,fsize,fcategory;
    @FXML
    private AnchorPane rootPane;
    private FXMLLoader mLLoader;
    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if(empty||item==null){}
        else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/filesListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            System.out.println(item.getName());
            fname.setText(item.getName());
            fsize.setText(Long.toString(item.getSize()));
            fcategory.setText(item.getCategory());

            setText(null);
            setGraphic(rootPane);
        }
    }


}
