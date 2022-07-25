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
package com.smsbr.desktop.services;

import java.io.File;
import java.util.function.Supplier;

import com.smsbr.desktop.model.Conversations;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * A background task runner for loading conversations from an XML backup file.
 * 
 * @author Alexandre DERMONT
 */
public class SmsBackupFileLoadingService extends Service<Conversations> {

    private Supplier<File> mCurrentFile;

    /**
     * Constructor.
     *
     * @param supplier File supplier.
     */
    public SmsBackupFileLoadingService(Supplier<File> supplier) {
	mCurrentFile = supplier;
    }

    /**
     * Create a new {@link SmsBackupFileLoadingTask}.
     * 
     * {@inheritDoc}
     */
    @Override
    protected Task<Conversations> createTask() {
	return new SmsBackupFileLoadingTask(mCurrentFile.get().getAbsolutePath());
    }
}
