package com.example.makis.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    TextView informationTextView;
    ImageView imageView;
    int tag=10;
    private static final int REQUEST_CODE_ASK_PERMISSIONS =123;
    private static final int PICK_IMAGE=100;
    Button buttonLoad;
    Uri imageURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.imgView);
        buttonLoad =(Button)findViewById(R.id.buttonLoad);
        informationTextView=(TextView)findViewById(R.id.inf_textView);

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGallery();
            }
        });
    }



    public void takePicture(View view) {
        CheckUserPermissions();

    }

    private void loadGallery(){
        Intent gallery=new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery,PICK_IMAGE);

    }

    void CheckUserPermissions(){
        if(Build.VERSION.SDK_INT >=23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
                }
            }
        
        TakePicture();
    }

    private void TakePicture() {
        Intent camera=new Intent();
        camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,tag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==tag && resultCode == RESULT_OK){
            Bundle b=data.getExtras();
            Bitmap img=(Bitmap)b.get("data");
            imageView.setImageBitmap(img);
        }

        if (requestCode==PICK_IMAGE && resultCode == RESULT_OK){

            // Get the dimensions of the View
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            try {
                imageURI = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageURI);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(String.valueOf(imageURI), bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                informationTextView.setText("targetW:"+String.valueOf(targetW)+"\ntargetH:"+String.valueOf(targetH)+"\nphotoW:"+String.valueOf(photoW)+"\nphotoH:"+String.valueOf(photoH));
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }


        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    TakePicture();
                }
                else{
                    Toast.makeText(this,"cannot store file", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
