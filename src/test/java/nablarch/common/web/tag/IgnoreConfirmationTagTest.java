package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;

/**
 * {@link IgnoreConfirmationTag}のテスト。
 * @author Kiyohito Itoh
 */
public class IgnoreConfirmationTagTest extends TagTestSupport<IgnoreConfirmationTag> {

    public IgnoreConfirmationTagTest() {
        super(new IgnoreConfirmationTag());
    }

    /**
     * 入れ子で使用された場合。
     */
    @Test
    public void testInvalidLocation() throws Exception {

        target.doStartTag();
        try {
            target.doStartTag(); // nested
            fail("IllegalStateExceptionがスローされる");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(),
                       is("invalid location of the ignoreConfirmation tag. "
                        + "the ignoreConfirmation tag cannot be nested."));
        }
    }

    /**
     * 入力画面で動作すること。
     */
    @Test
    public void testInputPage() throws Exception {

        // リクエストスコープに画面状態が何も入っていない場合
        assertThat(TagUtil.isConfirmationPage(pageContext), is(false));
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(TagUtil.isConfirmationPage(pageContext), is(false));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagUtil.isConfirmationPage(pageContext), is(false));

        // リクエストスコープに画面状態＝falseが入っている場合
        TagUtil.setInputPage(pageContext);
        assertThat(TagUtil.isConfirmationPage(pageContext), is(false));
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(TagUtil.isConfirmationPage(pageContext), is(false));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagUtil.isConfirmationPage(pageContext), is(false));
    }

    /**
     * 確認画面で動作すること。
     */
    @Test
    public void testConfirmationPage() throws Exception {

        // リクエストスコープに画面状態＝trueが入っている場合
        TagUtil.setConfirmationPage(pageContext);
        assertThat(TagUtil.isConfirmationPage(pageContext), is(true));
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(TagUtil.isConfirmationPage(pageContext), is(false));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagUtil.isConfirmationPage(pageContext), is(true));
    }
}
