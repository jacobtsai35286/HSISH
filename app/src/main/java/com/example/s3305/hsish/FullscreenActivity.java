package com.example.s3305.hsish;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;

import com.flir.flironesdk.*;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;

import static com.flir.flironesdk.RenderedImage.ImageType.ThermalRadiometricKelvinImage;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements Device.Delegate , FrameProcessor.Delegate {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private TextView showTemp ;
    private FrameProcessor frameProcessor;
    private ImageView t = null;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frameProcessor = new FrameProcessor(this, this, EnumSet.of(RenderedImage.ImageType.BlendedMSXRGBA8888Image));
        setContentView(R.layout.activity_fullscreen);
 //       t = (ImageView)findViewById(R.id.imageView2);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        showTemp = findViewById(R.id.tv_max);

//        showTemp.setText(t.getLeft());


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  toggle();
                t = (ImageView)findViewById(R.id.imageView);
                int left = t.getLeft();
                int right = t.getRight();
                int top = t.getTop();
                int bottom = t.getBottom();
                String l = String.valueOf(left);
                String m = String.valueOf(right);
                String n = String.valueOf(top);
                String o = String.valueOf(bottom);
                //showTemp.setText("Left:"+l+"\nRight:"+m+"\nTop:"+n+"\nBottom:"+o);
                showTemp.setText("High Tempature : 62.3℃\nLow Tempature : 24.4 ℃");
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
//        test();
    }
    @SuppressLint("ResourceType")
    public void test(){
        t = (ImageView)findViewById(R.id.imageView);
        int left = t.getLeft();
        int right = t.getRight();
        int top = t.getTop();
        int bottom = t.getBottom();
        String l = String.valueOf(left);
        String m = String.valueOf(right);
        String n = String.valueOf(top);
        String o = String.valueOf(bottom);
        showTemp.setText("kkokoko"+"kkoko");
        //showTemp.setText("High Tempature :38.6 ℃\n Low Tempature :26.8 ℃");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    Device flirDevice;

    @Override
    protected void onResume() {
        super.onResume();
        Device.startDiscovery(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Device.stopDiscovery();
    }

    @Override
    public void onTuningStateChanged(Device.TuningState tuningState) {

    }

    @Override
    public void onAutomaticTuningChanged(boolean b) {

    }

    @Override
    public void onDeviceConnected(Device device) {
        flirDevice = device;
        device.startFrameStream(new Device.StreamDelegate() {
            @Override
            public void onFrameReceived(Frame frame) {
                frameProcessor.processFrame(frame);
            }
        });
    }

    @Override
    public void onDeviceDisconnected(Device device) {

    }

    private Bitmap thermalBitmap = null;
    @Override
    public void onFrameProcessed(final RenderedImage renderedImage) {
        final Bitmap imageBitmap = Bitmap.createBitmap(renderedImage.width(),renderedImage.height(),Bitmap.Config.ARGB_8888);
        imageBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(renderedImage.pixelData()));
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(imageBitmap);
                int n = imageView.getBaseline();

                if(renderedImage.imageType() == renderedImage.imageType().ThermalRadiometricKelvinImage){
                    short [] shortPixels = new short[renderedImage.pixelData().length/2];
                    byte [] argbPixels = new byte[renderedImage.width() * renderedImage.height() *4];
                    ImageHelp imageHelp = null ;
                    File file = null;
                    ImageInfo info = imageHelp.getInfoFromName(file.getName());
                    int dot = info.getAverTemp().indexOf(".") + 2;

                    showTemp.setText("最高温度：" + info.getMaxTemp() + "℃  平均温度：" + info.getAverTemp().substring(0, dot) + "℃");

                    /**
                                        * Here is a simple example of showing color for 9 bands of tempuratures:
                                        *  Below 0 celceus is black
                                        * 0-10C is dark blue
                                        * 10-20C is light blue
                                        * 20-36C is dark red
                                        * 36-50C is yellow
                                        * 50-80C is orange
                                        * Above 80C is white
                                        */
                    ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);
                    final byte aPixValue = (byte) 255;
                    for(int p = 0 ; p < shortPixels.length ; p++){
                        int destP = p*4;
                        int tempInC = (shortPixels[p]-27315/100);
                        byte rPixValue;
                        byte gPixValue = 0;
                        byte bPixValue;
                        if(tempInC < 0){
                            rPixValue = gPixValue = bPixValue =0;
                        }else if(tempInC < 10){
                            rPixValue = gPixValue = 0;
                            bPixValue = 127;
                        }else if(tempInC < 20){
                            rPixValue = gPixValue = 0;
                            bPixValue = (byte) 255;
                        }else if(tempInC < 36){
                            rPixValue = bPixValue = 0;
                            gPixValue = (byte)160;
                        }else  if(tempInC < 40){
                            bPixValue = gPixValue = 0;
                            rPixValue = 127;
                        }else if(tempInC < 50){
                            bPixValue = gPixValue = 0;
                            rPixValue = (byte) 255;
                        }else if(tempInC < 60){
                            rPixValue = (byte) 255;
                            gPixValue = (byte) 166;
                            bPixValue = 0;
                        }else if(tempInC < 100){
                            rPixValue = gPixValue = (byte) 255;
                            bPixValue = 0;
                        }else{
                            bPixValue = rPixValue = gPixValue = (byte) 255;
                        }

                        argbPixels[destP + 3] = aPixValue;
                        argbPixels[destP] = rPixValue;
                        argbPixels[destP + 1] = gPixValue;
                        argbPixels[destP + 2] = bPixValue;
                    }
                    thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(argbPixels));
                }
            }
        });
    }
}
