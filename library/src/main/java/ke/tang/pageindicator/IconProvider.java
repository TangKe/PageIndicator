package ke.tang.pageindicator;

/**
 * 用于为{@link PageIndicator}提供图标数据源
 */
public interface IconProvider {
    int getIcon(int position);
}