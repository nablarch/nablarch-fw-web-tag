package nablarch.common.web.tag;

import nablarch.common.web.handler.WebTestUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.util.Builder;
import org.junit.Test;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PopupButtonTagTest extends TagTestSupport<PopupButtonTag> {

    public PopupButtonTagTest() {
        super(new PopupButtonTag());
    }
    
    @Test
    public void testInputPageForDefault() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // button
        target.setName("name_test");

        target.setPopupWindowName("popup");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        target.setPopupOption("width=400, height=300");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));
        
        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=400, height=300"));
    }

    /**
     * popupOptionプロパティが明示的に指定されていない場合、
     * デフォルト値のwindowOptionが設定されること。
     */
    @Test
    public void testDefaultWindowOptions() throws JspException {
        // デフォルト値を設定
        TagTestUtil.setUpDefaultWithPopupOption();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // button
        target.setName("name_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        //target.setPopupOption("width=400, height=300");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<button",
                "name=\"name_test\"",
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\"></button>"
                ).replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertFalse(formContext.getInputNames().contains("name_test"));

        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.isAllowDoubleSubmission(), is(true));
        assertThat(info.getAction(), is(SubmissionAction.POPUP));
        assertThat(info.getPopupOption(), is("width=500, height=400"));
    }
    
    /**
     * 本タグがFormタグ内に定義されていない場合（FormContextが設定されていない場合）に、
     * IllegalStateExceptionがスローされることのテスト。
     */
    @Test
    public void testNotChildOfForm() throws Exception {
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});
        
        // input
        target.setName("name_test");

        try {
            target.doStartTag();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("invalid location of the popupButton tag. the popupButton tag must locate in the form tag."));
        }
    }
}
