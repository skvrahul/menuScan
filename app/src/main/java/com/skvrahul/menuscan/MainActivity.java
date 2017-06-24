package com.skvrahul.menuscan;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    int REQUEST_CODE = 99;
    int preferences = ScanConstants.OPEN_CAMERA;
    Button scanButton;
    ImageView imageView;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = (Button)findViewById(R.id.scanButton);
        imageView = (ImageView)findViewById(R.id.scannedImageView);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(),ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preferences);
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            97);
                }else{
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                getContentResolver().delete(uri, null, null);
                imageView.setImageBitmap(bitmap);
                if(!textRecognizer.isOperational()) {
                    Log.w("textRecognize", "Detector dependencies are not yet available.");

                    // Check for low storage.  If there is low storage, the native library will not be
                    // downloaded, so detection will not become operational.
                    IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                    boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                    if (hasLowStorage) {
                        Toast.makeText(this, "Low Storage", Toast.LENGTH_LONG).show();
                        Log.w("textRecognize", "Low Storage");
                    }
                }else{
                    Frame imageFrame = new Frame.Builder()
                            .setBitmap(bitmap)
                            .build();
                    Log.i("textScan", "start");
                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                    Log.i("textScan", "end");
                    Toast.makeText(getBaseContext(), ""+textBlocks.size(), Toast.LENGTH_LONG).show();
                    Log.i("textScan", textBlocks.size()+"");
                    for (int i = 0; i < textBlocks.size(); i++) {
                        TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                        Toast.makeText(getBaseContext(), textBlock.getValue(),Toast.LENGTH_LONG).show();

                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 97) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                startActivityForResult(intent, REQUEST_CODE);
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }
}
