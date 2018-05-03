package net.smartbetter.wonderful.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Markable on 2018/5/2.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class ItemNewsComment implements Serializable {
    private NewsEntity mNewsEntity;
    private List<LikeEntity> mLikeEntity;
    private List<CommentEntity> mCommentEntity;

    public List<LikeEntity> getLikeEntity() {
        return mLikeEntity;
    }

    public void setLikeEntity(List<LikeEntity> likeEntity) {
        mLikeEntity = likeEntity;
    }

    public NewsEntity getNewsEntity() {
        return mNewsEntity;
    }

    public void setNewsEntity(NewsEntity newsEntity) {
        mNewsEntity = newsEntity;
    }

    public List<CommentEntity> getCommentEntity() {
        return mCommentEntity;
    }

    public void setCommentEntity(List<CommentEntity> commentEntity) {
        mCommentEntity = commentEntity;
    }
}
