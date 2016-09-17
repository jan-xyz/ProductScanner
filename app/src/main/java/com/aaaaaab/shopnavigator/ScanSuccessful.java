package com.aaaaaab.shopnavigator;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.BarcodePickerActivity;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;

public class ScanSuccessful extends AppCompatActivity {

    // Main element for barcodescanning
    private BarcodePicker mBarcodePicker;
    // camara permission is number 0
    private final int CAMERA_PERMISSION_REQUEST = 0;
    private final int REQUEST_BARCODE_PICKER_ACTIVITY = 55;
    private boolean mDeniedCameraAccess = false;
    // Scandit API Key
    public static final String sScanditSdkAppKey = "IbZ9GJnmSzrhvkNu0p0f5A25f9wg4SCnJPuk7QMoQPs";
    // Activity is paused
    private boolean mPaused = true;
    private Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_successful);

        ScanditLicense.setAppKey(sScanditSdkAppKey);

        launchPicker();
    }

    private void launchPicker(){
        Intent launchIntent = new Intent(ScanSuccessful.this,
                BarcodePickerActivity.class);
        launchIntent.putExtra("enabledSymbologies", new int[] {
                Barcode.SYMBOLOGY_EAN13,
                Barcode.SYMBOLOGY_QR
        });
        launchIntent.putExtra("restrictScanningArea", true);
        launchIntent.putExtra("scanningAreaHeight", 0.1f);
        startActivityForResult(launchIntent, REQUEST_BARCODE_PICKER_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_BARCODE_PICKER_ACTIVITY) return;
        String message = "ERRPR: No Code";
        if (data.getBooleanExtra("barcodeRecognized", false)) {
            message = data.getStringExtra("barcodeData");
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
