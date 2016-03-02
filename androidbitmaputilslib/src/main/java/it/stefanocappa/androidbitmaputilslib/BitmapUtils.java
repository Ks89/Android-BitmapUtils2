package it.stefanocappa.androidbitmaputilslib;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefano Cappa on 05/08/15.
 */
public class BitmapUtils {

    /**
     * Method to remove color in a Bitmap, creating a gray scale image.
     *
     * @param bmpOriginal The original Bitmap.
     * @return The gray scale Bitmap.
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        Bitmap bmpGrayscale = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);

        return bmpGrayscale;
    }

    /**
     * TODO doc
     *
     * @param source
     * @param angle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * TODO doc
     *
     * @param source
     * @return
     */
    public static Bitmap flipVerticallyBitmap(Bitmap source) {
        Matrix m = new Matrix();
        m.preScale(1, -1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, false);
    }

    /**
     * TODO doc
     *
     * @param source
     * @return
     */
    public static Bitmap flipHorizonallyBitmap(Bitmap source) {
        Matrix m = new Matrix();
        m.setScale(-1, 1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, false);
    }

    public static Bitmap scaleBitmap(Bitmap source, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    public static Bitmap scaleBitmapByFactor(Bitmap source, float factor) {
        int newWidth = (int) (source.getWidth() * factor);
        int newHeight = (int) (source.getHeight() * factor);
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    public static Bitmap clearBitmap(Bitmap sourceBitmap) {
        Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Bitmap mutableBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap.eraseColor(Color.TRANSPARENT);
        return mutableBitmap;
    }

    /**
     * Method to scale {@code sourceBitmap}, maintaining the same original size of the bitmap,
     * but with a transparent frame and the scaled and centered {@code sourceBitmap} inside.
     *
     * @return
     */
    public static Bitmap scaleInsideWithFrame(Bitmap mutableBitmap, float factor, int color) {
        Bitmap clearBitmap = mutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        clearBitmap.eraseColor(color);

        Bitmap resizedInsideBitmap = scaleBitmapByFactor(mutableBitmap, factor);

        int frameWidth = clearBitmap.getWidth();
        int frameHeight = clearBitmap.getHeight();
        int imageWidth = resizedInsideBitmap.getWidth();
        int imageHeight = resizedInsideBitmap.getHeight();

        Canvas canvas = new Canvas(clearBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        canvas.drawBitmap(resizedInsideBitmap, (frameWidth - imageWidth) / 2, (frameHeight - imageHeight) / 2, paint);
        return clearBitmap;
    }

    /**
     * TODO write documentation
     *
     * @param sourceBitmap
     * @param color
     * @return
     */
    public static Bitmap overlayColor(Bitmap sourceBitmap, int color) {
        Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Bitmap mutableBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);
        return mutableBitmap;
    }


    /**
     * TODO doc
     *
     * @param sourceBitmap
     * @param color
     * @return
     */
    public static Drawable getDrawableSilhouetteWithColor(Drawable sourceBitmap, int color) {
        sourceBitmap.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return sourceBitmap;
    }


    /**
     * Todo doc
     *
     * @param sourceBitmap
     * @param scale
     * @param frameColor
     * @param silhouetteColor
     * @return
     */
    public static Bitmap getScaledColorSilhouetteInsideColoredFrame(Bitmap sourceBitmap, float scale, int frameColor, int silhouetteColor) {
        Bitmap scaledMutableBitmap = BitmapUtils.scaleInsideWithFrame(sourceBitmap, scale, frameColor);
        Canvas c = new Canvas(scaledMutableBitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColorFilter(new PorterDuffColorFilter(silhouetteColor, PorterDuff.Mode.SRC_ATOP));
        c.drawBitmap(scaledMutableBitmap, 0.f, 0.f, p);
        return scaledMutableBitmap;
    }


    /**
     * TODO doc
     *
     * @param original
     * @param color
     * @return
     */
    public static Bitmap overlayColorOnGrayScale(Bitmap original, int color) {
        Bitmap result = toGrayscale(original);
        return overlayColor(result, color);
    }

    /**
     * Method to split an image in {@code numStages} pieces.
     *
     * @param bmpOriginal The original Bitmap.
     * @param numStages   int that represents the number of pieces.
     * @return A List of Bitmap, i.e. a List of pieces of {@code bmpOriginal}
     */
    public static List<Bitmap> splitImage(Bitmap bmpOriginal, int numStages) {
        List<Bitmap> pieces = new ArrayList<>();
        int width = bmpOriginal.getWidth() / numStages;
        int start = 0;
        for (int i = 0; i < numStages; i++) {
            Bitmap pieceBitmap = Bitmap.createBitmap(bmpOriginal, start, 0, width - 1, bmpOriginal.getHeight() - 1);
            pieces.add(pieceBitmap);
            start = (bmpOriginal.getWidth() / numStages) * (i + 1);
        }
        return pieces;
    }

    /**
     * Method to combine images side by side.
     *
     * @param leftBmp  The left Bitmap.
     * @param rightBmp The right Bitmap.
     * @return A Bitmap with left and right bitmap are glued side by side.
     */
    public static Bitmap combineImagesSideBySide(Bitmap leftBmp, Bitmap rightBmp) {
        int width;
        int height = leftBmp.getHeight();

        if (leftBmp.getWidth() > rightBmp.getWidth()) {
            width = leftBmp.getWidth() + rightBmp.getWidth();
        } else {
            width = rightBmp.getWidth() + rightBmp.getWidth();
        }

        Bitmap cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);
        comboImage.drawBitmap(leftBmp, 0f, 0f, null);
        comboImage.drawBitmap(rightBmp, leftBmp.getWidth(), 0f, null);

        return cs;
    }


    //new version with incorporated the grayscale conversion.
    public static Bitmap getNewCombinedByPiecesAlsoGrayscaled(List<Bitmap> bitmapList, int currentStage, int numStages) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(colorMatrixColorFilter);

        //i mean, don't use greyscale, but add here all the functionalities to reuse the canvas
        int originalTotalWidth = bitmapList.get(0).getWidth() * numStages;
        Bitmap finalBitmap = Bitmap.createBitmap(originalTotalWidth, bitmapList.get(0).getHeight(), Bitmap.Config.ARGB_8888);
        float delta = 0f;
        Canvas comboImage = new Canvas(finalBitmap);
        for (int i = 0; i < numStages; i++) {
            comboImage.translate(delta, 0f);
            if (i > currentStage) {
                comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
            } else {
                comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
            }
            delta = originalTotalWidth / numStages;
        }
        return finalBitmap;
    }

    @Deprecated
    public static Bitmap getCombinedByPieces(List<Bitmap> bitmapList, int numStages) {
        //TODO add here the method to greyscale to use the same canvas but to draw a grayscale version
        //i mean, don't use greyscale, but add here all the functionalities to reuse the canvas
        int originalTotalWidth = bitmapList.get(0).getWidth() * numStages;
        Bitmap finalBitmap = Bitmap.createBitmap(originalTotalWidth, bitmapList.get(0).getHeight(), Bitmap.Config.ARGB_8888);
        float delta = 0f;
        Canvas comboImage = new Canvas(finalBitmap);
        for (int i = 0; i < numStages; i++) {
            comboImage.translate(delta, 0f);
            comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
            delta = originalTotalWidth / numStages;
        }
        return finalBitmap;
    }


    /**
     * Method to get a single Bitmap combining multiple pieces side by side.
     * Pieces are combined from left to right iterating over {@code bitmapListCopy}.
     *
     * @param bitmapListCopy The List of Bitmaps' pieces.
     * @param numStages      the maximum number of stages
     * @return The file Bitmap with all pieces combined.
     */
    @Deprecated
    public static Bitmap getCombinedBitmapByPieces(List<Bitmap> bitmapListCopy, int numStages) {
        Bitmap finalBitmap = bitmapListCopy.get(0);

        for (int i = 0; i < numStages; i++) {
            if (i > 0) { //skip first cycle
                finalBitmap = combineImagesSideBySide(finalBitmap, bitmapListCopy.get(i));
            }
        }
        return finalBitmap;
    }

    /**
     * TODO doc
     *
     * @param photoBitmap
     * @return
     */
    public static byte[] toByteArray(Bitmap photoBitmap) throws IOException {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
        byte[] photoByteArray = blob.toByteArray();
        blob.close();
        return photoByteArray;
    }

    /**
     * TODO doc
     *
     * @param b
     * @return
     */
    public static Bitmap fromByteArraytoBitmap(byte[] b) {
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * TODO doc
     *
     * @param data
     * @return
     */
    private static int byteSizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return data.getByteCount();
        } else {
            return data.getAllocationByteCount();
        }
    }


    /**
     * TODO: Never tested. I don't remember if it works, but plese don't remove this.
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    //Never tested and probably bugged
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}
