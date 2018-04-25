package net.smartbetter.wonderful.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.entity.CommentEntity;
import net.smartbetter.wonderful.entity.NewsEntity;
import net.smartbetter.wonderful.utils.LogUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Markable on 2018/4/25.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class PopCommentAdapter extends RecyclerView.Adapter<PopCommentAdapter.ViewHolder> {

    private Context mContext;
    private List<CommentEntity> mCommentEntities;

    public PopCommentAdapter(Context context, List<CommentEntity> commentEntities) {
        mContext = context;
        mCommentEntities = commentEntities;
    }

    @Override
    public PopCommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_fragment_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CommentEntity commentEntity = mCommentEntities.get(position);

        //头像
        Glide.with(mContext)
                .load(commentEntity.getUserEntity().getAvatar().getFileUrl())
                .error(R.drawable.default_avatar)
                .into(holder.avatar);
        //姓名
        holder.author.setText(commentEntity.getUserEntity().getName());
        //位置
        holder.position.setText((position + 1) + " 楼");
        //内容
        holder.content.setText(commentEntity.getCommentContent());
    }


    @Override
    public int getItemCount() {
        return mCommentEntities != null ? mCommentEntities.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar; // 头像
        TextView author; // 作者
        TextView position;//位置
        TextView content;// 内容

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.item_comment_avevr);
            author = (TextView) itemView.findViewById(R.id.item_comment_name);
            position = (TextView) itemView.findViewById(R.id.item_comment_position);
            content = (TextView) itemView.findViewById(R.id.item_comment_content);
        }
    }
}
