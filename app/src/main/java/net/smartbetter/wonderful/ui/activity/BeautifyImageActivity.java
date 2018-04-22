package net.smartbetter.wonderful.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.muzhi.camerasdk.library.views.HorizontalListView;


import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.adapter.Filter_Effect_Adapter;
import net.smartbetter.wonderful.adapter.FragmentViewPagerAdapter;
import net.smartbetter.wonderful.entity.BLBeautifyParam;
import net.smartbetter.wonderful.entity.BLResultParam;
import net.smartbetter.wonderful.entity.Filter_Effect_Info;
import net.smartbetter.wonderful.ui.fragment.BLBeautifyFragment;
import net.smartbetter.wonderful.utils.BLConfigManager;
import net.smartbetter.wonderful.utils.FilterUtils;
import net.smartbetter.wonderful.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2018/4/15.
 * 美化图片
 */

public class BeautifyImageActivity extends BaseToolBarActivity {
    private static int TXT_NORMAL_COLOR = Color.BLACK;
    private static int TXT_SELECTED_COLOR;

    private CustomViewPager mViewPager;
    private RelativeLayout mRlContainer;
    private HorizontalListView mHlvFilter;

    private Filter_Effect_Adapter mFilterAdapter;
    private List<Filter_Effect_Info> mFilterData = new ArrayList<>(); //特效
    private List<String> imageList;

    private FragmentViewPagerAdapter fAdapter;
    private List<Fragment> fragments;
    private int curPosition;

    private BLBeautifyParam mParam;


    @Override
    protected int getContentLayoutId() {
        return R.layout.bl_activity_beautify_image;
    }

    @Override
    protected void customToolBarStyle() {
        mToolbar.inflateMenu(R.menu.menu_preview);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.preview_menu) {
                    Observable.create(new Observable.OnSubscribe<BLResultParam>() {
                        @Override
                        public void call(Subscriber<? super BLResultParam> subscriber) {
                            BLResultParam resultParam = new BLResultParam();
                            List<String> mergeList = new ArrayList<>();

                            for (Fragment f : fragments) {
                                if (f instanceof BLBeautifyFragment) {
                                    BLBeautifyFragment fragment = (BLBeautifyFragment) f;
//                                    fragment.complete();
                                    String path = fragment.complete();
                                    if (path != null && !path.equals("")) {
                                        mergeList.add(path);
                                    }
                                }
                            }
                            resultParam.setImageList(mergeList);
                            subscriber.onNext(resultParam);
                            subscriber.onCompleted();
                        }
                    })
                            .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                            .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                            .subscribe(new Subscriber<BLResultParam>() {
                                @Override
                                public void onCompleted() {
                                    toast("图片已保存");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    toast("图片保存失败");
                                }

                                @Override
                                public void onNext(BLResultParam param) {
                                    Intent intent = new Intent();
                                    intent.putExtra(BLResultParam.KEY, param);
                                    setResult(RESULT_OK, intent);
                                    onBackPressed();
                                }
                            });

                }
                return false;
            }
        });
    }

    @Override
    protected void initView() {
        mViewPager = getViewById(R.id.beautify_image_viewpager);
        mRlContainer = getViewById(R.id.content_container);
        mHlvFilter = getViewById(R.id.effect_listview);
    }

    @Override
    protected void otherLogic() {
        TXT_SELECTED_COLOR = BLConfigManager.getPrimaryColor();
        mParam = getIntent().getParcelableExtra(BLBeautifyParam.KEY);
        imageList = mParam.getImages();
        fragments = new ArrayList<>();
        for (String path : imageList) {
            fragments.add(BLBeautifyFragment.newInstance(path));
        }
        fAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPager, fragments);

        mViewPager.setAdapter(fAdapter);
        mViewPager.setCurrentItem(curPosition);
        mViewPager.setOffscreenPageLimit(imageList.size());

        mFilterData = FilterUtils.getEffectList();
        setToolbarTitle(curPosition);
        //默认滤镜被选中
        onFilterClick();
    }

    @Override
    protected void setListener() {

        mHlvFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mFilterAdapter.setSelectItem(position);
                final int itemWidth = view.getWidth();
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHlvFilter.scrollTo(itemWidth * (position - 1) - itemWidth / 4);
                    }
                }, 100);
                if (null != getCurrentFragment())
                    getCurrentFragment().addFilter(mFilterData.get(position).getFilterType());
            }
        });

        fAdapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                curPosition = i;
                setToolbarTitle(curPosition);
            }
        });

    }

    /**
     * 设置标题
     *
     * @param position
     */
    private void setToolbarTitle(int position) {
        mToolbar.setTitle("图片美化(" + (position + 1) + "/" + imageList.size() + ")");
    }

    private BLBeautifyFragment getCurrentFragment() {
        Fragment f = fragments.get(curPosition);
        if (f instanceof BLBeautifyFragment) {
            return (BLBeautifyFragment) f;
        } else {
            return null;
        }
    }


    private void onFilterClick() {
        mHlvFilter.setVisibility(View.VISIBLE);
        if (mFilterAdapter == null) {
            mFilterAdapter = new Filter_Effect_Adapter(mInstance, mFilterData);
            mHlvFilter.setAdapter(mFilterAdapter);
        }
    }


}
