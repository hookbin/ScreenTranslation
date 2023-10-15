package io.github.hookbin.screentranslation;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import io.github.hookbin.screentranslation.util.AppPathUtil;

public class MApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        LogUtils.getConfig()
                .setGlobalTag("Screentranslation[" + BuildConfig.VERSION_NAME + "]")
                .setLogSwitch(true);

        FileUtils.deleteAllInDir(AppPathUtil.getCacheDir());
    }
}
