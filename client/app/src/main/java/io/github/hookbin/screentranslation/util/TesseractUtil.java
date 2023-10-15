package io.github.hookbin.screentranslation.util;


import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.BOX;
import org.bytedeco.leptonica.BOXA;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Single;

import static org.bytedeco.leptonica.global.leptonica.pixDestroy;
import static org.bytedeco.leptonica.global.leptonica.pixRead;
import static org.bytedeco.tesseract.global.tesseract.RIL_TEXTLINE;

public class TesseractUtil {

    public TesseractUtil() {

    }

    public List<Result> ocr(final String filePath) throws Exception {
        final List<Result> resultList = new ArrayList<>();
        TessBaseAPI api = new TessBaseAPI();

        final String dataFilePath = AppPathUtil.getTraineddataDir().getAbsolutePath() + "/" + "kor.traineddata";
        if (!FileUtils.isFileExists(dataFilePath)) {
            ResourceUtils.copyFileFromAssets("kor.traineddata", dataFilePath);
        }
        final int initCode = api.Init(AppPathUtil.getTraineddataDir().getAbsolutePath(), "kor");
        LogUtils.d("initCode=" + initCode);
        if (initCode != 0) {
            throw new Exception("Could not initialize tesseract");
        }

        // Open input image with leptonica library
        PIX image = pixRead(filePath);
        api.SetImage(image);
        // Get OCR result
        // Lookup all component images
        int[] blockIds = {};
        BOXA boxes = api.GetComponentImages(RIL_TEXTLINE, true, null, blockIds);

        BytePointer outText;
        for (int i = 0; i < boxes.n(); i++) {
            // For each image box, OCR within its area
            BOX box = boxes.box(i);
            api.SetRectangle(box.x(), box.y(), box.w(), box.h());
            outText = api.GetUTF8Text();
            String ocrResult = outText.getString();
            int conf = api.MeanTextConf();

            resultList.add(new Result(box.x(), box.y(), box.w(), box.h(), conf, ocrResult));

            String boxInformation = String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d, confidence: %d, text: %s", i, box.x(), box.y(), box.w(), box.h(), conf, ocrResult);
            LogUtils.d(boxInformation);

            outText.deallocate();
        }
        api.End();
        pixDestroy(image);
        return resultList;
    }

    public Single<List<Result>> ocrRx(final String filePath) {
        return Single.fromCallable(new Callable<List<Result>>() {
            @Override
            public List<Result> call() throws Exception {
                return ocr(filePath);
            }
        });
    }

    public static class Result {
        int x;
        int y;
        int w;
        int h;
        int conf;
        String result;

        public Result(int x, int y, int w, int h, int conf, String result) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.conf = conf;
            this.result = result;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "x=" + x +
                    ", y=" + y +
                    ", w=" + w +
                    ", h=" + h +
                    ", conf=" + conf +
                    ", result='" + result + '\'' +
                    '}';
        }
    }
}
