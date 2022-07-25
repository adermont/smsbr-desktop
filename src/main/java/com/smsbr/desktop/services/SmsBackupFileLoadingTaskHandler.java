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

import com.smsbr.desktop.io.SmsBackupFileMetadata;
import com.smsbr.desktop.model.Sms;

/**
 * Event handler for SMS backup files loading.
 * 
 * @author Alexandre DERMONT
 */
public interface SmsBackupFileLoadingTaskHandler {

    /**
     * Called when file's metadata are loaded.
     * 
     * @param metadata The loaded metadata.
     */
    void onMetadataLoaded(SmsBackupFileMetadata metadata);

    /**
     * Called whenever a new message (SMS or MMS) has been read.
     * 
     * @param message The read message with optional attachments.
     */
    void onMessageLoaded(Sms message);
}
