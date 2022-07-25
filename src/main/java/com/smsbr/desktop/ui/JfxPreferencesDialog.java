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

import java.util.Locale;

import com.smsbr.desktop.app.Bundle;
import com.smsbr.desktop.app.Preferences;
import com.smsbr.desktop.model.Order;
import com.smsbr.desktop.model.OrderBy;

import javafx.geometry.HPos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

/**
 * Preferences dialog box which records the preferences into the underlying
 * model when user presses 'OK' button.
 *
 * @author Alexandre DERMONT
 */
public class JfxPreferencesDialog extends Dialog<ButtonType> {

    protected static final String PREFIX = JfxPreferencesDialog.class.getName();
    protected static final String WINDOW_TITLE = PREFIX + ".windowTitle";
    protected static final String LOAD_LAST_FILE = PREFIX + ".loadLastFile.label";
    protected static final String MESSAGE_WIDTH = PREFIX + ".messageWidth.label";
    protected static final String ORDER_BY = PREFIX + ".orderBy.label";
    protected static final String ORDER_CONTACTS = PREFIX + ".orderContacts.label";
    protected static final String ORDER_MESSAGES_LABEL = PREFIX + ".orderMessages.label";
    protected static final String ORDER_CONTACT_DESC_LABEL = PREFIX + ".orderContactDesc.label";
    protected static final String ORDER_CONTACT_ASC_LABEL = PREFIX + ".orderContactAsc.label";
    protected static final String ORDER_MESSAGE_DESC_LABEL = PREFIX + ".orderMessageDesc.label";
    protected static final String ORDER_MESSAGE_ASC_LABEL = PREFIX + ".orderMessageAsc.label";
    protected static final String ORDER_BY_CONTACT_LABEL = PREFIX + ".orderByContact.label";
    protected static final String ORDER_BY_DATE_LABEL = PREFIX + ".orderByDate.label";
    protected static final String LANGUAGE_LABEL = PREFIX + ".language.label";
    protected static final String PALETTE_LABEL = PREFIX + ".palette.label";
    protected static final String IMAGE_HEIGHT_LABEL = PREFIX + ".imageHeight.label";
    protected static final String EMOJI_SIZE_LABEL = PREFIX + ".emojiSize.label";

    /** The user's preferences. */
    protected Preferences mPreferencesModel;
    /** The application's bundle for labels. */
    protected Bundle mBundle;

    /** Text field for bubbles width inside the conversation pane. */
    protected TextField mTextBubblesWidth;
    /** Default ordering method. */
    protected ComboBox<OrderBy> mComboOrderBy;
    /** Default ordering for messages. */
    protected ComboBox<Order> mComboMessageOrder;
    /** Default ordering for contacts. */
    protected ComboBox<Order> mComboContactOrder;
    /** Default height for images in the conversation pane. */
    protected Spinner<Integer> mSpinnerImageHeight;
    /** Default size for smileys/emojis in the conversation pane. */
    protected Spinner<Integer> mSpinnerEmojiSize;
    /** Default palette for conversations bubbles and background. */
    protected JfxPaletteComboBox mComboBoxPalette;
    /** Load the last opened file ? */
    protected CheckBox mCheckBoxLoadLastFile;
    /** Smooth emojis images */
    protected CheckBox mCheckBoxSmoothImages;
    /** Default language. */
    protected JfxLanguageComboBox mComboBoxLanguage;

    /**
     * Constructor.
     *
     * @param parent The parent window.
     * @param model  The preferences' model.
     * @param bundle The resources bundle containing labels.
     */
    public JfxPreferencesDialog(Window parent, Preferences model, Bundle bundle) {
	initOwner(parent);
	mPreferencesModel = model;
	mBundle = bundle;

	// Defaults
	setTitle(mBundle.getString(WINDOW_TITLE));
	setWidth(600);
	setHeight(400);

	// Initializes the widgets
	initComponents();

	// Initializes behaviors of the widgets
	initBehaviors();

	// Initialize the pane's layout
	initLayout();

	// push model values into the view widgets
	modelToView();
    }

    /**
     * Builds JavaFX components.
     */
    private void initComponents() {

	mTextBubblesWidth = new TextField();
	mComboBoxPalette = new JfxPaletteComboBox(mBundle);
	mComboBoxLanguage = new JfxLanguageComboBox();
	mCheckBoxLoadLastFile = new CheckBox();
	mCheckBoxSmoothImages = new CheckBox();

	mComboOrderBy = new ComboBox<>();
	mComboOrderBy.getItems().add(OrderBy.DATE);
	mComboOrderBy.getItems().add(OrderBy.CONTACT_NAME);

	mComboMessageOrder = new ComboBox<>();
	mComboMessageOrder.getItems().add(Order.ASC);
	mComboMessageOrder.getItems().add(Order.DESC);

	mComboContactOrder = new ComboBox<>();
	mComboContactOrder.getItems().add(Order.ASC);
	mComboContactOrder.getItems().add(Order.DESC);

	mSpinnerImageHeight = new Spinner<>(100, 1000, 100, 1);
	mSpinnerImageHeight.setEditable(true);
	mSpinnerEmojiSize = new Spinner<>(16, 48, 28, 4);

    }

    /**
     * Set up the layout.
     */
    private void initLayout() {
	int y = 0;

	GridPane mRootPane = new GridPane();
	mRootPane.setVgap(5);

	// LOAD LAST FILE ----------------------------
	Label labelLoadLastFile = new Label(mBundle.getString(LOAD_LAST_FILE));
	labelLoadLastFile.setTooltip(new Tooltip(labelLoadLastFile.getText()));

	mRootPane.add(labelLoadLastFile, 0, y);
	mRootPane.add(mCheckBoxLoadLastFile, 1, y);
	y++;

	// MESSAGE WIDTH ----------------------------
	Label label = new Label(mBundle.getString(MESSAGE_WIDTH));

	mRootPane.add(label, 0, y);
	mRootPane.add(mTextBubblesWidth, 1, y);
	y++;

	// ORDER BY ----------------------------
	Label labelOrderBy = new Label(mBundle.getString(ORDER_BY));
	mRootPane.add(labelOrderBy, 0, y);
	mRootPane.add(mComboOrderBy, 1, y);
	y++;

	// CONTACTS ORDER ----------------------------
	Label labelContactOrder = new Label(mBundle.getString(ORDER_CONTACTS));
	mRootPane.add(labelContactOrder, 0, y);
	mRootPane.add(mComboContactOrder, 1, y);
	y++;

	// MESSAGE ORDER ----------------------------
	Label labelMessageOrder = new Label(mBundle.getString(ORDER_MESSAGES_LABEL));
	mRootPane.add(labelMessageOrder, 0, y);
	mRootPane.add(mComboMessageOrder, 1, y);
	y++;

	// IMAGE HEIGHT ----------------------------
	Label labelImageHeight = new Label(mBundle.getString(IMAGE_HEIGHT_LABEL));
	mRootPane.add(labelImageHeight, 0, y);
	mRootPane.add(mSpinnerImageHeight, 1, y);
	y++;

	// EMOJI SIZE ----------------------------
	Label labelEmojiSize = new Label(mBundle.getString(EMOJI_SIZE_LABEL));
	mRootPane.add(labelEmojiSize, 0, y);
	mRootPane.add(mSpinnerEmojiSize, 1, y);
	y++;

	// PALETTE ----------------------------
	Label labelPalette = new Label(mBundle.getString(PALETTE_LABEL));
	mRootPane.add(labelPalette, 0, y);
	mRootPane.add(mComboBoxPalette, 1, y);
	y++;

	// LANGUAGE ----------------------------
	Label labelLanguage = new Label(mBundle.getString(LANGUAGE_LABEL));
	mRootPane.add(labelLanguage, 0, y);
	mRootPane.add(mComboBoxLanguage, 1, y);
	y++;

	// FREE SPACE ----------------------------
	mRootPane.add(new Separator(), 0, y, 2, 1);

	mRootPane.getColumnConstraints().add(new ColumnConstraints(250, 250, 250, Priority.ALWAYS, HPos.LEFT, true));
	mRootPane.getColumnConstraints().add(new ColumnConstraints(100, 150, 250, Priority.ALWAYS, HPos.LEFT, true));

	getDialogPane().getButtonTypes().add(ButtonType.OK);
	getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
	getDialogPane().setContent(mRootPane);
    }

    /**
     * Initializes components' behavior.
     */
    private void initBehaviors() {

	mComboOrderBy.setButtonCell(new CustomOrderByCell());
	mComboOrderBy.setCellFactory(list -> new CustomOrderByCell());
	mComboOrderBy.setOnAction(e -> mComboContactOrder
		.setDisable(mComboOrderBy.getSelectionModel().getSelectedItem() != OrderBy.CONTACT_NAME));

	mComboMessageOrder.setButtonCell(new CustomOrderCell());
	mComboMessageOrder.setCellFactory(list -> new CustomOrderCell());

	mComboContactOrder.setButtonCell(new CustomOrderCell());
	mComboContactOrder.setCellFactory(list -> new CustomOrderCell());
	mComboContactOrder.setDisable(mComboOrderBy.getSelectionModel().getSelectedItem() != OrderBy.CONTACT_NAME);
    }

    /**
     * Save the values into a model object.
     * 
     * @param prefs The model to update with values of this view.
     */
    public void saveTo(Preferences prefs) {
	prefs.setPreferredMessageWidth(mTextBubblesWidth.getText());
	prefs.setPreferredOrderBy(mComboOrderBy.getSelectionModel().getSelectedItem());
	prefs.setPreferredMessageOrder(mComboMessageOrder.getSelectionModel().getSelectedItem());
	prefs.setPreferredContactOrder(mComboContactOrder.getSelectionModel().getSelectedItem());
	prefs.setPreferredImageHeight(mSpinnerImageHeight.getValue());
	prefs.setPreferredEmojiSize(mSpinnerEmojiSize.getValue());
	prefs.setPreferredPalette(Integer.parseInt(mComboBoxPalette.getSelectionModel().getSelectedItem()));
	prefs.setPreferredLocale(new Locale(mComboBoxLanguage.getSelectionModel().getSelectedItem()));
	prefs.setLoadLastFile(mCheckBoxLoadLastFile.isSelected());
    }

    /**
     * Fill components values with the underlying model object.
     */
    protected void modelToView() {
	mTextBubblesWidth.setText(mPreferencesModel.getPreferredMessageWidth());
	mComboOrderBy.getSelectionModel().select(mPreferencesModel.getPreferredOrder());
	mComboMessageOrder.getSelectionModel().select(mPreferencesModel.getPreferredMessageOrder());
	mComboContactOrder.getSelectionModel().select(mPreferencesModel.getPreferredContactOrder());
	mSpinnerImageHeight.getValueFactory().setValue(mPreferencesModel.getPreferredImageHeight());
	mSpinnerEmojiSize.getValueFactory().setValue(mPreferencesModel.getPreferredEmojiSize());
	mComboBoxPalette.getSelectionModel().select(String.valueOf(mPreferencesModel.getPreferredPalette()));
	mComboBoxLanguage.getSelectionModel().select(mPreferencesModel.getPreferredLocale().getLanguage());
	mCheckBoxLoadLastFile.setSelected(mPreferencesModel.isLoadLastFile());
    }

    /**
     * Custom renderer for ComboBox button and cells.
     */
    private class CustomOrderCell extends ListCell<Order> {
	@Override
	protected void updateItem(Order item, boolean empty) {
	    super.updateItem(item, empty);
	    if (empty || item == null) {
		setText(null);
		setGraphic(null);
	    } else {
		if (item == Order.ASC) {
		    setText(mBundle.getString(ORDER_CONTACT_ASC_LABEL));
		} else if (item == Order.DESC) {
		    setText(mBundle.getString(ORDER_CONTACT_DESC_LABEL));
		}
	    }
	}
    }

    /**
     * Custom renderer for ComboBox button and cells.
     */
    private class CustomOrderByCell extends ListCell<OrderBy> {
	@Override
	protected void updateItem(OrderBy item, boolean empty) {
	    super.updateItem(item, empty);
	    if (empty || item == null) {
		setText(null);
		setGraphic(null);
	    } else {
		if (item == OrderBy.DATE) {
		    setText(mBundle.getString(ORDER_BY_DATE_LABEL));
		} else if (item == OrderBy.CONTACT_NAME) {
		    setText(mBundle.getString(ORDER_BY_CONTACT_LABEL));
		}
	    }
	}
    }
}
