module javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires gson;
    requires java.sql;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires  org.apache.httpcomponents.httpasyncclient;
    requires org.apache.httpcomponents.httpcore.nio;
    requires com.jfoenix;
    opens ui;
    opens backend;
}
