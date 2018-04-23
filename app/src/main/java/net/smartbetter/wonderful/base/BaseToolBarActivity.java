package net.smartbetter.wonderful.base;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.smartbetter.wonderful.R;


/**
 * Created by Administrator on 2018/4/13.
 * Github: https://github.com/ddz-mark
 * Info:
 */

public abstract class BaseToolBarActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected FrameLayout mFlContent;
    protected AppCompatActivity mInstance;
    protected LayoutInflater mInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bl_activity_toolbar);
        mInstance = this;
        mInflater = LayoutInflater.from(this);
        getSupportActionBar().hide();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_base);
        mFlContent = (FrameLayout) findViewById(R.id.toolbar_base_fl);
        mFlContent.addView(mInflater.inflate(getContentLayoutId(), null));
        setToolBar(mToolbar);

        initView();
        otherLogic();
        setListener();
    }

    /**
     * 状态栏颜色
     * @param color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    protected void setToolBar(Toolbar toolbar){
//        toolbar.setBackgroundColor(R.color.colorPrimaryDark);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        customToolBarStyle();
    }

    /**
     * 内容布局
     * @return
     */
    protected abstract int getContentLayoutId();

    /**
     * 自定义toolbar的样式
     */
    protected abstract void customToolBarStyle();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 其他业务逻辑
     */
    protected abstract void otherLogic();

    /**
     * 初始化监听器
     */
    protected abstract void setListener();
}
