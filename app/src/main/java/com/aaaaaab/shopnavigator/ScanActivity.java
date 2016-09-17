package com.aaaaaab.shopnavigator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.scandit.barcodepicker.*;
import com.scandit.recognition.Barcode;
import com.scandit.recognition.SymbologySettings;

public class ScanActivity extends AppCompatActivity implements OnScanListener {

    // Main element for barcodescanning
    private BarcodePicker mBarcodePicker;
    // camara permission is number 0
    private final int CAMERA_PERMISSION_REQUEST = 0;
    private boolean mDeniedCameraAccess = false;
    // Scandit API Key
    public static final String sScanditSdkAppKey = "IbZ9GJnmSzrhvkNu0p0f5A25f9wg4SCnJPuk7QMoQPs";
    // Activity is paused
    private boolean mPaused = true;
    Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScanditLicense.setAppKey(sScanditSdkAppKey);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }

    protected void onPause() {
        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();
        mPaused = true;
        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissionsThenStartScanning() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (mDeniedCameraAccess == false) {
                // it's pretty clear for why the camera is required. We don't need to give a
                // detailed reason.
                this.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
            }

        } else {
            // we already have the permission
            mBarcodePicker.startScanning();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDeniedCameraAccess = false;
                if (!mPaused) {
                    mBarcodePicker.startScanning();
                }
            } else {
                mDeniedCameraAccess = true;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        // handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning();
        } else {
            // Once the activity is in the foreground again, restart scanning.
            mBarcodePicker.startScanning();
        }
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        // The scanning behavior of the barcode picker is configured through scan
        // settings. We start with empty scan settings and enable a very generous
        // set of symbologies. In your own apps, only enable the symbologies you
        // actually need.
        ScanSettings settings = ScanSettings.create();
        int[] symbologiesToEnable = new int[] {
                Barcode.SYMBOLOGY_EAN13,
                Barcode.SYMBOLOGY_EAN8,
                Barcode.SYMBOLOGY_UPCA,
                Barcode.SYMBOLOGY_DATA_MATRIX,
                Barcode.SYMBOLOGY_QR,
                Barcode.SYMBOLOGY_CODE39,
                Barcode.SYMBOLOGY_CODE128,
                Barcode.SYMBOLOGY_INTERLEAVED_2_OF_5,
                Barcode.SYMBOLOGY_UPCE
        };
        for (int sym : symbologiesToEnable) {
            settings.setSymbologyEnabled(sym, true);
        }


        // Some 1d barcode symbologies allow you to encode variable-length data. By default, the
        // Scandit BarcodeScanner SDK only scans barcodes in a certain length range. If your
        // application requires scanning of one of these symbologies, and the length is falling
        // outside the default range, you may need to adjust the "active symbol counts" for this
        // symbology. This is shown in the following few lines of code.

        SymbologySettings symSettings = settings.getSymbologySettings(Barcode.SYMBOLOGY_CODE39);
        short[] activeSymbolCounts = new short[] {
                7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
        };
        symSettings.setActiveSymbolCounts(activeSymbolCounts);
        // For details on defaults and how to calculate the symbol counts for each symbology, take
        // a look at http://docs.scandit.com/stable/c_api/symbologies.html.



        // prefer the back-facing camera, is there is any.
        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_BACK);


        // Some Android 2.3+ devices do not support rotated camera feeds. On these devices, the
        // barcode picker emulates portrait mode by rotating the scan UI.
        boolean emulatePortraitMode = !BarcodePicker.canRunPortraitPicker();
        if (emulatePortraitMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        BarcodePicker picker = new BarcodePicker(this, settings);

        setContentView(picker);
        mBarcodePicker = picker;

        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.setOnScanListener(this);

    }

    /**
     *  Called when a barcode has been decoded successfully.
     */
    public void didScan(ScanSession session) {
        String message = "";
        for (Barcode code : session.getNewlyRecognizedCodes()) {
            String data = code.getData();
            // truncate code to certain length
            String cleanData = data;
            if (data.length() > 30) {
                cleanData = data.substring(0, 25) + "[...]";
            }
            if (message.length() > 0) {
                message += "\n\n\n";
            }
            message += cleanData;
            message += "\n\n(" + code.getSymbologyName() + ")";
        }
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }


}
