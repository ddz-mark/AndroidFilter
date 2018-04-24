package net.smartbetter.wonderful.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.adapter.NewNewsListAdapter;
import net.smartbetter.wonderful.base.BaseFragment;
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
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Markable on 2018/4/24.
 * Github: https://github.com/ddz-mark
 * Info:
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

    private Subscription mSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.app_name);
        setHasOptionsMenu(true); // 配置Actionbar可先的属性
        if (mLayout == null) {
            mLayout = (LinearLayout) inflater.inflate(R.layout.fragment_new_news, container, false);
            ButterKnife.bind(this, mLayout);
            initView();
        }
        getData(0);
        return mLayout;
    }

    private void initView() {
        mSubscription = RxBus.getDefault().toObserverable(RefreshNews.class).subscribe(new Action1<RefreshNews>() {
            @Override
            public void call(RefreshNews news) {
                newsEntitys.clear();
                getData(0);
            }
        });

        //下拉加载
//        mTalkingSwiprefresh.setProgressViewOffset(false, 0, DensityUtil.dip2px(getContext(), 24));
        mTalkingSwiprefresh.setRefreshing(true);
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
        mTalkingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTalkingRecyclerView.setPAGE_SIZE(LIMIT);
        //上拉刷新
        mTalkingRecyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                getData(currentPage + 1);
            }
        });
        mAdapter = new NewNewsListAdapter(getActivity(), newsEntitys);
        mTalkingRecyclerView.setAdapter(mAdapter);
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


}
