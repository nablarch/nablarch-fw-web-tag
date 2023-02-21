package nablarch.common.web.tag;

import jakarta.servlet.jsp.PageContext;

import nablarch.core.util.annotation.Published;

/**
 * 値をフォーマットするインタフェース。
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public interface ValueFormatter {
    /**
     * 指定されたパターンを使用して値をフォーマットする。
     * @param pageContext ページコンテキスト
     * @param name name属性の値
     * @param value 値
     * @param pattern パターン
     * @return フォーマット済みの値
     */
    String format(PageContext pageContext, String name, Object value, String pattern);
}
