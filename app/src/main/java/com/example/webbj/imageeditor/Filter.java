package com.example.webbj.imageeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
        Log.i(TAG, "here11");

        final Bitmap bitmapImage = imageViewtoBitmap(mainImageView);
        invertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "inverting");
                mainImageView.setImageBitmap(invertImage(bitmapImage));
                //could be a problem
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

        Bitmap finalImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

        int A, R, G, B; //alpha, red, green, blue
        int pixelColor;
        int height = image.getHeight();
        int width = image.getWidth();

        // builds the new copied image
        for (int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                pixelColor = image.getPixel(x,y);
                A = Color.alpha(pixelColor);
                //inverted forms of red, green, and blue
                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);
                finalImage.setPixel(x,y, Color.argb(A, R, G,B));
            }

        }
        return finalImage;
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
