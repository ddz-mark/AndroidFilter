package net.smartbetter.wonderful.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Markable on 2018/4/24.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class CommentEntity extends BmobObject {

    private UserEntity mUserEntity;
    private String commentContent;

    public UserEntity getUserEntity() {
        return mUserEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        mUserEntity = userEntity;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
