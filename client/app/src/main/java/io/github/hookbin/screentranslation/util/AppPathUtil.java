package io.github.hookbin.screentranslation.util;

import com.blankj.utilcode.util.Utils;

import java.io.File;

public class AppPathUtil {
    public static File getCacheDir() {
        return Utils.getApp().getCacheDir();
    }

    public static File getTraineddataDir() {
        return Utils.getApp().getFilesDir();
    }
}
