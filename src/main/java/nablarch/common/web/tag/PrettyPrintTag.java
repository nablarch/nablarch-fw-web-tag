package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;

/**
 * 事前に登録されたHTMLタグをそのまま出力する。
 * 
 * @see WriteTag
 * @see RawWriteTag
 * @author Iwauo Tajima
 */
public class PrettyPrintTag extends WriteTag {
    
    /** デフォルト値 */
    private static final String[] DEFAULT_SAFE_TAGS = {
        "b", "big", "blockquote", "br", "caption", "center"
      , "dd", "del", "dl", "dt", "em", "font", "h1", "h2", "h3"
      , "hr", "i",  "ins",  "li", "ol",  "p", "small", "strong"
      , "sub", "sup", "table", "td", "th", "tr", "u", "ul"
    };

    /** デフォルト値 */
    private static final String[] DEFAULT_SAFE_ATTRIBUTES = {
        "color", "size",  "border",  "colspan",  "rowspan", "bgcolor"
    };
    
    @Override
    public int doStartTag() throws JspException {
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        
        String[] safeTags = (config.getSafeTags() == null)
                          ? DEFAULT_SAFE_TAGS
                          : config.getSafeTags();
          
        String[] safeAttributes = (config.getSafeAttributes() == null)
                                ? DEFAULT_SAFE_ATTRIBUTES
                                : config.getSafeAttributes();
        setHtmlEscape(true);
        setWithHtmlFormat(false);
        setSafeTags(safeTags);
        setSafeAttributes(safeAttributes);
        return super.doStartTag();
    }
}
