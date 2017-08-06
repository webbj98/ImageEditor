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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;




public class Crop extends AppCompatActivity{

    ImageView imageView;
    //captured picture uri
    Uri picUri;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    String TAG = "DebugMessage";
    Button crop;
    Intent CropIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Bundle extras = getIntent().getExtras();

        Bundle data = getIntent().getExtras(); // get extra info from another Intent
        if(data == null){
            //if nothing is passed
            return;
        }
        imageView = (ImageView)findViewById(R.id.picture);


        setContentView(R.layout.activity_crop);

        crop =(Button)findViewById(R.id.crop_button);

        imageView = (ImageView)findViewById(R.id.picture);
        //convert the string uri back to a uri object
        Log.i(TAG, data.getString("crop image"));
        picUri = Uri.parse(getIntent().getStringExtra("crop image"));
        if (picUri == null){
            return;
        }
        Log.i(TAG, picUri.toString());
        //sets imageView to display uri
        imageView.setImageURI(picUri);

        //delete the temporary image in the internal storage

    }

    public void onClick(View v) {
        CropImage();
    }

    private void CropImage() {

        try{

            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(picUri,"image/*");

            CropIntent.putExtra("crop","true");
            CropIntent.putExtra("outputX",1000);
            CropIntent.putExtra("outputY",1000);
            CropIntent.putExtra("aspectX",1);
            CropIntent.putExtra("aspectY",1);
            CropIntent.putExtra("scaleUpIfNeeded",false);
            CropIntent.putExtra("return-data",true);

            startActivityForResult(CropIntent,1);
        }
        catch (ActivityNotFoundException ex)
        {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK)
            CropImage();
        else if(requestCode == 2)
        {
            if(data != null)
            {
                Log.i(TAG, "weh");
                picUri = data.getData();
                CropImage();
            }
        }
        //the above codes are used if gotten pics from camera
        else if (requestCode == 1) {

            if (data != null) {
                Log.i(TAG, "Blah");
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                imageView.setImageBitmap(bitmap);
                Filter.saveImage(this.getContentResolver(),bitmap );
            }
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


