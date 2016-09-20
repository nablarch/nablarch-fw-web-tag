package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.List;

/**
 * リスト表示に使用するフォーマットを表す列挙型。
 * @author Kiyohito Itoh
 */
public enum ListFormat {
    
    /** brタグ */
    BR {
        /** {@inheritDoc}} */
        public String getElementTag(HtmlAttributes attributes, String content) {
            return content + "<br />";
        }
    },
    
    /** divタグ */
    DIV {
        /** {@inheritDoc}} */
        public String getElementTag(HtmlAttributes attributes, String content) {
            return TagUtil.createTagWithBody("div", attributes, content);
        }
    },

    /** spanタグ */
    SPAN {
        /** {@inheritDoc}} */
        public String getElementTag(HtmlAttributes attributes, String content) {
            return TagUtil.createTagWithBody("span", attributes, content);
        }
    },
    
    /** ulタグタグ */
    UL {
        /** {@inheritDoc}} */
        public String getListStartTag(HtmlAttributes attributes) {
            return TagUtil.createStartTag("ul", attributes);
        }
        /** {@inheritDoc}} */
        public String getListEndTag() {
            return TagUtil.createEndTag("ul");
        }
        /** {@inheritDoc}} */
        public String getElementTag(HtmlAttributes attributes, String content) {
            return TagUtil.createTagWithBody("li", attributes, content);
        }
    },
    
    /** olタグタグ */
    OL {
        /** {@inheritDoc}} */
        public String getListStartTag(HtmlAttributes attributes) {
            return TagUtil.createStartTag("ol", attributes);
        }
        /** {@inheritDoc}} */
        public String getListEndTag() {
            return TagUtil.createEndTag("ol");
        }
        /** {@inheritDoc}} */
        public String getElementTag(HtmlAttributes attributes, String content) {
            return TagUtil.createTagWithBody("li", attributes, content);
        }
    },
    
    /** スペース */
    SP {
        /** {@inheritDoc}} */
        public String getElementTag(HtmlAttributes attributes, String content) {
            return content + "&nbsp;";
        }
    };
    
    /**
     * リスト要素を含める開始タグを取得する。
     * @param attributes 属性
     * @return リスト要素を含める開始タグ
     */
    public String getListStartTag(HtmlAttributes attributes) {
        return "";
    }
    
    /**
     * リスト要素を含める終了タグを取得する。
     * @return リスト要素を含める終了タグ
     */
    public String getListEndTag() {
        return "";
    }
    
    /**
     * リスト要素のタグを取得する。
     * @param attributes 属性
     * @param content タグの内容
     * @return リスト要素のタグ
     */
    public abstract String getElementTag(HtmlAttributes attributes, String content);
    
    /** リスト表示に使用するフォーマット */
    private static final List<String> FORMATS;
    
    static {
        FORMATS = new ArrayList<String>(ListFormat.values().length);
        for (ListFormat lf : ListFormat.values()) {
            FORMATS.add(lf.name().toLowerCase());
        }
    }
    
    /**
     * リスト表示に使用するフォーマットを取得する。
     * @return リスト表示に使用するフォーマット
     */
    public static List<String> getFormats() {
        return FORMATS;
    }
    
    /**
     * リスト表示に使用するフォーマットを取得する。
     * @param tagName タグ名
     * @return リスト表示に使用するフォーマット
     */
    public static ListFormat getFormatByTagName(String tagName) {
        return ListFormat.valueOf(tagName.toUpperCase());
    }
}
