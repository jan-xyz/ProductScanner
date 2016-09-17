package com.aaaaaab.shopnavigator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.scandit.barcodepicker.*;
import com.scandit.recognition.Barcode;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScanditLicense.setAppKey(sScanditSdkAppKey);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }

}
