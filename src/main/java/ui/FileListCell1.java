package ui;

import backend.CurrentUser;
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

public class FileListCell1 extends JFXListCell<File>  {
    @FXML
    private Label fname,fsize,fcategory,count;
    @FXML
    private AnchorPane rootPane;
    private FXMLLoader mLLoader;
    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if(empty||item==null){}
        else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/fileListCell1.fxml"));
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
            int co=0;
            for(CurrentUser i:MainPage.peers){
                for(int j=0;j<i.getFilesDownloaded().length;j++){
                    if(item.getRootHash().equals(i.getFilesDownloaded()[j])){
                        co++;
                        break;
                    }
                }
            }
            count.setText(Integer.toString(co));
            setText(null);
            setGraphic(rootPane);
        }
    }


}
