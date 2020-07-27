package co.haslo.excelregistermapcontrolboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceHandler;
import co.haslo.excelregistermapcontrolboard.util.Dlog;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    public DeviceHandler mDeviceHandler;
    /**
     * Get Function Activity
     */
    public FullscreenClickAction mFullscreenClickAction;
    public FullscreenController mFullscreenController;
    public FullscreenLogBox mFullscreenLogBox;
    public FullscreenImaging mFullscreenImaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mDeviceHandler = new DeviceHandler(this);
        mFullscreenController = new FullscreenController(this);
        mFullscreenClickAction = new FullscreenClickAction(this, mDeviceHandler);
        mFullscreenLogBox = new FullscreenLogBox(this);
        mFullscreenImaging = new FullscreenImaging(this, mDeviceHandler);

        mDeviceHandler.initialize();
        mFullscreenClickAction.initialize();
        mFullscreenLogBox.initialize();
        mFullscreenController.initialize();
        mFullscreenImaging.initialize();

    }

    protected void onStart() {
        super.onStart();
        Dlog.d("onStart");
        mDeviceHandler.handlingStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dlog.d("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dlog.d("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Dlog.d("onStop");
        mDeviceHandler.handlingStop();
        Dlog.i("Device Handler Stop And Reset Complete");
        mDeviceHandler.handlingClear();
        Dlog.i("Device Handler Clear Complete");

        Dlog.i("onStop Completed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Dlog.d("onDestroy");
    }

    @Override
    public void onBackPressed() {
        Dlog.d("Application Finish");
        finish();
    }

}
