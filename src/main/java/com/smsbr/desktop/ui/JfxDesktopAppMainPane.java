/**
 * Copyright 2022 Alexandre DERMONT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.smsbr.desktop.ui;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.swing.Timer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.smsbr.desktop.app.Bundle;
import com.smsbr.desktop.app.Preferences;
import com.smsbr.desktop.model.Contact;
import com.smsbr.desktop.model.Conversations;
import com.smsbr.desktop.model.IConversationsListener;
import com.smsbr.desktop.model.ImagePart;
import com.smsbr.desktop.model.Order;
import com.smsbr.desktop.model.OrderBy;
import com.smsbr.desktop.model.Sms;
import com.smsbr.desktop.services.SmsBackupFileLoadingService;
import com.smsbr.desktop.util.FxUtil;
import com.smsbr.desktop.util.Htmlizer;
import com.smsbr.desktop.util.ImageUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main panel of the application.
 * 
 * @author Alexandre DERMONT
 */
public class JfxDesktopAppMainPane extends HBox implements IConversationsListener {

    /** Private logger for this class. */
    private static Logger logger = System.getLogger(JfxDesktopAppMainPane.class.getName());

    /** Size of icons for menus and buttons. */
    private static final int ICONS_SIZE = 16;

    protected static final String PREFIX = JfxDesktopAppMainPane.class.getName();
    protected static final String WEB_VIEW_WELCOME_MESSAGE = PREFIX + ".webView.welcomeMessage";
    protected static final String GENERIC_ERROR_MESSAGE = PREFIX + ".genericErrorMessage";

    protected static final String MENU_FILE = PREFIX + ".menuFile";
    protected static final String MENU_FILE_ACTION_QUIT = PREFIX + ".menuFile.actionQuit";
    protected static final String MENU_FILE_ACTION_PRINT = PREFIX + ".menuFile.actionPrint";
    protected static final String MENU_FILE_ACTION_EXPORT = PREFIX + ".menuFile.actionExport";
    protected static final String MENU_FILE_ACTION_RELOAD = PREFIX + ".menuFile.actionReload";
    protected static final String MENU_FILE_ACTION_OPEN = PREFIX + ".menuFile.actionOpen";
    protected static final String MENU_FILE_ACTION_REFRESH = PREFIX + ".menuFile.actionRefresh";
    protected static final String MENU_FILE_ACTION_OPEN_ERROR = PREFIX + ".menuFile.actionOpen.error";
    protected static final String MENU_FILE_ACTION_EXPORT_FILE_DESCRIPTION = PREFIX
	    + ".menuFile.actionExport.fileDescription";
    protected static final String MENU_FILE_ACTION_OPEN_FILE_DESCRIPTION = PREFIX
	    + ".menuFile.actionOpen.fileDescription";

    protected static final String MENU_EDIT = PREFIX + ".menuEdit";
    protected static final String MENU_EDIT_ACTION_PREFERENCES = PREFIX + ".menuEdit.actionPreferences";

    protected static final String MENU_TOOLS = PREFIX + ".menuTools";
    protected static final String MENU_TOOLS_ACTION_EXTRACT_IMAGES = PREFIX + ".menuTools.actionExtractImages";

    protected static final String MENU_HELP = PREFIX + ".menuHelp";
    protected static final String MENU_HELP_ACTION_ABOUT = PREFIX + ".menuHelp.actionAbout";

    protected static final String TABS_IMAGES = PREFIX + ".tabs.images";
    protected static final String BUTTON_CLOSE_LABEL = PREFIX + ".closeButton.label";
    protected static final String BUTTON_CANCEL_LABEL = PREFIX + ".cancelButton.label";
    protected static final String CONTEXT_MENU_SAVE_IMAGE = PREFIX + ".contextMenuSaveImageAs";
    protected static final String CONTEXT_MENU_SAVE_ALL_IMAGES = PREFIX + ".contextMenuSaveAllImages";
    protected static final String ALERT_TEXT_NO_IMAGE_TO_EXPORT = PREFIX + ".exportAllImages.noimage.alert";
    protected static final String ASK_OVERWRITE_MESSAGE = PREFIX + ".overwriteDestinationFiles.alert";

    protected static final String EXPORT_IMAGES_DATE_FORMAT = "yyyyMMdd_HHmmss";

    /** The user's preferences (in {@link Preferences#DEFAULT_PREFERENCES_DIR}). */
    protected Preferences mPreferences;
    /** The application's bundle with localized labels. */
    protected Bundle mBundle;
    /** The application's bundle with version infos. */
    protected Bundle mVersionBundle;

    /** Root pane displayed when a loading task is running. */
    protected VBox mWorkInProgressLayer;
    /** The progress value of a running task is displayed as a ProgressIndicator. */
    protected ProgressIndicator mProgressIndicator;

    /** SmsbrDesktopApp's tool bar. */
    protected ToolBar mToolbar;
    /** SmsbrDesktopApp's menu bar. */
    protected MenuBar mMenuBar;
    /** The 'File' menu. */
    protected Menu mMenuFile;
    /** The 'Edit' menu. */
    protected Menu mMenuEdit;
    /** The 'Tools' menu. */
    protected Menu mMenuTools;
    /** The 'Help' menu. */
    protected Menu mMenuHelp;
    /** The 'File > Open' menu item. */
    protected MenuItem mMenuItemFileOpen;
    /** The 'File > Reload' menu item. */
    protected MenuItem mMenuItemFileReload;
    /** The 'File > Export to HTML' menu item. */
    protected MenuItem mMenuItemFileExportToHtml;
    /** The 'File > Print' menu item. */
    protected MenuItem mMenuItemFilePrint;
    /** The 'File > Quit' menu item. */
    protected MenuItem mMenuItemFileQuit;
    /** The 'Edit > Preferences' menu item. */
    protected MenuItem mMenuItemEditPreferences;
    /** The 'Tools > Extract images' menu item. */
    protected MenuItem mMenuItemToolsExtractImages;
    /** The 'Help > Preferences' menu item. */
    protected MenuItem mMenuItemHelpAbout;

    /** Tool bar button for opening a new file. */
    protected Button mButtonOpenFile;
    /** Tool bar button to refresh current conversation (debug purpose). */
    protected Button mButtonRefreshConversation;

    /**
     * Cancel button displayed in the {{@link #mWorkInProgressLayer} when a loading
     * task is performed. It allows to cancel the loading task.
     */
    protected Button mActionCancelTask;

    /**
     * Global split pane : left is for contacts, middle is for conversation
     * (WebView) and right part is a set of tabs for images or else.
     */
    protected SplitPane mMainSplitPane;

    /** Root panel of the middle part of the {@link #mMainSplitPane} panel. */
    protected StackPane mConversationStackPane;
    /** WebView for displaying conversations. */
    protected WebView mConversationWebView;
    /** Layer that is displayed over the WebView when user clicks on an image. */
    protected HBox mImagePreviewLayer;

    /** Tab pane in the right side of the {@link #mMainSplitPane}. */
    protected TabPane mTabPaneOverview;
    /** Tab to display a quick overview of all images of a conversation. */
    protected Tab mTabImagesOverview;
    /** Panel to layout images in the {@link #mTabImagesOverview} tab. */
    protected FlowPane mImagesOverviewPane;

    /** Sorted list of all the contacts found in the XML backup file. */
    protected ListView<Contact> mContactListView;

    /** ComboBox for choosing colors of the current conversation in the WebView. */
    protected JfxPaletteComboBox mPalettesComboBox;

    /** ComboBox for choosing the preferred language. */
    protected JfxLanguageComboBox mLanguageComboBox;

    /** Directory of the current loaded backup file. */
    protected File mCurrentDir;
    /** Current backup file (which may be loaded or not). */
    protected File mCurrentFile;

    /** SmsbrDesktopApp's model */
    protected Conversations mModel;

    /** A cache used to quickly export HTML from the WebView to an HTML file. */
    protected String mModelAsHtml;

    /** Current color palette. */
    protected int mNumPalette;

    /** Service used for launching background loading tasks. */
    protected Service<Conversations> mFileLoadingService;

    /**
     * Buffer that stores keyboards characters pressed when
     * {@link #mContactListView} has focus. This buffer is used to search contacts
     * in the list.
     */
    protected StringBuilder mKeyEventsBuffer;
    /** Timer that will reset the {@link #mKeyEventsBuffer} every 1200ms. */
    protected Timer mTimerForKeyEventsAccumulation;

    /** Util for regular text to HTML conversions. */
    protected Htmlizer mHtmlizer;

    // ---------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param preferences User's preferences.
     * @param bundle      SmsbrDesktopApp's resources bundle.
     */
    public JfxDesktopAppMainPane(Preferences preferences, Bundle bundle, Bundle versionBundle) {
	super();
	mPreferences = preferences;
	mBundle = bundle;
	mVersionBundle = versionBundle;
	mBundle.addBundleListener(this::reloadLabelsFromBundle);

	mKeyEventsBuffer = new StringBuilder();
	mTimerForKeyEventsAccumulation = new Timer(1200, e -> resetKeyEventBuffer());

	mHtmlizer = new Htmlizer(bundle, preferences);

	initServices();
	initComponents();
	initBehaviors();
	initLayout();
	initAccelerators();
	injectPreferences();
	refreshConversationView();
	reloadLabelsFromBundle();
    }

    /**
     * Reset the key event buffer.
     */
    protected void resetKeyEventBuffer() {
	mKeyEventsBuffer.delete(0, mKeyEventsBuffer.length());
    }

    /**
     * Initialize all required services responsible for running background tasks
     * (like loading files...etc).
     */
    private void initServices() {

	// Build the file loading service
	mFileLoadingService = new SmsBackupFileLoadingService(() -> mCurrentFile);

	// Initialize the loading service behaviors
	mFileLoadingService.setOnRunning(e -> showBusyOverlay(true));

	mFileLoadingService.setOnSucceeded(e -> {
	    setModel(mFileLoadingService.getValue());
	    mPreferences.save();
	    mMenuItemFilePrint.setDisable(mContactListView.getSelectionModel().isEmpty());
	    mMenuItemFileReload.setDisable(mCurrentFile == null);
	    showBusyOverlay(false);
	    mFileLoadingService.reset();
	});
	mFileLoadingService.setOnCancelled(e -> {
	    showBusyOverlay(false);
	    mFileLoadingService.reset();
	});
	mFileLoadingService.setOnFailed(e -> {
	    logAndDisplayError(e.getSource().getException());
	    showBusyOverlay(false);
	    mFileLoadingService.reset();
	});
    }

    /**
     * Instantiates JavaFX components.
     */
    private void initComponents() {
	mActionCancelTask = new JfxButtonAction("glasspane-button-cancel", mBundle.getString(BUTTON_CANCEL_LABEL));

	mContactListView = new ListView<>();
	mContactListView.setCellFactory((ListView<Contact> view) -> new ContactCell());

	mPalettesComboBox = new JfxPaletteComboBox(mBundle);
	mLanguageComboBox = new JfxLanguageComboBox();

	mConversationWebView = new WebView();
	mConversationWebView.getEngine().setJavaScriptEnabled(true);

	mImagePreviewLayer = new HBox();
	mImagePreviewLayer.setAlignment(Pos.TOP_CENTER);
	mImagePreviewLayer.setBackground(new Background(
		new BackgroundFill(javafx.scene.paint.Color.BLACK.deriveColor(0, 0, 0, 0.9), null, null)));
	mImagePreviewLayer.setOnMouseReleased(e -> hideImagePreviewLayer());

	mTabImagesOverview = new Tab(mBundle.getString(TABS_IMAGES));
	ImageView imageTabIcon = Resources.getIcon(Resources.ICON_PICTURE_URL, 16, 16);
	mTabImagesOverview.setGraphic(imageTabIcon);
	mTabImagesOverview.setClosable(false);

	mTabPaneOverview = new TabPane(mTabImagesOverview);
	mImagesOverviewPane = new FlowPane();

	mProgressIndicator = new ProgressIndicator();
	mProgressIndicator.setPrefSize(100, 100);
	mProgressIndicator.setMaxSize(300, 300);

	mWorkInProgressLayer = new VBox();
	mWorkInProgressLayer.setBackground(Background.fill(Color.WHITE));
	mWorkInProgressLayer.setOpacity(0.8);

	mConversationStackPane = new StackPane();

	mButtonRefreshConversation = new JfxButtonAction("toolbar-button-refresh",
		mBundle.getString(MENU_FILE_ACTION_REFRESH), Resources.getIcon(Resources.ICON_REFRESH_URL, ICONS_SIZE));

	mButtonOpenFile = new JfxButtonAction("toolbar-button-openfile", mBundle.getString(MENU_FILE_ACTION_OPEN),
		Resources.getIcon(Resources.ICON_OPEN_URL, ICONS_SIZE));

	mToolbar = new ToolBar(new Separator(), mButtonOpenFile, mButtonRefreshConversation, new Separator(),
		mLanguageComboBox);

	mMenuFile = new Menu(mBundle.getString(MENU_FILE));
	mMenuEdit = new Menu(mBundle.getString(MENU_EDIT));
	mMenuTools = new Menu(mBundle.getString(MENU_TOOLS));
	mMenuHelp = new Menu(mBundle.getString(MENU_HELP));

	mMenuItemFileOpen = new MenuItem(mBundle.getString(MENU_FILE_ACTION_OPEN),
		Resources.getIcon(Resources.ICON_OPEN_URL, ICONS_SIZE));
	mMenuItemFileReload = new MenuItem(mBundle.getString(MENU_FILE_ACTION_RELOAD),
		Resources.getIcon(Resources.ICON_REFRESH_URL, ICONS_SIZE));
	mMenuItemFileExportToHtml = new MenuItem(mBundle.getString(MENU_FILE_ACTION_EXPORT),
		Resources.getIcon(Resources.ICON_EXPORT_URL, ICONS_SIZE));
	mMenuItemFilePrint = new MenuItem(mBundle.getString(MENU_FILE_ACTION_PRINT),
		Resources.getIcon(Resources.ICON_PRINT_URL, ICONS_SIZE));
	mMenuItemFileQuit = new MenuItem(mBundle.getString(MENU_FILE_ACTION_QUIT),
		Resources.getIcon(Resources.ICON_CLOSE_URL, ICONS_SIZE));
	mMenuItemEditPreferences = new MenuItem(mBundle.getString(MENU_EDIT_ACTION_PREFERENCES),
		Resources.getIcon(Resources.ICON_PREFERENCES_URL, ICONS_SIZE));
	mMenuItemToolsExtractImages = new MenuItem(mBundle.getString(MENU_TOOLS_ACTION_EXTRACT_IMAGES),
		Resources.getIcon(Resources.ICON_PICTURE_URL, ICONS_SIZE));
	mMenuItemHelpAbout = new MenuItem(mBundle.getString(MENU_HELP_ACTION_ABOUT),
		Resources.getIcon(Resources.ICON_ABOUT_URL, ICONS_SIZE));

	mMenuFile.getItems().addAll(mMenuItemFileOpen, mMenuItemFileReload, mMenuItemFileExportToHtml,
		new SeparatorMenuItem(), mMenuItemFilePrint, new SeparatorMenuItem(), mMenuItemFileQuit);
	mMenuEdit.getItems().addAll(mMenuItemEditPreferences);
	mMenuTools.getItems().addAll(mMenuItemToolsExtractImages);
	mMenuHelp.getItems().add(mMenuItemHelpAbout);

	mMenuBar = new MenuBar(mMenuFile, mMenuEdit, mMenuTools, mMenuHelp);

	mContactListView.setId("listview-contacts");
	mPalettesComboBox.setId("combobox-palettes");
	mLanguageComboBox.setId("combobox-language");
	mMenuFile.setId("menu-file");
	mMenuEdit.setId("menu-edit");
	mMenuTools.setId("menu-tools");
	mMenuHelp.setId("menu-help");
	mMenuItemFileOpen.setId("menu-file-open");
	mMenuItemFileReload.setId("menu-file-reload");
	mMenuItemFileExportToHtml.setId("menu-file-exporttohtml");
	mMenuItemFilePrint.setId("menu-file-print");
	mMenuItemFileQuit.setId("menu-file-quit");
	mMenuItemEditPreferences.setId("menu-edit-preferences");
	mMenuItemToolsExtractImages.setId("menu-tools-extractimages");
	mMenuItemHelpAbout.setId("menu-help-about");
    }

    /**
     * Initialize the JavaFX component's behavior.
     */
    private void initBehaviors() {

	mButtonRefreshConversation.setOnAction(event -> refreshConversationView());
	mButtonOpenFile.setOnAction(this::onOpenFileAction);

	mConversationWebView.getEngine().setOnAlert(this::onWebViewAlertEvent);
	mConversationWebView.setOnMouseClicked(event -> {
	    if (event.getButton() == MouseButton.BACK) {
		goBackward();
	    } else if (event.getButton() == MouseButton.FORWARD) {
		goForward();
	    }
	});
	mConversationWebView.getEngine().setCreatePopupHandler(this::onWebViewPopupEvent);

	// Disallow 'prompt' javascript calls in the WebView.
	mConversationWebView.getEngine().setPromptHandler(param -> null);

	// File menu
	mMenuItemFileOpen.setOnAction(this::onOpenFileAction);
	mMenuItemFileReload.setOnAction(this::onReloadFileAction);
	mMenuItemFileExportToHtml.setOnAction(me -> onExportToHtmlAction(mContactListView.getItems()));
	mMenuItemFilePrint.setOnAction(e -> onPrintAction());
	mMenuItemFileQuit.setOnAction(e -> getScene().getWindow()
		.fireEvent(new WindowEvent(getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST)));

	// Edit menu
	mMenuItemEditPreferences.setOnAction(actionEvent -> onEditPreferencesAction());

	// Tools menu
	mMenuItemToolsExtractImages.setOnAction(actionEvent -> onExtractAllImagesAction(Collections.emptyList()));

	// Help menu
	mMenuItemHelpAbout.setOnAction(actionEvent -> onAboutAction());

	mContactListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	mContactListView.getSelectionModel().selectedItemProperty()
		.addListener((observable, oldValue, newValue) -> onSelectedContactChanged());

	mContactListView.setOnContextMenuRequested(e -> {
	    MenuItem menuItemExportAllImages = new MenuItem(mBundle.getString(CONTEXT_MENU_SAVE_ALL_IMAGES),
		    Resources.getIcon(Resources.ICON_PICTURE_URL, ICONS_SIZE));
	    MenuItem menuItemFileExportToHtml = new MenuItem(mBundle.getString(MENU_FILE_ACTION_EXPORT),
		    Resources.getIcon(Resources.ICON_EXPORT_URL, ICONS_SIZE));
	    menuItemFileExportToHtml
		    .setOnAction(me -> onExportToHtmlAction(mContactListView.getSelectionModel().getSelectedItems()));
	    menuItemExportAllImages.setOnAction(
		    ae -> onExtractAllImagesAction(mContactListView.getSelectionModel().getSelectedItems()));
	    ContextMenu menu = new ContextMenu(menuItemFileExportToHtml, menuItemExportAllImages);
	    menu.show(getScene().getWindow(), e.getScreenX(), e.getScreenY());
	});
	// When pressing "DELE" key, delete selected conversation locally.
	// When pressing a character, select the first contact whose name starts with
	// this char.
	mContactListView.setOnKeyPressed(e -> {
	    if (e.getCode() == KeyCode.DELETE) {
		mModel.removeAllConversations(new ArrayList<>(mContactListView.getSelectionModel().getSelectedItems()));
	    } else {
		if (!(e.isAltDown() || e.isControlDown() || e.isMetaDown() || e.isShortcutDown())
			&& !StringUtils.isBlank(e.getText())) {

		    processKeyPressedInContactList(e.getText());
		}
	    }
	});
	mPalettesComboBox.setOnAction(event -> onSelectedPaletteChanged());

	mMenuItemFilePrint.setDisable(mContactListView.getSelectionModel().isEmpty());

	mImagesOverviewPane.setOnContextMenuRequested(event -> {
	    MenuItem menuItem = new MenuItem(mBundle.getString(CONTEXT_MENU_SAVE_ALL_IMAGES));
	    menuItem.setOnAction(menuItemEvent -> onExtractAllImagesAction(
		    Arrays.asList(mContactListView.getSelectionModel().getSelectedItem())));
	    new ContextMenu(menuItem).show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());
	});
    }

    /**
     * Initialize accelerator keys.
     */
    private void initAccelerators() {
	mMenuItemFileOpen.setAccelerator(KeyCombination.keyCombination("ctrl+o"));
	mMenuItemFileExportToHtml.setAccelerator(KeyCombination.keyCombination("ctrl+s"));
	mMenuItemFilePrint.setAccelerator(KeyCombination.keyCombination("ctrl+p"));
	mMenuItemFileQuit.setAccelerator(KeyCombination.keyCombination("ctrl+q"));
	mMenuItemToolsExtractImages.setAccelerator(KeyCombination.keyCombination("ctrl+e"));

	getChildren().get(0).setOnKeyPressed(e -> {
	    if (e.getCode() == KeyCode.F5) {
		mButtonRefreshConversation.fire();
	    }
	});
    }

    /**
     * Organize the whole pane layout.
     */
    private void initLayout() {

	mWorkInProgressLayer.setSpacing(10);
	mWorkInProgressLayer.setAlignment(Pos.CENTER);
	mProgressIndicator.prefWidthProperty().bind(mConversationStackPane.widthProperty());
	mWorkInProgressLayer.getChildren().add(mProgressIndicator);
	mWorkInProgressLayer.getChildren().add(mActionCancelTask);
	setHgrow(mProgressIndicator, Priority.ALWAYS);

	mConversationStackPane.setAlignment(Pos.TOP_CENTER);
	mConversationStackPane.getChildren().add(mConversationWebView);

	ScrollPane contactListScrollPane = new ScrollPane(mContactListView);
	contactListScrollPane.setFitToWidth(true);
	contactListScrollPane.setFitToHeight(true);
	contactListScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
	contactListScrollPane.lookupAll(".scroll-bar:vertical")
		.forEach(scrollbar -> ((ScrollBar) scrollbar).setUnitIncrement(10.0));

	GridPane grid = new GridPane();
	mPalettesComboBox.setMaxWidth(Double.MAX_VALUE);
	grid.addColumn(0, contactListScrollPane, mPalettesComboBox);

	GridPane.setConstraints(contactListScrollPane, 0, 0, 1, 1, HPos.CENTER, VPos.TOP, Priority.ALWAYS,
		Priority.ALWAYS, Insets.EMPTY);
	GridPane.setConstraints(mPalettesComboBox, 0, 1, 1, 1, HPos.LEFT, VPos.BOTTOM, Priority.NEVER, Priority.NEVER,
		Insets.EMPTY);

	mImagesOverviewPane.setAlignment(Pos.BASELINE_LEFT);
	mImagesOverviewPane.setHgap(5);
	mImagesOverviewPane.setVgap(5);
	mImagesOverviewPane.setBorder(
		new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));

	ScrollPane scrollImages = new ScrollPane(mImagesOverviewPane);
	scrollImages.setFitToWidth(true);

	mTabImagesOverview.setContent(scrollImages);

	mMainSplitPane = new SplitPane();
	mMainSplitPane.setOrientation(Orientation.HORIZONTAL);
	mMainSplitPane.getItems().add(grid);
	mMainSplitPane.getItems().add(mConversationStackPane);
	mMainSplitPane.getItems().add(mTabPaneOverview);
	mMainSplitPane.setDividerPosition(0, 0.2);
	mMainSplitPane.setDividerPosition(1, 0.8);

	BorderPane borderPane = new BorderPane();
	borderPane.setTop(mToolbar);
	borderPane.setCenter(mMainSplitPane);

	VBox vbox = new VBox();
	vbox.getChildren().add(mMenuBar);
	vbox.getChildren().add(borderPane);
	setHgrow(vbox, Priority.ALWAYS);

	VBox.setVgrow(mMenuBar, Priority.NEVER);
	VBox.setVgrow(borderPane, Priority.ALWAYS);

	getChildren().add(vbox);
    }

    /**
     * Load a new file in a background task.
     * 
     * @param file The file to be loaded.
     */
    public void loadFile(File file) {
	mCurrentFile = file;
	mPreferences.setLastFile(file.getAbsolutePath());

	mActionCancelTask.setOnAction(e -> mFileLoadingService.cancel());
	mProgressIndicator.progressProperty().bind(mFileLoadingService.progressProperty());
	mFileLoadingService.restart();
    }

    /**
     * Hot replacement of all the labels after the application's bundle is reloaded.
     */
    protected void reloadLabelsFromBundle() {
	mMenuFile.setText(mBundle.getString(MENU_FILE));
	mMenuEdit.setText(mBundle.getString(MENU_EDIT));
	mMenuTools.setText(mBundle.getString(MENU_TOOLS));
	mMenuHelp.setText(mBundle.getString(MENU_HELP));

	mButtonOpenFile.setText(mBundle.getString(MENU_FILE_ACTION_OPEN));
	mButtonRefreshConversation.setText(mBundle.getString(MENU_FILE_ACTION_REFRESH));

	mMenuItemFileOpen.setText(mBundle.getString(MENU_FILE_ACTION_OPEN));
	mMenuItemFileReload.setText(mBundle.getString(MENU_FILE_ACTION_RELOAD));
	mMenuItemFileExportToHtml.setText(mBundle.getString(MENU_FILE_ACTION_EXPORT));
	mMenuItemFilePrint.setText(mBundle.getString(MENU_FILE_ACTION_PRINT));
	mMenuItemFileQuit.setText(mBundle.getString(MENU_FILE_ACTION_QUIT));
	mMenuItemEditPreferences.setText(mBundle.getString(MENU_EDIT_ACTION_PREFERENCES));
	mMenuItemToolsExtractImages.setText(mBundle.getString(MENU_TOOLS_ACTION_EXTRACT_IMAGES));
	mMenuItemHelpAbout.setText(mBundle.getString(MENU_HELP_ACTION_ABOUT));

	mActionCancelTask.setText(mBundle.getString(BUTTON_CANCEL_LABEL));
	mTabImagesOverview.setText(mBundle.getString(TABS_IMAGES));
    }

    /**
     * Initialize the components values from user's preferences.
     */
    protected void injectPreferences() {
	mNumPalette = mPreferences.getPreferredPalette();

	mPreferences.addPropertyChangeListener((PropertyChangeEvent evt) -> {
	    if (evt.getPropertyName().equals(Preferences.P_MESSAGE_WIDTH)
		    || evt.getPropertyName().equals(Preferences.P_MESSAGE_ORDER)
		    || evt.getPropertyName().equals(Preferences.P_EMOJI_SIZE)) {
		refreshConversationView();
	    } else if (evt.getPropertyName().equals(Preferences.P_ORDER_BY)
		    || evt.getPropertyName().equals(Preferences.P_CONTACT_ORDER)) {
		refreshContactsList();
	    } else if (evt.getPropertyName().equals(Preferences.P_IMAGE_HEIGHT)) {
		refreshConversationView();
	    } else if (evt.getPropertyName().equals(Preferences.P_LOCALE)) {
		mLanguageComboBox.getSelectionModel().select(mPreferences.getPreferredLocale().getLanguage());
		reloadLabelsFromBundle();
	    } else if (evt.getPropertyName().equals(Preferences.P_PALETTE)) {
		mNumPalette = mPreferences.getPreferredPalette();
		refreshConversationView();
	    }
	});

	mPalettesComboBox.getSelectionModel().select(String.valueOf(mPreferences.getPreferredPalette()));
	mLanguageComboBox.getSelectionModel().select(mPreferences.getPreferredLocale().getLanguage());
    }

    /**
     * Performs the task from a new service.
     * 
     * @param <T>       Type of the task's return.
     * @param task      The task to be executed in a background service.
     * @param onSuccess runnable to run after the task succeeds, or
     *                  <code>null</code>. Note that '<code>onSuccess</code>' will
     *                  be executed in a daemon thread, not in the JavaFX thread.
     */
    protected <T> void doInBackground(Task<T> task, Runnable... onSuccess) {
	Service<T> service = new Service<>() {
	    public Task<T> createTask() {
		return task;
	    }
	};
	showBusyOverlay(true);
	mProgressIndicator.progressProperty().bind(service.progressProperty());
	mActionCancelTask.setOnAction(ae -> service.cancel());
	service.setOnFailed(e -> {
	    logger.log(Level.ERROR, "", service.getException());
	    showBusyOverlay(false);
	});
	service.setOnCancelled(e -> showBusyOverlay(false));
	service.setOnSucceeded(e -> {
	    try {
		if (onSuccess != null) {
		    Arrays.stream(onSuccess).forEach(Runnable::run);
		}
	    } finally {
		showBusyOverlay(false);
	    }
	});
	service.start();
    }

    /**
     * Display the 'work in progress' layer over the current conversation.
     * 
     * @param isBusy <code>true</code> to display the 'work in progress' layer,
     *               <code>false</code> to hide it.
     */
    protected void showBusyOverlay(boolean isBusy) {
	ObservableList<Node> children = mConversationStackPane.getChildren();
	if (isBusy && !children.contains(mWorkInProgressLayer)) {
	    children.add(mWorkInProgressLayer);
	} else if (children.contains(mWorkInProgressLayer)) {
	    children.remove(mWorkInProgressLayer);
	}
    }

    /**
     * Modify the model and update the contact list.
     *
     * @param conversations The new model.
     */
    public void setModel(Conversations conversations) {
	mModel = conversations;
	refreshContactsList();
	mContactListView.getSelectionModel().clearAndSelect(0);
	mModel.addListener(this);
    }

    /**
     * Triggered when a new message is added to the underlying model.
     */
    @Override
    public void onSmsAdded(Sms message) {
	if (mContactListView.getItems().filtered(e -> message.getContact().equals(e)).isEmpty()) {
	    refreshContactsList();
	}
    }

    /**
     * Triggered when a contact and all its messages are deleted.
     * 
     * @param contact The removed contact.
     */
    @Override
    public void onContactRemoved(Contact contact) {
	mContactListView.getItems().remove(contact);
    }

    /**
     * Global handler for <code>javascript:alert()</code> events in the WebView.
     * 
     * @param event The underlying triggered event.
     */
    protected void onWebViewAlertEvent(WebEvent<String> event) {
	String data = event.getData();
	if (data.startsWith("image:")) {
	    showImagePreview(data.substring("image:".length()));
	} else if (data.startsWith("link:")) {
	    browseHyperlink(data.substring("link:".length()));
	} else if (data.equals("back")) {
	    goBackward();
	}
    }

    /**
     * Triggered when user clicks on the WebView context menu actions, like 'open
     * link in new window' or 'open image in new window'.
     * 
     * @param popupFeatures The popup configuration.
     * @return The WebEngine used to display this alert() window.
     */
    protected WebEngine onWebViewPopupEvent(PopupFeatures popupFeatures) {
	// Create a stub WebEngine that will be freed as it becomes visible.
	// When it becomes visible, just grab the URL location and free resources.
	// If the URL location is an image, we display it in a separate Stage.
	WeakReference<WebEngine> engine = new WeakReference<>(new WebEngine());
	engine.get().setOnVisibilityChanged(visibilityChangedEvent -> {
	    String location = engine.get().getLocation();
	    if (location.startsWith(Htmlizer.IMAGE_LINK_PREFIX)) {
		String base64 = location.substring(Htmlizer.IMAGE_LINK_PREFIX.length());
		try {
		    ImageView imageView = new ImageView(ImageUtil.decodeBase64Mime(base64));
		    imageView.setPreserveRatio(true);
		    Stage stage = new Stage();
		    Scene scene = new Scene(new HBox(imageView));
		    stage.setScene(scene);
		    stage.setResizable(false);
		    stage.show();
		} catch (IOException ex) {
		    logAndDisplayError(ex);
		}
	    } else {
		browseHyperlink(location);
	    }
	    // Cancel the WebEngine stub loading
	    engine.get().getLoadWorker().cancel();
	    // Free the WebEngine reference
	    engine.clear();
	});
	return engine.get();
    }

    /**
     * Action triggered when user presses a key in the {@link #mContactListView}.
     */
    protected void processKeyPressedInContactList(String typedText) {
	mKeyEventsBuffer.append(typedText);

	mModel.getContactNamesSortedByLexicographicOrder(Order.ASC).stream()
		.filter(c -> c.toString().toLowerCase().startsWith(mKeyEventsBuffer.toString())).findFirst()
		.ifPresent(foundContact -> {
		    mContactListView.getSelectionModel().clearSelection();
		    mContactListView.getSelectionModel().select(foundContact);
		    mContactListView.scrollTo(foundContact);
		});

	mTimerForKeyEventsAccumulation.restart();
    }

    /**
     * Triggered when the selected palette has changed.
     */
    protected void onSelectedPaletteChanged() {
	String selectedItem = mPalettesComboBox.getSelectionModel().getSelectedItem();
	mNumPalette = Integer.parseInt(selectedItem);
	refreshConversationView();
    }

    /**
     * Triggered when the selection model changes.
     */
    protected void onSelectedContactChanged() {
	boolean isEmpty = mContactListView.getSelectionModel().isEmpty();
	if (!isEmpty) {
	    refreshConversationView();
	}
	mMenuItemFilePrint.setDisable(isEmpty);
    }

    /**
     * Action handler when clicking in the 'Open' menu or 'Open' button bar button.
     * 
     * @param event The underlying triggered event.
     */
    protected void onOpenFileAction(ActionEvent event) {

	FileChooser fc = new FileChooser();
	if (mCurrentDir != null) {
	    fc.setInitialDirectory(mCurrentDir);
	} else if (mCurrentFile != null) {
	    fc.setInitialDirectory(mCurrentFile.getParentFile());
	}
	fc.getExtensionFilters()
		.setAll(new ExtensionFilter(mBundle.getString(MENU_FILE_ACTION_OPEN_FILE_DESCRIPTION), "*.xml"));

	File result = fc.showOpenDialog(getScene().getWindow());
	if (result != null) {
	    mCurrentDir = result.getParentFile();
	    if (result.exists() && result.isFile()) {
		loadFile(result);
	    }
	}
    }

    /**
     * Action handler when clicking on the 'Reload file' menu.
     * 
     * @param event The underlying triggered event.
     */
    protected void onReloadFileAction(ActionEvent event) {
	if (mCurrentFile != null) {
	    loadFile(mCurrentFile);
	}
    }

    /**
     * Action handler when clicking on the 'Export' menu.
     * 
     * @param selectedItems Selected conversations to export as HTML files.
     */
    protected void onExportToHtmlAction(List<Contact> selectedConversations) {

	// Local copy of the selection model (selection can change)
	List<Contact> selectedItems = new ArrayList<>(selectedConversations);
	if (selectedItems.size() == 1) {
	    FileChooser fc = new FileChooser();
	    if (mCurrentDir != null) {
		fc.setInitialDirectory(mCurrentDir);
	    }

	    fc.getExtensionFilters().add(new ExtensionFilter(
		    mBundle.getString(MENU_FILE_ACTION_EXPORT_FILE_DESCRIPTION), "*.html", "*.htm"));

	    refreshConversationView();

	    Contact selectedItem = selectedItems.get(0);
	    String completeName = selectedItem.getCompleteName();
	    if (Contact.UNKNOWN_CONTACT.equals(completeName)) {
		completeName = selectedItem.getPhoneNumber();
	    }

	    File selectedFile = new File(completeName + ".html");
	    fc.setInitialFileName(selectedFile.getName());

	    File result = fc.showSaveDialog(getScene().getWindow());
	    if (result != null) {
		mCurrentDir = result.getParentFile();
		exportConversationsToDir(selectedConversations, result.getParentFile(), Optional.of(ButtonType.YES));
	    }
	} else {
	    DirectoryChooser chooser = new DirectoryChooser();
	    if (mCurrentDir != null) {
		chooser.setInitialDirectory(mCurrentDir);
	    }
	    File toDir = chooser.showDialog(getScene().getWindow());
	    if (toDir != null) {
		mCurrentDir = toDir;
		exportConversationsToDir(selectedConversations, toDir, Optional.empty());
	    }
	}
    }

    /**
     * Add a listener on the language ComboBox.
     * 
     * @param listener The listener to add.
     */
    public void addLanguageListener(ChangeListener<String> listener) {
	mLanguageComboBox.valueProperty().addListener(listener);
    }

    /**
     * Remove a listener from the language ComboBox.
     * 
     * @param listener The listener to remove.
     */
    public void removeLanguageListener(ChangeListener<String> listener) {
	mLanguageComboBox.valueProperty().removeListener(listener);
    }

    /**
     * Open the preferences dialog box.
     */
    protected void onEditPreferencesAction() {
	JfxPreferencesDialog dialog = new JfxPreferencesDialog(getScene().getWindow(), mPreferences, mBundle);
	dialog.initModality(Modality.APPLICATION_MODAL);
	Optional<ButtonType> result = dialog.showAndWait();
	if (result.isPresent() && result.get() == ButtonType.OK) {
	    dialog.saveTo(mPreferences);
	    mPreferences.save();
	}
    }

    /**
     * Open the 'About...' dialog box.
     */
    protected void onAboutAction() {
	Alert dialog = new Alert(AlertType.INFORMATION);
	dialog.setGraphic(Resources.getIcon(Resources.ICON_LOGO_URL, ICONS_SIZE));
	dialog.setTitle(mBundle.getString(MENU_HELP_ACTION_ABOUT));
	String appname = mVersionBundle.getString("appname");
	String version = mVersionBundle.getString("version");
	String build = mVersionBundle.getString("build");
	String license = mVersionBundle.getString("license");
	dialog.setHeaderText(appname);

	VBox vbox = new VBox(5.0);

	Text textFlow = new Text(
		String.format("Version: %s, build=%s%nLicense: %s%n%nCredits:", version, build, license));

	String text = "";
	try {
	    text = Files.readString(Path.of("./NOTICE.txt"), StandardCharsets.UTF_8);
	} catch (IOException e) {
	    text = "ERROR";
	    logger.log(Level.TRACE, "", e);
	}
	TextArea credits = new TextArea(text);
	credits.setFont(Font.font("Monospaced", 11.0));
	credits.setEditable(false);

	vbox.getChildren().setAll(textFlow, credits);
	VBox.setVgrow(credits, Priority.ALWAYS);

	dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
	dialog.getDialogPane().setContent(vbox);
	dialog.setResizable(true);
	dialog.getDialogPane().setMinWidth(400d);
	dialog.showAndWait();
    }

    /**
     * Displays a DirectoryChooser and save all images of a conversation in the
     * selected folder.
     * 
     * @param conversations Conversations you want to extract images from.
     */
    protected void onExtractAllImagesAction(List<Contact> conversations) {
	List<Sms> messagesWithImages = null;
	if (conversations.isEmpty()) {
	    messagesWithImages = mModel.getAllMessagesWithImages();
	} else {
	    messagesWithImages = conversations.stream()
		    .flatMap(contact -> mModel.getAllMessagesWithImages(contact).stream()).toList();
	}
	if (messagesWithImages.isEmpty()) {
	    FxUtil.alertAndWait(mBundle.getString(ALERT_TEXT_NO_IMAGE_TO_EXPORT));
	    return;
	}

	DirectoryChooser chooser = new DirectoryChooser();
	chooser.setInitialDirectory(mCurrentDir);
	File saveDir = chooser.showDialog(getScene().getWindow());
	if (saveDir == null) {
	    return;
	}
	mCurrentDir = saveDir;

	SimpleDateFormat df = new SimpleDateFormat(EXPORT_IMAGES_DATE_FORMAT);

	for (Sms sms : messagesWithImages) {
	    Contact contact = sms.getContact();

	    List<ImagePart> images = sms.getImages();
	    for (ImagePart image : images) {
		String ext = image.getMimeType().substring("image/".length());
		String filename = contact.getCompleteName() + "_" + df.format(sms.getDate()) + "." + ext;
		try {
		    File file = new File(saveDir.getAbsolutePath(), filename);

		    logger.log(Level.TRACE, "Exporting ''{0}''", file.getAbsolutePath());
		    ImageUtil.exportBase64ToFile(image.getBase64(), file);

		} catch (IOException | IllegalArgumentException e) {
		    logger.log(Level.WARNING, "", e);
		}
	    }
	}
    }

    /**
     * Opens a print dialog box for the current conversation.
     */
    protected void onPrintAction() {
	PrinterJob job = PrinterJob.createPrinterJob();
	if (job != null) {
	    boolean isOk = job.showPrintDialog(null);
	    if (isOk) {
		mConversationWebView.getEngine().print(job);
	    }
	    job.endJob();
	}
    }

    /**
     * Triggered when user clicks on "Save image as..." context menu. Open a "Save"
     * file chooser to save an image on disk.
     * 
     * @param i Image to save on disk.
     */
    protected void onSaveImageAction(ImagePart i) {
	FileChooser chooser = new FileChooser();
	chooser.getExtensionFilters().add(new ExtensionFilter("Image PNG (*.png)", "*.png"));
	chooser.getExtensionFilters().add(new ExtensionFilter("Image JPEG (*.jpg)", "*.jpg,*.jpeg"));
	chooser.getExtensionFilters().add(new ExtensionFilter("Image GIF (*.gif)", "*.gif"));
	File fileToSave = chooser.showSaveDialog(getScene().getWindow());

	if (fileToSave != null) {
	    try {
		ImageUtil.exportBase64ToFile(i.getBase64(), fileToSave);
	    } catch (IOException e) {
		logAndDisplayError(e);
	    }
	}
    }

    /**
     * Export current conversation in HTML file.
     *
     * @param contacts    List of conversations to export.
     * @param toDir       Destination directory.
     * @param isOverwrite Tells whether we should prompt the user for overwriting
     *                    existing files.
     */
    public void exportConversationsToDir(List<Contact> contacts, File toDir, Optional<ButtonType> isOverwrite) {
	List<Contact> conversations = new ArrayList<>(contacts);
	if (conversations.isEmpty()) {
	    // export all if empty
	    conversations = mModel.getContactNamesSortedByDate(Order.DESC);
	}
	final List<Contact> conversationsToExport = conversations;

	// If the user asked to export only one file,
	Optional<ButtonType> overwrite = isOverwrite.isPresent() ? isOverwrite
		: askForOverwriteExistingFiles(conversationsToExport, toDir);
	if (!overwrite.isPresent() || overwrite.get() == ButtonType.CANCEL) {
	    return;
	}

	doInBackground(new Task<Void>() {
	    @Override
	    protected Void call() {
		int progress = 0;
		int countExportedFiles = 0;
		File lastExportedFile = null;

		for (Contact contact : conversationsToExport) {
		    if (isCancelled()) {
			return null;
		    }
		    try {
			String name = contact.getCompleteName();
			if (Contact.UNKNOWN_CONTACT.equals(name)) {
			    name = contact.getPhoneNumber();
			}
			File toFile = new File(toDir, FilenameUtils.normalize(name + ".html"));
			if (!toFile.exists() || (overwrite.isPresent() && overwrite.get() == ButtonType.YES)) {
			    String conversationHtml = loadConversationNotInterruptible(contact);
			    Files.writeString(toFile.toPath(), conversationHtml, StandardCharsets.UTF_8);
			    countExportedFiles++;
			    lastExportedFile = toFile;
			}

		    } catch (IOException e) {
			Platform.runLater(() -> logAndDisplayError(e));
		    }
		    updateProgress(progress, conversationsToExport.size());
		}
		try {
		    if (Desktop.getDesktop().isSupported(Action.OPEN)) {
			if (countExportedFiles == 1) {
			    Desktop.getDesktop().open(lastExportedFile);
			} else if (countExportedFiles > 1) {
			    Desktop.getDesktop().open(toDir);
			}
		    }
		} catch (IOException e) {
		    logger.log(Level.WARNING, "", e);
		}
		return null;
	    }
	});
    }

    /**
     * Ask user whether he/she wants to overwrite existing files.
     * 
     * @param selectedContacts Conversations to export as HTML files.
     * @param exportDir        Destination directory.
     * @return Optional response with the user's choice.
     */
    protected Optional<ButtonType> askForOverwriteExistingFiles(List<Contact> selectedContacts, File exportDir) {
	Optional<ButtonType> result = Optional.of(ButtonType.YES);

	List<File> alreadyExistingFiles = new ArrayList<>();
	for (Contact contact : selectedContacts) {
	    String name = contact.getCompleteName();
	    if (Contact.UNKNOWN_CONTACT.equals(name)) {
		name = contact.getPhoneNumber();
	    }
	    File toFile = new File(exportDir, FilenameUtils.normalize(name + ".html"));
	    if (toFile.exists()) {
		alreadyExistingFiles.add(toFile);
	    }
	}
	if (!alreadyExistingFiles.isEmpty()) {
	    Alert prompt = new Alert(AlertType.WARNING);
	    prompt.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);

	    Button noButton = (Button) prompt.getDialogPane().lookupButton(ButtonType.NO);
	    noButton.setDefaultButton(true);
	    Button yesButton = (Button) prompt.getDialogPane().lookupButton(ButtonType.YES);
	    yesButton.setDefaultButton(false);

	    ListView<String> listFiles = new ListView<>();
	    listFiles.setFocusTraversable(false);

	    for (File file : alreadyExistingFiles) {
		listFiles.getItems().add(file.getAbsolutePath());
	    }

	    VBox vbox = new VBox(5.0);
	    vbox.setFillWidth(true);
	    ScrollPane scrollPane = new ScrollPane(listFiles);
	    scrollPane.setFitToWidth(true);
	    scrollPane.setFitToHeight(true);
	    vbox.getChildren().add(scrollPane);
	    prompt.setHeaderText(mBundle.getString(ASK_OVERWRITE_MESSAGE));
	    prompt.getDialogPane().setContent(vbox);
	    prompt.setResizable(true);
	    result = prompt.showAndWait();
	}
	return result;
    }

    /**
     * Refresh the contacts list (e.g. after the contacts sorting order changes).
     */
    public void refreshContactsList() {
	if (mModel != null) {
	    List<Contact> contacts = new ArrayList<>();

	    if (mPreferences.getPreferredOrder() == OrderBy.DATE) {
		contacts.addAll(mModel.getContactNamesSortedByDate(mPreferences.getPreferredContactOrder()));
	    } else if (mPreferences.getPreferredOrder() == OrderBy.CONTACT_NAME) {
		contacts.addAll(
			mModel.getContactNamesSortedByLexicographicOrder(mPreferences.getPreferredContactOrder()));
	    }
	    mContactListView.getItems().setAll(contacts);
	}
    }

    /**
     * Refresh conversation displayed in the WebView according to the selected
     * contact in the contact list.
     */
    public void refreshConversationView() {
	Contact selectedContact = mContactListView.getSelectionModel().getSelectedItem();
	if (selectedContact != null) {
	    mImagesOverviewPane.getChildren().clear();
	}
	showConversationForContact(selectedContact);
    }

    /**
     * Load all SMS of a conversation into a separate background thread.
     * 
     * @param selectedContact The contact you want to load conversation from.
     */
    public void showConversationForContact(Contact selectedContact) {
	Service<String> mServiceLoadConversation = new Service<>() {
	    public Task<String> createTask() {
		return new Task<String>() {
		    @Override
		    protected String call() throws Exception {
			StringBuilder sb = new StringBuilder(mHtmlizer.getHtmlDocumentHeader(mNumPalette));
			if (selectedContact != null) {
			    sb.append("<h1>").append(selectedContact).append("</h1>");

			    List<Sms> messages = mModel.getConversation(selectedContact,
				    mPreferences.getPreferredMessageOrder());
			    long currentDay = -1;

			    int count = 0;
			    for (Sms sms : messages) {
				if (isCancelled()) {
				    return null;
				}
				if (currentDay == -1 || mHtmlizer.isNewDay(currentDay, sms.getDate())) {
				    sb.append(mHtmlizer.getHtmlDateDivider(sms.getDate()));
				}
				sb.append(mHtmlizer.getHtmlSmsContent(sms));
				currentDay = sms.getDate();
				Platform.runLater(() -> fillImagesOverviewTabWithThumbnails(sms));
				updateProgress(count++, messages.size());
			    }
			} else {
			    sb.append(mBundle.getString(WEB_VIEW_WELCOME_MESSAGE));
			}
			return sb.toString();
		    }
		};
	    }
	};

	mServiceLoadConversation.setOnRunning(e -> showBusyOverlay(true));
	mServiceLoadConversation.setOnCancelled(e -> showBusyOverlay(false));
	mServiceLoadConversation.setOnFailed(e -> {
	    logger.log(Level.ERROR, "", e.getSource().getException());
	    showBusyOverlay(false);
	});
	mServiceLoadConversation.setOnSucceeded(e -> {
	    mModelAsHtml = mServiceLoadConversation.getValue();
	    mConversationWebView.getEngine().loadContent(mModelAsHtml); // asynchronous
	    hideImagePreviewLayer();
	    showBusyOverlay(false);
	});

	mActionCancelTask.setOnAction(ae -> mServiceLoadConversation.cancel());
	mProgressIndicator.progressProperty().bind(mServiceLoadConversation.progressProperty());
	mServiceLoadConversation.start();
    }

    /**
     * Load a single conversation. Process is not interruptible, contrary to
     * {@link #showConversationForContact(Contact)}.
     * 
     * @param selectedContact The contact's conversation to be loaded.
     * @return The full conversation as a string.
     */
    protected String loadConversationNotInterruptible(Contact selectedContact) {
	StringBuilder sb = new StringBuilder(mHtmlizer.getHtmlDocumentHeader(mNumPalette));
	if (selectedContact != null) {
	    sb.append("<h1>").append(selectedContact).append("</h1>");

	    List<Sms> messages = mModel.getConversation(selectedContact, mPreferences.getPreferredMessageOrder());
	    long currentDay = -1;

	    for (Sms sms : messages) {
		if (currentDay == -1 || mHtmlizer.isNewDay(currentDay, sms.getDate())) {
		    sb.append(mHtmlizer.getHtmlDateDivider(sms.getDate()));
		}
		sb.append(mHtmlizer.getHtmlSmsContent(sms));
		currentDay = sms.getDate();
	    }
	} else {
	    sb.append(mBundle.getString(WEB_VIEW_WELCOME_MESSAGE));
	}
	return sb.toString();
    }

    /**
     * Extract all images of a SMS and add them into the
     * {@link #mTabImagesOverview}. Each image has 2 default mouse actions ; primary
     * mouse button shows t he image over the conversation pane (see
     * {@link #showImagePreview(String)}) and secondary mouse button displays a
     * context menu with action {@link #onSaveImageAction(ImagePart)}.
     * 
     * @param sms The SMS containing images to load.
     */
    protected void fillImagesOverviewTabWithThumbnails(Sms sms) {
	sms.getImages().forEach(i -> {
	    try {
		ImageView img = new ImageView(ImageUtil.decodeBase64Mime(i.getBase64()));
		img.setPreserveRatio(true);
		img.setFitWidth(100);
		img.setFitHeight(100);
		mImagesOverviewPane.getChildren().add(img);

		// Do not propagate the CONTEXT_MENU_REQUESTED event to underlying StackPane
		img.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, ContextMenuEvent::consume);

		img.setOnMouseClicked(mouseClicked -> {
		    if (mouseClicked.getButton() == MouseButton.PRIMARY) {
			showImagePreview(i.getUniqueId());
		    } else if (mouseClicked.getButton() == MouseButton.SECONDARY) {
			MenuItem menuItem = new MenuItem(mBundle.getString(CONTEXT_MENU_SAVE_IMAGE));
			menuItem.setOnAction(menuItemEvent -> onSaveImageAction(i));
			ContextMenu menu = new ContextMenu(menuItem);
			menu.show(this.getScene().getWindow(), mouseClicked.getScreenX(), mouseClicked.getScreenY());
		    }
		});
		img.setOnMouseEntered(mouseEntered -> getScene().setCursor(Cursor.HAND));
		img.setOnMouseExited(mouseExited -> getScene().setCursor(Cursor.DEFAULT));

	    } catch (IOException e) {
		logger.log(Level.ERROR, e.getMessage(), e);
	    }
	});
    }

    /**
     * Go back in history.
     */
    protected void goBackward() {
	Platform.runLater(() -> {
	    WebEngine engine = mConversationWebView.getEngine();
	    if (engine.getHistory().getCurrentIndex() == 0 && !engine.getHistory().getEntries().isEmpty()) {
		refreshConversationView();
	    } else if (engine.getHistory().getCurrentIndex() > 0) {
		engine.getHistory().go(-1);
	    }
	});
    }

    /**
     * Go forward in history.
     */
    protected void goForward() {
	Platform.runLater(() -> {
	    WebEngine engine = mConversationWebView.getEngine();
	    if (engine.getHistory().getCurrentIndex() < engine.getHistory().getEntries().size()) {
		engine.getHistory().go(1);
	    }
	});
    }

    /**
     * Displays an image on the front layer of the conversation StackPane.
     *
     * @param imageId Image id in the HTML document.
     */
    public void showImagePreview(String imageId) {

	Button closeButton = new Button(mBundle.getString(BUTTON_CLOSE_LABEL));
	closeButton.setContentDisplay(ContentDisplay.LEFT);
	closeButton.setGraphic(Resources.getIcon(Resources.ICON_BACK_URL));
	closeButton.setOnAction(e -> hideImagePreviewLayer());

	Element elementById = mConversationWebView.getEngine().getDocument().getElementById(imageId);
	String attribute = elementById.getAttribute("src");
	String base64 = attribute.substring(Htmlizer.IMAGE_LINK_PREFIX.length());
	try {
	    Image fxImage = ImageUtil.decodeBase64Mime(base64);
	    ImageView imageView = new ImageView(fxImage);
	    imageView.setPreserveRatio(true);

	    ChangeListener<? super Number> resizeListener = (observable, oldValue,
		    newValue) -> adaptImagePreviewToContainerSize(imageView);
	    mConversationStackPane.heightProperty().addListener(resizeListener);
	    mConversationStackPane.widthProperty().addListener(resizeListener);
	    adaptImagePreviewToContainerSize(imageView);

	    VBox vbox = new VBox(15, imageView, closeButton);
	    vbox.setAlignment(Pos.CENTER);
	    mImagePreviewLayer.getChildren().setAll(vbox);

	    if (mConversationStackPane.getChildren().size() > 1) {
		mConversationStackPane.getChildren().remove(1);
	    }
	    mConversationStackPane.getChildren().add(mImagePreviewLayer);
	} catch (IOException e) {
	    logger.log(Level.WARNING, "", e);
	}
    }

    /**
     * Hide the image preview overlay.
     */
    protected void hideImagePreviewLayer() {
	mConversationStackPane.getChildren().remove(mImagePreviewLayer);
    }

    /**
     * Adapt the size of image to the size of {@link #mConversationStackPane}.
     * 
     * @param imageView The image to adapt.
     */
    protected void adaptImagePreviewToContainerSize(ImageView imageView) {
	imageView.setFitWidth(mConversationStackPane.getWidth() - 100);
	imageView.setFitHeight(mConversationStackPane.getHeight() - 100);
    }

    /**
     * Browse an hyperlink in a separate window.
     * 
     * @param hyperlink The link to browse.
     */
    public void browseHyperlink(String hyperlink) {
	try {
	    if (Desktop.getDesktop().isSupported(Action.OPEN)) {
		Desktop.getDesktop().browse(new URI(hyperlink));
	    }
	} catch (IOException | URISyntaxException e) {
	    logger.log(Level.ERROR, "", e);
	}
    }

    /**
     * Log an error in the logger and then display a popup alert.
     * 
     * @param error The error to log.
     */
    protected void logAndDisplayError(Throwable error) {
	logger.log(Level.WARNING, error.getLocalizedMessage(), error);
	Alert alert = new Alert(AlertType.ERROR);
	alert.setTitle(mBundle.getString(MENU_FILE_ACTION_OPEN_ERROR));
	alert.setHeaderText(mBundle.getString(GENERIC_ERROR_MESSAGE));
	alert.setContentText(error.getMessage());
	alert.getButtonTypes().setAll(ButtonType.CLOSE);
	alert.showAndWait();
    }

    /**
     * Renders a cell of the contacts list.
     */
    protected class ContactCell extends ListCell<Contact> {
	protected static final String DRAFT = PREFIX + ".draft";

	@Override
	protected void updateItem(Contact item, boolean empty) {

	    super.updateItem(item, empty);

	    if (empty || item == null) {
		setText(null);
		setGraphic(null);
	    } else {
		List<Sms> conversation = mModel.getConversation(item, mPreferences.getPreferredContactOrder());

		LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(conversation.get(0).getDate()),
			ZoneId.systemDefault());
		String sDate = dt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
		String sContact = formatContact(item);

		Text labelDate = new Text(sDate);
		labelDate.setFont(Font.font(mPreferences.getContactListDatetimeFont(), FontWeight.NORMAL, 12.0));
		Text labelContact = new Text(sContact);
		labelContact.setFont(Font.font(mPreferences.getContactListNameFont(), FontWeight.BOLD, 14.0));
		VBox label = new VBox(labelDate, labelContact);

		setText(null);
		setGraphic(label);
	    }
	}

	protected String formatContact(Contact contact) {
	    String formattedContact = StringUtils.isBlank(contact.getCompleteName()) ? contact.getPhoneNumber()
		    : contact.toString();
	    return StringUtils.isAllBlank(contact.getCompleteName(), contact.getPhoneNumber())
		    ? mBundle.getString(DRAFT)
		    : formattedContact;
	}
    }
}
