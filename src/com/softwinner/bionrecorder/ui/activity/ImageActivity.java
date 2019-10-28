package com.softwinner.bionrecorder.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.github.chrisbanes.photoview.PhotoView;
import com.softwinner.bionrecorder.R;

// import butterknife.BindView;
// import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.softwinner.bionrecorder.R.id.picturePV;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class ImageActivity extends AppCompatActivity{
    private static final String TAG = "ImageActivity";
    private static final boolean DEBUG = true;

    // @BindView(R.id.picturePV)
    PhotoView mPicturePV;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);

        mPicturePV = (PhotoView) findViewById(R.id.picturePV);
        // ButterKnife.bind(this);

        final String path = getIntent().getExtras().getString("path");
        if (path != null) {
            Flowable.just(path)
                    .map(new Function<String, Bitmap>() {
                        @Override
                        public Bitmap apply(@NonNull String s) throws Exception {
                            return BitmapFactory.decodeFile(s);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            mPicturePV.setImageBitmap(bitmap);
                        }
                    });
        }
    }
}
