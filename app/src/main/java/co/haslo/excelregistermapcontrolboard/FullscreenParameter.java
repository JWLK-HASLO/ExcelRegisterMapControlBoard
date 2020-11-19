package co.haslo.excelregistermapcontrolboard;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceHandler;
import co.haslo.excelregistermapcontrolboard.util.Dlog;

import static co.haslo.excelregistermapcontrolboard.util.InterfaceUtil.showToast;

public class FullscreenParameter {

    public static int MOD_NUMBER = 2;

    private AppCompatActivity appCompatActivity;
    DeviceHandler mDeviceHandler ;

    RadioGroup modeGroup;
    RadioButton mode_B, mode_D, mode_C;

    FullscreenParameter(AppCompatActivity appCompatActivity, DeviceHandler deviceHandler) {
        this.appCompatActivity = appCompatActivity;
        mDeviceHandler = deviceHandler;
    }

    void initialize() {
        Dlog.d("Parameter Activity Ready");
        setXmlComponent();
        setButtonControl();
    }

    void setXmlComponent() {
        modeGroup = appCompatActivity.findViewById(R.id.mode_group);
        mode_B = appCompatActivity.findViewById(R.id.mode_b);
        mode_D = appCompatActivity.findViewById(R.id.mode_d);
        mode_C = appCompatActivity.findViewById(R.id.mode_c);
    }

    void setButtonControl() {
        modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == mode_B.getId()){
                    showToast(appCompatActivity,"SET B-MODE");
                    MOD_NUMBER = 1;
                } else if(i == mode_D.getId()) {
                    showToast(appCompatActivity,"SET D-MODE");
                    MOD_NUMBER = 2;
                } else if(i == mode_C.getId()) {
                    showToast(appCompatActivity,"SET C-MODE");
                    MOD_NUMBER = 3;
                } else {
                    showToast(appCompatActivity,"SET NONE");
                    MOD_NUMBER = 0;
                }
            }
        });
    }
}
