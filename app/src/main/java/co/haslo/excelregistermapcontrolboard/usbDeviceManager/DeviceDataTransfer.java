package co.haslo.excelregistermapcontrolboard.usbDeviceManager;


import android.annotation.SuppressLint;

import java.util.ArrayList;

import co.haslo.excelregistermapcontrolboard.FullscreenImaging;
import co.haslo.excelregistermapcontrolboard.util.Dlog;

public class DeviceDataTransfer {

    public static final int SEQUENCE_DATA_SIZE = 4096; // Byte => 1BULK = 4096 BYTE = 16384 BIT
    public static final int BYTE = 4;
    public static final int BUNDLE = 1;
    public static final int BULK_OF_FRAME = 8;
    public static final int FRAME_NUMBER = 1;

    public static Boolean ReadBulkStartTrigger = false;
    private static final int defaultBITDataSize = SEQUENCE_DATA_SIZE * BYTE * BUNDLE; // 16,384 BIT
    private static final int defaultMultiFrameDataSize = defaultBITDataSize * BULK_OF_FRAME * FRAME_NUMBER; //
    public static int defaultBulkCounter = 0;
    public static int defaultFrameCounter = 0;


    private static DeviceDataTransfer mDataTransferInstance = null;
    private static final Object mSyncBlock = new Object();

    private Thread mDataTransferThread;
    private DeviceCommunicator mDeviceCommunicator;
    private final Object mDataTransferBlock;

    public static byte[] bufferArray_00 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_01 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_02 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_03 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_04 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_05 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_06 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_07 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_08 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_09 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_10 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_11 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_12 = new byte[defaultBITDataSize];
    public static byte[] bufferArray_13 = new byte[defaultBITDataSize];


    public static byte[] bufferArrayMulti = new byte[defaultMultiFrameDataSize];


    private class DeviceDataTransferThread extends Thread {

        public DeviceDataTransferThread() {
            super();
        }

        @SuppressLint("DefaultLocale")
        public void run(){

            final byte[] readBuffer = new byte[defaultBITDataSize];
            int readSize;
            String convertString;
            //String[] convertHexaArray = new String[SEQUENCE_SIZE];
            while (!isInterrupted()) {
                try
                {
                    readSize = mDeviceCommunicator.ReadBulkTransfer(readBuffer, 0, defaultBITDataSize);

                    if(isInterrupted()) {
                        Dlog.i("Thread is interrupted");
                        break;
                    }

                } catch (Exception e) {
                    Dlog.e("Thread Read Exception : " + e);
                    readSize = -1;
                }

                if(readSize <= -1) {
                    continue;
                } else {

                    Dlog.i(String.format("DeviceDataTransferThread : readSize [%d] / BulkCounter [%d} / FrameCounter [%d]", readSize, defaultBulkCounter, defaultFrameCounter));

                    int arrayStartCounter = (defaultBulkCounter - 1);
                    if(ReadBulkStartTrigger && arrayStartCounter!= -1) {
                        System.arraycopy(readBuffer,0, bufferArrayMulti, arrayStartCounter*readSize, defaultBITDataSize);
                    }

                    if(defaultBulkCounter % BULK_OF_FRAME == 0){
                        defaultFrameCounter++;
                        defaultBulkCounter = 0;
//                        FullscreenImaging.arrayIntData = DeviceHandler.registerConvertINT(bufferArrayMulti);
                        FullscreenImaging.arrayIntData = DeviceHandler.registerConvertImaging(bufferArrayMulti);
                    }
                    defaultBulkCounter++;



//                    int arrayStartCounter = (defaultBulkCounter - 1);
//                    if(ReadBulkStartTrigger && arrayStartCounter!= -1) {
//                        Dlog.i(String.format("Trigger data : %d", arrayStartCounter*readSize));
//                        System.arraycopy(readBuffer,0, bufferArrayMulti, arrayStartCounter*readSize, defaultBITDataSize);
//                        Dlog.i(String.format("End data : %d", bufferArrayMulti.length));
//                    }
//
//                    if(defaultBulkCounter % BULK_OF_FRAME == 0){
//                        defaultFrameCounter++;
////                        defaultBulkCounter = 0;
//                    }
//
//                    if(defaultFrameCounter > FRAME_NUMBER ){
//                        FullscreenImaging.arrayIntData = DeviceHandler.registerConvert(bufferArrayMulti);
//                        Dlog.i(String.format("registerConvert : %d", FullscreenImaging.arrayIntData.length));
//                        defaultFrameCounter = 1;
//                    }
//                    defaultBulkCounter++;
                }

            }

        }

    }

    private DeviceDataTransfer() {
        mDataTransferThread = null;
        mDataTransferBlock = new Object();
        mDeviceCommunicator = null;

    }

    public static DeviceDataTransfer getInstance() {
        if(mDataTransferInstance != null) {
            return mDataTransferInstance;
        }
        synchronized (mSyncBlock) {
            if(mDataTransferInstance == null) {
                mDataTransferInstance = new DeviceDataTransfer();
            }
        }
        return mDataTransferInstance;
    }


    //Communicator Set && Thread Start
    public void registerDeviceCommunicator(DeviceCommunicator communicator) {
        Dlog.i("Device Communicator Setting...");
        synchronized (mDataTransferBlock) {
            _interruptThreadAndReleaseUSB();
            mDeviceCommunicator = communicator;
            mDataTransferThread = new DeviceDataTransferThread();
            mDataTransferThread.start();
        }
        Dlog.i("Device Communicator Setting Complete!");
    }

    private void _interruptThreadAndReleaseUSB() {
        Dlog.i("Interrupt Thread Connection trying...");

        if(mDataTransferThread != null){
            mDataTransferThread.interrupt();
            try {
                mDataTransferThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mDataTransferThread = null;
        }
        if(mDeviceCommunicator != null) {
            mDeviceCommunicator.Clear();
            mDeviceCommunicator = null;
        }
        Dlog.i("Interrupt Thread Connection Complete!");
    }

    public void deregisterDeivceCommunicator() {
        Dlog.i("Device Communicator Resetting`...");
        synchronized (mDataTransferBlock)
        {
            _interruptThreadAndReleaseUSB();
        }
        Dlog.i("Device Communicator Reset Complete!");
    }

}
