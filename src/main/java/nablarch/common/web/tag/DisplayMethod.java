package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.List;

/**
 * フォームのサブミットを行うタグを表示制御する場合の表示方法を表す列挙型
 * 
 * @author Tomokazu Kagawa
 */
public enum DisplayMethod {
    
    /**
     * 非表示を表す。
     */
    NODISPLAY,
    
    /**
     * 非活性を表す。
     */
    DISABLED,
    
    /**
     * 通常表示を表す。（非表示も非活性も行わない）
     */
    NORMAL;

    /**
     * 設定可能な表示方法一覧
     */
    private static final List<String> ALLOWED_DISPLAY_METHODS = new ArrayList<String>();

    static {
        for (DisplayMethod allowedDisplayMethod : values()) {
            ALLOWED_DISPLAY_METHODS.add(allowedDisplayMethod.name());
        }
    }

    /**
     * {@link DisplayMethod}を取得する。</br>
     * 表示方法が指定可能か否かをチェックし、指定可能な場合は対応する{@link DisplayMethod}を取得する。 
     * 指定できない表示方法の場合は、実行時例外を発生する。
     * 
     * @param displayMethod サブミットを行うタグの表示制御を行う場合の表示方法
     * @return 指定された文字列に対応する{@link DisplayMethod}
     */
    static DisplayMethod getDisplayMethod(String displayMethod) {

        if (!ALLOWED_DISPLAY_METHODS.contains(displayMethod)) {
            throw new IllegalArgumentException(
                    String.format(
                            "displayMethod was invalid. displayMethod must specify the following values. values = %s displayMethod = [%s]",
                            ALLOWED_DISPLAY_METHODS, displayMethod));
        }
        return valueOf(displayMethod);

    }
}
