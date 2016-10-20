package nablarch.common.web.tag;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Kiyohito Itoh
 */
@SuppressWarnings("serial")
public class XHtmlTagSupportTest {

    @Test
    public void testDoCatch() throws Throwable {
        HtmlTagSupport tag = new HtmlTagSupport() {};
        try {
            tag.doCatch(new IllegalArgumentException("test_message"));
            fail("must be thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("test_message"));
        }
    }
    
    @Test
    public void testDoFinally() {
        
        GenericAttributesTagSupport tag = new GenericAttributesTagSupport() {

            @Override
            protected String getTagName() {
                return "test";
            }};
        
        assertTrue(tag.getAttributes().toHTML("input").length() == 0);
        
        tag.setId("id_test");
        assertTrue(tag.getAttributes().toHTML("input").length() != 0);
        
        tag.doFinally();
        assertTrue(tag.getAttributes().toHTML("input").length() == 0);
    }
}
