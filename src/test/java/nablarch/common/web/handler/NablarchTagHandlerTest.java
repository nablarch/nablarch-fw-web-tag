package nablarch.common.web.handler;

import nablarch.common.util.WebRequestUtil;
import nablarch.common.web.hiddenencryption.HiddenEncryptionUtil;
import nablarch.common.web.hiddenencryption.KeyEncryptionContextNotFoundException;
import nablarch.common.web.hiddenencryption.TamperingDetectedException;
import nablarch.common.web.tag.CustomTagConfig;
import nablarch.common.web.tag.TagUtil;
import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.*;

import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.test.support.web.servlet.MockServletRequest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.jsp.PageContext;
import java.util.*;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;

/**
 * @author Kiyohito Itoh
 */
public class NablarchTagHandlerTest {
    
    private ServletExecutionContext context;
    private MockHttpRequest request;

    @BeforeClass
    public static void setUpClass() {
        SystemRepository.clear();
    }

    @SuppressWarnings({ "serial" })
    private void init(String requestId, boolean enc) {
        
        MockPageContext pageContext = new MockPageContext();
        List<String> requestIds = new ArrayList<String>() {{add("R0001"); add("R0002");}};
        Map<String, List<String>> values = new HashMap<String, List<String>>() {
            {
                put("param1", new ArrayList<String>() {{add("param1_value");}});
                put("nablarch_hidden_submit_go", new ArrayList<String>() {{ add("go1=aaa|go2=bbb|chgParam2=chgSample2"); }});
                put("nablarch_hidden_submit_cancel", new ArrayList<String>() {{ add("can1=ccc|can2=ddd"); }});
                put("nablarch_hidden_submit_blank", new ArrayList<String>() {{ add(""); }});
            }
        };
        String hiddenValue;
        if (enc) {
            hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext, requestIds, values);
        } else {
            hiddenValue = WebRequestUtil.convertToParamsString(values);
        }
        
        context = new ServletExecutionContext(initServletReq(), null, null);
        context.setHandlerQueue(Arrays.asList(new FinalHandler()));
        context.setSessionScopeMap(pageContext.getAttributes(PageContext.SESSION_SCOPE));
        
        
        request = new MockHttpRequest();
        request.setParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME, hiddenValue);
        request.setParam(HiddenEncryptionUtil.KEY_SUBMIT_NAME, "go");
        request.setMethod("POST");
                
        ThreadContext.setRequestId(requestId);
    }
    
    private MockServletRequest initServletReq() {
        MockHttpSession session = new MockHttpSession();
        session.setId("session_id_test");
        MockServletRequest servletReq = new MockServletRequest();
        servletReq.setSession(session);
        return servletReq;
    }
    
    private static class FinalHandler implements HttpRequestHandler {
        public HttpResponse handle(HttpRequest request, ExecutionContext context) {
            return new HttpResponse(200);
        }
    }
    
    /**
     * 改竄を検出できること。
     */
    @Test
    public void testHandleForTamperingDetected() {

        CustomTagConfig config = TagUtil.getCustomTagConfig();
        boolean useHiddenEncryption = config.getUseHiddenEncryption();
        config.setUseHiddenEncryption(true);

        NablarchTagHandler handler = new NablarchTagHandler();
        handler.setStatusCode(400);
        handler.setPath("/error.jsp");
        
        // failed to decrypt
        init("R0001", true);
        context.getSessionScopeMap().clear();
        try {
            handler.handle(request, context);
            fail();
        } catch (HttpErrorResponse e) {
            HttpResponse response = e.getResponse();
            assertThat(e.getCause().getClass().getName(), is(KeyEncryptionContextNotFoundException.class.getName()));
            assertThat(response.getStatusCode(), is(400));
            assertThat(response.getContentPath().getPath(), is("/error.jsp"));
            assertNull(request.getParam("param1"));
            assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        }

        handler.setSessionExpirePath("/sessionExpired.jsp");
        handler.setSessionExpireStatusCode(401);
        // failed to decrypt
        init("R0001", true);
        context.getSessionScopeMap().clear();
        try {
            handler.handle(request, context);
            fail();
        } catch (HttpErrorResponse e) {
            HttpResponse response = e.getResponse();
            assertThat(e.getCause().getClass().getName(), is(KeyEncryptionContextNotFoundException.class.getName()));
            assertThat(response.getStatusCode(), is(401));
            assertThat(response.getContentPath().getPath(), is("/sessionExpired.jsp"));
            assertNull(request.getParam("param1"));
            assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        }

        // nablarch_hidden param is null
        init("R0001", true);
        request.getParamMap().remove(HiddenEncryptionUtil.KEY_HIDDEN_NAME);
        try {
            handler.handle(request, context);
            fail();
        } catch (HttpErrorResponse e) {
            Throwable cause = e.getCause();
            assertThat(cause.getClass().getName(), is(TamperingDetectedException.class.getName()));
            assertThat(cause.getMessage(), is("valid hidden parameter not found."));
        }

        // nablarch_hidden param is multiple
        init("R0001", true);
        request.setParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME, new String[] {"test1", "test2"});
        try {
            handler.handle(request, context);
            fail();
        } catch (HttpErrorResponse e) {
            Throwable cause = e.getCause();
            assertThat(cause.getClass().getName(), is(TamperingDetectedException.class.getName()));
            assertThat(cause.getMessage(), is("valid hidden parameter not found."));
        }

        // nablarch_submit param is null
        init("R0001", true);
        request.getParamMap().remove(HiddenEncryptionUtil.KEY_SUBMIT_NAME);
        try {
            handler.handle(request, context);
            fail();
        } catch (HttpErrorResponse e) {
            Throwable cause = e.getCause();
            assertThat(cause.getClass().getName(), is(TamperingDetectedException.class.getName()));
            assertThat(cause.getMessage(), is("valid submitName parameter not found."));
        }

        // nablarch_submit param is multiple
        init("R0001", true);
        request.setParam(HiddenEncryptionUtil.KEY_SUBMIT_NAME, new String[] {"test1", "test2"});
        try {
            handler.handle(request, context);
            fail();
        } catch (HttpErrorResponse e) {
            Throwable cause = e.getCause();
            assertThat(cause.getClass().getName(), is(TamperingDetectedException.class.getName()));
            assertThat(cause.getMessage(), is("valid submitName parameter not found."));
        }

        // not hit submitName
        init("R0001", true);
        request.setParam(HiddenEncryptionUtil.KEY_SUBMIT_NAME, new String[] {"unknown"});
        try {
            handler.handle(request, context);
            fail();
        } catch (HttpErrorResponse e) {
            Throwable cause = e.getCause();
            assertThat(cause.getClass().getName(), is(TamperingDetectedException.class.getName()));
            assertThat(cause.getMessage(), is("submitName was invalid."));
        }
        config.setUseHiddenEncryption(useHiddenEncryption);
    }
    
    /**
     * hiddenタグを暗号化しないリクエストIDの設定に応じて動作すること。
     */
    @SuppressWarnings("serial")
    @Test
    public void testHandlerForIgnoreRequestIds() {
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        boolean useHiddenEncryption = config.getUseHiddenEncryption();
        config.setUseHiddenEncryption(true);

        TagUtil.getCustomTagConfig().setNoHiddenEncryptionRequestIds(new ArrayList<String>() {{add("R0001");add("R0003");}});
        
        NablarchTagHandler handler = new NablarchTagHandler();
        
        // contains
        init("R0001", false);
        HttpResponse response = handler.handle(request, context);
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        
        // not contains
        init("R0002", true);
        response = handler.handle(request, context);
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        
        TagUtil.getCustomTagConfig().getNoHiddenEncryptionRequestIds().clear();
        config.setUseHiddenEncryption(useHiddenEncryption);
    }
    
    /**
     * hidden暗号化機能の使用有無の設定に応じて動作すること。
     */
    @Test
    public void testHandlerForUseHiddenEncryption() {
        
        boolean defaultValue = TagUtil.getCustomTagConfig().getUseHiddenEncryption();
        
        NablarchTagHandler handler = new NablarchTagHandler();
        
        // not use hidden encryption
        init("R0001", false);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
        HttpResponse response = handler.handle(request, context);
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        
        // use hidden encryption
        init("R0001", true);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        response = handler.handle(request, context);
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        
        // use hidden encryption
        init("R0001", true);
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
        request.setParam(HiddenEncryptionUtil.KEY_NEEDS_ENCRYPTION, "true");
        response = handler.handle(request, context);
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(defaultValue);
    }
    
    /**
     * デフォルト設定で改竄チェックと複合を行うこと。
     */
    @Test
    public void testHandlerForDefault() {
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        boolean useHiddenEncryption = config.getUseHiddenEncryption();
        config.setUseHiddenEncryption(true);

        NablarchTagHandler handler = new NablarchTagHandler();
        
        // submitName = "go"
        init("R0001", true);
        HttpResponse response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertThat(request.getParam("go1")[0], is("aaa"));
        assertThat(request.getParam("go2")[0], is("bbb"));
        assertNull(request.getParam("can1"));
        assertNull(request.getParam("can2"));
        
        // submitName = "cancel"
        init("R0001", true);
        request.setParam(HiddenEncryptionUtil.KEY_SUBMIT_NAME, "cancel");
        response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertThat(request.getParam("can1")[0], is("ccc"));
        assertThat(request.getParam("can2")[0], is("ddd"));
        assertNull(request.getParam("go1"));
        assertNull(request.getParam("go2"));

        // submitName = "blank"
        init("R0001", true);
        request.setParam(HiddenEncryptionUtil.KEY_SUBMIT_NAME, "blank");
        response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertNull(request.getParam("go1"));
        assertNull(request.getParam("go2"));
        assertNull(request.getParam("can1"));
        assertNull(request.getParam("can2"));
        config.setUseHiddenEncryption(useHiddenEncryption);
    }
    
    /**
     * 2回呼ばれた場合に改竄エラーにならないこと。
     * (1回目、2回目ともに暗号化対象の場合)
     */
    @Test
    public void testHandlerForMultipleInvoke() {

        CustomTagConfig config = TagUtil.getCustomTagConfig();
        boolean useHiddenEncryption = config.getUseHiddenEncryption();
        config.setUseHiddenEncryption(true);

        NablarchTagHandler handler = new NablarchTagHandler();
        CustomTagConfig tagConfig = TagUtil.getCustomTagConfig();
        
        init("R0001", true);
        
        // 1st
        
        HttpResponse response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertThat((CustomTagConfig) context.getRequestScopedVar(NablarchTagHandler.CUSTOM_TAG_CONFIG_KEY), is(tagConfig));
        
        // 2nd (Ex. when forwarding)
        
        context.setHandlerQueue(Arrays.asList(new FinalHandler()));
        response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertThat((CustomTagConfig) context.getRequestScopedVar(NablarchTagHandler.CUSTOM_TAG_CONFIG_KEY), is(
                tagConfig));

        config.setUseHiddenEncryption(useHiddenEncryption);
    }

    /**
     * 2回呼ばれた場合に改竄エラーにならないこと。
     * (1回目が暗号化対象外、2回目が暗号化対象の場合)
     */
    @Test
    public void testHandlerForMultipleInvoke2() {
        CustomTagConfig config = TagUtil.getCustomTagConfig();
        boolean useHiddenEncryption = config.getUseHiddenEncryption();
        config.setUseHiddenEncryption(true);

        NablarchTagHandler handler = new NablarchTagHandler();
        handler.setStatusCode(400);
        handler.setPath("/error.jsp");
        CustomTagConfig tagConfig = TagUtil.getCustomTagConfig();

        tagConfig.setNoHiddenEncryptionRequestIds(new ArrayList<String>() {{add("R0001");add("R0003");}});
        
        init("R0001", false);
        
        // 1st
        
        HttpResponse response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertThat((CustomTagConfig) context.getRequestScopedVar(NablarchTagHandler.CUSTOM_TAG_CONFIG_KEY), is(tagConfig));
        
        // 2nd (Ex. when forwarding)
        
        ThreadContext.setRequestId("R9999");
        context.setHandlerQueue(Arrays.asList(new FinalHandler()));
        response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertThat((CustomTagConfig) context.getRequestScopedVar(NablarchTagHandler.CUSTOM_TAG_CONFIG_KEY), is(tagConfig));
        
        tagConfig.getNoHiddenEncryptionRequestIds().clear();
        config.setUseHiddenEncryption(useHiddenEncryption);
    }
    
    /**
     * リクエストに変更パラメータが設定されること。
     */
    @Test
    public void testHandlerForChangingParameters() {
        
        boolean defaultValue = TagUtil.getCustomTagConfig().getUseHiddenEncryption();
        
        NablarchTagHandler handler = new NablarchTagHandler();
        
        // hidden暗号化を使用する場合。
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        
        init("R0001", true);
        request.setParam("chgParam1", "chgParam1_test");
        request.setParam("chgParam2", "chgParam2_test");
        request.setParam("chgParam3", "chgParam3_test");
        HttpResponse response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertThat(request.getParam("go1")[0], is("aaa"));
        assertThat(request.getParam("go2")[0], is("bbb"));
        assertNull(request.getParam("can1"));
        assertNull(request.getParam("can2"));
        
        assertThat(Arrays.asList(request.getParam("chgParam1")), hasItems("chgParam1_test"));
        assertThat(Arrays.asList(request.getParam("chgParam2")), hasItems("chgSample2"));
        assertThat(Arrays.asList(request.getParam("chgParam3")), hasItems("chgParam3_test"));
        
        // hidden暗号化を使用しない場合。
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);
        init("R0001", false);
        request.setParam("chgParam1", "chgParam1_test");
        request.setParam("chgParam2", "chgParam2_test");
        request.setParam("chgParam3", "chgParam3_test");
        response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertNotNull(request.getParam("param1"));
        assertNull(request.getParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME));
        assertNotNull(request.getParam("go1"));
        assertNotNull(request.getParam("go2"));
        assertNull(request.getParam("can1"));
        assertNull(request.getParam("can2"));
        
        assertThat(Arrays.asList(request.getParam("chgParam1")), hasItems("chgParam1_test"));
        assertThat(Arrays.asList(request.getParam("chgParam2")), hasItems("chgSample2"));
        assertThat(Arrays.asList(request.getParam("chgParam3")), hasItems("chgParam3_test"));
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(defaultValue);
    }
    
    /**
     * リクエストにcheckboxタグのチェックなしに対する値が設定されること。
     */
    @Test
    public void testHandlerForCheckboxOffParamters() {
        
        boolean defaultValue = TagUtil.getCustomTagConfig().getUseHiddenEncryption();
        
        NablarchTagHandler handler = new NablarchTagHandler();
        
        // hidden暗号化を使用する場合。
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
        
        init("R0001", true);
        request.setParam("nablarch_cbx_off_param_user.useDirectMail", "0");
        request.setParam("nablarch_cbx_off_param_user.useEmail", "0");
        request.setParam("user.useDirectMail", "1");
        HttpResponse response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertThat(request.getParam("user.useDirectMail")[0], is("1"));
        assertThat(request.getParam("user.useEmail")[0], is("0"));
        
        // hidden暗号化を使用しない場合。
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(false);

        init("R0001", false);
        request.setParam("nablarch_cbx_off_param_user.useDirectMail", "0");
        request.setParam("nablarch_cbx_off_param_user.useEmail", "0");
        request.setParam("user.useDirectMail", "1");
        response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertThat(request.getParam("user.useDirectMail")[0], is("1"));
        assertThat(request.getParam("user.useEmail")[0], is("0"));
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(defaultValue);
    }
    
    /**
     * GETリクエスト使用時、ハンドラ内の処理が実行されないこと。
     */
    @Test
    public void testHandlerForUseGetRequest() {
    	
        NablarchTagHandler handler = new NablarchTagHandler();
        
        // GETリクエスト使用かつリクエストのメソッドが"GET"の場合、ハンドラ内の処理が実行されないこと
        init("R0001", true);
        request.setMethod("GET");
        TagUtil.getCustomTagConfig().setUseGetRequest(true);
        HttpResponse response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertThat(context.getRequestScopedVar(NablarchTagHandler.CUSTOM_TAG_CONFIG_KEY), is(nullValue()));
        
        // GETリクエスト未使用かつリクエストのメソッドが"GET"の場合、ハンドラ内の処理が実行されること
        init("R0001", true);
        request.setMethod("GET");
        TagUtil.getCustomTagConfig().setUseGetRequest(false);
        response = handler.handle(request, context);
        
        assertThat(response.getStatusCode(), is(200));
        assertThat((CustomTagConfig) context.getRequestScopedVar(NablarchTagHandler.CUSTOM_TAG_CONFIG_KEY), is(TagUtil.getCustomTagConfig()));
        
    }
}
