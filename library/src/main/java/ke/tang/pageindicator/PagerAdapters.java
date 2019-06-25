package ke.tang.pageindicator;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * 提供便捷方式生成对应的{@link PagerAdapter}
 * Created by TangKe on 2017/1/5.
 */
public class PagerAdapters {

    /**
     * 创建{@link PagerAdapter}Builder类
     */
    public static class Builder {
        private Context mContext;

        public Builder(Context context) {
            mContext = context;
        }

        public FragmentPagerAdapterBuilder from(Fragment... fragments) {
            return new FragmentPagerAdapterBuilder(mContext, fragments);
        }

        public ViewPagerAdapterBuilder from(@LayoutRes int... layout) {
            //TODO 暂时未实现
            return null;
        }
    }

    public static class ViewPagerAdapterBuilder {
        @LayoutRes
        private int[] mLayoutRes;

        private ViewPagerAdapterBuilder(@LayoutRes int... layout) {
            mLayoutRes = layout;
        }
    }

    /**
     * 针对使用{@link Fragment}需要生成{@link PagerAdapter}的Builder类
     */
    public static class FragmentPagerAdapterBuilder {
        private Context mContext;
        private IconProvider mIconProvider;
        private CharSequence[] mTitles;
        private FragmentManager mFragmentManager;
        private Fragment[] mFragments;

        private FragmentPagerAdapterBuilder(Context context, Fragment[] fragments) {
            mContext = context;
            mFragments = fragments;
        }

        public FragmentPagerAdapterBuilder setIconProvider(IconProvider iconProvider) {
            mIconProvider = iconProvider;
            return this;
        }

        public FragmentPagerAdapterBuilder setTitles(CharSequence... titles) {
            mTitles = titles;
            return this;
        }

        public FragmentPagerAdapterBuilder setTitles(@ArrayRes int titleRes) {
            mTitles = mContext.getResources().getStringArray(titleRes);
            return this;
        }

        public PagerAdapter create(FragmentManager manager) {
            return new LenderFragmentPagerAdapter(mContext.getApplicationContext(), manager, mIconProvider, mTitles, mFragments);
        }
    }

    /**
     * 针对{@link Fragment}的{@link PagerAdapter}实现
     * 默认实现了图标数据源, 图标和标题的优先级如下
     * 1, 设置的{@link IconProvider}和CharSequence[]
     * 2, 对每一页对应的{@link Fragment}抽取{@link Tab}的数据
     */
    private static class LenderFragmentPagerAdapter extends FragmentPagerAdapter implements IconProvider {
        private Context mContext;
        private Fragment[] mFragments;
        private IconProvider mOptionalIconProvider;
        private CharSequence[] mTitles;

        public LenderFragmentPagerAdapter(Context context, FragmentManager fm, IconProvider iconProvider, CharSequence[] titles, Fragment[] fragments) {
            super(fm);
            mContext = context;
            mTitles = titles;
            mOptionalIconProvider = iconProvider;
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return null == mFragments ? 0 : mFragments.length;
        }

        @Override
        public int getIcon(int position) {
            if (null != mOptionalIconProvider) {
                return mOptionalIconProvider.getIcon(position);
            }

            Fragment item = getItem(position);
            Tab tab = TabExtractor.extractTab(item.getClass());
            if (null != tab) {
                return tab.icon();
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (null != mTitles && position < mTitles.length) {
                return mTitles[position];
            }

            Fragment item = getItem(position);
            Tab tab = TabExtractor.extractTab(item.getClass());
            if (null != tab) {
                return 0 >= tab.title() ? null : mContext.getString(tab.title());
            }
            return null;
        }
    }
}
