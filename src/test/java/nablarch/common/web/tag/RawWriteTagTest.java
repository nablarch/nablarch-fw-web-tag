package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;

public class RawWriteTagTest extends TagTestSupport<RawWriteTag> {
    
    public static final class Bean {
        private String text;
        public Bean(String text) {
            this.text = text;
        }
        public String getText() {
            return text;
        }
    }

    public RawWriteTagTest() {
        super(new RawWriteTag());
    }

    @Test
    public void testRawWrite() throws Exception {
        Bean bean = new Bean("<script>hoge</script>");
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put("bean", bean);
        target.setName("bean.text");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        String actual = TagTestUtil.getOutput(pageContext);
        TagTestUtil.assertTag(actual, "<script>hoge</script>", " ");
    }
    
    /**
     * 入力画面で配列の要素がnullの場合は空文字列が出力されること
     * @throws Exception
     */
    @Test
    public void testInputPageArrayWithValue() throws Exception {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("array", new String[] {"<script>hoge</script>"});

        target.setName("array");

        target.doStartTag();
        target.doEndTag();

        final String actual = TagTestUtil.getOutput(pageContext);
        assertThat(actual, is("<script>hoge</script>"));
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
