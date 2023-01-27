package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import jakarta.servlet.jsp.tagext.Tag;

import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class ChangeParamNameTagTest extends TagTestSupport<ChangeParamNameTag> {
    
    private FormContext formContext;
    private SubmissionInfo currentSubmissionInfo;
    
    public ChangeParamNameTagTest() {
        super(new ChangeParamNameTag());
    }
    
    private HtmlAttributes createTagNameof(String name) {
        HtmlAttributes tag = new HtmlAttributes();
        tag.put(HtmlAttribute.NAME, name);
        return tag;
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        formContext = new FormContext("dummy");
        formContext.addSubmissionInfo(SubmissionAction.POPUP, createTagNameof("dummy"), "./D0001", true, "D0001", "subWin1", "width=400, height=300", DisplayMethod.NORMAL);
        formContext.addSubmissionInfo(SubmissionAction.POPUP, createTagNameof("go"), "/R0001", true, "R0001", null, null, DisplayMethod.NORMAL);
        currentSubmissionInfo = formContext.getCurrentSubmissionInfo();
        TagUtil.setFormContext(pageContext, formContext);
    }
    
    @Test
    public void testInvalidLocation() throws Exception {
        
        String expectedMsg = "invalid location of the changeParamName tag. the changeParamName tag must locate in the tag to submit(popupSubmit or popupLink or popupButton).";
        
        TagUtil.setFormContext(pageContext, null);
        
        try {
            target.doStartTag();
            fail("must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(expectedMsg));
        }
        
        TagUtil.setFormContext(pageContext, new FormContext("test_form_name"));
        
        try {
            target.doStartTag();
            fail("must throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(expectedMsg));
        }
    }
    
    @Test
    public void testInputPage() throws Exception {
        
        // nablarch
        target.setParamName("paramName_test");
        target.setInputName("inputName_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(currentSubmissionInfo.getChangeParamNames().size(), is(1));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getParamName(), is("paramName_test"));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getInputName(), is("inputName_test"));
    }

    @Test
    public void testSurrogatepairInputPage() throws Exception {

        // nablarch
        target.setParamName("🙊🙊🙊");
        target.setInputName("🙈🙈🙈");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");

        assertThat(currentSubmissionInfo.getChangeParamNames().size(), is(1));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getParamName(), is("🙊🙊🙊"));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getInputName(), is("🙈🙈🙈"));
    }

    @Test
    public void testInputPageWithHtml() throws Exception {
        
        // nablarch
        target.setParamName("paramName_test" + TagTestUtil.HTML);
        target.setInputName("inputName_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = "";
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertThat(currentSubmissionInfo.getChangeParamNames().size(), is(1));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getParamName(), is("paramName_test" + TagTestUtil.HTML));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getInputName(), is("inputName_test" + TagTestUtil.HTML));
    }
}
