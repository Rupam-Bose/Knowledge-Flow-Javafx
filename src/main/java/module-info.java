module com.example.knowlwdgeflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.graphics;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
    requires org.slf4j.simple;
    requires javafx.swing;
    requires java.desktop;
    requires java.prefs;

    opens com.example.knowlwdgeflow to javafx.fxml;
    opens com.example.knowlwdgeflow.Controllers to javafx.fxml;
    exports com.example.knowlwdgeflow;
    exports com.example.knowlwdgeflow.model;
    exports com.example.knowlwdgeflow.dao;
}