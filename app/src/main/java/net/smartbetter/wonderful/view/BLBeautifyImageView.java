package net.smartbetter.wonderful.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.muzhi.camerasdk.library.filter.GPUImageView;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.utils.BLBitmapUtils;
import net.smartbetter.wonderful.utils.FilterUtils;


/**
 * Created by Administrator on 2017/4/15.
 */

public class BLBeautifyImageView extends FrameLayout {
    private Context mContext;
    private GPUImageView mGpuImageView;

    public BLBeautifyImageView(@NonNull Context context) {
        this(context, null);
    }

    public BLBeautifyImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BLBeautifyImageView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.bl_beautify_image_view, this, true);
        mGpuImageView = (GPUImageView) rootView.findViewById(R.id.bl_gpu_image_view);

    }

    /**********************************GPUImageView相关*********************************/
    public void addFilter(ImageFilterTools.FilterType filterType,String path) {
        FilterUtils.addFilter(mContext, filterType, mGpuImageView,path);
    }

    /**
     * 设置网络加载图片
     *
     * @param url
     */
    public void setImageUrl(String url) {
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        float width = (float) resource.getWidth();
                        float height = (float) resource.getHeight();
                        float ratio = width / height;
                        mGpuImageView.setRatio(ratio);
                        setImage(resource);
                    }
                });
    }

    /**
     * 设置本地路径图片
     *
     * @param path
     */
    public void setImage(String path) {
        mGpuImageView.setImage(path);
    }

    public void setImage(Bitmap bitmap) {
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        float ratio = width / height;
        mGpuImageView.setRatio(ratio);
        mGpuImageView.setImage(bitmap);
    }

    public Bitmap getGPUBitmap() {
        return mGpuImageView.getCurrentBitMap();
    }

    public Bitmap getInitBitmap(String path) {
        return BLBitmapUtils.getBitmap(path);
    }

    public GPUImageView getGPUImageView() {
        return mGpuImageView;
    }


    public String save() {
        return getFilterImage();
    }

    /**
     * 合并图片
     *
     * @return
     */
    public String getFilterImage() {
        mGpuImageView.setDrawingCacheEnabled(true);
        Bitmap editbmp = Bitmap.createBitmap(mGpuImageView.getDrawingCache());
        try {
            Bitmap fBitmap = mGpuImageView.capture();
            Bitmap bitmap = Bitmap.createBitmap(fBitmap.getWidth(), fBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(fBitmap, 0, 0, null);
            cv.drawBitmap(editbmp, 0, 0, null);

            //最终合并生成图片
            String path = BLBitmapUtils.saveAsBitmap(mContext, bitmap);
            bitmap.recycle();
            return path;

        } catch (Exception e) {
            return "";
        }
    }


}
