package net.smartbetter.wonderful.entity;

import android.app.Activity;

import net.smartbetter.wonderful.ui.activity.PhotoPickActivity;
import net.smartbetter.wonderful.utils.ActivityUtils;


/**
 * Created by Administrator on 2018/4/23.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class BLPickerParam {

    public static final int REQUEST_CODE_PHOTO_PICKER = 0x1003;

    public static void startActivity(Activity activity){
        ActivityUtils.startActivityForResult(activity, PhotoPickActivity.class, REQUEST_CODE_PHOTO_PICKER);
    }
}
