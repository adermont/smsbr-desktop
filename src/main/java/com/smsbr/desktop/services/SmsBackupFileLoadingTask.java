/**
 * Copyright 2022 Alexandre DERMONT
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
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
package com.smsbr.desktop.services;

import com.smsbr.desktop.io.SmsBackupFile;
import com.smsbr.desktop.io.SmsBackupFileMetadata;
import com.smsbr.desktop.model.Conversations;
import com.smsbr.desktop.model.Sms;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

/**
 * This task is responsible for loading a {@link SmsBackupFile} in background.
 * 
 * @author Alexandre DERMONT
 */
public class SmsBackupFileLoadingTask extends Task<Conversations> implements SmsBackupFileLoadingTaskHandler {

    /** The source file to be loaded. */
    private StringProperty mSourceFile;

    /** Metadata of the loaded file. */
    private SmsBackupFileMetadata mMetadata;

    /** Data of the loaded file. */
    private Conversations mConversations;

    /**
     * Constructs a new task.
     * 
     * @param sourceFilePath The file to be loaded.
     */
    public SmsBackupFileLoadingTask(String sourceFilePath) {
	mSourceFile = new SimpleStringProperty(sourceFilePath);
    }

    /**
     * The source file.
     * 
     * @return The <code>sourceFile</code> property.
     */
    public StringProperty sourceFileProperty() {
	return mSourceFile;
    }

    /**
     * @return The sourceFile path as a string.
     */
    public String getSourceFile() {
	return mSourceFile.get();
    }

    /**
     * Set the sourceFile path.
     * 
     * @param newSourceFile Path of the new sourceFile.
     */
    public void setSourceFile(String newSourceFile) {
	mSourceFile.set(newSourceFile);
    }

    /**
     * Task processing.
     */
    @Override
    protected Conversations call() throws Exception {

	// Reset metadata and data
	mMetadata = null;
	mConversations = new Conversations();

	// Parse the sourceFile
	new SmsBackupFile(mSourceFile.get()).parse(this, this);

	if (isCancelled()) {
	    mMetadata = null;
	    mConversations = null;
	    return null;
	}
	return mConversations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMetadataLoaded(SmsBackupFileMetadata metadata) {
	mMetadata = metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessageLoaded(Sms message) {
	mConversations.add(message);
	updateProgress(mConversations.getMessageCount(), mMetadata.getMessageCount());
	updateMessage(
		String.format("Loading message %d/%d", mConversations.getMessageCount(), mMetadata.getMessageCount()));
    }
}