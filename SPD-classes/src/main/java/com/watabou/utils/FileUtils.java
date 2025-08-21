/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.utils;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtils {
    
    /**
     * Calculates a SHA-256 checksum for a file
     * @param file The file to calculate checksum for
     * @return Hex string of the checksum
     * @throws IOException If an I/O error occurs
     */
    public static String calculateChecksum(FileHandle file) throws IOException {
        try (InputStream is = file.read()) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] hash = digest.digest();
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Failed to calculate checksum", e);
        }
    }
    
    /**
     * Validates that a file exists and has content
     * @param file The file to validate
     * @return true if the file is valid, false otherwise
     */
    public static boolean validateFile(FileHandle file) {
        return file != null && file.exists() && !file.isDirectory() && file.length() > 0;
    }
    
    private static final String TAG = "FileUtils";
    private static Files.FileType defaultFileType = null;
    private static String defaultPath = "";
    
    private static void log(String message) {
        if (Gdx.app != null) {
            Gdx.app.log(TAG, message);
        } else {
            // Fallback to system out if Gdx.app is not initialized yet
            System.out.println("[" + TAG + "] " + message);
        }
    }
    
    private static void error(String message, Throwable e) {
        if (Gdx.app != null) {
            Gdx.app.error(TAG, message, e);
        } else {
            // Fallback to system err if Gdx.app is not initialized yet
            System.err.println("[" + TAG + " ERROR] " + message);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }
    
    public static void setDefaultFileProperties(Files.FileType type, String path) {
        log("Setting default file properties - Type: " + type + ", Path: " + path);
        defaultFileType = type;
        defaultPath = path;
    }
    
    public static FileHandle getFileHandle(String name) {
        return getFileHandle(defaultFileType, defaultPath, name);
    }
    
    public static FileHandle getFileHandle(Files.FileType type, String name) {
        return getFileHandle(type, "", name);
    }
    
    public static FileHandle getFileHandle(Files.FileType type, String basePath, String name) {
        try {
            switch (type) {
                case Classpath:
                    return Gdx.files.classpath(basePath + name);
                case Internal:
                    return Gdx.files.internal(basePath + name);
                case External:
                    return Gdx.files.external(basePath + name);
                case Absolute:
                    return Gdx.files.absolute(basePath + name);
                case Local:
                    return Gdx.files.local(basePath + name);
                default:
                    error("Unknown file type: " + type, null);
                    return null;
            }
        } catch (Exception e) {
            error("Error getting file handle for " + basePath + name, e);
            return null;
        }
    }

    // File operations

    public static boolean fileExists(String name) {
        try {
            FileHandle file = getFileHandle(name);
            return file != null && file.exists() && !file.isDirectory() && file.length() > 0;
        } catch (Exception e) {
            error("Error checking if file exists: " + name, e);
            return false;
        }
    }

    public static long fileLength(String name) {
        try {
            FileHandle file = getFileHandle(name);
            if (file == null || !file.exists() || file.isDirectory()) {
                return 0;
            } else {
                return file.length();
            }
        } catch (Exception e) {
            error("Error getting file length for: " + name, e);
            return 0;
        }
    }

    public static boolean deleteFile(String name) {
        try {
            FileHandle file = getFileHandle(name);
            return file != null && file.exists() && file.delete();
        } catch (Exception e) {
            error("Error deleting file: " + name, e);
            return false;
        }
    }

    public static void overwriteFile(String name, int bytes) {
        try {
            byte[] data = new byte[bytes];
            Arrays.fill(data, (byte) 1);
            FileHandle file = getFileHandle(name);
            if (file != null) {
                file.writeBytes(data, false);
            }
        } catch (Exception e) {
            error("Error overwriting file: " + name, e);
        }
    }

    // Directory operations

    public static boolean dirExists(String name) {
        try {
            FileHandle dir = getFileHandle(name);
            return dir != null && dir.exists() && dir.isDirectory();
        } catch (Exception e) {
            error("Error checking if directory exists: " + name, e);
            return false;
        }
    }

    public static boolean deleteDir(String name) {
        try {
            FileHandle dir = getFileHandle(name);
            return dir != null && dir.isDirectory() && dir.deleteDirectory();
        } catch (Exception e) {
            error("Error deleting directory: " + name, e);
            return false;
        }
    }

    public static ArrayList<String> filesInDir(String name) {
        try {
            FileHandle dir = getFileHandle(name);
            ArrayList<String> result = new ArrayList<>();
            if (dir != null && dir.isDirectory()) {
                for (FileHandle file : dir.list()) {
                    result.add(file.name());
                }
            }
            return result;
        } catch (Exception e) {
            error("Error listing files in directory: " + name, e);
            return new ArrayList<>();
        }
    }

    // Bundle operations

    public static Bundle bundleFromFile(String fileName) throws IOException {
        log("Attempting to load bundle from file: " + fileName);
        try {
            FileHandle file = getFileHandle(fileName);
            if (file == null) {
                String error = "Could not get file handle for: " + fileName;
                log(error);
                throw new IOException(error);
            }
            
            log("File handle obtained. Path: " + file.file().getAbsolutePath() + 
                ", exists: " + file.exists() + 
                ", isDirectory: " + file.isDirectory() + 
                ", length: " + file.length() + " bytes");
                
            if (!file.exists() || file.isDirectory() || file.length() == 0) {
                String error = "File does not exist or is invalid: " + fileName;
                log(error);
                throw new IOException(error);
            }
            
            log("Reading bundle from file...");
            Bundle bundle = bundleFromStream(file.read());
            log("Successfully loaded bundle from file: " + fileName);
            return bundle;
            
        } catch (GdxRuntimeException e) {
            log("GdxRuntimeException while loading bundle: " + e.getMessage());
            throw new IOException(e);
        } catch (Exception e) {
            log("Unexpected error loading bundle: " + e.getMessage());
            throw e;
        }
    }

    private static Bundle bundleFromStream(InputStream input) throws IOException {
        try {
            Bundle bundle = Bundle.read(input);
            input.close();
            return bundle;
        } catch (Exception e) {
            try { input.close(); } catch (IOException ignored) {}
            throw new IOException("Error reading bundle from stream", e);
        }
    }

    public static boolean bundleToFile(String fileName, Bundle bundle) throws IOException {
        log("Saving bundle to file: " + fileName);
        try {
            FileHandle file = getFileHandle(fileName);
            if (file == null) {
                String error = "Could not get file handle for: " + fileName;
                log(error);
                throw new IOException(error);
            }
            log("File handle obtained. Path: " + file.file().getAbsolutePath());

            // Write to a temp file first to prevent corruption if the process is interrupted
            FileHandle temp = getFileHandle(fileName + ".tmp");
            if (temp != null) {
                log("Writing to temp file: " + temp.file().getAbsolutePath());
                bundleToStream(temp.write(false), bundle);
                log("Successfully wrote to temp file");
                
                if (file.exists()) {
                    log("Deleting existing file");
                    boolean deleted = file.delete();
                    log("Existing file " + (deleted ? "deleted" : "not deleted"));
                }
                
                log("Moving temp file to final location");
                temp.moveTo(file);
                log("File saved successfully: " + file.file().getAbsolutePath() + 
                    ", size: " + file.length() + " bytes");
                return true;
            } else {
                String error = "Could not create temp file for: " + fileName;
                log(error);
                throw new IOException(error);
            }
        } catch (GdxRuntimeException e) {
            log("GdxRuntimeException: " + e.getMessage());
            throw new IOException(e);
        } catch (Exception e) {
            log("Unexpected error: " + e.getMessage());
            throw e;
        }
    }

    private static void bundleToStream(OutputStream output, Bundle bundle) throws IOException {
        try {
            Bundle.write(bundle, output);
            output.close();
        } catch (Exception e) {
            try { output.close(); } catch (IOException ignored) {}
            throw new IOException("Error writing bundle to stream", e);
        }
    }

    // Temp file handling

    public static boolean cleanTempFiles() {
        return cleanTempFiles("");
    }

    public static boolean cleanTempFiles(String dirName) {
        try {
            FileHandle dir = getFileHandle(dirName);
            if (dir == null || !dir.exists()) {
                return false;
            }

            boolean foundTemp = false;
            for (FileHandle file : dir.list()) {
                if (file.isDirectory()) {
                    foundTemp = cleanTempFiles(dirName + file.name()) || foundTemp;
                } else if (file.length() == 0) {
                    file.delete();
                } else if (file.name().endsWith(".tmp")) {
                    processTempFile(file);
                    foundTemp = true;
                }
            }
            return foundTemp;
        } catch (Exception e) {
            error("Error cleaning temp files in " + dirName, e);
            return false;
        }
    }

    private static void processTempFile(FileHandle temp) {
        try {
            FileHandle original = getFileHandle(defaultFileType, "", temp.path().replace(".tmp", ""));
            if (original == null) {
                temp.delete();
                return;
            }

            // Verify temp file is valid
            try (InputStream in = temp.read()) {
                Bundle bundle = Bundle.read(in);
                if (bundle == null) throw new IOException("Invalid bundle in temp file");
            }

            // If original exists, check which is newer
            if (original.exists()) {
                try (InputStream in = original.read()) {
                    Bundle bundle = Bundle.read(in);
                    if (bundle != null && temp.lastModified() <= original.lastModified()) {
                        temp.delete();
                        return;
                    }
                } catch (Exception e) {
                    // Original is corrupted, will be replaced by temp
                }
            }

            // Replace original with temp
            temp.moveTo(original);

        } catch (Exception e) {
            temp.delete();
        }
    }
}
