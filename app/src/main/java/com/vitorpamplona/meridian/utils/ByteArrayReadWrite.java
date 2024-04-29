/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact me at vitor@vitorpamplona.com.
 * For AGPL licensing, see below.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This application has not been clinically tested, approved by or registered in any health agency.
 * Even though this repository grants licenses to use to any person that follow it's license,
 * any clinical or commercial use must additionally follow the laws and regulations of the
 * pertinent jurisdictions. Having a license to use the source code does not imply on having
 * regulatory approvals to use or market any part of this code.
 */
package com.vitorpamplona.meridian.utils;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteArrayReadWrite {

    // super secret constructor
    private ByteArrayReadWrite() {
    }

    /** Read the given binary file, and return its contents as a byte array.*/
    public static byte[] readAndroid(String aInputFileName, int filenumber) {
        return read(getAndroidFileName(aInputFileName, filenumber));
    }

    public static byte[] read(File file) {
        Logr.d("FILE", "Reading in binary file named : " + file);
        //File file = new File(aInputFileName);

        Logr.d("File", "File size: " + file.length());
        byte[] result = new byte[(int) file.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while (totalBytesRead < result.length) {
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0) {
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }

                log("Num bytes read: " + totalBytesRead);
            } finally {
                log("Closing input stream.");
                input.close();
            }
        } catch (FileNotFoundException ex) {
            log("File not found.");
        } catch (IOException ex) {
            log(ex);
        }
        return result;
    }

    /**
     Write a byte array to the given file.
     Writing binary data is significantly simpler than reading it.
     */
    public static void writeAndroid(byte[] aInput, String name, int filenumber) {
        write(aInput, getAndroidFileName(name, filenumber));
    }

    public static File getAndroidFileName(String name, int filenumber) {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "MeridianDebug" + "/");
        path.mkdirs();
        String fname = name + "_" + filenumber + ".txt";
        return new File(path, fname);
    }

    /**
     Write a byte array to the given file.
     Writing binary data is significantly simpler than reading it.
     */
    public static void write(byte[] aInput, File file) {
        log("Writing binary file...");

        if (file.exists()) file.delete();

        try {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(file.toString()));
                output.write(aInput);
            } finally {
                output.close();
            }
        } catch (FileNotFoundException ex) {
            log("File not found.");
        } catch (IOException ex) {
            log(ex);
        }
    }


    private static void log(Object aThing) {
        Logr.d("FILE", String.valueOf(aThing));
    }

} 
