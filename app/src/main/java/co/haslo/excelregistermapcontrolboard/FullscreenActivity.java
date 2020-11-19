package co.haslo.excelregistermapcontrolboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

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
    public FullscreenController mFullscreenController;
    public FullscreenParameter mFullscreenParameter;
    public FullscreenClickAction mFullscreenClickAction;
    public FullscreenLogBox mFullscreenLogBox;
    public FullscreenImaging mFullscreenImaging;

    /*TEST Dpp Data*/
    //GetData;
    public InputStream inputStreamInphase, inputStreamQuadrature;
    public byte[] getByteDataInphase = new byte[0];
    public byte[] getByteDataQuadrature = new byte[0];

    public String[] getLinesInphase, getLinesQuadrature;

    public int[] convertIntArrayInphase= new int[128];
    public int[] convertIntArrayQuadrature = new int[128];

    public static double[] convertDoubleArrayInphase= new double[128];
    public static double[] convertDoubleArrayQuadrature = new double[128];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mDeviceHandler = new DeviceHandler(this);
        mFullscreenController = new FullscreenController(this);
        mFullscreenParameter = new FullscreenParameter(this, mDeviceHandler);
        mFullscreenClickAction = new FullscreenClickAction(this, mDeviceHandler);
        mFullscreenLogBox = new FullscreenLogBox(this);
        mFullscreenImaging = new FullscreenImaging(this, mDeviceHandler);

        mDeviceHandler.initialize();
        mFullscreenParameter.initialize();
        mFullscreenClickAction.initialize();
        mFullscreenLogBox.initialize();
        mFullscreenController.initialize();
        mFullscreenImaging.initialize();


        try {
        //*/ TEST DATA
            inputStreamInphase = getResources().openRawResource(R.raw.inphase);
            inputStreamQuadrature = getResources().openRawResource(R.raw.quadrature);
            getByteDataInphase = new byte[inputStreamInphase.available()];
            getByteDataQuadrature = new byte[inputStreamQuadrature.available()];
            inputStreamInphase.read(getByteDataInphase);
            inputStreamQuadrature.read(getByteDataQuadrature);
            getLinesInphase = (new String(getByteDataInphase)).split("\\r?\\n");
            getLinesQuadrature = (new String(getByteDataQuadrature)).split("\\r?\\n");

            for (int i = 0; i < getLinesInphase.length; i++) {
                //System.out.println("getLinesInphase " + i + " : " + getLinesInphase[i]);
                convertIntArrayInphase[i] = Integer.parseInt(getLinesInphase[i]);
                convertDoubleArrayInphase[i] = Integer.parseInt(getLinesInphase[i]);
            }

            for (int i = 0; i < getLinesQuadrature.length; i++) {
                //System.out.println("getLinesQuadrature " + i + " : " + getLinesQuadrature[i]);
                convertIntArrayQuadrature[i] = Integer.parseInt(getLinesQuadrature[i]);
                convertDoubleArrayQuadrature[i] = Integer.parseInt(getLinesQuadrature[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*/

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
