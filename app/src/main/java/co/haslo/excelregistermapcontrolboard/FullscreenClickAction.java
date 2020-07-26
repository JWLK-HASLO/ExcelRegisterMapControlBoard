package co.haslo.excelregistermapcontrolboard;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer;
import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceHandler;
import co.haslo.excelregistermapcontrolboard.util.Dlog;
import co.haslo.excelregistermapcontrolboard.util.InterfaceUtil;

import static co.haslo.excelregistermapcontrolboard.util.InterfaceUtil.showToast;

public class FullscreenClickAction {

    private AppCompatActivity appCompatActivity;
    DeviceHandler mDeviceHandler ;
    String convertString;

    private boolean setTrigger = false;
    private boolean startTrigger = false;
    private boolean uploadTrigger = false;

    FullscreenClickAction(AppCompatActivity appCompatActivity, DeviceHandler deviceHandler) {
        this.appCompatActivity = appCompatActivity;
        mDeviceHandler = deviceHandler;
    }

    void initialize() {
        Dlog.d("Ready");
        setButton();

    }

    /* Button Bundle */
    private void setButton() {

        /*Log Box*/
        loadMenuButtonClear();
        loadMenuButtonTop();
        loadMenuButtonBottom();

        /*Center*/
        setData_led();
        t_start();
        t_reset();
        loadFrameData();
        loadData_1_register_all();
        loadData_2_register_all();
        loadData_3_register_all();
        loadData_4_register_all();
        startData_register_all();
        stopData_register_all();

        /*Left*/


        /*Right*/

    }


    /**
     * LOG BOX Button
     */

    /*Log Box Button */
    private void loadMenuButtonClear() {
        final TextView logBoxText =  appCompatActivity.findViewById(R.id.log_box_text);
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_clear);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"Clear Log Box");
                FullscreenLogBox.clearLogcat();
                logBoxText.setText(null);
                FullscreenLogBox.lengthSaver = 0;
            }
        });
    }

    private void loadMenuButtonTop() {
        final TextView logBoxText =  appCompatActivity.findViewById(R.id.log_box_text);
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_top);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"Going to Top!");
                InterfaceUtil.scrollTop(logBoxText);
            }
        });
    }
    private void loadMenuButtonBottom() {
        final TextView logBoxText =  appCompatActivity.findViewById(R.id.log_box_text);
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_bottom);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"Going to Bottom!");
                InterfaceUtil.scrollBottom(logBoxText);
            }
        });
    }



    /**
     * Center Button
     */
    /* Screen Test Button */
    private void setData_led() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.setData_led);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"setData_ledon Button Click");
                mDeviceHandler.registerHandlerLED();
            }
        });
    }

    private void t_start() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.set_test_start);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceDataTransfer.defaultBulkCounter = 0;
                DeviceDataTransfer.ReadBulkStartTrigger = true;
                showToast(appCompatActivity,"Test Start Button Click");
                mDeviceHandler.run();
            }
        });
    }

    private void t_reset() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.set_test_reset);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceDataTransfer.defaultBulkCounter = 0;
                DeviceDataTransfer.ReadBulkStartTrigger = false;
                showToast(appCompatActivity,"Test Reset Button Click");
                mDeviceHandler.reset();
            }
        });
    }

    private void loadFrameData() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.load_frame_data);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceDataTransfer.ReadBulkStartTrigger = false;
                showToast(appCompatActivity,"load Multiple Frame Data Button Click");
                mDeviceHandler.registerHandlerLoad_frameData();
            }
        });
    }

    /* Screen Test Button */
    private void loadData_1_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.load_register_1);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"loadData_1_register_all Button Click");
                mDeviceHandler.registerHandlerLoad_1();
            }
        });
    }

    private void loadData_2_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.load_register_2);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"loadData_2_register_all Button Click");
                mDeviceHandler.registerHandlerLoad_2();
            }
        });
    }


    private void loadData_3_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.load_register_3);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"loadData_3_register_all Button Click");
                mDeviceHandler.registerHandlerLoad_3();
            }
        });
    }

    private void loadData_4_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.load_register_4);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"loadData_4_register_all Button Click");
                mDeviceHandler.registerHandlerLoad_4();
            }
        });
    }


    private void startData_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.start_register);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"startData_register_all Button Click");
                mDeviceHandler.registerHandlerStart();
            }
        });
    }

    private void stopData_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.stop_register);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"stopData_register_all Button Click");

                mDeviceHandler.registerHandlerStop();
            }
        });
    }




    /**
     * RIGHT Button
     */

}
