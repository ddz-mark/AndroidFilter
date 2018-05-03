package net.smartbetter.wonderful.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.adapter.NewNewsListAdapter;
import net.smartbetter.wonderful.adapter.PopCommentAdapter;
import net.smartbetter.wonderful.base.BaseFragment;
import net.smartbetter.wonderful.entity.CommentEntity;
import net.smartbetter.wonderful.entity.FolderInfo;
import net.smartbetter.wonderful.entity.ItemNewsComment;
import net.smartbetter.wonderful.entity.LikeEntity;
import net.smartbetter.wonderful.entity.NewsEntity;
import net.smartbetter.wonderful.entity.UserEntity;
import net.smartbetter.wonderful.entity.event.RefreshData;
import net.smartbetter.wonderful.entity.event.RefreshNews;
import net.smartbetter.wonderful.ui.activity.ShareActivity;
import net.smartbetter.wonderful.utils.ActivityUtils;
import net.smartbetter.wonderful.utils.ConstantUtils;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.utils.RxBus;
import net.smartbetter.wonderful.utils.ToastUtils;
import net.smartbetter.wonderful.view.LoadMoreRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by Markable on 2018/4/24.
 * Github: https://github.com/ddz-mark
 * Info:
 * TODO:评论请求在item请求之后未完成，造成网络请求渲染太慢
 */

public class NewNewsFragment extends BaseFragment {

    @BindView(R.id.talking_recyclerView)
    LoadMoreRecyclerView mTalkingRecyclerView;
    @BindView(R.id.talking_swiprefresh)
    SwipeRefreshLayout mTalkingSwiprefresh;

    private LinearLayout mLayout;
    private NewNewsListAdapter mAdapter;
    private int currentPage = 0; // 当前页面
    private static final int LIMIT = 4; // 每页的数据是4条
    private List<ItemNewsComment> mItemNewsCommentList = new ArrayList<>();

    private Subscription mSubscription;

    private RecyclerView recyclerView;
    private PopCommentAdapter mPopCommentAdapter;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.app_name);
        setHasOptionsMenu(true); // 配置Actionbar可先的属性
        if (mLayout == null) {
            mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_new_news, container, false);
            ButterKnife.bind(this, mLayout);
            initView();
            setListener();
        }
        getData(0);
        return mLayout;
    }

    private void initView() {
        mTalkingSwiprefresh.setRefreshing(true);
        mTalkingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTalkingRecyclerView.setPAGE_SIZE(LIMIT);

        mAdapter = new NewNewsListAdapter(getActivity(), mItemNewsCommentList);
        mTalkingRecyclerView.setAdapter(mAdapter);
    }

    private void setListener() {
        mSubscription = RxBus.getDefault().toObserverable(RefreshNews.class).subscribe(new Action1<RefreshNews>() {
            @Override
            public void call(RefreshNews news) {
                mItemNewsCommentList.clear();
                getData(0);
            }
        });
        //下拉加载
        mTalkingSwiprefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTalkingSwiprefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mItemNewsCommentList.clear();
                        getData(0);
                    }
                }, 1000);
            }
        });
        //上拉刷新
        mTalkingRecyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                getData(currentPage + 1);
            }
        });
        mAdapter.setOnCommentClickListener(new NewNewsListAdapter.OnCommentClickListener() {
            @Override
            public void onCommentClick(int position) {
                if (UserEntity.getCurrentUser() != null) {
                    showPopupFolder(position);
                } else {
                    ToastUtils.showShort(getActivity(), getString(R.string.text_default_no_login));
                }
            }

            @Override
            public void onLikeClick(final int position) {
                if (UserEntity.getCurrentUser() != null) {
                    showIsLike(position);
                } else {
                    ToastUtils.showShort(getActivity(), getString(R.string.text_default_no_login));
                }
            }
        });
    }

    // 得到每一条数据(包括新闻以及对应的评论)
    public void getData(final int page) {

        BmobQuery<NewsEntity> query = new BmobQuery<>();
        query.order("-createdAt"); // 按时间降序查询
        query.include("author"); // 希望在查询帖子信息的同时也把发布人的信息查询出来
        query.setLimit(LIMIT);// 设置每页数据个数
        query.setSkip(page * LIMIT);
        //查找数据
        query.findObjects(new FindListener<NewsEntity>() {
            @Override
            public void done(List<NewsEntity> list, BmobException e) {
                if (e == null) {
                    currentPage = page;
                    for (int i = 0; i < list.size(); i++) {
                        ItemNewsComment mItemNewsComment = new ItemNewsComment();
                        mItemNewsComment.setNewsEntity(list.get(i));
                        mItemNewsComment.setLikeEntity(getLikeData(list.get(i).getObjectId()));
                        mItemNewsComment.setCommentEntity(getCommentData(list.get(i).getObjectId()));
                        mItemNewsCommentList.add(mItemNewsComment);
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "请求服务器异常,请稍后重试");
                }
            }
        });
    }

    private List<CommentEntity> getCommentData(String id) {
        final List<CommentEntity> mCommentList = new ArrayList<>();
        BmobQuery<CommentEntity> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.order("-createdAt"); // 按时间降序查询
        query.include("mUserEntity"); // 希望在查询帖子信息的同时也把发布人的信息查询出来
        query.setLimit(100);//设置每页数据个数
        //查找数据
        query.findObjects(new FindListener<CommentEntity>() {
            @Override
            public void done(List<CommentEntity> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        mCommentList.add(list.get(i));
                    }
                    mTalkingRecyclerView.notifyDataChange(currentPage, mItemNewsCommentList.size());
                } else {
                    ToastUtils.showShort(getActivity(), "请求服务器异常,请稍后重试");
                }
                if (null != mTalkingSwiprefresh) {
                    mTalkingSwiprefresh.setRefreshing(false);
                }
            }
        });
        return mCommentList;
    }

    private List<LikeEntity> getLikeData(String id) {
        final List<LikeEntity> mLikeList = new ArrayList<>();
        BmobQuery<LikeEntity> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.order("-createdAt"); // 按时间降序查询
        query.include("mUserEntity"); // 希望在查询帖子信息的同时也把发布人的信息查询出来
        query.setLimit(100);//设置每页数据个数
        //查找数据
        query.findObjects(new FindListener<LikeEntity>() {
            @Override
            public void done(List<LikeEntity> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        mLikeList.add(list.get(i));
                    }
                } else {
                    ToastUtils.showShort(getActivity(), "请求服务器异常,请稍后重试");
                }
            }
        });
        return mLikeList;
    }

    private void showPopupFolder(final int position) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.popwindows_comment, null);
        final PopupWindow mPopupWindow = new PopupWindow(view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        //设置屏幕透明度
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        mPopupWindow.setAnimationStyle(R.style.PopupWindow_comment_anim_style);

        recyclerView = (RecyclerView) view.findViewById(R.id.pop_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final EditText editText = (EditText) view.findViewById(R.id.pop_et);
        final Button button = (Button) view.findViewById(R.id.pop_send_bt);
        textView = (TextView) view.findViewById(R.id.pop_num_tv);
        final ImageView imageView = (ImageView) view.findViewById(R.id.pop_dismiss);
        textView.setText(mItemNewsCommentList.get(position).getCommentEntity().size() + " 条评论");

        mPopCommentAdapter = new PopCommentAdapter(getActivity(), mItemNewsCommentList.get(position).getCommentEntity());
        recyclerView.setAdapter(mPopCommentAdapter);

        //从底部显示
        mPopupWindow.showAtLocation(mLayout, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        //发送评论
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    ToastUtils.showShort(getActivity(), "请输入评论");
                } else {
                    button.setEnabled(false);
                    UserEntity userEntity = BmobUser.getCurrentUser(UserEntity.class);
                    final CommentEntity commentEntity = new CommentEntity();
                    commentEntity.setUserEntity(userEntity);
                    commentEntity.setId(mItemNewsCommentList.get(position).getNewsEntity().getObjectId());
                    commentEntity.setCommentContent(editText.getText().toString().trim());
                    commentEntity.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                editText.setText("");
                                mItemNewsCommentList.get(position).getCommentEntity().add(0, commentEntity);
                                mPopCommentAdapter.notifyDataSetChanged();
                                textView.setText(mItemNewsCommentList.get(position).getCommentEntity().size() + " 条评论");
                                getCommentData(mItemNewsCommentList.get(position).getNewsEntity().getObjectId());
                            } else {
                                ToastUtils.showShort(getApplicationContext(), "评论失败");
                            }
                            button.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void showIsLike(final int position) {
        UserEntity userEntity = BmobUser.getCurrentUser(UserEntity.class);
        boolean isLike = false;
        int index = 0;
        // 如果存在，就删除这条记录
        for (int i = 0; i < mItemNewsCommentList.get(position).getLikeEntity().size(); i++) {
            if (mItemNewsCommentList.get(position).getLikeEntity().get(i).getUserEntity().getName().equals(userEntity.getName())) {
                isLike = true;
                index = i;
                break;
            }
        }
        if (isLike) {
            LikeEntity likeEntity = new LikeEntity();
            likeEntity.setObjectId(mItemNewsCommentList.get(position).getLikeEntity().get(index).getObjectId());
            final int finalIndex = index;
            likeEntity.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mItemNewsCommentList.get(position).getLikeEntity().remove(finalIndex);
                        mTalkingRecyclerView.notifyDataChange(currentPage, mItemNewsCommentList.size());
                    } else {
                        ToastUtils.showShort(getApplicationContext(), "删除失败");
                    }
                }
            });
        } else {
            final LikeEntity likeEntity = new LikeEntity();
            likeEntity.setUserEntity(userEntity);
            likeEntity.setId(mItemNewsCommentList.get(position).getNewsEntity().getObjectId());
            likeEntity.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        mItemNewsCommentList.get(position).getLikeEntity().add(likeEntity);
                        mTalkingRecyclerView.notifyDataChange(currentPage, mItemNewsCommentList.size());
                    } else {
                        ToastUtils.showShort(getApplicationContext(), "点赞失败");
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share_news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                if (UserEntity.getCurrentUser() != null) {
                    ActivityUtils.startActivity(getActivity(), ShareActivity.class);
                } else {
                    ToastUtils.showShort(getActivity(), getString(R.string.text_default_no_login));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
