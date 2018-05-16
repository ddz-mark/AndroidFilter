package net.smartbetter.wonderful.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.tencent.wstt.gt.client.GT;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.ui.fragment.NewNewsFragment;
import net.smartbetter.wonderful.ui.fragment.UserFragment;
import net.smartbetter.wonderful.utils.ActivityUtils;
import net.smartbetter.wonderful.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static net.smartbetter.wonderful.utils.ConstantUtils.REQUEST_CODE_PERMISSION;

/**
 * 主页
 * Created by gc on 2017/1/16.
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.main_radio)
    RadioGroup mMainRadio;
    @BindView(R.id.ib_photo)
    ImageButton mIbPhoto;

    private Fragment currentFragment;
    private NewNewsFragment newsFragment;
    private UserFragment userFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initNavBottom();
        initFragment();
    }

    /**
     * Initialize bottom navigation.
     */
    public void initNavBottom() {
        // 给radiogroup设置按钮选中状态监听事件
        mMainRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            // checkedId : 选中的按钮的id
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 3.切换界面操作
                // 判断选中的哪个按钮
                switch (checkedId) {
                    case R.id.rb_news:
                        // 新鲜事
                        switchFragment(newsFragment);
                        break;
                    case R.id.rb_user:
                        // 我的
                        switchFragment(userFragment);
                        break;
                    default:
                        break;
                }
            }
        });
        mIbPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPhotoPickActivity();
            }
        });
        // 设置默认选中首页
        mMainRadio.check(R.id.rb_news);
    }


    /**
     * Initialize fragment.
     */
    private void initFragment() {
        newsFragment = new NewNewsFragment();
        userFragment = new UserFragment();
        setDefaultFragment(newsFragment);
    }

    /**
     * Set default fragment.
     */
    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        currentFragment = fragment;
    }

    /**
     * Switch fragment.
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {
        if (fragment != currentFragment) {
            if (!fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(currentFragment)
                        .add(R.id.frame_layout, fragment).addToBackStack(null).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(currentFragment)
                        .show(fragment).addToBackStack(null).commit();
            }
            currentFragment = fragment;
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION)
    private void gotoPhotoPickActivity() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ActivityUtils.startActivity(MainActivity.this, PhotoPickActivity.class);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问读写权限", REQUEST_CODE_PERMISSION, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            ToastUtils.showShort(this, "您拒绝了读取图片的权限");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // 记录用户首次点击返回键的时间
    private long firstTime = 0;

    /**
     * 监听keyUp 实现双击退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtils.showShort(this, "再按一次退出程序");
                firstTime = secondTime;
                return true;
            } else {
//                GT测试工具退出应用
                GT.disconnect(getApplicationContext());
                finish();
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}