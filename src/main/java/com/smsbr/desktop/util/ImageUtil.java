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
package com.smsbr.desktop.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.smsbr.desktop.model.ImagePart;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Utilities for image conversions.
 * 
 * @author Alexandre DERMONT
 */
public class ImageUtil {

    private ImageUtil() {
	// No constructor for static classes
    }

    /**
     * Decode the content of the base64 string and then write a PNG file to disk. If
     * the parent folder soesn't exist, it is created.
     * 
     * @param base64 The base 64 image to decode.
     * @param toFile
     * @throws IOException
     */
    public static void exportBase64ToFile(String base64, File toFile) throws IOException {

	byte[] decodedImg = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
	Path destinationFile = toFile.toPath();
	Files.write(destinationFile, decodedImg);
    }

    /**
     * Decodes a base64 image.
     * 
     * @see {@link Base64} specifications for more details.
     * 
     * @param base64 The image as a Base64 string.
     * @return a JavaFX {@link Image}.
     * @throws IOException In case of I/O error (invalid bytes...etc).
     */
    public static Image decodeBase64Mime(String base64) throws IOException {
	try (ByteArrayInputStream bais = new ByteArrayInputStream(base64.getBytes());) {
	    InputStream in = Base64.getDecoder().wrap(bais);
	    return new Image(in);
	}
    }

    /**
     * Write an image on disk.
     * 
     * @param image  The image to write.
     * @param toFile Destination file.
     * @throws IOException
     */
    public static void writeImage(Image image, File toFile) throws IOException {
	BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
	if (bImage == null) {
	    throw new IOException("Unreadable image format");
	}
	ImageIO.write(bImage, FilenameUtils.getExtension(toFile.getName()), toFile);
    }

    /**
     * Adapt an image's dimensions (width and height) to a maximum size.
     *
     * @param image Image to be normalized.
     * @return The image's dimensions.
     */
    public static Dimension normalizeImageDimensions(ImagePart image, int maxHeight) {
	Dimension normalizedDim = new Dimension(image.getWidth(), image.getHeight());
	if (image.getHeight() > maxHeight) {
	    double ratio = maxHeight / (double) image.getHeight();
	    normalizedDim.width = (int) (ratio * image.getWidth());
	    normalizedDim.height = maxHeight;
	}
	return normalizedDim;
    }

}
