package net.smartbetter.wonderful.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Markable on 2018/4/24.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class CommentEntity extends BmobObject {

    private String id;//对某一分享的评论
    private UserEntity mUserEntity;
    private String commentContent;

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

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
