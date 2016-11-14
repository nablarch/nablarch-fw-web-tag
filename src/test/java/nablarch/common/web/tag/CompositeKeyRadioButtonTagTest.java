package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.tagext.Tag;

import nablarch.common.web.compositekey.CompositeKey;
import nablarch.core.util.Builder;

import org.junit.Test;

/**
 * {@link CompositeKeyRadioButtonTag}クラスのテスト。
 * @author Koichi Asano
 */
public class CompositeKeyRadioButtonTagTest extends TagTestSupport<CompositeKeyRadioButtonTag> {

    public CompositeKeyRadioButtonTagTest() {
        super(new CompositeKeyRadioButtonTag());
    }
    
    /**
     * checked 以外全て出力されることの確認。
     */
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");
        
        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");
        
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        target.setValueObject(values);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,val2\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("test.value"));
    }

    /**
     * test.key1 と test.key2 がリクエストパラメータで飛んできたらチェックされるテスト。
     * (実際にはtest.key1とtest.key2の分解をTagHandlerがやること前提)
     * @throws Exception
     */
    @Test
    public void testInputPageCheckedByParameter() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("test.key1", new String[] {"val1"});
        pageContext.getMockReq().getParams().put("test.key2", new String[] {"val2"});
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");
        
        // checkbox
        target.setValue("value_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");
        
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        target.setValueObject(values);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,val2\"",
                "checked=\"checked\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("test.value"));
    }

    /**
     * "test.value" のキーで CompositeKey を設定したらチェックされるテスト
     */
    @Test
    public void testInputPageForCheckedByRequestScope() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put("value", new CompositeKey("val1,val2"));
        pageContext.getMockReq().setAttribute("test", testMap);
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");
        
        // checkbox
        target.setValue("value_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");
        
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        target.setValueObject(values);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,val2\"",
                "checked=\"checked\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("test.value"));
    }


    /**
     * "test.value" のキーで CompositeKey以外だと無視されてチェックされないテスト
     */
    @Test
    public void testInputPageForCheckedByInvalidValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put("value", new Object());
        pageContext.getMockReq().setAttribute("test", testMap);
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");
        
        // checkbox
        target.setValue("value_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");
        
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        target.setValueObject(values);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,val2\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("test.value"));
    }

    /**
     * valueObject がキーを持っていなかった場合。
     */
    @Test
    public void testInputPageValueObjectNotHasValue() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");
        
        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");
        
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
            }
        };
        target.setValueObject(values);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,null\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");
        
        assertTrue(formContext.getInputNames().contains("test.value"));
    }

    @Test
    public void testInputPageValueObjectNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // generic
        TagTestUtil.setGenericAttributes(target);

        // focus
        TagTestUtil.setFocusAttributes(target);

        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");

        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", null);
            }
        };
        target.setValueObject(values);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,null\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("test.value"));
    }

    @Test
    public void testInputPageCompositeKeyNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put("value", new CompositeKey(new String[]{null}));
        pageContext.getMockReq().setAttribute("test", testMap);

        // generic
        TagTestUtil.setGenericAttributes(target);

        // focus
        TagTestUtil.setFocusAttributes(target);

        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");

        // checkbox
        target.setValue("value_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");

        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        target.setValueObject(values);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,val2\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("test.value"));
    }

    @Test
    public void testInputPageParameterNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("test.key1", new String[] {"val1"});
        pageContext.getMockReq().getParams().put("test.key2", new String[] {null});

        // generic
        TagTestUtil.setGenericAttributes(target);

        // focus
        TagTestUtil.setFocusAttributes(target);

        // input
        target.setName("test.value");
        target.setDisabled(true);
        target.setOnchange("onchange_test");

        // checkbox
        target.setValue("value_test");

        // HTML5
        target.setAutofocus(true);

        // nablarch
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");

        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        target.setValueObject(values);

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String expected = Builder.lines(
                "<input",
                "id=\"id_test\"",
                "class=\"css_test\"",
                "style=\"style_test\"",
                "title=\"title_test\"",
                "lang=\"lang_test\"",
                "xml:lang=\"xmlLang_test\"",
                "dir=\"dir_test\"",
                "accesskey=\"accesskey_test\"",
                "tabindex=\"3\"",
                "type=\"radio\"",
                "name=\"test.value\"",
                "value=\"val1,val2\"",
                "disabled=\"disabled\"",
                "onclick=\"onclick_test\"",
                "ondblclick=\"ondblclick_test\"",
                "onchange=\"onchange_test\"",
                "onmousedown=\"onmousedown_test\"",
                "onmouseup=\"onmouseup_test\"",
                "onmouseover=\"onmouseover_test\"",
                "onmousemove=\"onmousemove_test\"",
                "onmouseout=\"onmouseout_test\"",
                "onkeypress=\"onkeypress_test\"",
                "onkeydown=\"onkeydown_test\"",
                "onkeyup=\"onkeyup_test\"",
                "onfocus=\"onfocus_test\"",
                "onblur=\"onblur_test\"",
                "autofocus=\"autofocus\" /><label for=\"id_test\">label_test</label>").replace(Builder.LS, " ");
        TagTestUtil.assertTag(actual, expected, " ");

        assertTrue(formContext.getInputNames().contains("test.value"));
    }

    /**
     * 本タグがFormタグ内に定義されていない場合（FormContextが設定されていない場合）に、
     * IllegalStateExceptionがスローされることのテスト。
     */
    @Test
    public void testNotChildOfForm() throws Exception {
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});
        
        // input
        target.setLabel("label_test");
        target.setErrorCss("errorCss_test");

        target.setNamePrefix("test");
        target.setKeyNames("key1,key2");
        
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("key1", "val1");
                put("key2", "val2");
            }
        };
        target.setValueObject(values);
        

        try {
            target.doStartTag();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("invalid location of the compositeKeyRadioButton tag. the compositeKeyRadioButton tag must locate in the form tag."));
        }
    }

}
