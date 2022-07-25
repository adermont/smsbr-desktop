package com.smsbr.desktop.app;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@DisplayName("Launch and test the application")
class TestJfxDesktopApp {

    SmsbrDesktopApp app = new SmsbrDesktopApp();

    @Start
    public void start(Stage primaryStage) {
	Preferences prefs = new Preferences();
	prefs.setLoadLastFile(false);
	prefs.setPreferredPalette(2);
	assertDoesNotThrow(() -> {
	    app.setPreferences(prefs);
	    app.start(primaryStage);
	});
    }

    @Test
    void testAboutDialog(FxRobot robot) {
	// Assert that the application is not loading any file
	Assertions.assertThat(robot.lookup("#glasspane-button-cancel").tryQueryAs(Button.class)).isEmpty();
	Assertions.assertThat(robot.listWindows().size()).isEqualTo(1);

	// Click on 'Help > About...' menu
	robot.clickOn("#menu-help", MouseButton.PRIMARY);
	robot.clickOn("#menu-help-about", MouseButton.PRIMARY);

	WaitForAsyncUtils.waitForFxEvents();

	Assertions.assertThat(robot.listWindows().size()).isEqualTo(2);
	Assertions
		.assertThat(
			robot.from(robot.window(1).getScene().getRoot()).lookup(".header-panel .label").queryLabeled())
		.hasText("SMS Backup & Restore for Desktop");

	robot.clickOn(robot.from(robot.window(1).getScene().getRoot()).lookup(".button:close").queryButton());
	Assertions.assertThat(robot.listWindows().size()).isEqualTo(1);
    }

    @Test
    void testFileLoading(FxRobot robot) {
	robot.interactNoWait(() -> app.loadFile(new File("./src/test/resources/sample.xml")));
	WaitForAsyncUtils.waitForFxEvents();
	Assertions.assertThat(robot.lookup("#listview-contacts").queryListView()).hasExactlyNumItems(1);
    }
}
