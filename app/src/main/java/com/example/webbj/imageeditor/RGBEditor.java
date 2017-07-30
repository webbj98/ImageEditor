package com.example.webbj.imageeditor;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class RGBEditor extends AppCompatActivity {

    SeekBar seekBarRed;
    TextView textRedValue;
    SeekBar seekBarGreen;
    TextView textGreenValue;
    SeekBar seekBarBlue;
    TextView textBlueValue;

    ImageView imageView;
    Button getImage;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    private static final String TAG = "DebugMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_editor);

        Bundle data = getIntent().getExtras(); // get extra info from anothe r Intent
        if(data == null){
            //if nothing is passed
            return;
        }

        //convert the string uri back to a uri object
        Log.i(TAG, data.getString("selected image"));
        Uri imageUri = Uri.parse(getIntent().getStringExtra("selected image"));
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageURI(imageUri);

        //delete the temporary image in the internal storage
        deleteUri(this, imageUri);

        Bitmap bitmapImage = imageViewtoBitmap(imageView);
        seebbarr(bitmapImage, imageView);
    }



    public void seebbarr(final Bitmap bitmap, final ImageView imageView){
        seekBarRed = (SeekBar)findViewById(R.id.seekBarRed);
        textRedValue =(TextView)findViewById(R.id.textRedValue);
        textRedValue.setKeyListener(null);

        seekBarGreen = (SeekBar) findViewById(R.id.seekBarGreen) ;
        textGreenValue = (TextView)findViewById(R.id.textGreenValue);
        textGreenValue.setKeyListener(null);

        seekBarBlue = (SeekBar) findViewById(R.id.seekBarBlue);
        textBlueValue = (TextView)findViewById(R.id.textBlueValue);
        textBlueValue.setKeyListener(null);

        seekBarRed.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        //displays the value of the seek bar
                        textRedValue.setText(Integer.toString(progress));
                        imageView.setImageBitmap(changeImage(bitmap, seekBarRed.getProgress(),
                                seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textRedValue.setText(Integer.toString(progress_value));
                        imageView.setImageBitmap(changeImage(bitmap, seekBarRed.getProgress(),
                                seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    }
                }
        );

        seekBarGreen.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        //displays the value of the seek bar
                        textGreenValue.setText(Integer.toString(progress));
                        imageView.setImageBitmap(changeImage(bitmap, seekBarRed.getProgress(),
                                seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textGreenValue.setText(Integer.toString(progress_value));
                        imageView.setImageBitmap(changeImage(bitmap, seekBarRed.getProgress(),
                                seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    }
                }
        );

        seekBarBlue.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        //displays the value of the seek bar
                        textBlueValue.setText(Integer.toString(progress));
                        imageView.setImageBitmap(changeImage(bitmap, seekBarRed.getProgress(),
                                seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textBlueValue.setText(Integer.toString(progress_value));
                        imageView.setImageBitmap(changeImage(bitmap, seekBarRed.getProgress(),
                                seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    }
                }
        );
    }

    public static Bitmap changeImage(Bitmap image, int R, int G, int B){
        /**
         * Args:
         *     image: a bitmap image
         *     R: a red value
         *     B: a blue value
         *     G: a green value
         * Returns:
         *      an image based on the R, B, G values
         */


        Bitmap copyImage = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), false);


        int A, Rnew, Gnew, Bnew; //alpha, red, green, blue
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
            Rnew = Math.abs(R- Color.red(bitmapArray[i]));
            Gnew = Math.abs(G - Color.green(bitmapArray[i]));
            Bnew = Math.abs(B -Color.blue(bitmapArray[i]));
            bitmapArray[i] = Color.argb(A, Rnew, Gnew,Bnew);

        }

        //set the bitmap to the inverted array

        copyImage.setPixels(bitmapArray, 0, width, 0, 0, width, height);

        return copyImage;
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
    }

