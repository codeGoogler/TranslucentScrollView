
最近需求要做一个拉缩渐变的状态栏，往上拉的时候，需要显示actionBar，这个过程是渐变的，顶部的图片背景能实现拉缩，并且还要实现状态栏沉浸式

效果如如下：

![ ](http://upload-images.jianshu.io/upload_images/4614633-241c7af9606332e4.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 实现状态栏的透明化
- 实现ScrollView的拉缩
- 实现ActionBar的渐变

### 实现
1、至于试下实现ScrollView的拉缩这个效果很简单重写onTouchEvent方法，利用滑动的垂直方向的距离，然后在设置图片的大小
```
......
 case MotionEvent.ACTION_MOVE:
                    if (!mScaling) {
                        if (getScrollY() == 0) {
                            mFirstPosition = event.getY();
                        } else {
                            break;
                        }
                    }

                    int distance = (int) ((event.getY() - mFirstPosition) * 0.6);
                    if (distance < 0) {
                        break;
                    }
                    mScaling = true;
                    params.height = zoomViewInitHeight + distance;

                    Log.d(TAG, "params.height == " + params.height + ", zoomViewInitHeight == " + zoomViewInitHeight + ", distance == " + distance);
                    zoomView.setLayoutParams(params);
                    return true;
```
**这里要注意的是**：在手指释放的时候需要进行恢复图片的高度

2、ActionBar的透明度很简单了，在onScrollChanged里进行操作即可
```
 @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int transAlpha = getTransAlpha();

        if (transView != null) {
            Log.d(TAG, "[onScrollChanged .. in ], 透明度 == " + transAlpha);
            transView.setBackgroundColor(ColorUtils.setAlphaComponent(transColor, transAlpha));
        }
        if (translucentChangedListener != null) {
            translucentChangedListener.onTranslucentChanged(transAlpha);
        }
    }

```

3、至于沉浸式状态栏就很简单了，之前写过帖子

- [你这样玩过android沉浸式状态栏吗—教你玩出新花样](http://mp.weixin.qq.com/s?__biz=MzI3OTU0MzI4MQ==&mid=2247484479&idx=1&sn=017393b284bc9f746006994a1499dfc8&chksm=eb4768a1dc30e1b7b736c8055a511fb3aea9cc186442ecb7979b15665b59c9cba33a41226a6f&scene=21#wechat_redirect)​

- [ Activity样式 、状态栏透明、屏幕亮度问题全面解析](http://mp.weixin.qq.com/s?__biz=MzI3OTU0MzI4MQ==&mid=2247484366&idx=1&sn=93c36500ee10081ce8e7d319beaf0bf0&chksm=eb476f50dc30e646c0796a2614dc698c83c9023b800c3d7d80acdd09f1f9eab8f98649b8e0b7&scene=21#wechat_redirect)


这里我简单的封装了一些工具类
####

```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
```

在相应的Activity或基类执行这段代码就ok了。

可见在4.4到5.0的系统、5.0及以上系统的处理方式有所不同

除了这种代码修改额方式外，还可以通过主题来修改，需要在values、values-v19、values-v21目录下分别创建相应的主题：

```
//values
<style name="TranslucentTheme" parent="AppTheme">
</style>//values-v19<style name="TranslucentTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:windowTranslucentNavigation">false</item>
</style>//values-v21<style name="TranslucentTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:windowTranslucentNavigation">false</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
</style>
```

给相应Activity或Application设置该主题就ok了。

两种方式根据需求选择就好了，到这里我们就完成了第一步，将状态栏透明化了。

完成了第一步，我们开始给状态栏加上想要的色彩吧！

在values、values-v19目录添加如下尺寸：

```
//values<dimen name="padding_top">0dp</dimen>//values-v19<dimen name="padding_top">25dp</dimen>
```

关于25dp，在有些系统上可能有误差，这里不做讨论！

2.1 页面顶部使用Toolbar（或自定义title） 一般情况状态栏的颜色和Toolbar的颜色相同，既然状态栏透明化后，布局页面延伸到了状态栏，何不给Toolbar加上一个状态栏高度的顶部padding呢：

```
<android.support.v7.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/colorPrimary"
    android:paddingTop="@dimen/padding_top"
    android:theme="@style/AppTheme.AppBarOverlay" />
```

效果图下：

![](http://mmbiz.qpic.cn/mmbiz_png/CvQa8Yf8vq0NMYcSc4Bg8pKHPqFdo3ibkkHaQZFZTxk9yc0nJogzoJCGJECTicAApmbu1eoWzobYBtia9xD5E4sbw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)


![](http://upload-images.jianshu.io/upload_images/4614633-fa4d65185f306862.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

[最新2017（Android）面试题级答案（精选版）](https://mp.weixin.qq.com/s/C-S8Gs5wfW9OOIS_UJBzqw)

[“你还有什么事想问”——如何回答面试官的问题](http://mp.weixin.qq.com/s?__biz=MzI3OTU0MzI4MQ==&mid=2247484208&idx=1&sn=9f5292b50fd2198e13e4963e5ed2973d&chksm=eb476faedc30e6b8889106a9dac9ea3ea17da8e93fd32fbc6876ef4711537ccb97261e06ed8f&scene=21#wechat_redirect)

[Android 图片选择到裁剪之步步深坑](http://mp.weixin.qq.com/s?__biz=MzI3OTU0MzI4MQ==&mid=2247484873&idx=1&sn=ff61bb74db725970d939a7b40ab0e06e&chksm=eb476957dc30e0417f04e9463949482d52ec30e181d38029f0dd18388b58448d067404678839&scene=21#wechat_redirect)

>博客地址：
>
> http://www.jianshu.com/p/05aa5329c3d3

> 项目地址：
>
> https://github.com/androidstarjack/TranslucentScrollView


#### 相信自己，没有做不到的，只有想不到的

如果你觉得此文对您有所帮助， 欢迎加入微信公众号：终端研发部

![技术+职场](http://upload-images.jianshu.io/upload_images/4614633-a21e0b7f6fae5a81?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


