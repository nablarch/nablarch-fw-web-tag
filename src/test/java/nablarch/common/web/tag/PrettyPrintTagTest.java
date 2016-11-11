package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;

public class PrettyPrintTagTest extends TagTestSupport<PrettyPrintTag> {
    
    public static final class Bean {
        private String text;
        public Bean(String text) {
            this.text = text;
        }
        public String getText() {
            return text;
        }
        
    }

    public PrettyPrintTagTest() {
        super(new PrettyPrintTag());
    }

    @Test
    public void testWithDefaultPermittedTag() throws Exception {
        Bean bean = new Bean("<script>hoge</script>");
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("bean", bean);
        target.setName("bean.text");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        String actual = TagTestUtil.getOutput(pageContext);
        TagTestUtil.assertTag(actual, "&lt;script&gt;hoge&lt;/script&gt;", " ");
    }
    
    /**
     * 配列の単一要素に値がある場合その値が出力されること
     * @throws Exception
     */
    @Test
    public void testInputPageArrayWithValue() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("array", new String[] {"abc"});

        target.setName("array");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("abc"));
    }
    
    /**
     * 配列の単一要素に値がある場合その値が出力されること
     * @throws Exception
     */
    @Test
    public void testInputPageListWithValue() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("array", Collections.singletonList("<a"));

        target.setName("array");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("&lt;a"));
    }
    
    /**
     * 入力画面で配列の要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testInputPageArrayWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("array", new String[] {null});

        target.setName("array");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }

    /**
     * 入力画面で配列の要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testInputPageListWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("list", Collections.singletonList(null));

        target.setName("list");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }
    
    /**
     * 確認画面で配列の要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testConfirmationPageArrayWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("array", new String[] {null});

        target.setName("array");

        TagUtil.setConfirmationPage(pageContext);

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }

    /**
     * 確認画面で配列の要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testConfirmationPageListWithNull() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("list", Collections.singletonList(null));

        target.setName("list");

        TagUtil.setConfirmationPage(pageContext);
        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is(""));
    }
}
