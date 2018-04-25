package net.smartbetter.wonderful.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import net.smartbetter.wonderful.entity.NewsEntity;
import net.smartbetter.wonderful.entity.UserEntity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

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
    private static final int LIMIT = 4; // 每页的数据是5条
    private List<NewsEntity> newsEntitys = new ArrayList<>();
    private List<CommentEntity> mCommentEntities = new ArrayList<>();

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

        mAdapter = new NewNewsListAdapter(getActivity(), newsEntitys);
        mTalkingRecyclerView.setAdapter(mAdapter);
    }

    private void setListener() {
        mSubscription = RxBus.getDefault().toObserverable(RefreshNews.class).subscribe(new Action1<RefreshNews>() {
            @Override
            public void call(RefreshNews news) {
                newsEntitys.clear();
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
                        newsEntitys.clear();
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
        });
    }

    public void getData(final int page) {
        BmobQuery<NewsEntity> query = new BmobQuery<>();
        query.order("-createdAt"); // 按时间降序查询
        query.include("author"); // 希望在查询帖子信息的同时也把发布人的信息查询出来
        query.setLimit(LIMIT);//设置每页数据个数
        query.setSkip(page * LIMIT);
        //查找数据
        query.findObjects(new FindListener<NewsEntity>() {
            @Override
            public void done(List<NewsEntity> list, BmobException e) {
                if (e == null) {
                    currentPage = page;
                    newsEntitys.addAll(list);
                    mTalkingRecyclerView.notifyDataChange(currentPage, newsEntitys.size());
                } else {
                    ToastUtils.showShort(getActivity(), "请求服务器异常,请稍后重试");
                }
                if (null != mTalkingSwiprefresh) {
                    mTalkingSwiprefresh.setRefreshing(false);
                }
            }
        });
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

        mPopCommentAdapter = new PopCommentAdapter(getActivity(), mCommentEntities);
        recyclerView.setAdapter(mPopCommentAdapter);
        getCommentData(newsEntitys.get(position).getObjectId());

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
                    CommentEntity commentEntity = new CommentEntity();
                    commentEntity.setUserEntity(userEntity);
                    commentEntity.setId(newsEntitys.get(position).getObjectId());
                    commentEntity.setCommentContent(editText.getText().toString().trim());
                    commentEntity.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                editText.setText("");
                                getCommentData(newsEntitys.get(position).getObjectId());
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

    private void getCommentData(String id) {
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
                    mCommentEntities.clear();
                    mCommentEntities.addAll(list);
                    textView.setText(mCommentEntities.size() + " 条评论");
                    mPopCommentAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showShort(getActivity(), "请求服务器异常,请稍后重试");
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
}
