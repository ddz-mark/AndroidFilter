package net.smartbetter.wonderful.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.entity.CommentEntity;
import net.smartbetter.wonderful.entity.ItemNewsComment;
import net.smartbetter.wonderful.entity.LikeEntity;
import net.smartbetter.wonderful.entity.NewsEntity;
import net.smartbetter.wonderful.entity.UserEntity;
import net.smartbetter.wonderful.ui.activity.PhotoPreviewActivity;
import net.smartbetter.wonderful.utils.ActivityUtils;
import net.smartbetter.wonderful.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by Markable on 2018/4/24.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class NewNewsListAdapter extends RecyclerView.Adapter<NewNewsListAdapter.ViewHolder> {

    private Context context;
    private List<ItemNewsComment> list;

    public NewNewsListAdapter(Context context, List<ItemNewsComment> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public NewNewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsEntity newsEntity = list.get(position).getNewsEntity();
        final List<CommentEntity> commentEntityList = list.get(position).getCommentEntity();
        final List<LikeEntity> likeEntityList = list.get(position).getLikeEntity();

        //头像
        Glide.with(context)
                .load(newsEntity.getAuthor().getAvatar().getFileUrl())
                .error(R.drawable.default_avatar)
                .into(holder.avatar);

        //性别
        if (newsEntity.getAuthor().isSex()) {
            holder.sex.setImageResource(R.drawable.sex_boy);
        } else {
            holder.sex.setImageResource(R.drawable.sex_girl);
        }

        holder.author.setText(newsEntity.getAuthor().getName());
        if (newsEntity.getImg() != null) {
            holder.img.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(newsEntity.getImg().getFileUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .centerCrop()
                    .into(holder.img);
        } else {
            holder.img.setVisibility(View.GONE);
        }
        holder.content.setText(newsEntity.getContent());
        holder.createdTime.setText(newsEntity.getCreatedAt());

        boolean isLike = false;
        // 如果存在，就删除这条记录
        for (int i = 0; i < likeEntityList.size(); i++) {
            if (likeEntityList.get(i).getUserEntity().getName().equals(BmobUser.getCurrentUser(UserEntity.class).getName())) {
                isLike = true;
                break;
            }
        }
        if (isLike) {
            holder.like.setButtonDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_selected));
        } else {
            holder.like.setButtonDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.like_unselected_3));
        }
        if (likeEntityList.size() != 0) {
            holder.like.setText(" " + likeEntityList.size());
        } else {
            holder.like.setText("");
        }

        if (commentEntityList.size() != 0) {
            holder.comment.setText(" " + commentEntityList.size());
        } else {
            holder.comment.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar; // 头像
        TextView author; // 作者
        ImageView sex;
        ImageView img; // 图片
        TextView content; // 内容
        TextView createdTime; // 创建时间
        RadioButton like;
        RadioButton comment;
        RadioButton share;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
            author = (TextView) itemView.findViewById(R.id.tv_author);
            sex = (ImageView) itemView.findViewById(R.id.img_sex);
            img = (ImageView) itemView.findViewById(R.id.iv_img);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            createdTime = (TextView) itemView.findViewById(R.id.tv_created_time);
            like = (RadioButton) itemView.findViewById(R.id.fragment_user_radio_like);
            comment = (RadioButton) itemView.findViewById(R.id.fragment_user_radio_comment);
            share = (RadioButton) itemView.findViewById(R.id.fragment_user_radio_share);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PhotoPreviewActivity.getPhotoPreviewActivityIntent(context, list.get(getLayoutPosition()).getNewsEntity().getImg().getFileUrl());
                    ActivityUtils.startActivity(context, intent);
                }
            });
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCommentClickListener.onCommentClick(getLayoutPosition());
                }
            });
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnCommentClickListener.onLikeClick(getLayoutPosition());
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShort(context, "谢谢分享！");
                }
            });
        }
    }

    private OnCommentClickListener mOnCommentClickListener;//声明接口

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }

    public interface OnCommentClickListener {
        void onCommentClick(int position);

        void onLikeClick(int position);
    }

}
