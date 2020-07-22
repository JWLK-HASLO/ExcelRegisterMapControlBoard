package co.haslo.excelregistermapcontrolboard.usbDeviceManager;

import android.hardware.usb.UsbDevice;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import co.haslo.excelregistermapcontrolboard.util.ConvertDataType;
import co.haslo.excelregistermapcontrolboard.util.Dlog;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class DeviceHandler extends Handler {

    private AppCompatActivity appCompatActivity;
    private DeviceManager mDeviceManager;
    private DeviceCommunicator mDeviceCommunicator;
    private DeviceDataTransfer mDeviceDataTransfer;
    //private PropertyManager mPropertyManager;

    private SimpleDateFormat formatPrint = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss");

    private Thread mUSBRealTimeController = new Thread(){
        @Override
        public void run() {
            while (!isInterrupted()) {

                Date currentTime = new Date();
                String printString = formatPrint.format(currentTime);
                //Dlog.i(printString); // Timer

                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    Dlog.e("mUSBRealTimeController Thread Error : " + e );
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public DeviceHandler(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void initialize() {
        mDeviceDataTransfer = DeviceDataTransfer.getInstance();
        //mUSBRealTimeController.start();
    }

    public void handleMessage(Message msg) {
        UsbDevice device = (UsbDevice) msg.obj;

        if(msg.what == DeviceManager.MSG_USB_CONNECTION) {
            Dlog.i("Device Manager Monitoring Service Send Message : On USB Connected");
            try {
                mDeviceCommunicator = mDeviceManager.CreateDeviceCommunicator(appCompatActivity.getApplicationContext(), device);
            } catch (Exception e) {
                Dlog.e("handleMessage Error "+ e );
            }

            if(mDeviceCommunicator != null) {
                mDeviceDataTransfer.registerDeviceCommunicator(mDeviceCommunicator);
            }
        } else {
            Dlog.i("Device Manager Monitoring Service Send Message : On USB DisConnected");
            handlingClear();
        }
    }

    public void handlingStart() {
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.DeviceManagerMonitoringStart(appCompatActivity.getApplicationContext(), this);
    }

    public void handlingStop() {
        if(mDeviceCommunicator != null)
        {
            try {
                Dlog.i("Set freeze");
                //DeviceSetting.sendFreeze(mDeviceCommunicator);
                Dlog.i("Complete freeze");
            } catch(Exception e) {
                Dlog.e("Send device freeze error : " + e.getMessage());
            }
        }
        mDeviceManager.DeviceManagerMonitoringClear();
    }

    public void handlingClear() {
        mDeviceDataTransfer.deregisterDeivceCommunicator();
        mDeviceCommunicator = null;

    }

    public void sendData(String[] hexStringArray) {
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.writeBulkHexData(mDeviceCommunicator, hexStringArray);
        } else {
            deviceConnectionError();
        }
    }


    public void startCounter() {
        Dlog.i("Start Counter");
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.counterTest(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    public void resetCounter() {
        Dlog.i("Reset Counter");
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.counterReset(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    public void deviceConnectionError() {
        Toast warningMessage = Toast.makeText(appCompatActivity.getApplicationContext(),"Please Connect USB Device: "+mDeviceCommunicator, Toast.LENGTH_SHORT);
        warningMessage.show();
    }


    /**
     * Device Data RESET & START ;
     */

    public void resetData() {
        Dlog.i("Reset Data");
        if(mDeviceCommunicator != null) {
            //mDeviceCommunicator.DataTransferReset();
            DeviceRegisterSetting.reset(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    public void run() {
        Dlog.i("Run Data");
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.run(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    /**
     * Device Data Send Controller;
     */

    public void registerHandlerLED() {
        Dlog.i("registerHandlerLED");
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.registerSetLED(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    int loopLoadNumber = 0;
    int loopViewNumber = 0;

    public void registerHandlerView(int seelcted) {
        Dlog.i("registerHandlerView : " +seelcted);

        if(seelcted == 1){
            registerView(DeviceDataTransfer.bufferArray_00);
        } else if(seelcted == 2) {
            registerView(DeviceDataTransfer.bufferArray_01);
        } else if(seelcted == 3) {
            registerView(DeviceDataTransfer.bufferArray_02);
        } else if(seelcted == 4) {
            registerView(DeviceDataTransfer.bufferArray_03);
        } else if(seelcted == 5) {
            registerView(DeviceDataTransfer.bufferArray_04);
        } else if(seelcted == 6) {
            registerView(DeviceDataTransfer.bufferArray_05);
        } else if(seelcted == 7) {
            registerView(DeviceDataTransfer.bufferArray_06);
        } else if(seelcted == 8) {
            registerView(DeviceDataTransfer.bufferArray_07);
        } else if(seelcted == 9) {
            registerView(DeviceDataTransfer.bufferArray_08);
        } else if(seelcted == 10) {
            registerView(DeviceDataTransfer.bufferArray_09);
        } else if(seelcted == 11) {
            registerView(DeviceDataTransfer.bufferArray_10);
        } else if(seelcted == 12) {
            registerView(DeviceDataTransfer.bufferArray_11);
        } else if(seelcted == 13) {
            registerView(DeviceDataTransfer.bufferArray_12);
        } else if(seelcted == 14) {
            registerView(DeviceDataTransfer.bufferArray_13);
        }
    }



    public void registerView(byte[] bufferArray) {

        Dlog.i("TotalSize = " + bufferArray.length);
        String convertString;
        for(int i = 0, counter = 1; i < 4096*4; i+=4, counter++) {
            //Dlog.d("defaultBulkCounter : " + defaultBulkCounter);
            byte Data03 = bufferArray[i + 3];
            byte Data02 = bufferArray[i + 2];
            byte Data01 = bufferArray[i + 1];
            byte Data00 = bufferArray[i + 0];
            byte[] DataArray = {Data03,Data02,Data01,Data00};
            convertString = ConvertDataType.byteArrayToHexString(DataArray);

            Dlog.i("RX["+ counter +"] - "+ convertString);

            //Dlog.i(convertString);

        }
    }


    public void registerHandlerLoad_1() {
        Dlog.i("Load 1");
        DeviceRegisterSetting.sendRegisterButton(mDeviceCommunicator,registerLoad(0));
    }

    public void registerHandlerLoad_2() {
        Dlog.i("Load 2");
        DeviceRegisterSetting.sendRegisterButton(mDeviceCommunicator,registerLoad(1));
    }

    public void registerHandlerLoad_3() {
        Dlog.i("Load 3");
        DeviceRegisterSetting.sendRegisterButton(mDeviceCommunicator,registerLoad(2));
    }

    public void registerHandlerLoad_4() {
        Dlog.i("Load B_Coef");
        DeviceRegisterSetting.sendRegisterButton(mDeviceCommunicator,registerLoad_B_coef(2));
    }

    public void registerHandlerStart() {
        Dlog.i("START");
        DeviceRegisterSetting.sendRegisterButton(mDeviceCommunicator,registerLoad(3));
    }

    public void registerHandlerStop() {
        Dlog.i("STOP");
        DeviceRegisterSetting.sendRegisterButton(mDeviceCommunicator,registerLoad(4));
    }

    public ArrayList<String> registerLoad(int selectNumber) {
        ArrayList<String> dataSaveArrayList = new ArrayList<>();

        try {
//            InputStream is = appCompatActivity.getApplicationContext().getAssets().open("ExcelRegisterMap.xls");
            InputStream is = appCompatActivity.getApplicationContext().getAssets().open("REV_ExcelRegisterMap.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null) {
                Dlog.d("RegisterMap Init");
                Sheet sheet = wb.getSheet(selectNumber);   // 시트 불러오기
                if(sheet != null) {
                    int colIndexStart = 1;
                    int colTotal = sheet.getColumns();
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    ArrayList<String> arrayList = new ArrayList<>();
                    StringBuilder sb;

                    for(int col = colIndexStart; col < colTotal; col++) {

                        sb = new StringBuilder();

                        for(int row = rowIndexStart; row < rowTotal; row++) {

                            String contents = sheet.getCell(col, row).getContents();
                            arrayList.add(contents);
                            if(row == rowTotal-1) {
                                sb.append("row"+row+" : "+contents);
                            } else {
                                sb.append("row"+row+" : "+contents+" , ");
                            }
                            Dlog.i(col+","+row+" = "+contents);

                            //ADD Reigster
                            dataSaveArrayList.add(contents);
                        }

                    }
                    Dlog.i("Total = "+ arrayList.size());
                }
            }
        } catch (IOException | BiffException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }

        return dataSaveArrayList;

    }

    public ArrayList<String> registerLoad_B_coef(int selectNumber) {
        ArrayList<String> dataSaveArrayList = new ArrayList<>();

        try {
//            InputStream is = appCompatActivity.getApplicationContext().getAssets().open("ExcelRegisterMap.xls");
            InputStream is = appCompatActivity.getApplicationContext().getAssets().open("Tx_BF_B_delay_coef.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null) {
                Sheet sheet = wb.getSheet(selectNumber);   // 시트 불러오기
                if(sheet != null) {
                    int colIndexStart = 1;
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    ArrayList<String> arrayList = new ArrayList<>();
                    StringBuilder sb;

                    for(int col = colIndexStart; col < colTotal; col++) {

                        sb = new StringBuilder();

                        for(int row = rowIndexStart; row < rowTotal; row++) {

                            String contents = sheet.getCell(col, row).getContents();
                            arrayList.add(contents);
                            if(row == rowTotal-1) {
                                sb.append("row"+row+" : "+contents);
                            } else {
                                sb.append("row"+row+" : "+contents+" , ");
                            }
                            Dlog.i(col+","+row+" = "+contents);

                            //ADD Reigster
                            dataSaveArrayList.add(contents);
                        }

                    }
                    Dlog.i("Total = "+ arrayList.size());
                }
            }
        } catch (IOException | BiffException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }

        return dataSaveArrayList;

    }


}
