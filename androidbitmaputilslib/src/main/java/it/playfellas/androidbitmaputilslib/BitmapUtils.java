package it.playfellas.androidbitmaputilslib;

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

    public class Direction {
        public static final int UP_2_DOWN = 0;
        public static final int DOWN_2_UP = 1;
        public static final int LEFT_2_RIGHT = 2;
        public static final int RIGHT_2_LEFT = 3;
    }

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
     * @throws BitmapUtilsException
     */
    public static Bitmap scale(Bitmap source, int newWidth, int newHeight) throws BitmapUtilsException {
        if (newWidth < 0 || newHeight < 0) {
            throw new BitmapUtilsException(BitmapUtilsException.Reason.WRONGINPUTPARAMETER, "newWidth and newHeight must be >=0");
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    /**
     * Method to scale a Bitmap specifying the scaling factor as a float.
     * If {@code factor}==1, the source bitmap is returned and no new bitmap is
     * created.
     *
     * @param source The original Bitmap.
     * @param factor float that represents the scaling factor.
     * @return The scaled Bitmap.
     * @throws BitmapUtilsException
     */
    public static Bitmap scaleByFactor(Bitmap source, float factor) throws BitmapUtilsException {
        if (factor < 0) {
            throw new BitmapUtilsException(BitmapUtilsException.Reason.WRONGINPUTPARAMETER, "factor must be >=0");
        }
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
     * but with a transparent frame, scaled and centered {@code sourceBitmap} inside.
     * If you want to use a transparent image as {@code source}, all transparent pixels will be
     * replaced by {@code color}. Otherwise, the {@code source} bitmap will
     * be scaled with a colored frame outside (using {@code color}).
     *
     * @param source The original Bitmap.
     * @param factor float that represents the scaling factor (must be >=0 and <=1).
     * @param color  Color of the frame.
     * @return The scaled Bitmap with a colored (or TRANSPARENT) border.
     * @throws BitmapUtilsException
     */
    public static Bitmap scaleInsideColoredFrame(Bitmap source, float factor, int color) throws BitmapUtilsException {
        if (factor > 1f || factor < 0f) {
            throw new BitmapUtilsException(BitmapUtilsException.Reason.WRONGINPUTPARAMETER, "factor must be >=0 and <=1");
        }

        Bitmap clearBitmap = source.copy(Bitmap.Config.ARGB_8888, true);
        clearBitmap.eraseColor(color);

        Bitmap resizedInsideBitmap = scaleByFactor(source, factor);

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
     * @throws BitmapUtilsException
     */
    public static Bitmap getScaledColorSilhouetteInsideColoredFrame(Bitmap source, float factor, int frameColor, int silhouetteColor) throws BitmapUtilsException {
        if (factor > 1f || factor < 0f) {
            throw new BitmapUtilsException(BitmapUtilsException.Reason.WRONGINPUTPARAMETER, "factor must be >=0 and <=1");
        }
        Bitmap scaledMutableBitmap = BitmapUtils.scaleInsideColoredFrame(source, factor, frameColor);
        Canvas c = new Canvas(scaledMutableBitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColorFilter(new PorterDuffColorFilter(silhouetteColor, PorterDuff.Mode.SRC_ATOP));
        c.drawBitmap(scaledMutableBitmap, 0.f, 0.f, p);
        return scaledMutableBitmap;
    }


    /**
     * ATTENTION: DON'T USE THIS METHOD BECAUSE IT HAS BAD PERFORMANCES.
     *
     * @param source The original Bitmap.
     * @param color  Color to overlay.
     * @return the result image.
     */
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
     * This method creates automatically a gray scale bitmap from {@code source}.
     *
     * @param source The original colored Bitmap.
     * @param color  Color to overlay.
     * @return A colored gray scale Bitmap.
     */
    public static Bitmap overlayColorOnGrayScale(Bitmap source, int color) {
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


    /**
     * Method to overlay a color on a gray scale Bitmap, passed as a resource id {@code id}.
     * If you want to call this method from a Fragment/Activity call it in this way:
     * overlayColorOnGrayScale(getResources(), R.drawable.your_image, Color.RED)
     *
     * @param res   A reference to Resources.
     * @param id    The id of a drawable image.
     * @param color Color to overlay.
     * @return A colored gray scale Bitmap.
     * @throws IOException
     */
    public static Bitmap overlayColorOnGrayScale(Resources res, int id, int color) throws IOException {
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


//    public static Bitmap overlayColorOnGrayScaleOptimized2(Bitmap source, int color) throws IOException {
//        Bitmap mutableBitmap = getMutableBitmap(source);
//
//        Canvas canvas = new Canvas(mutableBitmap);
//        canvas.drawBitmap(mutableBitmap, 0, 0, getGrayScalePaint());
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        ColorFilter filter = new LightingColorFilter(color, 1);
//        paint.setColorFilter(filter);
//        canvas.drawBitmap(mutableBitmap, 0, 0, paint);
//
//        return mutableBitmap;
//    }


    /**
     * Method to combine a List of (colored) bitmaps into a single image, based on the {@code direction}, specified
     * using constants defined in the inner class {@link Direction}.
     * The result will be an image with some original pieces and other gray scaled.
     * <br>
     * ----------------------------------
     * -------------Examples-------------
     * ----5 pieces + threshold = 3------
     * ----------------------------------
     * <br>
     * Infos:
     * - v = colored
     * - x = gray scaled
     * - c = position of the threshold
     * - <,> = if the condition is true, the result will be a gray scaled image
     * ----------------------------------
     * <br>
     * Cases:
     * direction = Direction.LEFT_2_RIGHT
     * bitmap pieces: 0   1   2   3   4
     * result:        v   v   v   x   x
     * indexes:              c-1  c
     * 0...1..2..3>(3-1)
     * <p/>
     * direction = Direction.RIGHT_2_LEFT
     * bitmap pieces: 0   1   2   3   4
     * result:        x   x   v   v   v
     * indexes:              c-1  c
     * 0..1<(3-1)
     * <p/>
     * direction = Direction.UP_2_DOWN
     * bitmap   |  result  |  indexes
     * pieces   |          |
     * 0           v                  0
     * 1           v                  1
     * 2           v          c-1     2
     * 3           x           c    3>3-1
     * 4           x
     * <p/>
     * direction = Direction.DOWN_2_UP
     * bitmap   |  result  |  indexes
     * pieces   |          |
     * 0           x                  0
     * 1           x                1<3-1
     * 2           v          c-1     2
     * 3           v           c      3
     * 4           v
     *
     * @param bitmapList A List of pieces of Bitmaps to be combined vertically or horizontally.
     * @param threshold  The limit before to change from original to grayscaled images.
     * @param numPieces  int that represents the number of pieces.
     * @param direction  int the specifies the {@link Direction}.
     * @return The result bitmap partially gray scaled and partially as original.
     */
    public static Bitmap geCombinedGrayscaledByPieces(List<Bitmap> bitmapList, int threshold, int numPieces, int direction) {
        //get a grayscale paint object to draw grayscaled images
        Paint paint = getGrayScalePaint();

        Bitmap finalBitmap = null;
        float delta = 0f;
        Canvas comboImage;
        int originalTotalWidth, originalTotalHeight;

        //I'm not using the grayscale method, because in this way i archive higher performances.
        switch (direction) {
            case Direction.LEFT_2_RIGHT:
                originalTotalWidth = bitmapList.get(0).getWidth() * numPieces;
                finalBitmap = createTransparentBitmap(originalTotalWidth, bitmapList.get(0).getHeight());
                comboImage = new Canvas(finalBitmap);
                for (int i = 0; i < numPieces; i++) {
                    comboImage.translate(delta, 0f);
                    if (i > threshold - 1) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalWidth / numPieces;
                }
                break;
            case Direction.RIGHT_2_LEFT:
                originalTotalWidth = bitmapList.get(0).getWidth() * numPieces;
                finalBitmap = createTransparentBitmap(originalTotalWidth, bitmapList.get(0).getHeight());
                comboImage = new Canvas(finalBitmap);
                for (int i = 0; i < numPieces; i++) {
                    comboImage.translate(delta, 0f);
                    if (i < threshold - 1) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalWidth / numPieces;
                }
                break;
            case Direction.UP_2_DOWN:
                originalTotalHeight = bitmapList.get(0).getHeight() * numPieces;
                finalBitmap = createTransparentBitmap(bitmapList.get(0).getWidth(), originalTotalHeight);
                comboImage = new Canvas(finalBitmap);
                for (int i = 0; i < numPieces; i++) {
                    comboImage.translate(0f, delta);
                    if (i > threshold - 1) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalHeight / numPieces;
                }
                break;
            case Direction.DOWN_2_UP:
                originalTotalHeight = bitmapList.get(0).getHeight() * numPieces;
                finalBitmap = createTransparentBitmap(bitmapList.get(0).getWidth(), originalTotalHeight);
                comboImage = new Canvas(finalBitmap);
                for (int i = 0; i < numPieces; i++) {
                    comboImage.translate(0f, delta);
                    if (i < threshold - 1) {
                        //grey
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, paint);
                    } else {
                        comboImage.drawBitmap(bitmapList.get(i), 0f, 0f, null);
                    }
                    delta = originalTotalHeight / numPieces;
                }
                break;
        }
        return finalBitmap;
    }

    /**
     * Method to create a transparent Bitmap.
     *
     * @param w int that represents the width.
     * @param h int that represents the height.
     * @return A transparent Bitmap with the specified size.
     */
    public static Bitmap createTransparentBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    /**
     * Method to split an image vertically into a List
     * composed by {@code piecesNum} of Bitmaps.
     *
     * @param source    The source Bitmap.
     * @param piecesNum int that represents the number of pieces.
     * @return The List of Bitmap's pieces.
     */
    public static List<Bitmap> splitImageVertically(Bitmap source, int piecesNum) {
        List<Bitmap> pieces = new ArrayList<>();
        int height = source.getHeight() / piecesNum;
        int start = 0;
        for (int i = 0; i < piecesNum; i++) {
            Bitmap pieceBitmap = Bitmap.createBitmap(source, 0, start, source.getWidth() - 1, height - 1);
            pieces.add(pieceBitmap);
            start = (source.getHeight() / piecesNum) * (i + 1);
        }
        return pieces;
    }

    /**
     * Method to split an image horizontally into a List
     * composed by {@code piecesNum} of Bitmaps.
     *
     * @param source    The source Bitmap.
     * @param piecesNum int that represents the number of pieces.
     * @return The List of Bitmap's pieces.
     */
    public static List<Bitmap> splitImageHorizontally(Bitmap source, int piecesNum) {
        List<Bitmap> pieces = new ArrayList<>();
        int width = source.getWidth() / piecesNum;
        int start = 0;
        for (int i = 0; i < piecesNum; i++) {
            Bitmap pieceBitmap = Bitmap.createBitmap(source, start, 0, width - 1, source.getHeight() - 1);
            pieces.add(pieceBitmap);
            start = (source.getWidth() / piecesNum) * (i + 1);
        }
        return pieces;
    }

    /**
     * Method to get a mutable bitmap.
     *
     * @param immutable Bitmap to convert.
     * @return A mutable Bitmap.
     * @throws IOException
     */
    private static Bitmap getMutableBitmap(Bitmap immutable) throws IOException {
        byte[] byteArray = toByteArrayNew(immutable);
        Bitmap result = getMutableBitmap(byteArray, 0, byteArray.length);
        byteArray = null;
        return result;
    }

    private static Bitmap getMutableBitmap(byte[] data, int offset, int length) {
        return BitmapFactory.decodeByteArray(data, offset, length, getMutableOption());
    }

    private static Bitmap getMutableBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath, getMutableOption());
    }

    private static Bitmap getMutableBitmap(FileDescriptor fd, Rect outPadding) {
        return BitmapFactory.decodeFileDescriptor(fd, outPadding, getMutableOption());
    }

    private static Bitmap getMutableBitmap(Resources res, int id) {
        return BitmapFactory.decodeResource(res, id, getMutableOption());
    }

    private static Bitmap getMutableBitmap(Resources res, TypedValue value, InputStream is, Rect pad) {
        return BitmapFactory.decodeResourceStream(res, value, is, pad, getMutableOption());
    }

    private static Bitmap getMutableBitmap(InputStream is, Rect outPadding) {
        return BitmapFactory.decodeStream(is, outPadding, getMutableOption());
    }

    private static BitmapFactory.Options getMutableOption() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        return opt;
    }


    /**
     * Method to combine two images side by side.
     *
     * @param leftBmp  The left Bitmap.
     * @param rightBmp The right Bitmap.
     * @return A Bitmap with left and right bitmap are glued side by side.
     */
    public static Bitmap combineTwoImagesSideBySide(Bitmap leftBmp, Bitmap rightBmp) {
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

    /**
     * Method to convert a Bitmap into a byte[].
     * Attention: this method is very expensive!!!
     *
     * @param source The Bitmap to convert.
     * @return The byte[] that represents the source Bitmap.
     */
    public static byte[] toByteArray(Bitmap source) throws IOException {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        source.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
        byte[] photoByteArray = blob.toByteArray();
        blob.close();
        return photoByteArray;
    }


    //NOT WORKING, but more efficient that toByteArray()
    public static byte[] toByteArrayNew(Bitmap source) {
        //int size = source.getRowBytes() * source.getHeight();
        int size = source.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        source.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        byte[] b = byteBuffer.array();
        return b;
    }

    /**
     * Method to convert a byte[] into a Bitmap.
     *
     * @param b The byte[] passed as parameter.
     * @return The converted Bitmap.
     */
    public static Bitmap fromByteArraytoBitmap(byte[] b) {
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * Method to get the number of bytes of the source Bitmap.
     *
     * @param source The source Bitmap.
     * @return The number of bytes of the source Bitmap.
     */
    private static int byteSizeOf(Bitmap source) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return source.getRowBytes() * source.getHeight();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return source.getByteCount();
        } else {
            return source.getAllocationByteCount();
        }
    }


    /**
     * TODO: Never tested.
     * Method to convert a Drawable into a Bitmap.
     *
     * @param drawable source Drawable to convert.
     * @return The result Bitmap.
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
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

    /**
     * Method to convert dp into pixels.
     *
     * @param dp int that represents the number of dp
     * @return The number of pixels.
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    /**
     * TODO: NEVER TESTED
     * Method to convert pixels into dp.
     *
     * @param px int that represents the number of dp
     * @return The number of dp.
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}
