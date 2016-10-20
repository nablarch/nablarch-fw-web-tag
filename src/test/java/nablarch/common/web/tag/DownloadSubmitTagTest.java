package nablarch.common.web.tag;

import javax.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.WebTestUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.util.Builder;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * {@link DownloadSubmitTag}のテスト。
 * @author Kiyohito Itoh
 */
public class DownloadSubmitTagTest extends TagTestSupport<DownloadSubmitTag> {

    public DownloadSubmitTagTest() {
        super(new DownloadSubmitTag());
    }

    @Before
    public void setup() {
        TagTestUtil.setUpDefaultConfig();
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // input
        target.setName("name_test");
        
        // submit,button,image
        target.setType("submit");
        target.setValue("value_test");

        target.setSrc("download_src_value");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setUri("./R12345");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "type=\"submit\"",
                "name=\"name_test\"",
                "value=\"value_test\"",
                "src=\"download_src_value" + "?nablarch_static_content_version=1.0.0" + '"',
                "onclick=\"return window.nablarch_submit(event, this);\"",
                "autofocus=\"autofocus\" />")
                .replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertFalse(formContext.getInputNames().contains("name_test"));
        
        assertThat(formContext.getSubmissionInfoList().size(), is(1));
        SubmissionInfo info = formContext.getSubmissionInfoList().get(0);
        assertThat(info.getName(), is("name_test"));
        assertThat(info.getUri(), is("./R12345" + WebTestUtil.ENCODE_URL_SUFFIX));
        assertThat(info.getAction(), is(SubmissionAction.DOWNLOAD));
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
            assertThat(e.getMessage(), is("invalid location of the downloadSubmit tag. the downloadSubmit tag must locate in the form tag."));
        }
    }
}
