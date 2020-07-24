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
        viewData_0_register_all();
        viewData_1_register_all();
        viewData_2_register_all();
        viewData_3_register_all();
        viewData_4_register_all();
        viewData_5_register_all();
        viewData_6_register_all();
        viewData_7_register_all();
        viewData_8_register_all();
        viewData_9_register_all();
        viewData_10_register_all();
        viewData_11_register_all();
        viewData_12_register_all();
        viewData_13_register_all();
        viewData_14_register_all();

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

    private void viewData_0_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_0);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_1_register_all Button Click");
                mDeviceHandler.registerHandlerViewInputControl();
            }
        });
    }

    private void viewData_1_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_1);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_1_register_all Button Click");
                mDeviceHandler.registerHandlerView(1);
            }
        });
    }

    private void viewData_2_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_2);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_2_register_all Button Click");
                mDeviceHandler.registerHandlerView(2);
            }
        });
    }

    private void viewData_3_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_3);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_3_register_all Button Click");
                mDeviceHandler.registerHandlerView(3);
            }
        });
    }

    private void viewData_4_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_4);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_1_register_all Button Click");
                mDeviceHandler.registerHandlerView(4);
            }
        });
    }

    private void viewData_5_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_5);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_5_register_all Button Click");
                mDeviceHandler.registerHandlerView(5);
            }
        });
    }

    private void viewData_6_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_6);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_6_register_all Button Click");
                mDeviceHandler.registerHandlerView(6);
            }
        });
    }

    private void viewData_7_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_7);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_7_register_all Button Click");
                mDeviceHandler.registerHandlerView(7);
            }
        });
    }

    private void viewData_8_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_8);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_8_register_all Button Click");
                mDeviceHandler.registerHandlerView(8);
            }
        });
    }

    private void viewData_9_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_9);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_9_register_all Button Click");
                mDeviceHandler.registerHandlerView(9);
            }
        });
    }

    private void viewData_10_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_10);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_10_register_all Button Click");
                mDeviceHandler.registerHandlerView(10);
            }
        });
    }

    private void viewData_11_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_11);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_11_register_all Button Click");
                mDeviceHandler.registerHandlerView(11);
            }
        });
    }
    private void viewData_12_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_12);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_12_register_all Button Click");
                mDeviceHandler.registerHandlerView(12);
            }
        });
    }

    private void viewData_13_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_13);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_13_register_all Button Click");
                mDeviceHandler.registerHandlerView(13);
            }
        });
    }

    private void viewData_14_register_all() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.data_14);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"viewData_14_register_all Button Click");
                mDeviceHandler.registerHandlerView(14);
            }
        });
    }








}
