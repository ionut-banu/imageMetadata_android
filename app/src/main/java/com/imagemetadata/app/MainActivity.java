package com.imagemetadata.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 2;

    String path = "/storage/emulated/0/temp/picture3.jpg";
    private ImageView imageView;
    private TextView textView;
    private EditText input;
    private File pictureFile;
    private Uri mCapturedImageURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);


        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.view_metadata_GPS);
        input = (EditText) findViewById(R.id.metadata_input);

        final Button btnTakePicture = (Button)findViewById(R.id.button_take_picture);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                path = "/storage/emulated/0/temp/picture3.jpg";
                pictureFile = new File(path);
                mCapturedImageURI = Uri.fromFile(pictureFile);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                startActivityForResult(intent, 1);
            }
        });

        final Button btnRefresh = (Button)findViewById(R.id.button_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(input.getText().length()>0) {
                    String text = input.getText().toString();
                    Log.d("ACTION", "Refresh pressed");
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(path);

                        exif.setAttribute("Make", text);
                        exif.setAttribute("Model", text);

                        //exif.setAttribute("FNumber", text);
                        exif.setAttribute("DateTime", text);
                        exif.setAttribute("DateTimeDigitized", text);
                        /*exif.setAttribute("ExposureTime", "41");
                        exif.setAttribute("Flash", "1000");
                        exif.setAttribute("FocalLength", "42/1");
                        exif.setAttribute("GPSAltitude", "43/1");
                        exif.setAttribute("GPSAltitudeRef", "44");*/
                        exif.setAttribute("GPSDateStamp", text);
                        exif.setAttribute("GPSLongitudeRef", text);
                        exif.setAttribute("GPSLatitudeRef", text);
                        exif.setAttribute("GPSProcessingMethod", text);
                        exif.setAttribute("GPSTimeStamp", text);
                        exif.setAttribute("SubSecTime", text);

                        exif.saveAttributes();
                        textView.setText("Attributes modified");


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else textView.setText("No input found!");

            }
        });


        /*final Button btnSet = (Button)findViewById(R.id.button_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("ACTION", "Set pressed");
                if(input.getText().length() != 0)
                    setImageMetadata("Make", input.getText().toString());
                else
                    textView.setText("No new metadata provided!");
            }
        });*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(this, "camera permission granted", 400).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE);
                } else {

                    //Toast.makeText(this, "camera permission denied", 400).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(this, "write permission granted", Toast.LENGTH_LONG).show();
                } else {

                    //Toast.makeText(this, "write permission denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE);
                }
                return;
            }


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){

            showInfo();
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setImageMetadata(String attribute, String newMetadata){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
            exif.setAttribute(attribute, newMetadata);
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getImageMetadata(String attribute){
        String mString = null;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
            mString = exif.getAttribute(attribute);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mString;
    }

    public void showInfo() {

        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                if (getImageMetadata("GPSLatitude") != null && getImageMetadata("GPSLongitude") != null)
                    textView.setText("Metadata: " + getImageMetadata("Make") + " -- " +
                            "GPS: " + getImageMetadata("GPSLatitude") + " :: " + getImageMetadata("GPSLongitude"));
                else
                    textView.setText("Metadata: " + getImageMetadata("Make"));

            }else textView.setText("Picture doesn't exist!");

        }else textView.setText("No path provided!");
    }


}
