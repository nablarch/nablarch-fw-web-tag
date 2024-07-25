package nablarch.common.web.tag;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import nablarch.common.web.handler.MockPageContext;
import nablarch.common.web.handler.MockPageContext.MockJspWriter;
import nablarch.core.message.ApplicationException;
import nablarch.core.message.BasicStringResource;
import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.message.StringResource;
import nablarch.core.message.StringResourceHolder;
import nablarch.core.repository.SystemRepository;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;
import nablarch.core.validation.ValidationContext;
import nablarch.core.validation.ValidationResultMessage;
import nablarch.fw.ExecutionContext;

import org.junit.Before;

/**
 * @author Kiyohito Itoh
 */
class TagTestUtil {
    /** 作成した{@link nablarch.common.web.tag.FormContext}の数で、nameを自動付与する際の一部として利用する */
    private static int formCount = 0;

    @Before
    public void classSetup() throws Exception {
    }

    static FormContext createFormContext() {
        return createFormContextByMethod("post");
    }

    static FormContext createFormContextByMethod(String method) {
        return createFormContext(method, "test_form_name" + formCount++);
    }

    static FormContext createFormContextByName(String name) {
        // このファクトリメソッドではformCountの値はnameに含めないが、
        // FormContext自体は作成するのでその数を記録する意味でインクリメントしておく
        formCount++;
        return createFormContext("post", name);
    }

    static FormContext createFormContext(String method, String name) {
        HtmlAttributes formAttrs = new HtmlAttributes();
        formAttrs.put(HtmlAttribute.METHOD, method);
        return new FormContext(name);
    }

    static String getOutput(PageContext pageContext) {
        return ((MockJspWriter) pageContext.getOut()).getOutput();
    }

    static void clearOutput(PageContext pageContext) {
        clearOutput(pageContext, true);
    }

    static void clearOutput(PageContext pageContext, boolean clearSequence) {
        ((MockJspWriter) pageContext.getOut()).clearOutput();
        if (clearSequence) {
            pageContext.setAttribute("nablarch_checkbox", null, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("nablarch_radio", null, PageContext.REQUEST_SCOPE);
        }
    }

    static void assertTag(String actual, String expected, String separator) {
        String[] splitActual = actual.split(separator);
        String[] splitExpected = expected.split(separator);
        for (int i = 0; i < splitExpected.length; i++) {

            String msg = "baseActual[" + actual + "]";
            msg += "\n";
            msg += i == 0 ? "first" : "prev_actual[" + splitActual[i - 1] + "]";
            msg += "\n";
            msg += i == (splitExpected.length - 1) ? "last" :
                (i < splitActual.length - 1 ? "next_actual[" + splitActual[i + 1] + "]" : "splitActual[" + (i + 1) + "] not exists");

            assertThat(msg, splitActual[i], is(splitExpected[i]));
        }
    }

    static void setGenericAttributes(GenericAttributesTagSupport tag) {
        tag.setId("id_test");
        tag.setCssClass("css_test");
        tag.setStyle("style_test");
        tag.setTitle("title_test");
        tag.setLang("lang_test");
        tag.setXmlLang("xmlLang_test");
        tag.setDir("dir_test");
        tag.setOnclick("onclick_test");
        tag.setOndblclick("ondblclick_test");
        tag.setOnmousedown("onmousedown_test");
        tag.setOnmouseup("onmouseup_test");
        tag.setOnmouseover("onmouseover_test");
        tag.setOnmousemove("onmousemove_test");
        tag.setOnmouseout("onmouseout_test");
        tag.setOnkeypress("onkeypress_test");
        tag.setOnkeydown("onkeydown_test");
        tag.setOnkeyup("onkeyup_test");
    }

    static void setGenericAttributesForInputs(GenericAttributesTagSupport tag) {
//        tag.setId("id_test");
        tag.setCssClass("css_test");
        tag.setStyle("style_test");
        tag.setTitle("title_test");
        tag.setLang("lang_test");
        tag.setXmlLang("xmlLang_test");
        tag.setDir("dir_test");
        tag.setOnclick("onclick_test");
        tag.setOndblclick("ondblclick_test");
        tag.setOnmousedown("onmousedown_test");
        tag.setOnmouseup("onmouseup_test");
        tag.setOnmouseover("onmouseover_test");
        tag.setOnmousemove("onmousemove_test");
        tag.setOnmouseout("onmouseout_test");
        tag.setOnkeypress("onkeypress_test");
        tag.setOnkeydown("onkeydown_test");
        tag.setOnkeyup("onkeyup_test");
    }

    static final String HTML = "<table id=\"id\" style='style'>";
    static final String ESC_HTML = "&lt;table id=&#034;id&#034; style=&#039;style&#039;&gt;";
    static final String ESC_HTML_WITH_HTML_FORMAT = "&lt;table&nbsp;id=&#034;id&#034;&nbsp;style=&#039;style&#039;&gt;";

    static void setGenericAttributesWithHtml(GenericAttributesTagSupport tag) {
        tag.setId("id_test" + HTML);
        tag.setCssClass("css_test" + HTML);
        tag.setStyle("style_test" + HTML);
        tag.setTitle("title_test" + HTML);
        tag.setLang("lang_test" + HTML);
        tag.setXmlLang("xmlLang_test" + HTML);
        tag.setDir("dir_test" + HTML);
        tag.setOnclick("onclick_test" + HTML);
        tag.setOndblclick("ondblclick_test" + HTML);
        tag.setOnmousedown("onmousedown_test" + HTML);
        tag.setOnmouseup("onmouseup_test" + HTML);
        tag.setOnmouseover("onmouseover_test" + HTML);
        tag.setOnmousemove("onmousemove_test" + HTML);
        tag.setOnmouseout("onmouseout_test" + HTML);
        tag.setOnkeypress("onkeypress_test" + HTML);
        tag.setOnkeydown("onkeydown_test" + HTML);
        tag.setOnkeyup("onkeyup_test" + HTML);
    }

    static void setGenericAttributesWithHtmlForInputs(GenericAttributesTagSupport tag) {
//        tag.setId("id_test" + HTML);
        tag.setCssClass("css_test" + HTML);
        tag.setStyle("style_test" + HTML);
        tag.setTitle("title_test" + HTML);
        tag.setLang("lang_test" + HTML);
        tag.setXmlLang("xmlLang_test" + HTML);
        tag.setDir("dir_test" + HTML);
        tag.setOnclick("onclick_test" + HTML);
        tag.setOndblclick("ondblclick_test" + HTML);
        tag.setOnmousedown("onmousedown_test" + HTML);
        tag.setOnmouseup("onmouseup_test" + HTML);
        tag.setOnmouseover("onmouseover_test" + HTML);
        tag.setOnmousemove("onmousemove_test" + HTML);
        tag.setOnmouseout("onmouseout_test" + HTML);
        tag.setOnkeypress("onkeypress_test" + HTML);
        tag.setOnkeydown("onkeydown_test" + HTML);
        tag.setOnkeyup("onkeyup_test" + HTML);
    }

    static void setFocusAttributes(FocusAttributesTagSupport tag) {
        tag.setAccesskey("accesskey_test");
        tag.setTabindex(3);
        tag.setOnfocus("onfocus_test");
        tag.setOnblur("onblur_test");
    }

    static void setFocusAttributesForInputs(FocusAttributesTagSupport tag) {
//        tag.setAccesskey("accesskey_test");
//        tag.setTabindex(3);
        tag.setOnfocus("onfocus_test");
        tag.setOnblur("onblur_test");
    }

    static void setFocusAttributesWithHtml(FocusAttributesTagSupport tag) {
        tag.setAccesskey("accesskey_test" + HTML);
        tag.setTabindex(3);
        tag.setOnfocus("onfocus_test" + HTML);
        tag.setOnblur("onblur_test" + HTML);
    }

    static void setFocusAttributesWithHtmlForInputs(FocusAttributesTagSupport tag) {
//        tag.setAccesskey("accesskey_test" + HTML);
//        tag.setTabindex(3);
        tag.setOnfocus("onfocus_test" + HTML);
        tag.setOnblur("onblur_test" + HTML);
    }

    private static final String[][] MESSAGES = {
        { "MSG00000", "ja", "XXXのため登録できません。" },
        { "MSG00001", "ja", "YYYにより既に\n更新されています。" },
        { "MSG00002", "ja", "ZZZは既に削除されています。" },
        { "MSG11110", "ja", "{0}を入力して下さい。" },
        { "MSG11111", "ja", "{0}は半角英数で入力して下さい。" },
        { "MSG11112", "ja", "{0}は{1}桁以下で入力して下さい。" }};

    static void setErrorMessages(MockPageContext pageContext, Throwable error) {
        pageContext.getAttributes(PageContext.REQUEST_SCOPE).put(ExecutionContext.THROWN_APPLICATION_EXCEPTION_KEY, error);
    }

    static void setErrorMessages(MockPageContext pageContext) {
        setErrorMessages(pageContext, true);
    }

    static void setErrorMessages(MockPageContext pageContext, boolean all) {
        MockMessageResource res = SystemRepository.get("stringResourceHolder");
        res.setMessages(MESSAGES);

        ApplicationException error = new ApplicationException();

        if (all) {
            error.addMessages(new Message(MessageLevel.INFO, res.get("MSG00000"), new Object[0]));
            error.addMessages(new Message(MessageLevel.WARN, res.get("MSG00001"), new Object[0]));
            error.addMessages(new Message(MessageLevel.ERROR, res.get("MSG00002"), new Object[0]));
        }

        ValidationContext<Entity> validCtxt = new ValidationContext<Entity>("", Entity.class, null, null, null);
        validCtxt.addResultMessage("entity.aaa", "MSG11110", "AAA");
        validCtxt.addResultMessage("entity.bbb", "MSG11111", "BBB");
        validCtxt.addResultMessage("entity.ccc", "MSG11112", "CCC", "4");
        validCtxt.addResultMessage("entity.aaa.xxx", "MSG11110", "AAA1");
        validCtxt.addResultMessage("entity.aaa.yyy", "MSG11110", "AAA2");
        validCtxt.addResultMessage("entity.aaa.zzz", "MSG11110", "AAA3");
        validCtxt.addResultMessage("entity.surrogatepair", "MSG11110", "🙊🙊🙊");
        error.addMessages(validCtxt.getMessages());

        if (all) {
            error.addMessages(new ValidationResultMessage("", res.get("MSG11110"), new String[] {"XXX"}));
            error.addMessages(new ValidationResultMessage("", res.get("MSG11110"), new String[] {"YYY"}));
            error.addMessages(new ValidationResultMessage("", res.get("MSG11110"), new String[] {"ZZZ"}));
        }

        setErrorMessages(pageContext, error);
    }

    static class MockMessageResource extends StringResourceHolder {
        private Map<String, StringResource> messages = new HashMap<String, StringResource>();
        public void setMessages(String[][] messages) {
            for (String[] params: messages) {
                String msgId = params[0];
                Map<String, String> formats = new HashMap<String, String>();
                for (int i = 0; i * 2 + 2 <= params.length; i++) {
                    formats.put(params[i * 2 + 1], params[i * 2 + 2]);
                }

                this.messages.put(msgId, new BasicStringResource(msgId, formats));
            }
        }
        public StringResource get(String messageId) {
            return messages.get(messageId);
        }
    }

    public static class Entity {
        private String aaa = "";
        private String bbb = "１２３";
        private String ccc = "123456";
        public String getAaa() {
            return aaa;
        }
        public String getBbb() {
            return bbb;
        }
        public String getCcc() {
            return ccc;
        }
    }

    static void setList(PageContext pageContext, List<?> groups) {
        pageContext.setAttribute("groups", groups, PageContext.REQUEST_SCOPE);
    }

    static void setListWithStringId(PageContext pageContext) {
        List<Group<String>> groups = new ArrayList<Group<String>>();
        for (int i = 0; i < 5; i++) {
            groups.add(new Group<String>("G00" + i, "グループ" + i));
        }
        setList(pageContext, groups);
    }

    static void setListWithLabelNull(PageContext pageContext) {
        List<Group<String>> groups = new ArrayList<Group<String>>();
        for (int i = 0; i < 5; i++) {
            groups.add(new Group<String>("G00" + i, null));
        }
        setList(pageContext, groups);
    }

    static void setListWithValueNull(PageContext pageContext) {
        List<Group<String>> groups = new ArrayList<Group<String>>();
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                groups.add(new Group<String>(null, null));
                continue;
            }
            groups.add(new Group<String>("G00" + i, "グループ" + i));
        }
        setList(pageContext, groups);
    }

    static void setListWithIntegerId(PageContext pageContext) {
        List<Group<Integer>> groups = new ArrayList<Group<Integer>>();
        for (int i = 0; i < 5; i++) {
            groups.add(new Group<Integer>(i, "グループ" + i));
        }
        setList(pageContext, groups);
    }

    static void setListWithSurrogatepairStringId(PageContext pageContext) {
        List<Group<String>> groups = new ArrayList<Group<String>>();
        String[] surrogatepair = {"🙊🙊🙊","🙈🙈🙈","🙉🙉🙉","🙊🙈🙉","😸😸😸"};
        for (int i = 0; i < 5; i++) {
            groups.add(new Group<String>("G00" + i, surrogatepair[i] + i));
        }
        setList(pageContext, groups);
    }

    static void setListWithSurrogatepairValue(PageContext pageContext) {
        List<Group<Integer>> groups = new ArrayList<Group<Integer>>();
        String[] surrogatepair = {"🙊🙊🙊","🙈🙈🙈","🙉🙉🙉","🙊🙈🙉","😸😸😸"};
        for (int i = 0; i < 5; i++) {
            groups.add(new Group<Integer>(i, surrogatepair[i]));
        }
        setList(pageContext, groups);
    }

    static void setListWithHtml(PageContext pageContext) {
        List<Group<String>> groups = new ArrayList<Group<String>>();
        for (int i = 0; i < 5; i++) {
            groups.add(new Group<String>("G00" + i + HTML, "グループ" + i + HTML));
        }
        pageContext.setAttribute("groups", groups, PageContext.REQUEST_SCOPE);
    }

    public static class Group<I> {
        private I groupId;
        private String name;
        public Group(I groupId, String name) {
            this.groupId = groupId;
            this.name = name;
        }
        public I getGroupId() {
            return groupId;
        }
        public String getName() {
            return name;
        }
    }

    static void setUpMessageTagTest() throws Exception {
    }

    static void setUpMessageTagTestBeforeClass() throws Exception {
    }

    static void setUpCodeTagTestBeforeClass() throws Exception {
    }

    static void tearDownConnectionAfterClass() throws Exception {
    }

    static void setUpCodeTagTestWithDefault() throws Exception {
        setUpCodeTagTest( true);
    }

    static void setUpCodeTagTest() throws Exception {
        setUpCodeTagTest( false);
    }

    private static void setUpCodeTagTest(boolean withDefault) throws Exception {
    }

    static void setUpDefaultConfig() {
        load("nablarch/common/web/tag/default-tag-test.xml");
    }

    static void setUpDefaultConfigWithoutPort() {
        load("nablarch/common/web/tag/default-tag-without-port.xml");
    }

    static void setUpDefaultConfigWithLS() {
        load("nablarch/common/web/tag/default-tag-with-ls-test.xml");
    }

    static void setUpCustomFormatter() {
        load("nablarch/common/web/tag/custom-formatter-test.xml");
    }

    static void setUpDefaultWithDisplayControlSettings() {
        load("nablarch/common/web/tag/custom-submission-display-tag-test.xml");
    }

    static void setUpDefaultWithPopupOption() {
        load("nablarch/common/web/tag/default-tag-with-popup.xml");
    }

    static void setUpDefaultWithJspForDisabled() {
       load("nablarch/common/web/tag/default-tag-with-jsp-for-disabled.xml");
    }

    private static void load(String config) {
        XmlComponentDefinitionLoader loader =  new XmlComponentDefinitionLoader(config);
        DiContainer container = new DiContainer(loader);
        SystemRepository.load(container);
    }


}

