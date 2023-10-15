package io.github.hookbin.screentranslation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;

import java.util.List;

import io.github.hookbin.screentranslation.databinding.ActivityMainBinding;
import io.github.hookbin.screentranslation.util.AppPathUtil;
import io.github.hookbin.screentranslation.util.TesseractUtil;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MAppActivity extends AppCompatActivity implements OnClickListener {

    private ActivityMainBinding mRootBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        mRootBind = ActivityMainBinding.bind(view);
        setContentView(view);

        mRootBind.tvHello.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        final String filePath = AppPathUtil.getCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis();
        ResourceUtils.copyFileFromAssets("Screenshot_2023-10-15-22-28-53-245_com.epidgames..png", filePath);
        TesseractUtil tesseractUtil = new TesseractUtil();
        Disposable disposable = tesseractUtil.ocrRx(filePath)
                .subscribeOn(Schedulers.single())
                .subscribe(new BiConsumer<List<TesseractUtil.Result>, Throwable>() {
                    @Override
                    public void accept(List<TesseractUtil.Result> results, Throwable throwable) throws Throwable {
                        LogUtils.d(results, throwable);
                    }
                });
    }
}