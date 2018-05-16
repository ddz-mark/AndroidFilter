package net.smartbetter.wonderful.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muzhi.camerasdk.library.filter.util.ImageFilterTools;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.utils.BLBitmapUtils;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.view.BLBeautifyImageView;


/**
 * Created by Administrator on 2018/4/15.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class BLBeautifyFragment extends Fragment {
    private BLBeautifyImageView mBeautifyImage;
    private String mPath;

    public static BLBeautifyFragment newInstance(String path) {
        BLBeautifyFragment f = new BLBeautifyFragment();
        Bundle b = new Bundle();
        b.putString("path", path);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPath = getArguments().getString("path");
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
        if (mBeautifyImage != null) {
            mBeautifyImage.setImage(path);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bl_fragment_beautify, container, false);
        mBeautifyImage = (BLBeautifyImageView) view.findViewById(R.id.beautify_image);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //由于拍照的图片很大，所以要先做一下处理，否则在改变亮度和饱和度时会很卡

        mBeautifyImage.setImage(BLBitmapUtils.getBitmap(mPath));
    }

    /**
     * 添加滤镜
     *
     * @param filterType
     */
    public void addFilter(ImageFilterTools.FilterType filterType) {
        mBeautifyImage.addFilter(filterType,mPath);
    }

    public Bitmap getBitmap() {
        return mBeautifyImage.getGPUBitmap();
    }

    public void setBitmap(Bitmap bitmap) {
        mBeautifyImage.setImage(bitmap);
    }

    //保存图片
    public String complete() {
        if (mBeautifyImage != null) {
            return mBeautifyImage.save();
        } else {
            return mPath;
        }
    }

}
