package ui;

import backend.Proxy;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import static ui.Main.setProxy;

public class ProxyController {
    @FXML
    private JFXCheckBox authReq,manProxy;
    @FXML
    private JFXTextField ipTxt,portTxt,usrTxt;
    @FXML
    private JFXPasswordField passTxt;
    @FXML
    private AnchorPane rootPane;

    public ProxyController(){}

    @FXML
    public void initialize(){
        if(setProxy!=null){
            authReq.setSelected(setProxy.isAuth());
            manProxy.setSelected(setProxy.isProxySet());
            ipTxt.setText(setProxy.getIp());
            portTxt.setText(Integer.toString(setProxy.getPort()));
            usrTxt.setText(setProxy.getUsr());
            passTxt.setText(setProxy.getPass());
        }
        if(!manProxy.isSelected()){
            authReq.setDisable(true);
            ipTxt.setDisable(true);
            portTxt.setDisable(true);
            passTxt.setDisable(true);
            usrTxt.setDisable(true);
        }
        else if(!authReq.isSelected()){
            passTxt.setDisable(true);
            usrTxt.setDisable(true);
        }
    }

    @FXML
    public void setManProxy(ActionEvent ae){
        boolean flag=((JFXCheckBox)ae.getSource()).isSelected();
        if(flag){
            authReq.setDisable(false);
            ipTxt.setDisable(false);
            portTxt.setDisable(false);

        }
        else{
            authReq.setDisable(true);
            ipTxt.setDisable(true);
            portTxt.setDisable(true);
            passTxt.setDisable(true);
            usrTxt.setDisable(true);
        }
    }

    @FXML
    public void setAuthReq(ActionEvent ae){
        boolean flag=((JFXCheckBox)ae.getSource()).isSelected();
    if(flag){
        passTxt.setDisable(false);
        usrTxt.setDisable(false);
    }
    else{
        passTxt.setDisable(true);
        usrTxt.setDisable(true);
    }
    }

    @FXML
    public void saveProxy()throws IOException {
        if(!manProxy.isSelected())
            setProxy=null;
        else
        setProxy=new Proxy(manProxy.isSelected(),authReq.isSelected(),ipTxt.getText(),usrTxt.getText(),passTxt.getText(),Integer.parseInt(portTxt.getText()));
        ((Stage)rootPane.getScene().getWindow()).close();
    }

}
