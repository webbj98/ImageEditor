package com.example.webbj.imageeditor;

import android.content.ContentUris;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;


public class Crop extends AppCompatActivity{

    ImageView imageView;
    //captured picture uri
    Uri picUri;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    String TAG = "DebugMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = (ImageView)findViewById(R.id.picture);

        Log.i(TAG, "oncreate");
        setContentView(R.layout.activity_crop);
        Log.i(TAG, "set content view");
        imageView = (ImageView)findViewById(R.id.picture);
        //Bundle extras = getIntent().getExtras();
        Log.i(TAG, "here1");
        Bundle data = getIntent().getExtras(); // get extra info from another Intent
        if(data == null){
            //if nothing is passed

            return;
        }
        Log.i(TAG, "here2");
        //convert the string uri back to a uri object
        Log.i(TAG, data.getString("crop image"));
        picUri = Uri.parse(getIntent().getStringExtra("crop image"));
        if (picUri == null){
            return;
        }
        Log.i(TAG, picUri.toString());
        //sets imageView to display uri
        imageView.setImageURI(picUri);
        Log.i(TAG, "here4");
        //delete the temporary image in the internal storage
        deleteUri(this, picUri);


        final Bitmap bitmapImage = imageViewtoBitmap(imageView);

    }
  

    public void onClick(View v) {
        if (v.getId() == R.id.crop_button) {
            try {
                performCrop(picUri);
            }

            catch(ActivityNotFoundException anfe){
                //display an error message
                String errorMessage = "Whoops - your device doesn't support cropping!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //user is returning from cropping the image
        if(requestCode == PIC_CROP){
            //get the returned data
            Bundle extras = data.getExtras();
            //get the cropped bitmap
            Bitmap thePic = extras.getParcelable("data");

            //retrieve a reference to the ImageView
            ImageView picView = (ImageView)findViewById(R.id.picture);
            //display the returned cropped image
            picView.setImageBitmap(thePic);
        }
    }

    private void performCrop(Uri pictureURI){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(pictureURI, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public static Bitmap imageViewtoBitmap(ImageView imageView){
        /*
         * Args:
         *     imageview: an imageview
         * Returns:
         *     the bitmap of the imageview
         */

        //convert imageview to bitmap
        imageView.setDrawingCacheEnabled(true);

        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // hardcoded so i always know how big image is
        imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());

        imageView.buildDrawingCache(true);
        Bitmap bitmapImage = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false); // clear drawing cache

        return bitmapImage;


    }

    public static void deleteUri(Context context, Uri uri){
        /**
         * Args:
         *      The uri of the file
         *
         * Deletes the uri and the file associated with the uri. Most useful for deleting pictures
         * in the internal storage
         */

        long mediaId = ContentUris.parseId(uri);
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri itemUri = ContentUris.withAppendedId(contentUri, mediaId);

        /* int rows =*/ context.getContentResolver().delete(itemUri, null, null);
    }
}
