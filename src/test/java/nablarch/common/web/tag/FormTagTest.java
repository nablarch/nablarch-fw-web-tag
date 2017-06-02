package nablarch.common.web.tag;

import static nablarch.fw.ExecutionContext.FW_PREFIX;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.tagext.Tag;

import nablarch.common.web.handler.MockPageContext;
import nablarch.common.web.handler.WebTestUtil;
import nablarch.common.web.hiddenencryption.HiddenEncryptionUtil;
import nablarch.common.web.tag.SubmissionInfo.SubmissionAction;
import nablarch.core.util.Builder;
import nablarch.fw.web.handler.KeitaiAccessHandler;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class FormTagTest extends TagTestSupport<FormTag> {

    public FormTagTest() {
        super(new FormTag());
    }
    
    private static final String SUBMISSION_INFO_VAR = FW_PREFIX + "submission_info";

    private static final String SUBMISSION_END_MARK_PREFIX = SUBMISSION_INFO_VAR + ".endMark";

    private static final String SUBMIT_FUNCTION = Builder.lines(

            // サブミット時に呼ばれる関数
            "function $fwPrefix$submit(event, element) {",
            "    var isAnchor = element.tagName.match(/a/i);",

                 // formタグを取得する。
            "    var form = $fwPrefix$findForm(element, isAnchor);",
            "    if (form == null) {",
            "        return false;",
            "    }",

                 // サブミット制御のJavaScriptの出力が完了したことを示すマーカを取得する。
            "    var formName = form.attributes['name'].nodeValue;",
            "    if ($submissionEndMarkPrefix$[formName] == null) {",
            "        return false;",
            "    }",

            "    if ((typeof form.onsubmit) == \"function\") {",
                     // formタグのonsubmitを呼び出す。
            "        if (!$fwPrefix$invokeOnsubmit(form, event)) {",
            "            return false;",
            "        }",
            "    }",

            "    var submitName = element.name;",

                 // フォームに含まれるサブミット情報を取得する。
            "    var formData = $submissionInfoVar$[formName];",

                 // イベント発生元のサブミット情報を取得する。
            "    var submissionData = formData[submitName];",

            "    if (!submissionData.allowDoubleSubmission) {",
                     // リクエストの二重送信を防止する。
            "        element.onclick = $fwPrefix$stopSubmission;",
            "        if (!isAnchor) {",
            "            element.disabled = true;",
            "        }",
            "    }",

                 // nablarch_submitの値を更新する。
            "    form[\"" + HiddenEncryptionUtil.KEY_SUBMIT_NAME + "\"].value = submitName;",

            "    if (submissionData.submissionAction == \"$popupAction$\"",
            "            || submissionData.submissionAction == \"$downloadAction$\") {",
                     // 新しいフォームにサブミットする。
            "        $fwPrefix$submitToNewForm(submitName, form, submissionData)",
            "    } else {",
                     // 画面上のフォームをサブミットする。
            "        $fwPrefix$submitOnWindow(submitName, form, submissionData);",
            "    }",

            "    return false;",
            "}",
            
            // 画面上のフォームをサブミットする。
            "function $fwPrefix$submitOnWindow(submitName, form, submissionData) {",
                 // サブミット情報からサブミット先のactionを取得してサブミットする。
            "    form.action = submissionData.action;",
            "    form.submit();",
            "}",

            // オープンした画面を保持するハッシュ(keyはウィンドウ名)を宣言する。
            "var nablarch_opened_windows = {};",

            // 新しい画面にサブミットする。
            "function $fwPrefix$submitToNewForm(submitName, form, submissionData) {",

            "    var target = submissionData.popupWindowName;",
            "    if (target == null) {",
                     // 現在時刻のミリ秒を使用して一意なターゲット名を作成する。
            "        target = \"$fwPrefix$_target_\" + (+new Date());",
            "    }",

                 // ブランクで新しい画面をオープンする。
            "    if (submissionData.submissionAction == \"$popupAction$\") {",
            "        var windowOption = submissionData.popupOption;",
            "        var openedWindow = window.open(\"about:blank\", target, windowOption != null ? windowOption : \"\");",
            "        nablarch_opened_windows[target] = openedWindow;",
            "    }",

                 // サブミット用のフォームを新規に作成する。
            "    var tempForm = document.createElement(\"form\");",

                 // 元画面のフォームの要素をサブミット用のフォームにコピーする。
            "    var changeParamNames = submissionData.changeParamNames;",
            "    for (var i = 0; i < form.elements.length; i++) {",
            "        var element = form.elements[i];",
            "        if (element.type.match(/^submit$|^button$/i)) {",
            "            continue;",
            "        }",
            "        var paramName = changeParamNames[element.name];",
            "        if (paramName != null) {",
                         // パラメータ名の変更情報に一致する要素は、パラメータ名を変更する。
            "            $fwPrefix$addHiddenTagFromElement(tempForm, paramName, element);",
            "        } else {",
                         // パラメータ名の変更情報に一致しない要素は、そのまま送信する。
            "            $fwPrefix$addHiddenTagFromElement(tempForm, element.name, element);",
            "        }",
            "    }",

                 // サブミット情報からサブミット先のactionを取得してサブミットする。
            "    if (submissionData.submissionAction == \"$popupAction$\") {",
            "        tempForm.target = target;",
            "    }",
            "    tempForm.action = submissionData.action;",
            "    tempForm.method = \"post\";",

                 // 新規に作成したフォームを一時的に追加する。
            "    var body = document.getElementsByTagName(\"body\")[0];",
            "    body.appendChild(tempForm);",

                 // 新規に作成したフォームをサブミットする。
            "    tempForm.submit();",

                 // 新規に作成したフォームを削除する。
            "    body.removeChild(tempForm);",
            "}",
            
            // エレメントに対するフォームを検索する。
            "function $fwPrefix$findForm(element, isAnchor) {",
            "    if (isAnchor) {",
                     // aタグは親階層を辿って検索する。
            "        var parent = element.parentNode;",
            "        while (parent != null && !parent.tagName.match(/^form$|^body$/i)) {",
            "            parent = parent.parentNode;",
            "        }",
            "        if (parent == null || !parent.tagName.match(/form/i)) {",
            "            return null;",
            "        }",
            "        return parent;",
            "    } else {",
            "        return element.form;",
            "    }",
            "}",
            
            // formタグのonsubmitを呼び出す。
            "function $fwPrefix$invokeOnsubmit(form, event) {",
            "    var onSubmitFunc = form.onsubmit;",
            "    var ret = onSubmitFunc.call(form, event);",
                 // 明示的にfalseが返ってきた場合のみfalseを返す。
            "    return !( (ret != undefined && ret != null) && ret == false );",
            "}",
            
            // エレメントからhiddenタグを追加する。
            "function $fwPrefix$addHiddenTagFromElement(form, name, element) {",
            "    if (element.disabled) {",
            "        return;",
            "    }",
                 // select
            "    if (element.tagName.match(/select/i)) {",
            "        for (var i = 0; i < element.options.length; i++) {",
            "            var option = element.options[i];",
            "            if (option.selected) {",
            "                $fwPrefix$addHiddenTag(form, name, option.value);",
            "            }",
            "        }",

                 // checkboxとradio
            "    } else if (element.type.match(/^checkbox$|^radio$/i)) {",
            "        if (element.checked) {",
            "            $fwPrefix$addHiddenTag(form, name, element.value);",
            "        }",

                 // 上記以外
            "    } else {",
            "        $fwPrefix$addHiddenTag(form, name, element.value);",
            "    }",
            "}",

            // フォームにhiddenタグを追加する。
            "function $fwPrefix$addHiddenTag(form, name, value) {",
            "    var input = document.createElement(\"input\");",
            "    input.type = \"hidden\";",
            "    input.name = name;",
            "    input.value = value;",
            "    form.appendChild(input);",
            "}",
            
            // リクエストの二重送信防止時に、2回目以降のサブミット時に呼ばれる関数
            "function $fwPrefix$stopSubmission() {",
            "    if ((typeof $fwPrefix$handleDoubleSubmission) == \"function\") {",
            "         $fwPrefix$handleDoubleSubmission(this);",
            "    }",
            "    return false;",
            "}",
            "var $submissionInfoVar$ = {};",
            "$submissionEndMarkPrefix$ = {};")
                .replace("$fwPrefix$", FW_PREFIX)
                .replace("$submissionInfoVar$", SUBMISSION_INFO_VAR)
                .replace("$submissionEndMarkPrefix$", SUBMISSION_END_MARK_PREFIX)
                .replace("$popupAction$", SubmissionAction.POPUP.name())
                .replace("$downloadAction$", SubmissionAction.DOWNLOAD.name());
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        pageContext = new MockPageContext(true);
        target.setPageContext(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
    }

    @Test
    public void testInputPageForAllSetting() throws Exception {
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // form
        target.setAction("action_test");
        target.setMethod("method_test");
        target.setName("name_test");
        target.setEnctype("enctype_test");
        target.setOnsubmit("onsubmit_test");
        target.setOnreset("onreset_test");
        target.setAccept("accept_test");
        target.setAcceptCharset("acceptCharset_test");
        target.setTarget("target_test");

        // HTML5
        target.setAutocomplete("off");

        // nablarch
        target.setWindowScopePrefixes("user");
        target.setUseToken(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String token = getTokenFromSession();

        String ls = TagUtil.getCustomTagConfig().getLineSeparator();
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(ls);
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "id=\"id_test\"",
                    "class=\"css_test\"",
                    "style=\"style_test\"",
                    "title=\"title_test\"",
                    "lang=\"lang_test\"",
                    "xml:lang=\"xmlLang_test\"",
                    "dir=\"dir_test\"",
                    "name=\"name_test\"",
                    "action=\"action_test" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"method_test\"",
                    "enctype=\"enctype_test\"",
                    "accept=\"accept_test\"",
                    "accept-charset=\"acceptCharset_test\"",
                    "target=\"target_test\"",
                    "onclick=\"onclick_test\"",
                    "ondblclick=\"ondblclick_test\"",
                    "onmousedown=\"onmousedown_test\"",
                    "onmouseup=\"onmouseup_test\"",
                    "onmouseover=\"onmouseover_test\"",
                    "onmousemove=\"onmousemove_test\"",
                    "onmouseout=\"onmouseout_test\"",
                    "onkeypress=\"onkeypress_test\"",
                    "onkeydown=\"onkeydown_test\"",
                    "onkeyup=\"onkeyup_test\"",
                    "onsubmit=\"onsubmit_test\"",
                    "onreset=\"onreset_test\"",
                    "autocomplete=\"off\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".name_test = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".name_test = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    
    @Test
    public void testAllNablarchJsScriptSuppressedIfJsUnsupportedFlagSet() throws Exception {
        
        // JsUnsupprtedFlag
        pageContext.setAttribute(KeitaiAccessHandler.JS_UNSUPPORTED_FLAG_NAME, "true");
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // form
        target.setAction("action_test");
        target.setMethod("method_test");
        target.setName("name_test");
        target.setEnctype("enctype_test");
        target.setOnsubmit("onsubmit_test");
        target.setOnreset("onreset_test");
        target.setAccept("accept_test");
        target.setAcceptCharset("acceptCharset_test");
        target.setTarget("target_test");
        
        // nablarch
        target.setWindowScopePrefixes("user");
        target.setUseToken(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String token = getTokenFromSession();

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                Builder.lines(
                    "<form",
                    "id=\"id_test\"",
                    "class=\"css_test\"",
                    "style=\"style_test\"",
                    "title=\"title_test\"",
                    "lang=\"lang_test\"",
                    "xml:lang=\"xmlLang_test\"",
                    "dir=\"dir_test\"",
                    "name=\"name_test\"",
                    "action=\"action_test" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"method_test\"",
                    "enctype=\"enctype_test\"",
                    "accept=\"accept_test\"",
                    "accept-charset=\"acceptCharset_test\"",
                    "target=\"target_test\"",
                    "onclick=\"onclick_test\"",
                    "ondblclick=\"ondblclick_test\"",
                    "onmousedown=\"onmousedown_test\"",
                    "onmouseup=\"onmouseup_test\"",
                    "onmouseover=\"onmouseover_test\"",
                    "onmousemove=\"onmousemove_test\"",
                    "onmouseout=\"onmouseout_test\"",
                    "onkeypress=\"onkeypress_test\"",
                    "onkeydown=\"onkeydown_test\"",
                    "onkeyup=\"onkeyup_test\"",
                    "onsubmit=\"onsubmit_test\"",
                    "onreset=\"onreset_test\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "</form>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    
    private String getTokenFromSession() {
        return (String) pageContext.getNativeSession().getAttribute("/nablarch_session_token");
    }

    @Test
    public void testInputPageForAllSettingWithHtml() throws Exception {
        
        // generic
        TagTestUtil.setGenericAttributesWithHtml(target);
        
        // form
        target.setAction("action_test" + TagTestUtil.HTML);
        target.setMethod("method_test" + TagTestUtil.HTML);
        target.setName("name_test" + TagTestUtil.HTML);
        target.setEnctype("enctype_test" + TagTestUtil.HTML);
        target.setOnsubmit("onsubmit_test" + TagTestUtil.HTML);
        target.setOnreset("onreset_test" + TagTestUtil.HTML);
        target.setAccept("accept_test" + TagTestUtil.HTML);
        target.setAcceptCharset("acceptCharset_test" + TagTestUtil.HTML);
        target.setTarget("target_test" + TagTestUtil.HTML);
        
        // nablarch
        target.setWindowScopePrefixes("user");
        target.setUseToken(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String token = getTokenFromSession();

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "id=\"id_test" + TagTestUtil.ESC_HTML + "\"",
                    "class=\"css_test" + TagTestUtil.ESC_HTML + "\"",
                    "style=\"style_test" + TagTestUtil.ESC_HTML + "\"",
                    "title=\"title_test" + TagTestUtil.ESC_HTML + "\"",
                    "lang=\"lang_test" + TagTestUtil.ESC_HTML + "\"",
                    "xml:lang=\"xmlLang_test" + TagTestUtil.ESC_HTML + "\"",
                    "dir=\"dir_test" + TagTestUtil.ESC_HTML + "\"",
                    "name=\"name_test" + TagTestUtil.ESC_HTML + "\"",
                    "action=\"action_test" + TagTestUtil.ESC_HTML + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"method_test" + TagTestUtil.ESC_HTML + "\"",
                    "enctype=\"enctype_test" + TagTestUtil.ESC_HTML + "\"",
                    "accept=\"accept_test" + TagTestUtil.ESC_HTML + "\"",
                    "accept-charset=\"acceptCharset_test" + TagTestUtil.ESC_HTML + "\"",
                    "target=\"target_test" + TagTestUtil.ESC_HTML + "\"",
                    "onclick=\"onclick_test" + TagTestUtil.ESC_HTML + "\"",
                    "ondblclick=\"ondblclick_test" + TagTestUtil.ESC_HTML + "\"",
                    "onmousedown=\"onmousedown_test" + TagTestUtil.ESC_HTML + "\"",
                    "onmouseup=\"onmouseup_test" + TagTestUtil.ESC_HTML + "\"",
                    "onmouseover=\"onmouseover_test" + TagTestUtil.ESC_HTML + "\"",
                    "onmousemove=\"onmousemove_test" + TagTestUtil.ESC_HTML + "\"",
                    "onmouseout=\"onmouseout_test" + TagTestUtil.ESC_HTML + "\"",
                    "onkeypress=\"onkeypress_test" + TagTestUtil.ESC_HTML + "\"",
                    "onkeydown=\"onkeydown_test" + TagTestUtil.ESC_HTML + "\"",
                    "onkeyup=\"onkeyup_test" + TagTestUtil.ESC_HTML + "\"",
                    "onsubmit=\"onsubmit_test" + TagTestUtil.ESC_HTML + "\"",
                    "onreset=\"onreset_test" + TagTestUtil.ESC_HTML + "\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".name_test" + TagTestUtil.ESC_HTML + " = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".name_test" + TagTestUtil.ESC_HTML + " = true;",
                "-->",
                "</script>").split(Builder.LS);;
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }

    @Test
    public void testInputPageForDefault() throws Exception {
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);;
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    private HtmlAttributes createTagNameof(String name) {
        HtmlAttributes tag = new HtmlAttributes();
        tag.put(HtmlAttribute.NAME, name);
        return tag;
    }    
    
    @Test
    public void testInputPageForMultipleForms() throws Exception {
        
        // 1st
        
        // nablarch
        target.setUseToken(true);
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        
        FormContext formContext = TagUtil.getFormContext(pageContext);
        
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("test1"), "/test1", true, "test1", null, null, DisplayMethod.NORMAL);
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("test2"), "/test2", false, "test2", null, null, DisplayMethod.NORMAL);
        formContext.addSubmissionInfo(SubmissionAction.POPUP, createTagNameof("test3"), "/test3", false, "test3", "subWin3", "width=400, height=300", DisplayMethod.NORMAL);
        formContext.getSubmissionInfoList().get(2).addChangeParamName("paramName_test", "inputName_test");
        formContext.addSubmissionInfo(SubmissionAction.POPUP, createTagNameof("test4"), "/test4", false, "test4", null, null, DisplayMethod.NORMAL);
        formContext.getSubmissionInfoList().get(3).addChangeParamName("paramName_test1", "inputName_test1");
        formContext.getSubmissionInfoList().get(3).addChangeParamName("paramName_test2", "inputName_test2");
        formContext.getSubmissionInfoList().get(3).addChangeParamName("paramName_test3", "inputName_test3");
        formContext.addSubmissionInfo(SubmissionAction.DOWNLOAD, createTagNameof("test5"), "/test5", true, "test5", null, null, DisplayMethod.NORMAL);
        formContext.getSubmissionInfoList().get(4).addChangeParamName("dl_paramName_test", "dl_inputName_test");
        formContext.addSubmissionInfo(SubmissionAction.DOWNLOAD, new HtmlAttributes(), "/test6", false, "test6", null, null, DisplayMethod.NORMAL); // name attribute omitted!!
        
        formContext.getSubmissionInfoList().get(5).addChangeParamName("dl_paramName_test1", "dl_inputName_test1");
        formContext.getSubmissionInfoList().get(5).addChangeParamName("dl_paramName_test2", "dl_inputName_test2");
        formContext.getSubmissionInfoList().get(5).addChangeParamName("dl_paramName_test3", "dl_inputName_test3");
        
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String token = getTokenFromSession();
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " ")).split(Builder.LS);
        int index = 0;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
            index++;
        }
        assertThat(splitActual[index], containsString("<input type=\"hidden\" name=\"nablarch_hidden\" value=\""));
        assertThat(splitActual[index], containsString("nablarch_token=" + token));
        assertThat(splitActual[++index], containsString("<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />"));
        splitExpected = Builder.lines(
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"test1\": { \"action\": \"/test1\", \"allowDoubleSubmission\": true, \"submissionAction\": \"TRANSITION\" },",
                "\"test2\": { \"action\": \"/test2\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" },",
                "\"test3\": { \"action\": \"/test3\", \"allowDoubleSubmission\": false, \"submissionAction\": \"POPUP\", \"popupWindowName\": \"subWin3\", \"popupOption\": \"width=400, height=300\""
                    + ", \"changeParamNames\": {\"inputName_test\": \"paramName_test\"} },",
                "\"test4\": { \"action\": \"/test4\", \"allowDoubleSubmission\": false, \"submissionAction\": \"POPUP\", \"popupWindowName\": null, \"popupOption\": \"\""
                    + ", \"changeParamNames\": {\"inputName_test1\": \"paramName_test1\",\"inputName_test2\": \"paramName_test2\",\"inputName_test3\": \"paramName_test3\"} },",
                "\"test5\": { \"action\": \"/test5\", \"allowDoubleSubmission\": true, \"submissionAction\": \"DOWNLOAD\""
                    + ", \"changeParamNames\": {\"dl_inputName_test\": \"dl_paramName_test\"} },",
                "\"nablarch_form1_6\": { \"action\": \"/test6\", \"allowDoubleSubmission\": false, \"submissionAction\": \"DOWNLOAD\""
                    + ", \"changeParamNames\": {\"dl_inputName_test1\": \"dl_paramName_test1\",\"dl_inputName_test2\": \"dl_paramName_test2\",\"dl_inputName_test3\": \"dl_paramName_test3\"} }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        index++;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[index + i], splitExpected[i], " ");
        }
        
        TagTestUtil.clearOutput(pageContext);
        
        // 2nd
        target = new FormTag();
        
        // nablarch
        target.setUseToken(true);
        
        target.setPageContext(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        
        formContext = TagUtil.getFormContext(pageContext);  
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("test3"), "/test3", true, "test3", null, null, DisplayMethod.NORMAL);   
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("test4"), "/test4", false, "test4", null, null, DisplayMethod.NORMAL);       
        formContext.addSubmissionInfo(SubmissionAction.POPUP, createTagNameof("test5"), "/test5", false, "test3", "subWin5", "width=400, height=300", DisplayMethod.NORMAL);          
        formContext.addSubmissionInfo(SubmissionAction.POPUP, createTagNameof("test6"), "/test6", false, "test4", null, null, DisplayMethod.NORMAL);
        formContext.addSubmissionInfo(SubmissionAction.DOWNLOAD, createTagNameof("test7"), "/test7", false, "test7", null, null, DisplayMethod.NORMAL);        
        formContext.addSubmissionInfo(SubmissionAction.DOWNLOAD, createTagNameof("test8"), "/test8", true, "test8", null, null, DisplayMethod.NORMAL);
        
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        actual = TagTestUtil.getOutput(pageContext);
        splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        splitExpected = Builder.lines(
                "",
                /*
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                */
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form2\"",
                    "method=\"post\">").replace(Builder.LS, " ")).split(Builder.LS);
        index = 0;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
            index++;
        }
        assertThat(splitActual[index], containsString("<input type=\"hidden\" name=\"nablarch_hidden\" value=\""));
        assertThat(splitActual[index], containsString("nablarch_token=" + token));
        assertThat(splitActual[++index], containsString("<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />"));
        splitExpected = Builder.lines(
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form2 = {",
                "\"test3\": { \"action\": \"/test3\", \"allowDoubleSubmission\": true, \"submissionAction\": \"TRANSITION\" },",
                "\"test4\": { \"action\": \"/test4\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" },",
                "\"test5\": { \"action\": \"/test5\", \"allowDoubleSubmission\": false, \"submissionAction\": \"POPUP\", \"popupWindowName\": \"subWin5\", \"popupOption\": \"width=400, height=300\", \"changeParamNames\": {} },",
                "\"test6\": { \"action\": \"/test6\", \"allowDoubleSubmission\": false, \"submissionAction\": \"POPUP\", \"popupWindowName\": null, \"popupOption\": \"\", \"changeParamNames\": {} },",
                "\"test7\": { \"action\": \"/test7\", \"allowDoubleSubmission\": false, \"submissionAction\": \"DOWNLOAD\", \"changeParamNames\": {} },",
                "\"test8\": { \"action\": \"/test8\", \"allowDoubleSubmission\": true, \"submissionAction\": \"DOWNLOAD\", \"changeParamNames\": {} }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form2 = true;",
                "-->",
                "</script>").split(Builder.LS);
        index++;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[index + i], splitExpected[i], " ");
        }
    }

    @Test
    public void testInputPageForMultipleFormsWithDuplicateName() throws Exception {
        
        // 1st
        target.setName("duplicateName");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        // 2nd
        target = new FormTag();
        target.setName("duplicateName");
        target.setPageContext(pageContext);
        
        try {
            target.doStartTag();
            fail("例外が発生する。");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("name attribute of form tag has duplicated. name = [duplicateName]"));
        }
    }
    
    @Test
    public void testInputPageWithDuplicateSubmissionNames() throws Exception {
        
        target.setName("testForm");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        
        FormContext formContext = TagUtil.getFormContext(pageContext);
        HtmlAttributes inputTag1 = createTagNameof("test1");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, inputTag1, "/test1", true, "test1", null, null, DisplayMethod.NORMAL);
        
        HtmlAttributes inputTag2 = createTagNameof("test1");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, inputTag2, "/test2", false, "test2", null, null, DisplayMethod.NORMAL);
        
        assertEquals("test1", inputTag1.get(HtmlAttribute.NAME));
        assertEquals("testForm_2", inputTag2.get(HtmlAttribute.NAME));
    }
    
    @Test
    public void testFormThatHasInputTagsWithNoSubmissionName() throws Exception {
        
        target.setName("testForm");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        
        FormContext formContext = TagUtil.getFormContext(pageContext);
        HtmlAttributes inputTag1 = createTagNameof("");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, inputTag1, "/test1", true, "test1", null, null, DisplayMethod.NORMAL);
        
        HtmlAttributes inputTag2 = createTagNameof(null);
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, inputTag2, "/test2", false, "test2", null, null, DisplayMethod.NORMAL);
        
        assertEquals("testForm_1", inputTag1.get(HtmlAttribute.NAME));
        assertEquals("testForm_2", inputTag2.get(HtmlAttribute.NAME));      
    }
    
    @Test
    public void testConfirmationPageForDefault() throws Exception {
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String token = getTokenFromSession();
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    @Test
    public void testConfirmationPageForNotUseToken() throws Exception {
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        
        TagUtil.setConfirmationPage(pageContext);
        
        // nablarch
        target.setUseToken(false);
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " ")).split(Builder.LS);
        int index = 0;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
            index++;
        }
        assertThat(splitActual[index], containsString("<input type=\"hidden\" name=\"nablarch_hidden\" value=\""));
        assertThat(splitActual[++index], containsString("<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />"));
        splitExpected = Builder.lines(
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        index++;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[index + i], splitExpected[i], " ");
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void testInputPageForNotUseHiddenEncryption() throws Exception {
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.setAttribute("nablarch_versions", new ArrayList<String>(){{
            add("version:1");
            add("version:2");
        }});
        
        // nablarch
        target.setWindowScopePrefixes("user");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addInputName("user.remarks");
               
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"), "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " ")).split(Builder.LS);
        int index = 0;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
            index++;
        }
        assertThat(splitActual[index], containsString("<input type=\"hidden\" name=\"nablarch_hidden\" value=\""));
        assertThat(splitActual[++index], containsString("<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />"));
        splitExpected = Builder.lines(
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        index++;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[index + i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(1));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
    }
    
    @Test
    public void testConfirmationPageForNotUseHiddenEncryption() throws Exception {
        
        TagUtil.setConfirmationPage(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("user");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addInputName("user.remarks");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"), "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String token = getTokenFromSession();
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " ")).split(Builder.LS);
        int index = 0;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
            index++;
        }
        assertThat(splitActual[index], containsString("<input type=\"hidden\" name=\"nablarch_hidden\" value=\""));
        assertThat(splitActual[index], containsString("nablarch_token=" + token));
        assertThat(splitActual[++index], containsString("<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />"));
        splitExpected = Builder.lines(
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        index++;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[index + i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(1));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
    }

    @SuppressWarnings("serial")
    @Test
    public void testInputPageForUseHiddenEncryption() throws Exception {
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("user");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"), "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext,
                new ArrayList<String>() {{ add("R0001"); }},
                new HashMap<String, List<String>>() {
                    {
                        put("user.remarks", new ArrayList<String>() {{ add("remarks_sample"); }});
                        put("user.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2"); }});
                        put("nablarch_hidden_submit_go", new ArrayList<String>() {{ add("goParam1=goParam1_sample1|goParam1=goParam1_sample2|goParam2=goParam2_sample"); }});
                    }
                });
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"" + hiddenValue + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(1));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
    }
    
    @SuppressWarnings("serial")
    @Test
    public void testConfirmationPageForUseHiddenEncryption() throws Exception {
        
        TagUtil.setConfirmationPage(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("user");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"), "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        final String token = getTokenFromSession();
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext,
                new ArrayList<String>() {{ add("R0001"); }},
                new HashMap<String, List<String>>() {
                    {
                        put("nablarch_token", new ArrayList<String>() {{ add(token); }});
                        put("user.remarks", new ArrayList<String>() {{ add("remarks_sample"); }});
                        put("user.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2"); }});
                        put("nablarch_hidden_submit_go", new ArrayList<String>() {{ add("goParam1=goParam1_sample1|goParam1=goParam1_sample2|goParam2=goParam2_sample"); }});
                    }
                });
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"" + hiddenValue + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(1));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
    }
    
    @SuppressWarnings("serial")
    @Test
    public void testConfirmationPageForContainsNoHiddenEncryptionRequestIds() throws Exception {
        
        TagUtil.setConfirmationPage(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        TagUtil.getCustomTagConfig().setNoHiddenEncryptionRequestIds(new ArrayList<String>() {{add("R0001");}});
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("user");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addInputName("user.remarks");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"), "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        final String token = getTokenFromSession();
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " ")).split(Builder.LS);
        int index = 0;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
            index++;
        }
        assertThat(splitActual[index], containsString("<input type=\"hidden\" name=\"nablarch_hidden\" value=\""));
        assertThat(splitActual[index], containsString("nablarch_token=" + token));
        assertThat(splitActual[++index], containsString("<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />"));
        splitExpected = Builder.lines(
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        index++;
        for (int i = 0; i < splitExpected.length; i++) {
            TagTestUtil.assertTag(splitActual[index + i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(1));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
        
        TagUtil.getCustomTagConfig().getNoHiddenEncryptionRequestIds().clear();
    }
    
    @SuppressWarnings("serial")
    @Test
    public void testConfirmationPageForNotContainsNoHiddenEncryptionRequestIds() throws Exception {
        
        TagUtil.setConfirmationPage(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        TagUtil.getCustomTagConfig().setNoHiddenEncryptionRequestIds(new ArrayList<String>() {{add("R0002");}});
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("user");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addInputName("user.remarks");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"), "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        final String token = getTokenFromSession();
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext,
                new ArrayList<String>() {{ add("R0001"); }},
                new HashMap<String, List<String>>() {
                    {
                        put("nablarch_token", new ArrayList<String>() {{ add(token); }});
                        put("user.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2"); }});
                        put("nablarch_hidden_submit_go", new ArrayList<String>() {{ add("goParam1=goParam1_sample1|goParam1=goParam1_sample2|goParam2=goParam2_sample"); }});
                    }
                });
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"" + hiddenValue + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(1));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
        
        TagUtil.getCustomTagConfig().getNoHiddenEncryptionRequestIds().clear();
    }
    
    
    /**
     * 暗号化が必要なリクエストとそうでないリクエストが同一のFORM内に混在している場合の挙動。
     */
    @SuppressWarnings("serial")
    @Test
    public void testConfirmationPageForContainsARequestIdRequiresEncryptionAndAnotherOneThatDoesnotNeedEncrypted()
    throws Exception {
        
        TagUtil.setConfirmationPage(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        TagUtil.getCustomTagConfig().setNoHiddenEncryptionRequestIds(new ArrayList<String>() {{add("R001"); add("R0002");}});
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("user");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addInputName("user.remarks");
        formContext.addInputName("nablarch_needs_hidden_encryption");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"),   "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);     
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("back"),   "./R0002", false, "R0002", null, null, DisplayMethod.NORMAL);           
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        final String token = getTokenFromSession();
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext,
                new ArrayList<String>() {{ add("R0001"); add("R0002"); }},
                new HashMap<String, List<String>>() {
                    {
                        put("nablarch_token", new ArrayList<String>() {{ add(token); }});
                        put("nablarch_hidden_submit_back", new ArrayList<String>(){{add("");}});                   
                        put("user.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2"); }});
                        put("nablarch_hidden_submit_go", new ArrayList<String>() {{ add("goParam1=goParam1_sample1|goParam1=goParam1_sample2|goParam2=goParam2_sample"); }});
                    }
                });
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_needs_hidden_encryption\" value=\"\" />",
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"" + hiddenValue + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" },",
                "\"back\": { \"action\": \"./R0002\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(2));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
        assertThat(formContext.getRequestIds().get(1), is("R0002"));        
        
        TagUtil.getCustomTagConfig().getNoHiddenEncryptionRequestIds().clear();
    }
    
    
    
    /**
     * 空白文字をウィンドウスコーププレフィックスとして使用した場合のテスト。
     */
    @SuppressWarnings("serial")
    @Test
    public void testUsingWindowscopeWithSpecialPrefix() throws Exception {
        TagUtil.setConfirmationPage(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        TagUtil.getCustomTagConfig().setNoHiddenEncryptionRequestIds(new ArrayList<String>() {{add("R001"); add("R0002");}});
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addInputName("user.remarks");
        formContext.addInputName("nablarch_needs_hidden_encryption");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"),   "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);     
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("back"),   "./R0002", false, "R0002", null, null, DisplayMethod.NORMAL);           
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        final String token = getTokenFromSession();
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext,
                new ArrayList<String>() {{ add("R0001"); add("R0002"); }},
                new HashMap<String, List<String>>() {
                    {
                        put("nablarch_token", new ArrayList<String>() {{ add(token); }});                 
                        put("user.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2"); }});
                        put("emp.name", new ArrayList<String>() {{ add("name_sample"); }});
                        put("emp.address", new ArrayList<String>() {{ add("address_sample"); }});
                        put("emp.remarks", new ArrayList<String>() {{ add("remarks_sample"); }});
                        put("emp.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2");}});
                        put("nablarch_hidden_submit_back", new ArrayList<String>(){{add("");}});  
                        put("nablarch_hidden_submit_go", new ArrayList<String>() {{ add("goParam1=goParam1_sample1|goParam1=goParam1_sample2|goParam2=goParam2_sample"); }});
                    }
                });
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_needs_hidden_encryption\" value=\"\" />",                
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"" + hiddenValue + "\" />",       
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" },",
                "\"back\": { \"action\": \"./R0002\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(2));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
        assertThat(formContext.getRequestIds().get(1), is("R0002"));        
        
        TagUtil.getCustomTagConfig().getNoHiddenEncryptionRequestIds().clear();
    }    
    
    /**
     * 本タグがFormタグ内に定義されていない場合（FormContextが設定されていない場合）に、
     * IllegalArgumentExceptionがスローされないことのテスト。
     */
    @Test
    public void testNotChildOfForm() throws Exception {
        
        pageContext.getMockReq().getParams().put("name_test", new String[] {"value_test"});
        target.doStartTag();
        assertTrue(true);
    }

    @Test
    public void testInputPageForSecure() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        // generic
        TagTestUtil.setGenericAttributes(target);

        // form
        target.setSecure(true);       // secure
        target.setAction("/action_test");
        target.setMethod("method_test");
        target.setName("name_test");
        target.setEnctype("enctype_test");
        target.setOnsubmit("onsubmit_test");
        target.setOnreset("onreset_test");
        target.setAccept("accept_test");
        target.setAcceptCharset("acceptCharset_test");
        target.setTarget("target_test");

        // nablarch
        target.setWindowScopePrefixes("user");
        target.setUseToken(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String token = getTokenFromSession();

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "id=\"id_test\"",
                    "class=\"css_test\"",
                    "style=\"style_test\"",
                    "title=\"title_test\"",
                    "lang=\"lang_test\"",
                    "xml:lang=\"xmlLang_test\"",
                    "dir=\"dir_test\"",
                    "name=\"name_test\"",
                    "action=\"https://nablarch.co.jp:443/nablarch_test/action_test" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"method_test\"",
                    "enctype=\"enctype_test\"",
                    "accept=\"accept_test\"",
                    "accept-charset=\"acceptCharset_test\"",
                    "target=\"target_test\"",
                    "onclick=\"onclick_test\"",
                    "ondblclick=\"ondblclick_test\"",
                    "onmousedown=\"onmousedown_test\"",
                    "onmouseup=\"onmouseup_test\"",
                    "onmouseover=\"onmouseover_test\"",
                    "onmousemove=\"onmousemove_test\"",
                    "onmouseout=\"onmouseout_test\"",
                    "onkeypress=\"onkeypress_test\"",
                    "onkeydown=\"onkeydown_test\"",
                    "onkeyup=\"onkeyup_test\"",
                    "onsubmit=\"onsubmit_test\"",
                    "onreset=\"onreset_test\"",
                    "autocomplete=\"off\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".name_test = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".name_test = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }

    @Test
    public void testInputPageForNotSecure() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        // generic
        TagTestUtil.setGenericAttributes(target);

        // form
        target.setSecure(false);      // not secure
        target.setAction("/action_test");
        target.setMethod("method_test");
        target.setName("name_test");
        target.setEnctype("enctype_test");
        target.setOnsubmit("onsubmit_test");
        target.setOnreset("onreset_test");
        target.setAccept("accept_test");
        target.setAcceptCharset("acceptCharset_test");
        target.setTarget("target_test");

        // nablarch
        target.setWindowScopePrefixes("user");
        target.setUseToken(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String token = getTokenFromSession();

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "id=\"id_test\"",
                    "class=\"css_test\"",
                    "style=\"style_test\"",
                    "title=\"title_test\"",
                    "lang=\"lang_test\"",
                    "xml:lang=\"xmlLang_test\"",
                    "dir=\"dir_test\"",
                    "name=\"name_test\"",
                    "action=\"http://nablarch.co.jp:8080/nablarch_test/action_test" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"method_test\"",
                    "enctype=\"enctype_test\"",
                    "accept=\"accept_test\"",
                    "accept-charset=\"acceptCharset_test\"",
                    "target=\"target_test\"",
                    "onclick=\"onclick_test\"",
                    "ondblclick=\"ondblclick_test\"",
                    "onmousedown=\"onmousedown_test\"",
                    "onmouseup=\"onmouseup_test\"",
                    "onmouseover=\"onmouseover_test\"",
                    "onmousemove=\"onmousemove_test\"",
                    "onmouseout=\"onmouseout_test\"",
                    "onkeypress=\"onkeypress_test\"",
                    "onkeydown=\"onkeydown_test\"",
                    "onkeyup=\"onkeyup_test\"",
                    "onsubmit=\"onsubmit_test\"",
                    "onreset=\"onreset_test\"",
                    "autocomplete=\"off\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".name_test = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".name_test = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }

    @Test
    public void testInputPageForAutocomplete() throws Exception {

        // autocompleteDisableTarget = "all"の場合

        TagUtil.getCustomTagConfig().setAutocompleteDisableTarget("all");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\"",
                    "autocomplete=\"off\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);;
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }

        // autocompleteDisableTarget = "password"の場合

        pageContext = new MockPageContext(true);
        target = new FormTag();
        target.setPageContext(pageContext);
        TagUtil.getCustomTagConfig().setAutocompleteDisableTarget("password");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        actual = TagTestUtil.getOutput(pageContext);
        splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);;
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }

    @Test
    public void testChangeParamName() throws Exception {

        // formタグの開始
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));

        FormContext formContext = TagUtil.getFormContext(pageContext);

        // ポップアップボタンの追加
        formContext.addSubmissionInfo(SubmissionAction.POPUP, createTagNameof("popup1"), "./R1", true, "R1", "subWin1", null, DisplayMethod.NORMAL);
        SubmissionInfo currentSubmissionInfo = formContext.getCurrentSubmissionInfo();

        // フォーマット情報をhiddenに追加
        formContext.addHiddenTagInfo("oyaPf.date_nablarch_formatSpec", "yyyymmdd{MM/dd/yyyy|ja}");
        formContext.addHiddenTagInfo("oyaPf.date_nablarch_formatSpec_separator", "|");

        // n:changeParamNameタグの開始終了(フォーマットあり)
        ChangeParamNameTag cpnTag1 = new ChangeParamNameTag();
        cpnTag1.setPageContext(pageContext);
        cpnTag1.setParamName("subPf.date");
        cpnTag1.setInputName("oyaPf.date");
        cpnTag1.doStartTag();
        cpnTag1.doEndTag();

        // n:changeParamNameタグの開始終了(フォーマットなし)
        ChangeParamNameTag cpnTag2 = new ChangeParamNameTag();
        cpnTag2.setPageContext(pageContext);
        cpnTag2.setParamName("subPf.dateWithoutFormat");
        cpnTag2.setInputName("oyaPf.dateWithoutFormat");
        cpnTag2.doStartTag();
        cpnTag2.doEndTag();

        // formタグの終了
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        // n:changeParamNameタグの情報がサブミット情報に追加されていること
        assertThat(currentSubmissionInfo.getChangeParamNames().size(), is(2));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getParamName(), is("subPf.date"));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(0).getInputName(), is("oyaPf.date"));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(1).getParamName(), is("subPf.dateWithoutFormat"));
        assertThat(currentSubmissionInfo.getChangeParamNames().get(1).getInputName(), is("oyaPf.dateWithoutFormat"));

        // n:changeParamNameタグにより送信される可能性があるフォーマット情報がhiddenタグの情報に含まれること
        assertThat(formContext.getHiddenTagInfoList().size(), is(4));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.NAME), is("oyaPf.date_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(0).get(HtmlAttribute.VALUE), is("yyyymmdd{MM/dd/yyyy|ja}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.NAME), is("oyaPf.date_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(1).get(HtmlAttribute.VALUE), is("|"));
        assertThat((String) formContext.getHiddenTagInfoList().get(2).get(HtmlAttribute.NAME), is("subPf.date_nablarch_formatSpec"));
        assertThat((String) formContext.getHiddenTagInfoList().get(2).get(HtmlAttribute.VALUE), is("yyyymmdd{MM/dd/yyyy|ja}"));
        assertThat((String) formContext.getHiddenTagInfoList().get(3).get(HtmlAttribute.NAME), is("subPf.date_nablarch_formatSpec_separator"));
        assertThat((String) formContext.getHiddenTagInfoList().get(3).get(HtmlAttribute.VALUE), is("|"));
    }

    @Test
    public void testInputPageForPostResubmitPrevent() throws Exception {

        target.setPreventPostResubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "};",
                "-->",
                "</script>",
                "<input type=\"hidden\" name=\"nablarch_post_resubmit_prevent\" value=\"true\" />",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);;
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }

    @Test
    public void testConfirmationPageForPostResubmitPrevent() throws Exception {

        TagUtil.setConfirmationPage(pageContext);

        target.setPreventPostResubmit(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String token = getTokenFromSession();

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "};",
                "-->",
                "</script>",
                "<input type=\"hidden\" name=\"nablarch_post_resubmit_prevent\" value=\"true\" />",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);;
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    @Test
    public void testInputPageForUseGetRequestTrue() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // config
        TagUtil.getCustomTagConfig().setUseGetRequest(true);

        // form
        target.setMethod("get");
        target.setAction("/action_test");
        target.setName("name_test");
        target.setEnctype("enctype_test");
        target.setOnsubmit("onsubmit_test");
        target.setOnreset("onreset_test");
        target.setAccept("accept_test");
        target.setAcceptCharset("acceptCharset_test");
        target.setTarget("target_test");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                Builder.lines(
                    "<form",
                    "id=\"id_test\"",
                    "class=\"css_test\"",
                    "style=\"style_test\"",
                    "title=\"title_test\"",
                    "lang=\"lang_test\"",
                    "xml:lang=\"xmlLang_test\"",
                    "dir=\"dir_test\"",
                    "name=\"name_test\"",
                    "action=\"/nablarch_test/action_test" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"get\"",
                    "enctype=\"enctype_test\"",
                    "accept=\"accept_test\"",
                    "accept-charset=\"acceptCharset_test\"",
                    "target=\"target_test\"",
                    "onclick=\"onclick_test\"",
                    "ondblclick=\"ondblclick_test\"",
                    "onmousedown=\"onmousedown_test\"",
                    "onmouseup=\"onmouseup_test\"",
                    "onmouseover=\"onmouseover_test\"",
                    "onmousemove=\"onmousemove_test\"",
                    "onmouseout=\"onmouseout_test\"",
                    "onkeypress=\"onkeypress_test\"",
                    "onkeydown=\"onkeydown_test\"",
                    "onkeyup=\"onkeyup_test\"",
                    "onsubmit=\"onsubmit_test\"",
                    "onreset=\"onreset_test\"",
                    "autocomplete=\"off\">").replace(Builder.LS, " "),
                "</form>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    @Test
    public void testInputPageForUseGetRequestDefault() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // configはデフォルトのまま

        // form
        target.setMethod("get");
        target.setAction("/action_test");
        target.setName("name_test");
        target.setEnctype("enctype_test");
        target.setOnsubmit("onsubmit_test");
        target.setOnreset("onreset_test");
        target.setAccept("accept_test");
        target.setAcceptCharset("acceptCharset_test");
        target.setTarget("target_test");

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                Builder.lines(
                    "<form",
                    "id=\"id_test\"",
                    "class=\"css_test\"",
                    "style=\"style_test\"",
                    "title=\"title_test\"",
                    "lang=\"lang_test\"",
                    "xml:lang=\"xmlLang_test\"",
                    "dir=\"dir_test\"",
                    "name=\"name_test\"",
                    "action=\"/nablarch_test/action_test" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"get\"",
                    "enctype=\"enctype_test\"",
                    "accept=\"accept_test\"",
                    "accept-charset=\"acceptCharset_test\"",
                    "target=\"target_test\"",
                    "onclick=\"onclick_test\"",
                    "ondblclick=\"ondblclick_test\"",
                    "onmousedown=\"onmousedown_test\"",
                    "onmouseup=\"onmouseup_test\"",
                    "onmouseover=\"onmouseover_test\"",
                    "onmousemove=\"onmousemove_test\"",
                    "onmouseout=\"onmouseout_test\"",
                    "onkeypress=\"onkeypress_test\"",
                    "onkeydown=\"onkeydown_test\"",
                    "onkeyup=\"onkeyup_test\"",
                    "onsubmit=\"onsubmit_test\"",
                    "onreset=\"onreset_test\"",
                    "autocomplete=\"off\">").replace(Builder.LS, " "),
                "</form>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    @Test
    public void testInputPageForUseGetRequestFalse() throws Exception {
        TagTestUtil.setUpDefaultConfig();
        
        // generic
        TagTestUtil.setGenericAttributes(target);
        
        // config
        TagUtil.getCustomTagConfig().setUseGetRequest(false);

        // form
        target.setMethod("get");
        target.setAction("/action_test");
        target.setName("name_test");
        target.setEnctype("enctype_test");
        target.setOnsubmit("onsubmit_test");
        target.setOnreset("onreset_test");
        target.setAccept("accept_test");
        target.setAcceptCharset("acceptCharset_test");
        target.setTarget("target_test");

        // nablarch
        target.setWindowScopePrefixes("user");
        target.setUseToken(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        String token = getTokenFromSession();

        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "id=\"id_test\"",
                    "class=\"css_test\"",
                    "style=\"style_test\"",
                    "title=\"title_test\"",
                    "lang=\"lang_test\"",
                    "xml:lang=\"xmlLang_test\"",
                    "dir=\"dir_test\"",
                    "name=\"name_test\"",
                    "action=\"/nablarch_test/action_test" + WebTestUtil.ENCODE_URL_SUFFIX + "\"",
                    "method=\"get\"",
                    "enctype=\"enctype_test\"",
                    "accept=\"accept_test\"",
                    "accept-charset=\"acceptCharset_test\"",
                    "target=\"target_test\"",
                    "onclick=\"onclick_test\"",
                    "ondblclick=\"ondblclick_test\"",
                    "onmousedown=\"onmousedown_test\"",
                    "onmouseup=\"onmouseup_test\"",
                    "onmouseover=\"onmouseover_test\"",
                    "onmousemove=\"onmousemove_test\"",
                    "onmouseout=\"onmouseout_test\"",
                    "onkeypress=\"onkeypress_test\"",
                    "onkeydown=\"onkeydown_test\"",
                    "onkeyup=\"onkeyup_test\"",
                    "onsubmit=\"onsubmit_test\"",
                    "onreset=\"onreset_test\"",
                    "autocomplete=\"off\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".name_test = {",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".name_test = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
    }
    
    /**
     * hidden暗号化を使用しない場合のテスト。
     */
    @SuppressWarnings("serial")
    @Test
    public void testNoUseHiddenEncryption() throws Exception {
        TagUtil.setConfirmationPage(pageContext);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
        TagUtil.getCustomTagConfig().setNoHiddenEncryptionRequestIds(new ArrayList<String>() {{add("R001"); add("R0002");}});
        
        pageContext.getMockReq().getParams().put("user.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("user.address", new String[] {"address_sample",});
        pageContext.getMockReq().getParams().put("user.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("user.comments", new String[] {"comments_sample1", "comments_sample2"});
        pageContext.getMockReq().getParams().put("emp.name", new String[] {"name_sample"});
        pageContext.getMockReq().getParams().put("emp.address", new String[] {"address_sample"});
        pageContext.getMockReq().getParams().put("emp.remarks", new String[] {"remarks_sample"});
        pageContext.getMockReq().getParams().put("emp.comments", new String[] {"comments_sample1", "comments_sample2"});
        
        // nablarch
        target.setWindowScopePrefixes("nablarch");
        
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        FormContext formContext = TagUtil.getFormContext(pageContext);
        formContext.addInputName("user.name");
        formContext.addInputName("user.address");
        formContext.addInputName("user.remarks");
        formContext.addInputName("nablarch_needs_hidden_encryption");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("go"),   "./R0001", false, "R0001", null, null, DisplayMethod.NORMAL);     
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample1");
        formContext.getCurrentSubmissionInfo().addParam("goParam1", "goParam1_sample2");
        formContext.getCurrentSubmissionInfo().addParam("goParam2", "goParam2_sample");
        formContext.addSubmissionInfo(SubmissionAction.TRANSITION, createTagNameof("back"),   "./R0002", false, "R0002", null, null, DisplayMethod.NORMAL);           
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        final String token = getTokenFromSession();
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext,
                new ArrayList<String>() {{ add("R0001"); add("R0002"); }},
                new HashMap<String, List<String>>() {
                    {
                        put("nablarch_token", new ArrayList<String>() {{ add(token); }});                 
                        put("user.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2"); }});
                        put("emp.name", new ArrayList<String>() {{ add("name_sample"); }});
                        put("emp.address", new ArrayList<String>() {{ add("address_sample"); }});
                        put("emp.remarks", new ArrayList<String>() {{ add("remarks_sample"); }});
                        put("emp.comments", new ArrayList<String>() {{ add("comments_sample1"); add("comments_sample2");}});
                        put("nablarch_hidden_submit_back", new ArrayList<String>(){{add("");}});  
                        put("nablarch_hidden_submit_go", new ArrayList<String>() {{ add("goParam1=goParam1_sample1|goParam1=goParam1_sample2|goParam2=goParam2_sample"); }});
                    }
                });
        
        String actual = TagTestUtil.getOutput(pageContext);
        String[] splitActual = actual.split(TagUtil.getCustomTagConfig().getLineSeparator());
        String[] splitExpected = Builder.lines(
                "",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMIT_FUNCTION,
                "-->",
                "</script>",
                Builder.lines(
                    "<form",
                    "name=\"nablarch_form1\"",
                    "method=\"post\">").replace(Builder.LS, " "),
                "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"nablarch_token=" + token + "|nablarch_hidden_submit_go=goParam1\\=goParam1_sample1\\|goParam1\\=goParam1_sample2\\|goParam2\\=goParam2_sample|nablarch_hidden_submit_back=\" />",
                "<input type=\"hidden\" name=\"nablarch_submit\" value=\"\" />",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_INFO_VAR + ".nablarch_form1 = {",
                "\"go\": { \"action\": \"./R0001\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" },",
                "\"back\": { \"action\": \"./R0002\", \"allowDoubleSubmission\": false, \"submissionAction\": \"TRANSITION\" }",
                "};",
                "-->",
                "</script>",
                "</form>",
                "<script type=\"text/javascript\">",
                "<!--",
                SUBMISSION_END_MARK_PREFIX + ".nablarch_form1 = true;",
                "-->",
                "</script>").split(Builder.LS);
        for (int i = 0; i < splitActual.length; i++) {
            TagTestUtil.assertTag(splitActual[i], splitExpected[i], " ");
        }
        assertThat(formContext.getRequestIds().size(), is(2));
        assertThat(formContext.getRequestIds().get(0), is("R0001"));
        assertThat(formContext.getRequestIds().get(1), is("R0002"));        
        
        TagUtil.getCustomTagConfig().getNoHiddenEncryptionRequestIds().clear();
    }

    /**
     * リクエストパラメータがnullの時のテスト
     */
    @Test
    public void testNullValueOfRequestParam() throws Exception {
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
        // request param
        Map<String, String[]> paramMap = pageContext.getMockReq().getParameterMap();
        paramMap.put("user.name", new String[] {null});
        paramMap.put("user.address", new String[] {null, "sample_address"});
        paramMap.put("user.kana", new String[] {"sample_kana", null});
        target.setWindowScopePrefixes("user");
        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
    }
}
