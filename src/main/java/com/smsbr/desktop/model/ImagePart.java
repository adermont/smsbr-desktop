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
package com.smsbr.desktop.model;

/**
 * Represents an image attachment to a SMS/MMS.
 * 
 * @author Alexandre DERMONT
 */
public class ImagePart {

    protected String mMimeType;
    protected String mFilename;
    protected String mBase64;
    protected int mWidth;
    protected int mHeight;

    /**
     * Constructor.
     * 
     * @param mime
     * @param filename
     * @param base64
     * @param width
     * @param height
     */
    public ImagePart(String mime, String filename, String base64, int width, int height) {
	super();
	this.mMimeType = mime;
	this.mFilename = filename;
	this.mBase64 = base64;
	this.mWidth = width;
	this.mHeight = height;
    }

    /**
     * @return a unique identifier (scope = runtime only) for this image.
     */
    public String getUniqueId() {
	return String.valueOf(System.identityHashCode(this));
    }

    /**
     * @return the MIME Type.
     */
    public String getMimeType() {
	return mMimeType;
    }

    /**
     * @param mime the MIME Type to set.
     */
    public void setMimeType(String mime) {
	this.mMimeType = mime;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
	return mFilename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
	this.mFilename = filename;
    }

    /**
     * @return the base64
     */
    public String getBase64() {
	return mBase64;
    }

    /**
     * @param base64 the base64 to set
     */
    public void setBase64(String base64) {
	this.mBase64 = base64;
    }

    /**
     * @return the width
     */
    public int getWidth() {
	return mWidth;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
	this.mWidth = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
	return mHeight;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
	this.mHeight = height;
    }

}
