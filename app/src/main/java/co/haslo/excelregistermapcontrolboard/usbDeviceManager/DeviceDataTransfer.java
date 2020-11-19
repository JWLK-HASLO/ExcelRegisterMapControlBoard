package co.haslo.excelregistermapcontrolboard.usbDeviceManager;


import android.annotation.SuppressLint;
import android.util.Log;

import java.io.InputStream;

import co.haslo.excelregistermapcontrolboard.FullscreenActivity;
import co.haslo.excelregistermapcontrolboard.FullscreenImaging;
import co.haslo.excelregistermapcontrolboard.FullscreenParameter;
import co.haslo.excelregistermapcontrolboard.NativeProcessing.NativeWrapper;
import co.haslo.excelregistermapcontrolboard.util.Dlog;

public class DeviceDataTransfer {
    NativeWrapper nativeApi = new NativeWrapper();

    public static final int SEQUENCE_DATA_SIZE = 4096; // Byte => 1BULK = 4096 Char DATA = 4096*4 = 16,384 Byte Data
    public static final int BYTE = 4;
    public static final int BUNDLE = 1;
    public static final int BULK_OF_FRAME = 8;
    public static final int FRAME_NUMBER = 1;
    public static final int BULK_DPP_SCANLINE = 128;

    public static Boolean ReadBulkStartTrigger = false;
    private static final int defaultBITDataSize = SEQUENCE_DATA_SIZE * BYTE * BUNDLE; // 16,384 Byte
    private static final int defaultMultiFrameDataSize = defaultBITDataSize * BULK_OF_FRAME * FRAME_NUMBER; // 16,384 Byte * 8 = 131,072
    private static final int defaultDopplerDataSize = BULK_DPP_SCANLINE * BYTE; // 16,384 Byte * 8 = 131,072

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


    //*/B-mode
    public static byte[] bufferArrayMulti = new byte[defaultMultiFrameDataSize];

    //*/D-mode
    public static byte[] bufferArrayDMode = new byte[defaultDopplerDataSize];

    double[][] convertByteToIQArray = new double[2][128];
    double[] convertDoubleArrayInphase_IIR = new double[128];
    double[] convertDoubleArrayQuadrature_IIR = new double[128];

    double[] convertDoubleArrayInphase_FFT_Real = new double[256];
    double[] convertDoubleArrayInphase_FFT_Imag = new double[256];

    double[] convertDoubleArrayQuadrature_FFT_Real = new double[256];
    double[] convertDoubleArrayQuadrature_FFT_Imag = new double[256];

    double[][] convertDoubleArrayInphase_FFT = new double[2][256];
    double[][] convertDoubleArrayQuadrature_FFT = new double[2][256];

    double[] resultMagnitude = new double[256];
    int[] resultDopplerScanline = new int[256];


    private class DeviceDataTransferThread extends Thread {

        public DeviceDataTransferThread() {
            super();
        }

        @SuppressLint("DefaultLocale")
        public void run(){

            final byte[] readBuffer = new byte[defaultBITDataSize]; // 16,384 byte
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
                    int arrayStartCounter = (defaultBulkCounter - 1);

                    if(FullscreenParameter.MOD_NUMBER == 1) {
                        //*/ B-mode Setting
                        Dlog.i(String.format("DeviceDataTransferThread : readSize [%d] / BulkCounter [%d} / FrameCounter [%d]", readSize, defaultBulkCounter, defaultFrameCounter));
                        if(ReadBulkStartTrigger && arrayStartCounter!= -1) {
                            System.arraycopy(readBuffer, 0, bufferArrayMulti, arrayStartCounter*readSize, defaultBITDataSize);
                            //readBuffer  = 16,384
                        }

                        if(defaultBulkCounter % BULK_OF_FRAME == 0){
                            //Dlog.i(String.format("DeviceDataTransferThread : FrameCounter [%d]", defaultFrameCounter));

                            defaultFrameCounter++;
                            defaultBulkCounter = 0;
    //                        FullscreenImaging.arrayIntData = DeviceHandler.registerConvertINT(bufferArrayMulti);
                            //FullscreenImaging.arrayIntData = DeviceHandler.registerConvertImaging(bufferArrayMulti);
                            //DeviceHandler.registerConvertImagingTEST(bufferArrayMulti);

                            //Add: C++ Native Code
                            //FullscreenImaging.arrayIntData = nativeApi.nativeImaging(bufferArrayMulti);
                        }
                        defaultBulkCounter++;
                        //*/
                    } else if(FullscreenParameter.MOD_NUMBER == 2) {
                        //*/ D-mode Setting
                        //Dlog.i(String.format("DeviceDataTransferThread : readSize [%d] / BulkCounter [%d}", readSize, defaultBulkCounter));


                        if(ReadBulkStartTrigger && arrayStartCounter!= -1) {
                            System.arraycopy(readBuffer,0, bufferArrayDMode, 0, defaultDopplerDataSize);
                            //DeviceHandler.registerConvertINT(bufferArrayDMode);
                            convertByteToIQArray = nativeApi.nativeConvertIQ(bufferArrayDMode);

                            //*/IIR Filter
                            convertDoubleArrayInphase_IIR = nativeApi.nativeIIRFilter(convertByteToIQArray[0]);
                            System.arraycopy(convertDoubleArrayInphase_IIR, 0, convertDoubleArrayInphase_FFT_Real, 0, convertDoubleArrayInphase_IIR.length);
                            convertDoubleArrayQuadrature_IIR = nativeApi.nativeIIRFilter(convertByteToIQArray[1]);
                            System.arraycopy(convertDoubleArrayQuadrature_IIR, 0, convertDoubleArrayQuadrature_FFT_Real, 0, convertDoubleArrayQuadrature_IIR.length);
                            //*/

                            /*/ TEST IIR DATA
                            convertDoubleArrayInphase_IIR =  nativeApi.nativeIIRFilter(FullscreenActivity.convertDoubleArrayInphase);
                            System.arraycopy(convertDoubleArrayInphase_IIR, 0, convertDoubleArrayInphase_FFT_Real, 0, convertDoubleArrayInphase_IIR.length);
                            convertDoubleArrayQuadrature_IIR =  nativeApi.nativeIIRFilter(FullscreenActivity.convertDoubleArrayQuadrature);
                            System.arraycopy(convertDoubleArrayQuadrature_IIR, 0, convertDoubleArrayQuadrature_FFT_Real, 0, convertDoubleArrayQuadrature_IIR.length);
                            //*/

                            //FFT & FFTShift
                            convertDoubleArrayInphase_FFT = nativeApi.nativeFFT(-1,8, convertDoubleArrayInphase_FFT_Real, convertDoubleArrayInphase_FFT_Imag);
                            convertDoubleArrayQuadrature_FFT = nativeApi.nativeFFT(-1,8, convertDoubleArrayQuadrature_FFT_Real, convertDoubleArrayQuadrature_FFT_Imag);

                            //Magnitude
                            resultMagnitude = nativeApi.nativeFFTMagni(convertDoubleArrayInphase_FFT[0], convertDoubleArrayInphase_FFT[1], convertDoubleArrayQuadrature_FFT[0], convertDoubleArrayQuadrature_FFT[1], 256);
                            resultMagnitude = nativeApi.nativeMagShift(resultMagnitude,256);

                            resultDopplerScanline = nativeApi.nativeDopplerImaging(resultMagnitude);
                            //FullscreenImaging.arrayDmodeSaveData[defaultBulkCounter%128] = nativeApi.nativeDopplerImaging(resultMagnitude);


                            if(defaultBulkCounter < 125){
                                for(int k=0; k < 256; k++) {
                                    Dlog.i(String.format("CONVERT / %d / %d / %d", defaultBulkCounter, k, resultDopplerScanline[k]));
                                }
                            }

                        }

                        defaultBulkCounter++;
                        //*/
                    }

                    /* Sample Code /
                    int arrayStartCounter = (defaultBulkCounter - 1);
                    if(ReadBulkStartTrigger && arrayStartCounter!= -1) {
                        Dlog.i(String.format("Trigger data : %d", arrayStartCounter*readSize));
                        System.arraycopy(readBuffer,0, bufferArrayMulti, arrayStartCounter*readSize, defaultBITDataSize);
                        Dlog.i(String.format("End data : %d", bufferArrayMulti.length));
                    }

                    if(defaultBulkCounter % BULK_OF_FRAME == 0){
                        defaultFrameCounter++;
                        //defaultBulkCounter = 0;
                    }

                    if(defaultFrameCounter > FRAME_NUMBER ){
                        FullscreenImaging.arrayIntData = DeviceHandler.registerConvert(bufferArrayMulti);
                        Dlog.i(String.format("registerConvert : %d", FullscreenImaging.arrayIntData.length));
                        defaultFrameCounter = 1;
                    }
                    defaultBulkCounter++;
                    //*/
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
