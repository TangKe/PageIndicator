package ke.tang.pageindicator;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewParent;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ke.tang.tab.TabLayout;
import ke.tang.tab.Tab;
import ke.tang.tab.TabFactory;

/**
 * 生成对应{@link ViewPager}指示器, 默认情况下, 该指示器包含图标, 文本, Tab总宽度不足会自动均分宽度空间,
 * Tab宽度超过该指示器宽度, 会提供滚动功能
 * 具体用法, 在布局中
 * <android.support.v4.view.ViewPager
 * android:layout_width="match_parent"
 * android:layout_height="match_parent">
 * <ke.tang.LenderPageIndicator
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * android:layout_gravity="bottom"/>
 * </android.support.v4.view.ViewPager>
 * <p>
 * 支持自定义属性
 * indicator: 用于指示当前选中的Tab, 会绘制在背景之上, tab之下
 * tabTextAppearance: 用于设置Tab的文本样式
 * Created by TangKe on 2017/1/4.
 */
@ViewPager.DecorView
public class PageIndicator extends TabLayout {
    private ViewPager mPager;
    private PageListener mPageListener = new PageListener();

    private WeakReference<PagerAdapter> mAdapter;
    private LinkedList<Tab> mRecycler = new LinkedList<>();
    private IconProvider mIconProvider;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.pageIndicatorStyle);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (!(parent instanceof ViewPager)) {
            throw new IllegalStateException("父节点必须是ViewPager.");
        }

        final ViewPager pager = (ViewPager) parent;
        final PagerAdapter adapter = pager.getAdapter();

        pager.addOnPageChangeListener(mPageListener);
        pager.addOnAdapterChangeListener(mPageListener);
        mPager = pager;
        updateAdapter(mAdapter != null ? mAdapter.get() : null, adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRecycler.clear();
        if (mPager != null) {
            updateAdapter(mPager.getAdapter(), null);
            mPager.removeOnPageChangeListener(mPageListener);
            mPager.removeOnAdapterChangeListener(mPageListener);
            mPager = null;
        }
    }

    void updateAdapter(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mPageListener);
            mAdapter = null;
        }
        if (newAdapter != null) {
            newAdapter.registerDataSetObserver(mPageListener);
            mAdapter = new WeakReference<>(newAdapter);
        }

        if (newAdapter instanceof IconProvider) {
            mIconProvider = (IconProvider) newAdapter;
        }
        populateFromPagerAdapter();
    }

    @Override
    public void performTabClick(int index, Tab tab) {
        super.performTabClick(index, tab);
        if (null != mPager) {
            mPager.setCurrentItem(index);
        }
    }

    private void populateFromPagerAdapter() {
        final int tabCount = getTabCount();
        for (int index = 0; index < tabCount; index++) {
            Tab tab = getTabAt(0);
            removeTab(tab);
            mRecycler.offer(tab);
        }
        final PagerAdapter adapter = mAdapter != null ? mAdapter.get() : null;
        if (null != adapter) {
            final int adapterCount = adapter.getCount();
            for (int index = 0; index < adapterCount; index++) {
                Tab tab;
                CharSequence title = adapter.getPageTitle(index);
                if (null == mRecycler.peek()) {
                    tab = newTab();
                } else {
                    tab = mRecycler.poll();
                }
                prepareTab(tab, title, null == mIconProvider ? 0 : mIconProvider.getIcon(index));
                addTab(tab);
            }
        }
    }

    /**
     * 设置用于生成Tab图标的类, 默认会判断ViewPager的Adapter是否实现了IconProvider接口
     *
     * @param iconProvider
     */
    public void setIconProvider(IconProvider iconProvider) {
        mIconProvider = iconProvider;
        populateFromPagerAdapter();
        invalidateTabState();
    }

    @Override
    public void setTabFactory(TabFactory factory) {
        mRecycler.clear();
        populateFromPagerAdapter();
        super.setTabFactory(factory);
    }

    private void prepareTab(Tab tab, CharSequence title, @DrawableRes @XmlRes int iconRes) {
        tab.setText(title);
        tab.setIcon(iconRes);

        Drawable icon = tab.getIcon();
        if (null != icon) {
            icon.mutate();
        }
    }

    class PageListener extends DataSetObserver implements ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            updateAdapter(oldAdapter, newAdapter);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            offset(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            invalidateIndicator();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            invalidateIndicator();
        }

        @Override
        public void onChanged() {
            populateFromPagerAdapter();
        }
    }
}
