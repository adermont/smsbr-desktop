/**
 * Module for SMS Backup & Restore file viewer application.
 */
open module com.smsbr.desktop {
    // Java SDK dependencies
    requires transitive java.desktop;

    // JavaFX dependencies
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.web;
    requires transitive javafx.swing;
    requires transitive jdk.jsobject;

    // Other libraries dependencies
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.jsoup;

    // Public API :
    exports com.smsbr.desktop.app;
    exports com.smsbr.desktop.io;
    exports com.smsbr.desktop.model;
    exports com.smsbr.desktop.services;
    exports com.smsbr.desktop.tools;
    exports com.smsbr.desktop.ui;
    exports com.smsbr.desktop.util;
}