package net.smartbetter.wonderful.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Markable on 2018/5/3.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class LikeEntity extends BmobObject {

    private UserEntity mUserEntity;
    private String id;// 对某一分享的点赞

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUserEntity() {
        return mUserEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        mUserEntity = userEntity;
    }
}
