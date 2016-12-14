package xmg.com.androidfix;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.alipay.euler.andfix.patch.PatchManager;

import java.io.IOException;


/**
 * Description :
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/12/11 0011
 */
public class MainApplication extends Application {
    private PatchManager mPatchManager;
    private static final String TAG = "MainApplication";
    private static final String APATCH_PATH = "/out.apatch";

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize
        mPatchManager = new PatchManager(this);
        mPatchManager.init("1.0");
        Log.d(TAG, "inited.");


        // load patch
        mPatchManager.loadPatch();
        Log.d(TAG, "apatch loaded.");


        // add patch at runtime
        try {
            // .apatch file path
            String patchFileString = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + APATCH_PATH;
            //   /storage/sdcard/out.apatch  系统自带模拟器的路径
            //   /mnt/sdcard/out.apatch  genymotion的路径
            mPatchManager.addPatch(patchFileString);
            Log.d(TAG, "apatch:" + patchFileString + " added.");
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

    }
}
