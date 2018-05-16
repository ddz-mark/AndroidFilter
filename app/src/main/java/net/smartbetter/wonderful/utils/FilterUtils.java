package net.smartbetter.wonderful.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.muzhi.camerasdk.library.filter.GPUImageFilter;
import com.muzhi.camerasdk.library.filter.GPUImageView;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools.*;
import com.tencent.wstt.gt.client.GT;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.entity.Filter_Effect_Info;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * 特效文件
 * TODO:Canny算法不能用gpuimage实现，所以改换库opencv，需要把所有的滤镜用opencv实现
 */
public class FilterUtils {

    /**
     * 获取特效列表
     *
     * @return
     */
    public static ArrayList<Filter_Effect_Info> getEffectList() {

        ArrayList<Filter_Effect_Info> effect_list = new ArrayList<Filter_Effect_Info>();

        effect_list.add(new Filter_Effect_Info("原图", R.drawable.camerasdk_filter_normal, null));
        effect_list.add(new Filter_Effect_Info("黑白", R.drawable.camerasdk_filter_inkwell, FilterType.I_INKWELL));
        effect_list.add(new Filter_Effect_Info("底片", R.drawable.camerasdk_filter_hudson, FilterType.INVERT));
        effect_list.add(new Filter_Effect_Info("浮雕", R.drawable.camerasdk_filter_amaro, FilterType.EMBOSS));
        effect_list.add(new Filter_Effect_Info("边缘检测", R.drawable.camerasdk_filter_in1977, FilterType.SOBEL_EDGE_DETECTION));
        effect_list.add(new Filter_Effect_Info("锐化", R.drawable.camerasdk_filter_brannan, FilterType.SHARPEN));
        effect_list.add(new Filter_Effect_Info("RGB", R.drawable.camerasdk_filter_early_bird, FilterType.RGB));
        effect_list.add(new Filter_Effect_Info("LOMO", R.drawable.camerasdk_filter_lomo, FilterType.I_LOMO));
        effect_list.add(new Filter_Effect_Info("不羁", R.drawable.camerasdk_filter_nashville, FilterType.I_NASHVILLE));
        effect_list.add(new Filter_Effect_Info("森系", R.drawable.camerasdk_filter_rise, FilterType.I_NASHVILLE));
        effect_list.add(new Filter_Effect_Info("清新", R.drawable.camerasdk_filter_sierra, FilterType.I_SIERRA));
        effect_list.add(new Filter_Effect_Info("摩登", R.drawable.camerasdk_filter_sutro, FilterType.I_SUTRO));
        return effect_list;

    }

    /**
     * 添加滤镜
     *
     * @param context
     * @param filterType
     * @param imageView
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void addFilter(Context context, ImageFilterTools.FilterType filterType, GPUImageView imageView, String path) {
        GPUImageFilter filter;
        Bitmap initMap = BLBitmapUtils.getBitmap(path);
        imageView.setImage(initMap);

        if (filterType == null) {
            filter = new GPUImageFilter();
            imageView.setFilter(filter);
        } else if (filterType == FilterType.SOBEL_EDGE_DETECTION) {
            Bitmap carBitmap = imageView.getCurrentBitMap();
            Mat rgba = new Mat();
            Utils.bitmapToMat(carBitmap, rgba);
            Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
            Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
            Imgproc.Canny(edges, edges, 80, 100);
            Utils.matToBitmap(edges, carBitmap);
            imageView.setImage(carBitmap);
        } else {
            GT.startTime("线程内统计", filterType.name() + "算法");
            filter = ImageFilterTools.createFilterForType(context, filterType);
            GT.endTime("线程内统计", filterType.name() + "算法");
            imageView.setFilter(filter);
        }

    }


}
