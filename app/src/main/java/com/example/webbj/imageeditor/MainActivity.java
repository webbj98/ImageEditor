package com.example.webbj.imageeditor;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.data;
import static android.R.attr.slideEdge;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    //Button getImage;
    ImageButton getImage;
    ImageButton cameraButton;
    ImageButton filterButton;

    private static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri imageUri;
    Uri hiRezImageUri;

    private static final String TAG = "DebugMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
        //checks if we have permission to write to external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "poo");

            //asks whether to display the reason for the request
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //shows reason

            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        imageView = (ImageView)findViewById(R.id.imageView);
        getImage = (ImageButton)findViewById(R.id.galleryButton);
        cameraButton = (ImageButton)findViewById(R.id.cameraButton);
        filterButton = (ImageButton)findViewById(R.id.filterButton);

        //Disasble the button if user has not camera
        if(!hasCamera())
            cameraButton.setEnabled(false);


        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //passes the image to the filter intent
                Intent i = new Intent(MainActivity.this, Filter.class);


                //convert imageview to bitmap
                imageView.setDrawingCacheEnabled(true);

                // Without it the view will have a dimension of 0,0 and the bitmap will be null
                imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                // hardcoded so i always know how big image is
                imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                if (imageView == null){
                    Log.i(TAG, "nothing");
                }

                imageView.buildDrawingCache(true);
                Bitmap selectedImage = Bitmap.createBitmap(imageView.getDrawingCache());
                imageView.setDrawingCacheEnabled(false); // clear drawing cache

                Uri imageUri = getImageUri(MainActivity.this, selectedImage);

                Log.i(TAG, imageUri.toString());
        //        byte[] byte);


                //pass the uri as a string
                i.putExtra("selected image", imageUri.toString());
                i.putExtra("hirez image", hiRezImageUri.toString());
                startActivity(i);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i(TAG, "pldddds");
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted and carry out the request
                    Log.i(TAG, "poo");
                } else {

                    // permission denied
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public byte[] bitmaptoByteArray(Bitmap image){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

//    public void openFilterActivity(View v){
//
//        //passes the image to the filter intent
//        Intent i = new Intent(this, Filter.class);
//
//
//        //convert imageview to bitmap
//        imageView.setDrawingCacheEnabled(true);
//
//        // Without it the view will have a dimension of 0,0 and the bitmap will be null
//        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        // hardcoded so i always know how big image is
//        imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
//        if (imageView == null){
//            Log.i(TAG, "nothing");
//        }
//
//        imageView.buildDrawingCache(true);
//        Bitmap selectedImage = Bitmap.createBitmap(imageView.getDrawingCache());
//        imageView.setDrawingCacheEnabled(false); // clear drawing cache
//
//        Uri imageUri = getImageUri(this, selectedImage);
//
//
//        Log.i(TAG, imageUri.toString());
////        byte[] byte);
//
//
//        //pass the uri as a string
//        i.putExtra("selected image", imageUri.toString());
//        startActivity(i);
//    }

    public void openCropActivity(View v){
        Log.i(TAG, "openCropActivity");

        //creates intent to pass to crop activity
        Intent i = new Intent(this, Crop.class);

        //convert imageview to bitmap
        imageView.setDrawingCacheEnabled(true);

        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // hardcoded so i always know how big image is
        imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());

        imageView.buildDrawingCache(true);
        Bitmap selectedImage = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false); // clear drawing cache

        Uri imageUri = getImageUri(this, selectedImage);

        //pass the uri as a string
        i.putExtra("crop image", imageUri.toString());
        startActivity(i);
        Log.i(TAG, "finished opening crop activity");


    }

    public void openRGBActivity(View v){
        //passes the image to the filter intent
        Intent i = new Intent(this, RGBEditor.class);

        //convert imageview to bitmap
        imageView.setDrawingCacheEnabled(true);

        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // hardcoded so i always know how big image is
        imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
        if (imageView == null){
            Log.i(TAG, "nothing");
        }

        imageView.buildDrawingCache(true);
        Bitmap selectedImage = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false); // clear drawing cache

        Uri imageUri = getImageUri(this, selectedImage);


        Log.i(TAG, imageUri.toString());
//        byte[] byte);


        //pass the uri as a string
        i.putExtra("selected image", imageUri.toString());
        startActivity(i);

    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, requestCode, data);
        imageUri = data.getData();

        Log.i(TAG, "request code: " + Integer.toString(requestCode));
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageView.setImageURI(imageUri);
            try{
                //makes sure that the picture is rotated properly
                Log.i(TAG, "rotated");
                Bitmap hiRezBitmap = handleSamplingAndRotationBitmap(this, imageUri);

                imageView.setImageBitmap(Bitmap.createScaledBitmap(hiRezBitmap, (int) (hiRezBitmap.getWidth()/2.0),
                        (int)(hiRezBitmap.getHeight()/1.7), false));
                //imageView.setImageBitmap(handleSamplingAndRotationBitmap(this, imageUri));
                Bitmap scaledimage = Filter.imageViewtoBitmap(imageView);

                //store the uri of the  higher rez, scaled down version of the bitmap
                hiRezImageUri = getImageUri(this, hiRezBitmap);

                //Filter.saveImage(this.getContentResolver(), hiRezBitmap);
            }
            catch (Exception e){
            }
        }
        else if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            Log.i(TAG, "capture");
            imageUri = getImageUri(this, image);
            //imageView.setImageURI(imageUri);
            try{
                //makes sure that the picture is rotated properly
                Log.i(TAG, "rotated");
                imageView.setImageBitmap(handleSamplingAndRotationBitmap(this, imageUri));

            }
            catch (Exception e){
            }
            //imageView.setImageBitmap(image);
        }
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        /**
         * Fixes issues with image rotation if they exist
         *
         * Inputs:
         *      context: the activities current context
         *      selectedImage: The image that is being fixed
         *      IOException
         *
         * Returns:
         *      A bitmap image that has been rotated correctly
         */
        // determine the max dimensions of the picture
        int MAX_HEIGHT = 2048;
        int MAX_WIDTH = 2048;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateBitmap(context, selectedImage, img);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {

        /**
         * This method will return a sample size that will allow the program to use less memory when
         * loading the image
         *
         * Inputs:
         *      options: An options object with out* params already populated (run through a decode*
         *                  method with inJustDecodeBounds==true
         *      reqWidth: The requested width of the resulting bitmap
         *      reqHeight: The requested height of the resulting bitmap
         * Returns:
         *      the value to be used for inSampleSpace
         */
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static Bitmap rotateBitmap(Context context, Uri photoUri, Bitmap bitmap) {
        int orientation = getOrientation(context, photoUri);
        if (orientation <= 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return bitmap;
    }

    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        cursor = null;
        Log.i(TAG, "orientation: " + Integer.toString(orientation));
        return orientation;
    }

//    private static Bitmap rotateImage(Bitmap img, int degree) {
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
//        img.recycle();
//        return rotatedImg;
//    }

    public Uri getImageUri(Context Context, Bitmap image) {

        /**
         *
         * Args:
         *     Context: the context of the activity
         *     image: a bitmap
         *
         * Returns:
         *     The URI of the bitmap
         */
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(Context.getContentResolver(), image, "Title", null);
        Log.i(TAG, path);

        return Uri.parse(path);
    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Take a picture and pass results to onActivity Result
        Log.i(TAG, "pls");
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

}
