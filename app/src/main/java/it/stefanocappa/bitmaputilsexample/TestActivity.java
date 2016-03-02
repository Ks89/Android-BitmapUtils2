package it.stefanocappa.bitmaputilsexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import it.stefanocappa.androidbitmaputilslib.BitmapUtils;

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
        setContentView(R.layout.ignore_test_activity);
        ButterKnife.bind(this);

        bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.lion8);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.effects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private static final int ORIGINAL=0;
    private static final int GRAYSCALE=1;
    private static final int FLIPH=2;
    private static final int FLIPV=3;
    private static final int SCALECOLSILHOUETTEINSIDECOLOREDFRAME=4;
    private static final int OVERLAYCOLGRAYSCALE=5;
    private static final int ROTATE=6;
    private static final int SCALE=7;
    private static final int PIECESGRAYSCALED=8;
    private static final int SILHOUETTECOLOR=9;
    private static final int SCALEINSIDEFRAME=10;


    @OnItemSelected(R.id.effects_spinner)
    public void onItemSelected(int position) {
        switch(position) {
            default:
            case ORIGINAL:
                imageView.setImageBitmap(bitmap);
                break;
            case GRAYSCALE:
                imageView.setImageBitmap(BitmapUtils.toGrayscale(bitmap));
                break;
            case FLIPH:
                imageView.setImageBitmap(BitmapUtils.flipHorizonallyBitmap(bitmap));
                break;
            case FLIPV:
                imageView.setImageBitmap(BitmapUtils.flipVerticallyBitmap(bitmap));
                break;
            case SCALECOLSILHOUETTEINSIDECOLOREDFRAME:
                imageView.setImageBitmap(BitmapUtils.getScaledColorSilhouetteInsideColoredFrame(bitmap, 0.5f, Color.TRANSPARENT, Color.RED));
                break;
            case OVERLAYCOLGRAYSCALE:
                imageView.setImageBitmap(BitmapUtils.overlayColorOnGrayScale(bitmap, Color.YELLOW));
                break;
            case ROTATE:
                imageView.setImageBitmap(BitmapUtils.rotateBitmap(bitmap, 90));
                break;
            case SCALE:
                imageView.setImageBitmap(BitmapUtils.scaleBitmap(bitmap, 500, 500));
                break;
            case PIECESGRAYSCALED:
                List<Bitmap> piecesList = BitmapUtils.splitImage(BitmapFactory.decodeResource(getResources(), R.drawable.lion8), 5);
                //this colors the first 2 (1+1, because current starts from 0) pieces over the size of the piecesList
                imageView.setImageBitmap(BitmapUtils.getNewCombinedByPiecesAlsoGrayscaled(piecesList, 1, piecesList.size()));
                break;
            case SILHOUETTECOLOR:
                imageView.setImageDrawable(BitmapUtils.getDrawableSilhouetteWithColor(getResources().getDrawable(R.drawable.lion8), Color.YELLOW));
                break;
            case SCALEINSIDEFRAME:
                imageView.setImageBitmap(BitmapUtils.scaleInsideWithFrame(bitmap, 0.5f, Color.TRANSPARENT));
                break;
        }
    }
}
