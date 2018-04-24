package net.smartbetter.wonderful.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.entity.NewsEntity;
import net.smartbetter.wonderful.ui.activity.PhotoPreviewActivity;
import net.smartbetter.wonderful.utils.ActivityUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Markable on 2018/4/24.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public class NewNewsListAdapter extends RecyclerView.Adapter<NewNewsListAdapter.ViewHolder> {

    private Context context;
    private List<NewsEntity> list;

    public NewNewsListAdapter(Context context, List<NewsEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public NewNewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(NewNewsListAdapter.ViewHolder holder, int position) {
        final NewsEntity newsEntity = list.get(position);

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

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PhotoPreviewActivity.getPhotoPreviewActivityIntent(context, newsEntity.getImg().getFileUrl());
                ActivityUtils.startActivity(context, intent);
            }
        });
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

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar);
            author = (TextView) itemView.findViewById(R.id.tv_author);
            sex = (ImageView) itemView.findViewById(R.id.img_sex);
            img = (ImageView) itemView.findViewById(R.id.iv_img);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            createdTime = (TextView) itemView.findViewById(R.id.tv_created_time);
        }
    }

}
