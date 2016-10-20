package nablarch.common.web.tag;

import javax.servlet.jsp.PageContext;

/**
 * @author Kiyohito Itoh
 */
public class CustomFormatter implements ValueFormatter {

    /**
     * {@inheritDoc}
     */
    public String format(PageContext pageContext, String name, Object value, String pattern) {
        return "@@@" + value + "@@@";
    }
}
