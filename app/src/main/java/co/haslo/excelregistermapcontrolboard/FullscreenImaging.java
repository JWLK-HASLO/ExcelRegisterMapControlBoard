package co.haslo.excelregistermapcontrolboard;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer;
import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceHandler;
import co.haslo.excelregistermapcontrolboard.util.Dlog;

import static co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer.bufferArrayMulti;
import static co.haslo.excelregistermapcontrolboard.util.InterfaceUtil.showToast;

public class FullscreenImaging {
    private AppCompatActivity appCompatActivity;
    DeviceHandler mDeviceHandler ;
    static int frameSize = 1024*32;
    static int frameNumber = 1;
    public static int[] arrayIntData = new int[frameSize * frameNumber];
    public static int[] arraySaveData = new int[frameSize * frameNumber];
    public static String[] arraySaveBuffer = new String[4096*8];
    TextView timerView;
    Button scanControlImaging;
    Button timerControlButton;
    Button timerClearButton;
    Button wideViewButton;

    public static Boolean frameImagingTrigger = false;
    Thread timeThread = null;
    Boolean isRunning = true;

    ImageView bitmapImage;
    Bitmap bmp;
    Boolean wideViewTrigger = true;
    Boolean setLoadTrigger = false;

    int colorAccent;
    int colorWhite;
    int colorWhiteDark;
    int colorPrimary;
    int colorVioletR;
    int viewData = 0;



    FullscreenImaging(AppCompatActivity appCompatActivity, DeviceHandler deviceHandler) {
        this.appCompatActivity = appCompatActivity;
        mDeviceHandler = deviceHandler;
    }

    void initialize() {
        Dlog.d("Image Activity Ready");
        getColorMaker();
        loadDataImaging();
        setXmlComponent();
        setButtonControl();
    }

    void getColorMaker() {
        colorAccent = ContextCompat.getColor(appCompatActivity, R.color.colorAccent);
        colorWhite = ContextCompat.getColor(appCompatActivity, R.color.colorWhite);
        colorWhiteDark = ContextCompat.getColor(appCompatActivity, R.color.colorWhiteDark);
        colorPrimary = ContextCompat.getColor(appCompatActivity, R.color.colorPrimary);
        colorVioletR = ContextCompat.getColor(appCompatActivity, R.color.colorVioletR);
    }

    void setXmlComponent() {
        timerView = appCompatActivity.findViewById(R.id.timer_view);
        scanControlImaging = appCompatActivity.findViewById(R.id.button_scan_imaging);
        timerControlButton = appCompatActivity.findViewById(R.id.button_timer_control);
        timerClearButton = appCompatActivity.findViewById(R.id.button_timer_clear);
        wideViewButton = appCompatActivity.findViewById(R.id.button_wide_view);
        bitmapImage = appCompatActivity.findViewById(R.id.data_image);
    }

    private void loadDataImaging() {
        bmp = Bitmap.createBitmap(32, 1024, Bitmap.Config.ARGB_8888);

    }

    void setButtonControl() {
        scanControlImaging.setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                bmp = Bitmap.createBitmap(32, 1024, Bitmap.Config.ARGB_8888);

                if(!isRunning) isRunning = true;
                timeThread = new Thread(new timeThread());
                timeThread.start();
                scanControlImaging.setEnabled(false);
                scanControlImaging.setTextColor(colorWhiteDark);
                scanControlImaging.setBackgroundColor(colorPrimary);
                timerControlButton.setText("PAUSE");
                timerControlButton.setEnabled(true);
                timerControlButton.setTextColor(colorWhite);
                timerClearButton.setEnabled(true);
                timerClearButton.setTextColor(colorWhite);

                wideViewButton.setEnabled(true);
                wideViewButton.setTextColor(colorWhite);


                /*Register Test Start CMD */
                DeviceDataTransfer.defaultBulkCounter = 0;
                DeviceDataTransfer.ReadBulkStartTrigger = true;
                showToast(appCompatActivity,"Test Start Button Click");
//                mDeviceHandler.run();
                mDeviceHandler.registerHandlerStart();

            }
        });

        timerControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if (isRunning) {
                    timerControlButton.setText("PAUSE");
                } else {
                    arraySaveData = arrayIntData;
//                    arraySaveBuffer = convertStringArray;
                    timerControlButton.setText("START");
                }
            }
        });

        timerClearButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                bitmapImage.setImageBitmap(null);

                scanControlImaging.setEnabled(true);
                scanControlImaging.setTextColor(colorWhite);
                scanControlImaging.setBackgroundColor(colorVioletR);
                timerControlButton.setEnabled(false);
                timerControlButton.setTextColor(colorWhiteDark);
                timerClearButton.setEnabled(false);
                timerClearButton.setTextColor(colorWhiteDark);
                timeThread.interrupt();
                timerView.setText("00:00:00:0");


                wideViewButton.setEnabled(false);
                wideViewButton.setTextColor(colorWhiteDark);

                arrayIntData = new int[1024*32];

                /*Register Test Clear*/
                DeviceDataTransfer.defaultBulkCounter = 0;
                DeviceDataTransfer.defaultFrameCounter = 0;
                DeviceDataTransfer.ReadBulkStartTrigger = false;
                showToast(appCompatActivity,"Test Reset Button Click");
                mDeviceHandler.reset();
            }
        });

        wideViewButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(!wideViewTrigger){
                    wideViewButton.setText("NOW : Wide View");
                    wideViewTrigger = true;
                } else {
                    wideViewButton.setText("NOW : Original View");
                    wideViewTrigger = false;
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1 % 10;
            int sec = (msg.arg1 / 10) % 60;
            int min = (msg.arg1 / 10) / 60;
            int hour = (msg.arg1 / 10) / 360;
            final int frameCounter = msg.arg2;
            //1초 세기
//            int sec = msg.arg1 % 60;
//            int min = (msg.arg1/60) % 60;
//            int hour = (msg.arg1) / 3600;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

//            @SuppressLint("DefaultLocale") final String result = String.format("%02d:%02d:%02d", hour, min, sec);
            @SuppressLint("DefaultLocale") final String result = String.format("%02d:%02d:%02d:%01d", hour, min, sec, mSec);
//            if (result.equals("00:01:15:00")) {
//                Toast.makeText(FullscreenActivity.this, "1분 15초가 지났습니다.", Toast.LENGTH_SHORT).show();
//            }

            appCompatActivity.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    timerView.setText(result);
//                    bitmapImage.setImageBitmap(null);
                    setBitmapData(arrayIntData, frameCounter % frameNumber);
                    if(wideViewTrigger && setLoadTrigger){
                        bitmapImage.setImageBitmap(getResizedBitmap(bmp,640,480));
                        setLoadTrigger = false;
                    } else if (!wideViewTrigger && setLoadTrigger) {
                        bitmapImage.setImageBitmap(bmp);
                        setLoadTrigger = false;
                    }
                }
            });
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;
            int frameCounter = 0;
            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
//                    msg.arg2 = frameCounter++;
                    msg.arg2 = 0;
                    handler.sendMessage(msg);
                    //arrayIntData = DeviceHandler.registerConvert(bufferArrayMulti);
                    try {
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        appCompatActivity.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(appCompatActivity, "NEW TIMER HAS BEEN LAUNCHED", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postScale(480/4/32f, 480/1024f); // 32*1024

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, true);
//        bm.recycle();
        return resizedBitmap;
    }


    void setBitmapData(int[] data, int frameCounter) {
        int col = 32;
        int row = 1024;
        int dataNumber = 0;
        int dataInfo = 0;

        for(int x = 0; x < col; x++){
            for(int y = 0; y < row; y++){
                dataNumber = (32768*frameCounter) + (1024 * x) + y;
                dataInfo = data[dataNumber];
                //Need to filter Function
                // dataInfo = echoFilter(data[dataNumber]);
                bmp.setPixel(x, y, packRGB(dataInfo, dataInfo, dataInfo) );
            }
        }
        setLoadTrigger = true;

//        for(int x = 0; x < col; x++){
//            for(int y = 0; y < row; y++){
//                bmp.setPixel(x, y, packRGB(frameCounter*x*y, frameCounter*x*y, frameCounter+x*y));
//            }
//        }
//
//        if(viewData>255){
//            viewData = 0;
//        } else {
//            viewData++;
//        }

    }

    private static int packRGB(int r, int g, int b) {
        return 0xff000000 | r << 16 | g << 8 | b;
    }

}
