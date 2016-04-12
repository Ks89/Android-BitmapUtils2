package it.playfellas.bitmaputilsexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import it.playfellas.androidbitmaputilslib.BitmapUtils;
import it.playfellas.androidbitmaputilslib.BitmapUtilsException;

/**
 * Created by Stefano Cappa on 02/03/16.
 */
public class TestActivity extends AppCompatActivity {

    @Bind(R.id.testImageView)
    public ImageView imageView;
    @Bind(R.id.effects_spinner)
    public Spinner spinner;

    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        ButterKnife.bind(this);

        bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.lion8);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.effects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private static final int ORIGINAL = 0;
    private static final int GRAYSCALE = 1;
    private static final int FLIPH = 2;
    private static final int FLIPV = 3;
    private static final int SCALECOLSILHOUETTEINSIDECOLOREDFRAME = 4;
    private static final int OVERLAYCOLGRAYSCALE = 5;
    private static final int OVERLAYCOLGRAYSCALENEW = 6;
    private static final int ROTATE = 7;
    private static final int SCALE = 8;
    private static final int PIECESGRAYSCALED = 9;
    private static final int PIECESGRAYSCALEDVERTICAL = 10;
    private static final int SILHOUETTECOLOR = 11;
    private static final int SCALETRANSPARENTINSIDEFRAME = 12;
    private static final int SCALECOLOREDINSIDEFRAME = 13;
    private static final int DRAWABLETOBITMAP = 14;


    @OnItemSelected(R.id.effects_spinner)
    public void onItemSelected(int position) {
        List<Bitmap> piecesList;
        try {
            switch (position) {
                default:
                case ORIGINAL:
                    imageView.setImageBitmap(bitmap);
                    break;
                case GRAYSCALE:
                    imageView.setImageBitmap(BitmapUtils.toGrayscale(bitmap));
                    break;
                case FLIPH:
                    imageView.setImageBitmap(BitmapUtils.flipHorizonally(bitmap));
                    break;
                case FLIPV:
                    imageView.setImageBitmap(BitmapUtils.flipVertically(bitmap));
                    break;
                case SCALECOLSILHOUETTEINSIDECOLOREDFRAME:
                    imageView.setImageBitmap(BitmapUtils.getScaledColorSilhouetteInsideColoredFrame(bitmap, 0.5f, Color.TRANSPARENT, Color.RED));
                    break;
                case OVERLAYCOLGRAYSCALE:
                    imageView.setImageBitmap(BitmapUtils.overlayColorOnGrayScale(bitmap, Color.YELLOW));
                    break;
                case OVERLAYCOLGRAYSCALENEW:
//                try {
//                    imageView.setImageBitmap(BitmapUtils.overlayColorOnGrayScaleOptimized2(bitmap, Color.YELLOW));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                    try {
                        imageView.setImageBitmap(BitmapUtils.overlayColorOnGrayScale(getResources(), R.drawable.lion8, Color.YELLOW));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case ROTATE:
                    //imageView.setImageBitmap(BitmapUtils.createColoredBitmap(new int[]{Color.BLACK}, 0,1,1,1));
                    imageView.setImageBitmap(BitmapUtils.rotate(bitmap, 90));
                    break;
                case SCALE:
                    imageView.setImageBitmap(BitmapUtils.scale(bitmap, 500, 500));
                    break;
                case PIECESGRAYSCALED:
                    piecesList = BitmapUtils.splitImageHorizontally(BitmapFactory.decodeResource(getResources(), R.drawable.lion8), 5);
                    //this colors the first 2 (1+1, because current starts from 0) pieces over the size of the piecesList
                    imageView.setImageBitmap(BitmapUtils.geCombinedGrayscaledByPieces(piecesList,
                            3, piecesList.size(), BitmapUtils.Direction.LEFT_2_RIGHT));
                    break;
                case PIECESGRAYSCALEDVERTICAL:
                    piecesList = BitmapUtils.splitImageVertically(BitmapFactory.decodeResource(getResources(), R.drawable.lion8), 5);
                    //this colors the first 2 (1+1, because current starts from 0) pieces over the size of the piecesList
                    imageView.setImageBitmap(BitmapUtils.geCombinedGrayscaledByPieces(piecesList,
                            3, piecesList.size(), BitmapUtils.Direction.DOWN_2_UP));
                    break;
                case SILHOUETTECOLOR:
                    imageView.setImageDrawable(BitmapUtils.getSilhouetteWithColor(getResources().getDrawable(R.drawable.lion8), Color.YELLOW));
                    break;
                case SCALETRANSPARENTINSIDEFRAME:
                    imageView.setImageBitmap(BitmapUtils.scaleInsideColoredFrame(bitmap, 0.5f, Color.TRANSPARENT));
                    break;
                case SCALECOLOREDINSIDEFRAME:
                    imageView.setImageBitmap(BitmapUtils.scaleInsideColoredFrame(bitmap, 0.5f, Color.RED));
                    break;
                case DRAWABLETOBITMAP:
                    imageView.setImageBitmap(BitmapUtils.drawableToBitmap(getResources().getDrawable(R.drawable.lion8)));
                    break;
            }
        } catch (BitmapUtilsException e) {
            e.printStackTrace();
        }
    }
}
