package net.smartbetter.wonderful.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/16.
 * 返回给主页面的数据
 */

public class BLResultParam implements Parcelable {
    public static final String KEY = "result_param";

    private List<String> imageList = new ArrayList<>();


    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public BLResultParam() {
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.imageList);

    }

    protected BLResultParam(Parcel in) {
        this.imageList = in.createStringArrayList();
    }

    public static final Creator<BLResultParam> CREATOR = new Creator<BLResultParam>() {
        @Override
        public BLResultParam createFromParcel(Parcel source) {
            return new BLResultParam(source);
        }

        @Override
        public BLResultParam[] newArray(int size) {
            return new BLResultParam[size];
        }
    };
}
