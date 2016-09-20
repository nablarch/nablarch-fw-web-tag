package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.List;

import nablarch.core.util.StringUtil;

/**
 * autocomplete属性をOFFにする対象を表す列挙型。
 * 
 * @author Kiyohito Itoh
 * @version 1.1
 */
public enum AutocompleteDisableTarget {

    /** すべてのタグ */
    ALL,

    /** パスワードのみ */
    PASSWORD,

    /** 対象なし */
    NONE;

    /** autocomplete属性をOFFにする対象 */
    private static final List<String> TARGETS;

    static {
        TARGETS = new ArrayList<String>(AutocompleteDisableTarget.values().length);
        for (AutocompleteDisableTarget target : AutocompleteDisableTarget.values()) {
            TARGETS.add(target.name().toLowerCase());
        }
    }

    /**
     * autocomplete属性をOFFにする対象を取得する。
     * @return autocomplete属性をOFFにする対象
     */
    public static List<String> getTargets() {
        return TARGETS;
    }

    /**
     * 対象を表す文字列がこの列挙型に含まれているかを判定する。
     * 
     * 大文字小文字を区別せずに列挙型の名前と比較する。
     * 
     * @param target 対象を表す文字列
     * @return 対象を表す文字列がこの列挙型に含まれている場合はtrue
     */
    public static boolean contains(String target) {
        if (StringUtil.isNullOrEmpty(target)) {
            return false;
        }
        return getTargets().contains(target.toLowerCase());
    }
}

