package nablarch.common.web.tag;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * @author Kiyohito Itoh
 */
public class HiddenTagTest extends TagTestSupport<HiddenTag> {

    public HiddenTagTest() {
        super(new HiddenTag());
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
    }
    
    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // focus
        TagTestUtil.setFocusAttributes(target);
        
        // input
        target.setName("name_test");
        target.setDisabled(true);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));
        
        HtmlAttributes attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.ID), is("id_test"));
        assertThat(attrs.<String>get(HtmlAttribute.CLASS), is("css_test"));
        assertThat(attrs.<String>get(HtmlAttribute.STYLE), is("style_test"));
        assertThat(attrs.<String>get(HtmlAttribute.TITLE), is("title_test"));
        assertThat(attrs.<String>get(HtmlAttribute.LANG), is("lang_test"));
        assertThat(attrs.<String>get(HtmlAttribute.XML_LANG), is("xmlLang_test"));
        assertThat(attrs.<String>get(HtmlAttribute.DIR), is("dir_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ACCESSKEY), is("accesskey_test"));
        assertThat(attrs.<Integer>get(HtmlAttribute.TABINDEX), is(3));
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("value_test"));
        assertThat(attrs.<Boolean>get(HtmlAttribute.DISABLED), is(true));
        assertThat(attrs.<String>get(HtmlAttribute.ONCLICK), is("onclick_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONDBLCLICK), is("ondblclick_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEDOWN), is("onmousedown_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEUP), is("onmouseup_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEMOVE), is("onmousemove_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEOUT), is("onmouseout_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONKEYPRESS), is("onkeypress_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONKEYDOWN), is("onkeydown_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONKEYUP), is("onkeyup_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONFOCUS), is("onfocus_test"));
        assertThat(attrs.<String>get(HtmlAttribute.ONBLUR), is("onblur_test"));
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"value_test" + TagTestUtil.HTML});
        
        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);
        
        // focus
        TagTestUtil.setFocusAttributesWithHtml(target);
        
        // input
        target.setName("name_test" + TagTestUtil.HTML);
        target.setDisabled(true);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));
        
        HtmlAttributes attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.ID), is("id_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.CLASS), is("css_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.STYLE), is("style_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.TITLE), is("title_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.LANG), is("lang_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.XML_LANG), is("xmlLang_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.DIR), is("dir_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ACCESSKEY), is("accesskey_test" + TagTestUtil.HTML));
        assertThat(attrs.<Integer>get(HtmlAttribute.TABINDEX), is(3));
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("value_test" + TagTestUtil.HTML));
        assertThat(attrs.<Boolean>get(HtmlAttribute.DISABLED), is(true));
        assertThat(attrs.<String>get(HtmlAttribute.ONCLICK), is("onclick_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONDBLCLICK), is("ondblclick_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEDOWN), is("onmousedown_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEUP), is("onmouseup_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEMOVE), is("onmousemove_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONMOUSEOUT), is("onmouseout_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONKEYPRESS), is("onkeypress_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONKEYDOWN), is("onkeydown_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONKEYUP), is("onkeyup_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONFOCUS), is("onfocus_test" + TagTestUtil.HTML));
        assertThat(attrs.<String>get(HtmlAttribute.ONBLUR), is("onblur_test" + TagTestUtil.HTML));
        assertTrue(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    @Test
    public void testInputPageForDefault() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"unknown"});
        
        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));
        
        HtmlAttributes attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("unknown"));
        assertTrue(formContext.getInputNames().contains("name_test"));

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        

        /**********************************************************
        値がnullの場合
        **********************************************************/

        pageContext.getMockReq().getParams().put("name_test", new String[] {null});

        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));
        
        attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is(""));
        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    /**
     * サロゲートペアを扱うテストケース。
     * @throws Exception
     */
    @Test
    public void testInputPageForSurrogatepair() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.getMockReq().getParams().put("🙊🙈🙉", new String[] {"🙊🙊🙊"});

        // input
        target.setName("🙊🙈🙉");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));

        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));

        HtmlAttributes attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("🙊🙈🙉"));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("🙊🙊🙊"));
        assertTrue(formContext.getInputNames().contains("🙊🙈🙉"));

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
    }

    /**
     * 値がBigDecimal型の場合に指数表記にならないこと。
     * @throws Exception
     */
    @Test
    public void testInputPageBigDecimal() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        pageContext.setAttribute("name_test", new BigDecimal("0.0000000001"));

        target.setName("name_test");

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));

        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));

        HtmlAttributes attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("0.0000000001"));
        assertThat(formContext.getInputNames(), hasItem("name_test"));
    }

    @Test
    public void testInputPageWithoutValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));
        
        HtmlAttributes attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is(""));

        assertTrue(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testInputPageWithListNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        // input
        target.setName("name_test");

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));

        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(1));

        HtmlAttributes attrs = InfoList.get(0);
        assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
        assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
        assertThat(attrs.<String>get(HtmlAttribute.VALUE), is(""));

        assertTrue(formContext.getInputNames().contains("name_test"));
    }
    
    @Test
    public void testConfirmationPageForDefault() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"unknown"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(0));
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageWithoutValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(0));
        
        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageWithListNullValue() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);

        TagUtil.setConfirmationPage(pageContext);

        // input
        target.setName("name_test");

        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                .put("name_test", Collections.singletonList(null));

        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));

        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(0));

        assertFalse(formContext.getInputNames().contains("name_test"));
    }

    @Test
    public void testConfirmationPageForDefaultWithHtml() throws Exception {

        FormContext formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test" + TagTestUtil.HTML, new String[] {"unknown" + TagTestUtil.HTML});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // input
        target.setName("name_test" + TagTestUtil.HTML);
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        List<HtmlAttributes> InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(0));
        
        assertFalse(formContext.getInputNames().contains("name_test" + TagTestUtil.HTML));
    }

    /**
     * 多値でも使用できること。
     * 配列またはCollection限定。
     */
    @Test
    public void testInputPageForMultiValues() throws Exception {
        
        FormContext formContext;
        List<HtmlAttributes> InfoList;
        
        /**********************************************************
        配列の場合
        **********************************************************/

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"data1", "data2", "data3", "🙊🙊🙊"});
        
        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(4));
        
        for (int i = 0; i < 4; i++) {
            HtmlAttributes attrs = InfoList.get(i);
            assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
            assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
            if(i == 3){
                assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("🙊🙊🙊"));
            }else{
                assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("data" + (i + 1)));
            }
        }
        assertTrue(formContext.getInputNames().contains("name_test"));

        /**********************************************************
        リストの場合
        **********************************************************/

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.setAttribute("name_test", Arrays.asList(new String[] {"val1", "val2", "val3", "🙊🙊🙊"}));
        
        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(4));
        
        for (int i = 0; i < 4; i++) {
            HtmlAttributes attrs = InfoList.get(i);
            assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
            assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
            if(i == 3){
                assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("🙊🙊🙊"));
            }else{
                assertThat(attrs.<String>get(HtmlAttribute.VALUE), is("val" + (i + 1)));
            }
        }
        assertTrue(formContext.getInputNames().contains("name_test"));

        /**********************************************************
        リストの場合(値にnullを含む場合)
        **********************************************************/

        formContext = TagTestUtil.createFormContext();
        TagUtil.setFormContext(pageContext, formContext);
        
        pageContext.setAttribute("name_test", Arrays.asList(new String[] {"", null, ""}));
        
        // input
        target.setName("name_test");
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        assertThat(TagTestUtil.getOutput(pageContext), is(""));
        
        InfoList = formContext.getHiddenTagInfoList();
        assertThat(InfoList.size(), is(3));
        
        for (int i = 0; i < 3; i++) {
            HtmlAttributes attrs = InfoList.get(i);
            assertThat(attrs.<String>get(HtmlAttribute.TYPE), is("hidden"));
            assertThat(attrs.<String>get(HtmlAttribute.NAME), is("name_test"));
            assertThat(attrs.<String>get(HtmlAttribute.VALUE), is(""));
        }
        assertTrue(formContext.getInputNames().contains("name_test"));
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
            assertThat(e.getMessage(), is("invalid location of the hidden tag. the hidden tag must locate in the form tag."));
        }
    }

}
