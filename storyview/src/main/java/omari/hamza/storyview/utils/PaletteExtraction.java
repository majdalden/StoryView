package omari.hamza.storyview.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.View;

import androidx.palette.graphics.Palette;

import java.lang.ref.WeakReference;

public class PaletteExtraction extends AsyncTask<Void, Void, Palette> {

    private final WeakReference<View> viewWeakReference;
    private final WeakReference<Bitmap> mBitmapWeakReference;

    public PaletteExtraction(View view, Bitmap resource) {
        this.viewWeakReference = new WeakReference<>(view);
        this.mBitmapWeakReference = new WeakReference<>(resource);
    }

    @Override
    public Palette doInBackground(Void... voids) {
        if (mBitmapWeakReference.get() == null) return null;

        try {
            return Palette.from(mBitmapWeakReference.get()).generate();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(Palette aPalette) {
        super.onPostExecute(aPalette);

        try {
            if (aPalette == null) return;

            View view = viewWeakReference.get();
            if (view == null) return;

            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{aPalette.getDarkVibrantColor(0), aPalette.getLightMutedColor(0)});
            drawable.setCornerRadius(0f);
            view.setBackground(drawable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
