package net.smartbetter.wonderful.entity;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;


import net.smartbetter.wonderful.ui.activity.BeautifyImageActivity;
import net.smartbetter.wonderful.utils.ActivityUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/4/15.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class BLBeautifyParam implements Parcelable {
    public static final String KEY = "beautify_image";

    public static final int REQUEST_CODE_BEAUTIFY_IMAGE = 2;

    private List<String> images;

    public BLBeautifyParam(){}

    public BLBeautifyParam(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.images);
    }

    protected BLBeautifyParam(Parcel in) {
        this.images = in.createStringArrayList();
    }

    public static final Creator<BLBeautifyParam> CREATOR = new Creator<BLBeautifyParam>() {
        @Override
        public BLBeautifyParam createFromParcel(Parcel source) {
            return new BLBeautifyParam(source);
        }

        @Override
        public BLBeautifyParam[] newArray(int size) {
            return new BLBeautifyParam[size];
        }
    };

    public static void startActivity(Activity activity, BLBeautifyParam param){
        Intent intent = new Intent(activity, BeautifyImageActivity.class);

        intent.putExtra(KEY, param);
        ActivityUtils.startActivityForResult(activity, intent, REQUEST_CODE_BEAUTIFY_IMAGE);
    }

}
