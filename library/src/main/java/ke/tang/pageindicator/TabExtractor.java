package ke.tang.pageindicator;

import java.util.HashMap;

/**
 * 提取{@link Tab}注解信息
 * Created by TangKe on 2017/1/6.
 */
public class TabExtractor {
    private static HashMap<Class<?>, Tab> sCache = new HashMap<>();

    public static Tab extractTab(Class<?> clazz) {
        Tab tab = sCache.get(clazz);
        if (null == tab) {
            tab = clazz.getAnnotation(Tab.class);
            sCache.put(clazz, tab);
        }
        return tab;
    }
}
