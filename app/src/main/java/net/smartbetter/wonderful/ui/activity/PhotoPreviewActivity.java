package net.smartbetter.wonderful.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.base.BaseActivity;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Markable on 2018/4/24.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class PhotoPreviewActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    PhotoView mImageView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private String url;

    public static Intent getPhotoPreviewActivityIntent(Context context, String url) {
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        showLoading();
        Observable.just(1)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoading();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        hideLoading();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Glide.with(PhotoPreviewActivity.this)
                                .load(url)
                                .crossFade()
                                .into(mImageView);
                    }
                });
    }

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }
}
