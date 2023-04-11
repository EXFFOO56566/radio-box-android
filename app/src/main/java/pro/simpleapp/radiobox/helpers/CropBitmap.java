package pro.simpleapp.radiobox.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.Display;
import android.view.WindowManager;

import java.util.Objects;



public class CropBitmap {

    private Context context;


    public CropBitmap(Context context) {
        this.context = context;
    }


    public Bitmap cropBitmapFromCenterAndScreenSize(Bitmap bitmap) {
        float screenWidth, screenHeight;
        float bitmap_width = bitmap.getWidth(), bitmap_height = bitmap
                .getHeight();
        Display display = ((WindowManager) Objects.requireNonNull(context.getSystemService(Context.WINDOW_SERVICE)))
                .getDefaultDisplay();
        screenWidth = display.getWidth() - 100;
        screenHeight = display.getHeight();
        float bitmap_ratio = (float) (bitmap_width / bitmap_height);
        float screen_ratio = (float) (screenWidth / screenHeight);
        int bitmapNewWidth, bitmapNewHeight;
        if (screen_ratio > bitmap_ratio) {
            bitmapNewWidth = (int) screenWidth;
            bitmapNewHeight = (int) (bitmapNewWidth / bitmap_ratio);
        } else {
            bitmapNewHeight = (int) screenHeight;
            bitmapNewWidth = (int) (bitmapNewHeight * bitmap_ratio);
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmapNewWidth,
                bitmapNewHeight, true);
        int bitmapGapX, bitmapGapY;
        bitmapGapX = (int) ((bitmapNewWidth - screenWidth) / 2.0f);
        bitmapGapY = (int) ((bitmapNewHeight - screenHeight) / 2.0f);
        bitmap = Bitmap.createBitmap(bitmap, bitmapGapX, bitmapGapY,
                (int) Math.round(screenWidth), (int) Math.round(screenHeight));
        return bitmap;
    }


    public Bitmap cropCenter(Bitmap bmp) {
        int dimension = Math.min(bmp.getWidth(), bmp.getHeight());
        dimension = 1000;
        return ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
    }


}
