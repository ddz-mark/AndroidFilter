package net.smartbetter.wonderful.utils;

import android.content.Context;


import com.muzhi.camerasdk.library.filter.GPUImageFilter;
import com.muzhi.camerasdk.library.filter.GPUImageView;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools.*;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.entity.Filter_Effect_Info;

import java.util.ArrayList;


/**
 * 特效文件
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
        effect_list.add(new Filter_Effect_Info("边缘检测", R.drawable.camerasdk_filter_in1977, FilterType.SOBEL_EDGE_DETECTION));
        effect_list.add(new Filter_Effect_Info("浮雕", R.drawable.camerasdk_filter_amaro, FilterType.EMBOSS));
        effect_list.add(new Filter_Effect_Info("锐化", R.drawable.camerasdk_filter_brannan, FilterType.SHARPEN));
        effect_list.add(new Filter_Effect_Info("RGB", R.drawable.camerasdk_filter_early_bird, FilterType.RGB));
        effect_list.add(new Filter_Effect_Info("LOMO", R.drawable.camerasdk_filter_lomo, FilterType.I_LOMO));
        effect_list.add(new Filter_Effect_Info("回忆", R.drawable.camerasdk_filter_lord_kelvin, FilterType.I_LORDKELVIN));
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
    public static void addFilter(Context context, ImageFilterTools.FilterType filterType, GPUImageView imageView) {
        GPUImageFilter filter;
        if (filterType == null) {
            filter = new GPUImageFilter();
        } else {
            filter = ImageFilterTools.createFilterForType(context, filterType);
        }
        imageView.setFilter(filter);
//		imageView.requestRender();
    }


}
