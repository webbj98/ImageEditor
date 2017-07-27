package com.example.webbj.imageeditor;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class Filter extends AppCompatActivity {

    ImageView mainImageView;
    Button invertButton;
    private static final String TAG = "DebugMessage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Log.i(TAG, "here11");
        mainImageView = (ImageView)findViewById(R.id.mainImageView);
        invertButton = (Button)findViewById(R.id.invertButton);

        Bundle data = getIntent().getExtras(); // get extra info from anothe r Intent
        if(data == null){
            //if nothing is passed
            return;
        }

        //convert the string uri back to a uri object
        Log.i(TAG, data.getString("selected image"));
        Uri imageUri = Uri.parse(getIntent().getStringExtra("selected image"));
        mainImageView.setImageURI(imageUri);

        //delete the temporary image in the internal storage
        deleteUri(this, imageUri);


        final Bitmap bitmapImage = imageViewtoBitmap(mainImageView);
        Log.i(TAG, Integer.toString(bitmapImage.getHeight()) );
        Log.i(TAG, Integer.toString(bitmapImage.getWidth()));
        invertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "inverting");
                mainImageView.setImageBitmap(invertImage(bitmapImage));
                Bitmap invertedImage = imageViewtoBitmap(mainImageView);
                saveImage(getContentResolver(), invertedImage );

            }
        });
    }

    public static Bitmap invertImage(Bitmap image){
        /**
         * Args:
         *     image: a bitmap image
         * Returns:
         *      an inverted image
         */


        Bitmap copyImage = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), false);

        int A, R, G, B; //alpha, red, green, blue
        int height = copyImage.getHeight();
        int width =copyImage.getWidth();
        int size = copyImage.getHeight() * copyImage.getWidth();

        //build an array where length is number of pixels in the bitmap
        int[] bitmapArray = new int[size];

        //stores the argb values in the array to be manipulated by the color class
        copyImage.getPixels(bitmapArray, 0, width, 0,0, width, height);

        //invert each entry in the array
        for (int i = 0; i< size; i++){

            A = Color.alpha(bitmapArray[i]);
            R = 255 -  Color.red(bitmapArray[i]);
            G = 255 - Color.green(bitmapArray[i]);
            B = 255 - Color.blue(bitmapArray[i]);
            bitmapArray[i] = Color.argb(A, R, G,B);

        }

        //set the bitmap to the inverted array
        copyImage.setPixels(bitmapArray, 0, width, 0, 0, width, height);

        return copyImage;
    }

    public static Bitmap imageViewtoBitmap(ImageView imageView){
        /**
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
        if (imageView == null){
            Log.i(TAG, "nothing");
        }
        imageView.buildDrawingCache(true);
        Bitmap bitmapImage = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false); // clear drawing cache

        return bitmapImage;


    }

    public static void saveImage(ContentResolver contentResolver, Bitmap image){
        /**
         * Args:
         *     contentResolver: a contentResolver
         *     image: an image in bitmap
         *
         * Saves the image to the photos folder
         */
        MediaStore.Images.Media.insertImage(contentResolver, image, "title", "description");
    }

    public static File uriToFile(Uri uri){
        /**
         * Args:
         *     uri: an image's uri
         * Returns:
         *      The file path of that image
         */

        return new File(uri.getPath());


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


//        String path = itemUri.getEncodedPath();
//        if(rows == 0)
//        {
//            Log.i(TAG,"Could not delete "+path+" :(");
//        }
//        else {
//            Log.i(TAG, "Deleted " + path + " ^_^");
//        }
    }

}

