package com.yyh.status.key;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


import com.yyh.status.key.base.BaseActivity;
import com.yyh.status.key.R;
import com.yyh.status.key.inteml.ActionBarClickListener;
import com.yyh.status.key.widget.TranslucentActionBar;
import com.yyh.status.key.widget.TranslucentScrollView;

/**
 * 类功能描述：</br>
 *  Android沉浸式状态栏 + scrollView顶部伸缩 + actionBar渐变
 * 博客地址：http://blog.csdn.net/androidstarjack
 * 公众号：终端研发部
 * @author yuyahao
 * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
 */
public class MainActivity extends BaseActivity implements ActionBarClickListener, TranslucentScrollView.TranslucentChangedListener {

    private TranslucentScrollView translucentScrollView;
    private TranslucentActionBar actionBar;
    private View zoomView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        actionBar = (TranslucentActionBar) findViewById(R.id.actionbar);
        //初始actionBar
        actionBar.setData("我的", 0, null, 0, null, null);
        //开启渐变
        actionBar.setNeedTranslucent();
        //设置状态栏高度
        actionBar.setStatusBarHeight(getStatusBarHeight());

        translucentScrollView = (TranslucentScrollView) findViewById(R.id.pullzoom_scrollview);
        //设置透明度变化监听
        translucentScrollView.setTranslucentChangedListener(this);
        //关联需要渐变的视图
        translucentScrollView.setTransView(actionBar);

        zoomView = findViewById(R.id.lay_header);
        //关联伸缩的视图
        translucentScrollView.setPullZoomView(zoomView);
    }

    @Override
    public void onLeftClick() {

    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onTranslucentChanged(int transAlpha) {
        actionBar.tvTitle.setVisibility(transAlpha > 48 ? View.VISIBLE : View.GONE);
    }

}
