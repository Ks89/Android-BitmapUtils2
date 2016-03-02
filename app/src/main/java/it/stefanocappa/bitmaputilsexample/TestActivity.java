package it.stefanocappa.bitmaputilsexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.stefanocappa.androidbitmaputilslib.BitmapUtils;

/**
 * Created by Stefano Cappa on 05/08/15.
 */
public class TestActivity extends AppCompatActivity {

    @Bind(R.id.testImageView)
    public ImageView imageView;
    @Bind(R.id.tb1)
    public Button b1;
    @Bind(R.id.tb2)
    public Button b2;
    @Bind(R.id.tb3)
    public Button b3;
    @Bind(R.id.tb4)
    public Button b4;
    @Bind(R.id.tb5)
    public Button b5;
    @Bind(R.id.tb6)
    public Button b6;
    @Bind(R.id.tb7)
    public Button b7;
    @Bind(R.id.tb8)
    public Button b8;
    @Bind(R.id.tb9)
    public Button b9;
    @Bind(R.id.tb10)
    public Button b10;
    @Bind(R.id.tb11)
    public Button b11;

    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ignore_test_activity);
        ButterKnife.bind(this);

        bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.elephant_free_from_web);
    }

    @OnClick(R.id.tb1)
    public void onClickButton1(View view) {
        imageView.setImageBitmap(BitmapUtils.toGrayscale(bitmap));
    }

    @OnClick(R.id.tb2)
    public void onClickButton2(View view) {
        imageView.setImageBitmap(BitmapUtils.flipHorizonallyBitmap(bitmap));
    }

    @OnClick(R.id.tb3)
    public void onClickButton3(View view) {
        imageView.setImageBitmap(BitmapUtils.flipVerticallyBitmap(bitmap));
    }

    @OnClick(R.id.tb4)
    public void onClickButton4(View view) {
        imageView.setImageBitmap(BitmapUtils.getScaledColorSilhouetteInsideColoredFrame(bitmap, 0.5f, Color.TRANSPARENT, Color.RED));
    }

    @OnClick(R.id.tb5)
    public void onClickButton5(View view) {
        imageView.setImageBitmap(BitmapUtils.overlayColorOnGrayScale(bitmap, Color.YELLOW));
    }

    @OnClick(R.id.tb6)
    public void onClickButton6(View view) {
        imageView.setImageBitmap(BitmapUtils.rotateBitmap(bitmap, 90));
    }

    @OnClick(R.id.tb7)
    public void onClickButton7(View view) {
        imageView.setImageBitmap(BitmapUtils.scaleBitmap(bitmap, 100, 100));
    }

    @OnClick(R.id.tb8)
    public void onClickButton8(View view) {
        imageView.setImageBitmap(BitmapUtils.scaleBitmapByFactor(bitmap, 2));
    }

    @OnClick(R.id.tb9)
    public void onClickButton9(View view) {
        List<Bitmap> piecesList = BitmapUtils.splitImage(BitmapFactory.decodeResource(getResources(), R.drawable.elephant_free_from_web), 4);
        //this colors the first 2 (1+1, because current starts from 0) pieces over the size of the piecesList
//        imageView.setImageBitmap(BitmapUtils.getNewCombinedByPiecesAlsoGrayscaled(piecesList, 1));
    }

    @OnClick(R.id.tb10)
    public void onClickButton10(View view) {
        imageView.setImageDrawable(BitmapUtils.getDrawableSilhouetteWithColor(getResources().getDrawable(R.drawable.elephant_free_from_web), Color.BLUE));
    }

    @OnClick(R.id.tb11)
    public void onClickButton11(View view) {
        imageView.setImageBitmap(BitmapUtils.scaleInsideWithFrame(bitmap, 0.5f, Color.TRANSPARENT));
    }
}
