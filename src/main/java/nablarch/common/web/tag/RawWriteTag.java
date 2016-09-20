package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;

/**
 * HTMLエスケープ処理を行わないWriteTag
 * 
 * @see WriteTag
 * @see PrettyPrintTag
 * @author Iwauo Tajima
 */
public class RawWriteTag extends WriteTag {
    @Override
    public int doStartTag() throws JspException {
        setHtmlEscape(false);
        setWithHtmlFormat(false);
        return super.doStartTag();
    }
}
