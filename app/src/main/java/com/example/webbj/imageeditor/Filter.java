package com.example.webbj.imageeditor;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class Filter extends AppCompatActivity {

    ImageView mainImageView;
    Button invertButton;
    Button originalButton;
    Button greyButton;
    Button applyButton;

    private static final String TAG = "DebugMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Log.i(TAG, "here11");
        mainImageView = (ImageView)findViewById(R.id.picture);
        mainImageView = (ImageView)findViewById(R.id.picture);
        invertButton = (Button)findViewById(R.id.invertButton);
        applyButton = (Button)findViewById(R.id.applyButton);
        originalButton = (Button)findViewById(R.id.originalButton);
        greyButton = (Button)findViewById(R.id.monoButton);
        applyButton = (Button)findViewById(R.id.applyButton);

        Bundle data = getIntent().getExtras(); // get extra info from another Intent
        if(data == null){
            //if nothing is passed
            return;
        }

        //convert the string uri back to a uri object
        Log.i(TAG, data.getString("selected image"));
        Uri imageUri = Uri.parse(getIntent().getStringExtra("selected image"));
        Log.i(TAG, imageUri.toString());
        //sets imageView to display uri
        mainImageView.setImageURI(imageUri);

        //delete the temporary image in the internal storage
        MainActivity.deleteUri(this, imageUri);

        final Bitmap bitmapImage = imageViewtoBitmap(mainImageView);
        Log.i(TAG, Integer.toString(bitmapImage.getHeight()) );
        Log.i(TAG, Integer.toString(bitmapImage.getWidth()));


        //Invert Button Handler
        invertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "inverting");
////                mainImageView.setImageBitmap(invertImage(bitmapImage));
////                Bitmap invertedImage = imageViewtoBitmap(mainImageView);
////                saveImage(getContentResolver(), invertedImage );
//
//                // Creates a new bitmap object to represent the inverted image
//                Bitmap invertedImage;
//                invertedImage = invertImage(bitmapImage);
//                mainImageView.setImageBitmap(invertedImage);
////
////                //Creates a snackbar to indicate inversion
////                Snackbar.make(v, "Inverted", Snackbar.LENGTH_SHORT)
////                        .show();
                Log.i(TAG, "invert");
                Bitmap bitmapCopy = bitmapImage.copy(bitmapImage.getConfig(), true);
                Log.i(TAG, String.valueOf(bitmapCopy==bitmapImage));
                mainImageView.setColorFilter(getInvertFilter());
                Log.i(TAG, "mono done");
                Log.i(TAG,"help me");

            }
        });

        greyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "mono");
                Bitmap bitmapCopy = bitmapImage.copy(bitmapImage.getConfig(), true);
                Log.i(TAG, String.valueOf(bitmapCopy==bitmapImage));
                mainImageView.setColorFilter(getGreyscaleFilter());
                Log.i(TAG, "mono done");
                Log.i(TAG,"help me");

            }
        });

        //Original Button Handler
        originalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, String.valueOf(bitmapImage==imageViewtoBitmap(mainImageView)));
                mainImageView.setImageBitmap(bitmapImage);
                mainImageView.clearColorFilter();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.saveImage(getContentResolver(), imageViewtoBitmap(mainImageView));

            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event));
    }

//    public static Bitmap invertImage(Bitmap image){
//        /*
//         * Args:
//         *     image: a bitmap image
//         * Returns:
//         *      an inverted image
//         */
//        Bitmap bitmapCopy = image.copy(image.getConfig(), true);
//
//        int A, R, G, B; //alpha, red, green, blue
//        int height = bitmapCopy.getHeight();
//        int width = bitmapCopy.getWidth();
//        int size = bitmapCopy.getHeight() * image.getWidth();
//
//        //build an array where length is number of pixels in the bitmap
//        int[] bitmapArray = new int[size];
//
//        //stores the argb values in the array to be manipulated by the color class
//        bitmapCopy.getPixels(bitmapArray, 0, width, 0,0, width, height);
//
//        //invert each entry in the array
//        for (int i = 0; i< size; i++){
//
//            A = Color.alpha(bitmapArray[i]);
//            R = 255 -  Color.red(bitmapArray[i]);
//            G = 255 - Color.green(bitmapArray[i]);
//            B = 255 - Color.blue(bitmapArray[i]);
//            bitmapArray[i] = Color.argb(A, R, G,B);
//
//        }
//
//        //set the bitmap to the inverted array
//        bitmapCopy.setPixels(bitmapArray, 0, width, 0, 0, width, height);
//
//        return bitmapCopy;
//    }


    public static ColorMatrixColorFilter getInvertFilter() {

        float[] mx = new float[]{
                -1.0f, 0, 0, 0, 255, //red
                0, -1.0f, 0, 0, 255, //green
                0, 0, -1.0f, 0, 255, //blue
                0, 0, 0, 1.0f, 0 //alpha
        };

        return new ColorMatrixColorFilter(mx);
    }

    public static ColorMatrixColorFilter getGreyscaleFilter() {

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        return new ColorMatrixColorFilter(matrix);
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




    /**
     * Args:
     *     uri: an image's uri
     * Returns:
     *      The file path of that image
     */
    public static File uriToFile(Uri uri){

        return new File(uri.getPath());


    }



}

