package ke.tang.pageindicator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注Tab的信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Tab {
    /**
     * Tab的标题
     *
     * @return
     */
    int title() default 0;

    /**
     * Tab的图标
     *
     * @return
     */
    int icon() default 0;
}