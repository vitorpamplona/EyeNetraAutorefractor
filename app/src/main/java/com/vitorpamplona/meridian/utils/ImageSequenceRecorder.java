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

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Takes care of saving a sequence of frames as images.
 */
public class ImageSequenceRecorder {

    private static final String DIRECTORY = "MeridianDebug";
    private static final String PREFIX = "image_";
    private static final String TAG = ImageSequenceRecorder.class.getSimpleName();
    private static final File mDirPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + DIRECTORY + "/");


    public ImageSequenceRecorder() {

    }

    public void saveBitmap(Bitmap btm, int filenumber) {

        mDirPath.mkdirs();
        String fname = "A_" + filenumber + ".png";
        File file = new File(mDirPath, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            btm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDirectoryContents() {
        for (File file : mDirPath.listFiles()) file.delete();
    }

    public void getFilePaths(String path, ArrayList<File> files) {
        File directory = new File(path);

        // get all files of a given type from a directory and its sub-directories
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                getFilePaths(file.getAbsolutePath(), files);
            }
        }
    }

    int filenumber = -1;

    public void startRecording() {
        filenumber = 1;
    }

    public void stopRecording() {
        filenumber = -1;
    }

}
