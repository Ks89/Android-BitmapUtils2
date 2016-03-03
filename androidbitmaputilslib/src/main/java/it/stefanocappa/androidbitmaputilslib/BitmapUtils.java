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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefano Cappa on 05/08/15.
 * Updated by Stefano Cappa on 03/03/16 before the public release
 */
public class BitmapUtils {

    /**
     * Method to remove colors in a Bitmap, creating a gray scale image.
     *
     * @param source The original Bitmap.
     * @return The gray scale Bitmap.
     */
    public static Bitmap toGrayscale(Bitmap source) {
        Bitmap bmpGrayscale = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpGrayscale);
        canvas.drawBitmap(source, 0, 0, getGrayScalePaint());
        return bmpGrayscale;
    }

    //private method to get a gray scale paint, totally independent from bitmaps passed
    private static Paint getGrayScalePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(colorMatrixColorFilter);
        return paint;
    }

    /**
     * Method to rotate a Bitmap specifying the angle.
     *
     * @param source The original Bitmap.
     * @param angle  float that represents the rotation angle.
     * @return The rotated Bitmap.
     */
    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Method to flip vertically a Bitmap.
     *
     * @param source The original Bitmap.
     * @return The flipped Bitmap.
     */
    public static Bitmap flipVertically(Bitmap source) {
        Matrix m = new Matrix();
        m.preScale(1, -1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, false);
    }

    /**
     * Method to flip horizontally a Bitmap.
     *
     * @param source The original Bitmap.
     * @return The flipped Bitmap.
     */
    public static Bitmap flipHorizonally(Bitmap source) {
        Matrix m = new Matrix();
        m.setScale(-1, 1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, false);
    }

    /**
     * Method to scale a Bitmap specifying width and height.
     * If newWidth and newHeight are the same as the current width and height of
     * the source bitmap, the source bitmap is returned and no new bitmap is
     * created.
     * I suggest to use scaleByFactor(...) method to maintain original proportions.
     *
     * @param source    The original Bitmap.
     * @param newWidth  int that represents the desired width.
     * @param newHeight int that represents the desired height.
     * @return The scaled Bitmap.
     */
    public static Bitmap scale(Bitmap source, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    /**
     * Method to scale a Bitmap specifying the scaling factor as a float.
     * If factor==1, the source bitmap is returned and no new bitmap is
     * created.
     *
     * @param source The original Bitmap.
     * @param factor float that represents the scaling factor.
     * @return The scaled Bitmap.
     */
    public static Bitmap scaleByFactor(Bitmap source, float factor) {
        int newWidth = (int) (source.getWidth() * factor);
        int newHeight = (int) (source.getHeight() * factor);
        return scale(source, newWidth, newHeight);
    }

    /**
     * Method to create a fully-transparent Bitmap using the same size of the source passed as
     * parameter and also the same density.
     *
     * @param source The original Bitmap.
     * @return A transparent Bitmap with the same size of the source.
     */
    public static Bitmap clear(Bitmap source) {
        Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight());
        //to erase the color from a Bitmap, I must use a mutable Bitmap!!!!
        Bitmap mutableBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap.eraseColor(Color.TRANSPARENT);
        return mutableBitmap;
    }


    /**
     * Method to scale {@code sourceBitmap}, maintaining the same original size,
     * but with a transparent frame and the scaled and centered {@code sourceBitmap} inside.
     *
     * @param mutableBitmap The original Bitmap.
     * @param factor        float that represents the scaling factor.
     * @param color         Color of the frame.
     * @return The scaled Bitmap with a colored (or TRANSPARENT) border.
     */
    public static Bitmap scaleInsideColoredFrame(Bitmap mutableBitmap, float factor, int color) {
        Bitmap clearBitmap = mutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        clearBitmap.eraseColor(color);

        Bitmap resizedInsideBitmap = scaleByFactor(mutableBitmap, factor);

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
     * Method to get a colored Drawable silhouette.
     *
     * @param sourceBitmap The original Bitmap.
     * @param color        Color of the frame.
     * @return The colored Drawable silohuette.
     */
    public static Drawable getSilhouetteWithColor(Drawable sourceBitmap, int color) {
        sourceBitmap.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return sourceBitmap;
    }


    /**
     * Method to get a scaled colored silhouette inside a colored/transparent frame).
     *
     * @param source          The original Bitmap.
     * @param factor          float that represents the scaling factor.
     * @param frameColor      Color of the frame.
     * @param silhouetteColor Color of the silhouette that you want.
     * @return The scaled and colored silhouette inside a colored/transparent frame.
     */
    public static Bitmap getScaledColorSilhouetteInsideColoredFrame(Bitmap source, float factor, int frameColor, int silhouetteColor) {
        Bitmap scaledMutableBitmap = BitmapUtils.scaleInsideColoredFrame(source, factor, frameColor);
        Canvas c = new Canvas(scaledMutableBitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColorFilter(new PorterDuffColorFilter(silhouetteColor, PorterDuff.Mode.SRC_ATOP));
        c.drawBitmap(scaledMutableBitmap, 0.f, 0.f, p);
        return scaledMutableBitmap;
    }


    /**
     * Method to overlay color on a gray scale Bitmap.
     *
     * @param source The original Bitmap.
     * @param color  Color to overlay.
     * @return A colored gray scale Bitmap.
     */
    @Deprecated
    public static Bitmap overlayColorOnGrayScale(Bitmap source, int color) {
        Bitmap result = toGrayscale(source);
        return overlayColor(result, color);
    }

    @Deprecated
    private static Bitmap overlayColor(Bitmap source, int color) {
        Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight());
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
     * ---------------------------------------------------------------------------
     * USE THIS METHOD AND NOT THE OLDER VERSION CALLED: "overlayColorOnGrayScale"
     * ---------------------------------------------------------------------------
     * Method to overlay color on a gray scale Bitmap.
     *
     * @param source The original Bitmap.
     * @param color  Color to overlay.
     * @return A colored gray scale Bitmap.
     */
    public static Bitmap overlayColorOnGrayScaleOptimized1(Bitmap source, int color)  {
        Bitmap newBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap mutableBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(source, 0, 0, getGrayScalePaint());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);

        return mutableBitmap;
    }
    public static Bitmap overlayColorOnGrayScaleOptimized(Resources res, int id, int color) throws IOException {
        Bitmap mutableBitmap = getMutableBitmap(res, id);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, 0, 0, getGrayScalePaint());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);

        return mutableBitmap;
    }
    public static Bitmap overlayColorOnGrayScaleOptimized2(Bitmap source, int color) throws IOException {
        Bitmap mutableBitmap = getMutableBitmap(source);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, 0, 0, getGrayScalePaint());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        canvas.drawBitmap(mutableBitmap, 0, 0, paint);

        return mutableBitmap;
    }


    //*************************************************************************************************


    //new exp version with orientation
    public static Bitmap getNewCombinedByPiecesAlsoGrayscaled(List<Bitmap> bitmapList, int currentStage, int numStages, String direction) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(colorMatrixColorFilter);

        Bitmap finalBitmap = null;
        float delta = 0f;
        Canvas comboImage;
        int originalTotalWidth, originalTotalHeight;

        //i mean, don't use greyscale, but add here all the functionalities to reuse the canvas
        switch (direction) {

            case "L2R":
                originalTotalWidth = bitmapList.get(0).getWidth() * numStages;
                finalBitmap = createTransparentBitmap(originalTotalWidth, bitmapList.get(0).getHeight());
                comboImage = new Canvas(finalBitmap);
                for (int i = 0; i < numStages; i++) {
                    comboImage.translate(delta, 0f);
                    //   0  1  2  3  4
                    //   v  v  v  x  x
                    //            c
                    //    0..1..2<3
                    if (i > currentStage) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalWidth / numStages;
                }
                break;
            case "U2D":
                originalTotalHeight = bitmapList.get(0).getHeight() * numStages;
                finalBitmap = createTransparentBitmap(bitmapList.get(0).getWidth(), originalTotalHeight);
                comboImage = new Canvas(finalBitmap);
                for (int i = 0; i < numStages; i++) {
                    comboImage.translate(0f, delta);
                    /*
                        0  v      0
                        1  v      1
                        2  v c-1  2
                        3  x  c  3>c-1
                        4  x

                     */
                    if (i > currentStage - 1) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalHeight / numStages;
                }
                break;
            case "D2U":
                originalTotalHeight = bitmapList.get(0).getHeight() * numStages;
                finalBitmap = createTransparentBitmap(bitmapList.get(0).getWidth(), originalTotalHeight);
                comboImage = new Canvas(finalBitmap);
                for (int i = 0 ; i < numStages; i++) {
                    comboImage.translate(0f, delta);
                    if (i < currentStage - 1) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalHeight / numStages;
                }
                break;
            case "R2L":
                originalTotalWidth = bitmapList.get(0).getWidth() * numStages;
                finalBitmap = createTransparentBitmap(originalTotalWidth, bitmapList.get(0).getHeight());
                comboImage = new Canvas(finalBitmap);
                for (int i = 0 ; i < numStages; i++) {
                    comboImage.translate(delta, 0f);
                    //   0  1  2  3  4
                    //   x  x  v  v  v
                    //        c-1
                    //   0..1<3-1
                    if (i < currentStage - 1) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalWidth / numStages;
                }
                break;
        }
        return finalBitmap;
    }


    public static Bitmap createTransparentBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap createColoredBitmap(int colors[], int offset, int stride, int width, int height, Bitmap.Config config) {
        return Bitmap.createBitmap(colors, offset, stride, width, height, config);
    }


    public static List<Bitmap> splitImageVertically(Bitmap bmpOriginal, int numStages) {
        List<Bitmap> pieces = new ArrayList<>();
        int height = bmpOriginal.getHeight() / numStages;
        int start = 0;
        for (int i = 0; i < numStages; i++) {
            Bitmap pieceBitmap = Bitmap.createBitmap(bmpOriginal, 0, start, bmpOriginal.getWidth() - 1, height - 1);
            pieces.add(pieceBitmap);
            start = (bmpOriginal.getHeight() / numStages) * (i + 1);
        }
        return pieces;
    }

    //efficient way to get a mutable bitmap
    public static Bitmap getMutableBitmap(Bitmap immutable) throws IOException {
        byte[] byteArray = toByteArrayNew(immutable);
        Bitmap result = getMutableBitmap(byteArray, 0, byteArray.length);
        byteArray = null;
        return result;
    }

    public static Bitmap getMutableBitmap(byte[] data, int offset, int length) {
        return BitmapFactory.decodeByteArray(data, offset, length, getMutableOption());
    }

    public static Bitmap getMutableBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath, getMutableOption());
    }

    public static Bitmap getMutableBitmap(FileDescriptor fd, Rect outPadding) {
        return BitmapFactory.decodeFileDescriptor(fd, outPadding, getMutableOption());
    }

    public static Bitmap getMutableBitmap(Resources res, int id) {
        return BitmapFactory.decodeResource(res, id, getMutableOption());
    }

    public static Bitmap getMutableBitmap(Resources res, TypedValue value, InputStream is, Rect pad) {
        return BitmapFactory.decodeResourceStream(res, value, is, pad, getMutableOption());
    }

    public static Bitmap getMutableBitmap(InputStream is, Rect outPadding) {
        return BitmapFactory.decodeStream(is, outPadding, getMutableOption());
    }

    private static BitmapFactory.Options getMutableOption() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        return opt;
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

    public static byte[] toByteArrayNew(Bitmap source) {
        //int size = source.getRowBytes() * source.getHeight();
        int size = source.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        source.copyPixelsToBuffer(byteBuffer);
        //byteBuffer.rewind();
        Log.d("sas", "sas." + byteBuffer.get(1300));
        byte[] b = byteBuffer.array();
        return b;
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
