package co.haslo.excelregistermapcontrolboard.usbDeviceManager;


import java.util.ArrayList;

import co.haslo.excelregistermapcontrolboard.util.Dlog;

import static co.haslo.excelregistermapcontrolboard.util.ConvertDataType.hexStringToShort16bit4HexArray;
import static co.haslo.excelregistermapcontrolboard.util.ConvertDataType.hexStringArrayToInt32bit8HexArray;


public class DeviceRegisterSetting {

    public static void writeCommandHexData(DeviceCommunicator device, String hexString)
    {
        short[] dataShort = hexStringToShort16bit4HexArray(hexString);
        for(short shorts : dataShort){
            Dlog.d(String.format("0x%04X", shorts));
        }

        try {
            device.DataTransferSingleWrite(dataShort[0], dataShort[1]);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void writeBulkHexData(DeviceCommunicator device, String[] mHexStringArray)
    {
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(mHexStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(mHexStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void counterTest(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"98000000","980103FF", "9807001F","98080FFF","980900FF"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void counterReset(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"98000000", "98010000", "98000003"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    /**
     * RESET & START Button
     */

    public static void reset(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"980A0000","900C0000"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void set(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"98000000", "98020000", "9801007F", "980B5DBF", "980C0001", "980D007F", "980E003F", "980F0040", "981001FF"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void run(DeviceCommunicator device)
    {                               /*98000003,980A0001*/
        String[] sendStringArray =  {"980A0001"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray) {
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    /**
     * Variable
     */

    static ArrayList<String> dataSaveArrayList = new ArrayList<>();
    static int loopSwitchLED = 0;

    int modeChecker = 0;  // 0 = B-Mode, 1 = D-Mode, 2 = C-Mode,
    int depthChecker = 0;
    int cycleChecker = 0;


    /**
     * DEBUG Button
     */

    public static void registerSetLED(DeviceCommunicator device) {
        if(loopSwitchLED%2 == 0) {
            //LED ON
            dataSaveArrayList.add("8009003F"); //3F : ALL LED ON
            loopSwitchLED++;
            sendRegisterButton(device, dataSaveArrayList);
        } else if(loopSwitchLED%2 == 1) {
            //LED OFF
            dataSaveArrayList.add("80090001"); //0 : LED OFF
            loopSwitchLED++;
            sendRegisterButton(device, dataSaveArrayList);
        }

    }


    public static void sendRegisterButton(DeviceCommunicator device, ArrayList<String> arrayList) {
        String[] sendStringArray =  arrayList.toArray(new String[arrayList.size()]);
        /*
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
         */

        try {
            device.DataTransferBulkWrite(sendStringArray);
            Dlog.d("Send Register Data ");
            dataSaveArrayList = new ArrayList<>();
            Dlog.d("dataSaveArrayList Clear ");
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }


}
