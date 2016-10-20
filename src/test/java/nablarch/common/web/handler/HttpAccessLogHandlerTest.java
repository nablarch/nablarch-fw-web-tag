package nablarch.common.web.handler;

import nablarch.common.web.hiddenencryption.HiddenEncryptionUtil;
import nablarch.common.web.tag.TagUtil;
import nablarch.core.ThreadContext;

import nablarch.core.log.Logger;

import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.Result;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.ResourceLocator;
import nablarch.fw.web.handler.HttpAccessLogFormatter.HttpAccessLogContext;
import nablarch.fw.web.handler.HttpAccessLogUtil;
import nablarch.fw.web.handler.HttpRequestJavaPackageMapping;
import nablarch.fw.web.servlet.HttpRequestWrapper;

import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.test.SystemPropertyResource;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import nablarch.test.support.web.servlet.MockServletRequest;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.jsp.PageContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HttpAccessLogHandlerTest {

    @Rule
    public final SystemPropertyResource systemPropertyResource = new SystemPropertyResource();
  
    private HttpRequestWrapper request;
    private ServletExecutionContext context;
    
    @SuppressWarnings("serial")
    private void init(String method, String uri) {
        
        MockServletRequest servletReq = initServletReq(method, uri);
        context = new ServletExecutionContext(servletReq, null, null);
        request = new HttpRequestWrapper(context.getServletRequest());
        
        MockPageContext pageContext = new MockPageContext();
        ThreadContext.setRequestId("R0001");
        ThreadContext.setUserId("U12345678");
        List<String> requestIds = new ArrayList<String>() {{add(ThreadContext.getRequestId());}};
        Map<String, List<String>> values = new HashMap<String, List<String>>() {
            {
                put("nablarch_hidden_submit_go", new ArrayList<String>(){{ add(""); }});
            }
        };
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext, requestIds, values);
        request.setParam(HiddenEncryptionUtil.KEY_HIDDEN_NAME, hiddenValue);
        request.setParam(HiddenEncryptionUtil.KEY_SUBMIT_NAME, "go");
        
        context.setSessionScopeMap(pageContext.getAttributes(PageContext.SESSION_SCOPE));
        
        context.setHandlerQueue(Arrays.asList(new Handler[] {
            new NablarchTagHandler()
          , new HttpRequestJavaPackageMapping("/", "nablarch.common.web")
        }));
        
        OnMemoryLogWriter.clear();
        
        System.setProperty("nablarch.appLog.filePath", "classpath:nablarch/common/web/handler/app-log-default.properties");
        
        TagUtil.getCustomTagConfig().setUseHiddenEncryption(true);
    }
    
    private MockServletRequest initServletReq(String method, String uri) {
        MockHttpSession session = new MockHttpSession();
        session.setId("session_id_test");
        MockServletRequest servletReq = new MockServletRequest();
        servletReq.setContextPath("");
        servletReq.setSession(session);
        servletReq.setRequestUrl("request_url_test");
        servletReq.setRequestURI(uri);
        servletReq.setMethod(method);
        servletReq.setServerPort(9999);
        servletReq.setRemoteAddr("remote_addr_test");
        servletReq.setRemoteHost("remote_host_test");
        servletReq.getParams().put("req_param1", new String[] {"req_param1_test"});
        servletReq.getParams().put("req_param2", new String[] {"req_param2_test"});
        servletReq.getParams().put("req_param3", new String[] {"req_param3_test"});
        return servletReq;
    }
    
    public static final class OccurExceptionHandler extends HttpAccessLogHandler {
        private boolean inRequest;
        public OccurExceptionHandler(boolean inRequest) {
            this.inRequest = inRequest;
        }
        protected Object[] getRequestOptions(HttpRequest request, ExecutionContext context) {
            if (inRequest) {
                throw new IllegalArgumentException("exception occurred in request");
            }
            return super.getRequestOptions(request, context);
        }
        protected Object[] getResponseOptions(HttpRequest request, HttpResponse response, ExecutionContext context) {
            if (!inRequest) {
                throw new IllegalArgumentException("exception occurred in response");
            }
            return super.getResponseOptions(request, response, context);
        }
    }
    
    /**
     * リクエスト処理開始時に例外が発生した場合に例外がスローされること。
     */
    @Test
    public void testUnexpectedExceptionOccurredInRequest() {
        
        init("POST", "/handler/NormalHandler/index.html");
        
        HttpAccessLogHandler handler = new OccurExceptionHandler(true);
        
        try {
            handler.handle(request, context);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("exception occurred in request"));
        }
        
        List<String> messages = OnMemoryLogWriter.getMessages("writer.appLog");
        assertTrue(messages.isEmpty());
    }

    /**
     * リクエスト処理終了時に例外が発生した場合に例外がスローされること。
     */
    @Test
    public void testUnexpectedExceptionOccurredInResponse() {
        
        init("POST", "/handler/NormalHandler/index.html");
        
        HttpAccessLogHandler handler = new OccurExceptionHandler(false);
        
        try {
            handler.handle(request, context);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("exception occurred in response"));
        }
        
        List<String> messages = OnMemoryLogWriter.getMessages("writer.appLog");
        assertTrue(messages.isEmpty());
    }
    
    /**
     * マスク文字の設定不備の場合に例外がスローされること。
     */
    @Test
    public void testInvalidMaskingCharSetting() {
        
        ClassLoader defaultCL = Thread.currentThread().getContextClassLoader();
        ClassLoader customCL = new CustomClassLoader(defaultCL);
        Thread.currentThread().setContextClassLoader(customCL);
        
        try {
            System.setProperty("httpAccessLogFormatter.maskingChar", "$$");
            try {
                new HttpAccessLogHandler();
                fail("must throw IllegalArgumentException.");
            } catch (IllegalArgumentException e) {
                assertThat(e.getMessage(), is("maskingChar was not char type. maskingChar = [$$]"));
            }
        } finally {
            Thread.currentThread().setContextClassLoader(defaultCL);
        }
    }
    
    /**
     * デフォルト設定で正しく出力されること。
     */
    @Test
    public void testDefaultFormat() {
        
        init("POST", "/handler/NormalHandler/index.html");
        
        HttpAccessLogHandler handler = new HttpAccessLogHandler();
        
        HttpResponse response = (HttpResponse) handler.handle(request, context);
        assertThat(response.getStatusCode(), is(200));
        
        List<String> messages = OnMemoryLogWriter.getMessages("writer.accessLog");
        
        String[] splitMsg = messages.get(0).split(Logger.LS);
        int index = 0;
        
        assertThat(splitMsg[index++], is("INFO ACC @@@@ BEGIN @@@@ rid = [R0001] uid = [U12345678] sid = [session_id_test]"));
        assertThat(splitMsg[index++], is("\turl         = [request_url_test]"));
        assertThat(splitMsg[index++], is("\tmethod      = [POST]"));
        assertThat(splitMsg[index++], is("\tport        = [9999]"));
        assertThat(splitMsg[index++], is("\tclient_ip   = [remote_addr_test]"));
        assertThat(splitMsg[index++], is("\tclient_host = [remote_host_test]"));
        
        splitMsg = messages.get(1).split(Logger.LS);
        index = 0;
        
        assertThat(splitMsg[index++], is("INFO ACC @@@@ PARAMETERS @@@@"));
        assertThat(splitMsg[index++], is("\tparameters  = [{"));
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\]}]").matcher(splitMsg[index++]).matches());
        
        splitMsg = messages.get(2).split(Logger.LS);
        index = 0;
        assertThat(splitMsg[index++], is("INFO ACC @@@@ DISPATCHING CLASS @@@@ class = [nablarch.common.web.handler.NormalHandler]"));
        
        splitMsg = messages.get(3).split(Logger.LS);
        index = 0;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("^\\[(.+)\\]");
        
        String[] resLine = splitMsg[index++].split(" ");
        int resLineIndex = 0;
        assertThat(resLine[resLineIndex++], is("INFO"));
        assertThat(resLine[resLineIndex++], is("ACC"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("END"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("rid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[R0001]"));
        assertThat(resLine[resLineIndex++], is("uid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[U12345678]"));
        assertThat(resLine[resLineIndex++], is("sid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[session_id_test]"));
        assertThat(resLine[resLineIndex++], is("url"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[request_url_test]"));
        assertThat(resLine[resLineIndex++], is("status_code"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[200]"));
        assertThat(resLine[resLineIndex++], is("content_path"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[servlet:///success.jsp]"));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("start_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("end_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("execution_time"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("max_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("free_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
    }
    
    /**
     * ResponseLocatorが取得できない場合に正しく出力されること。
     */
    @Test
    public void testNoResponseLocator() {
        
        init("POST", "/handler/ReturnNoResponseLocatorHandler/index.html");
        
        HttpAccessLogContext logContext = HttpAccessLogUtil.getAccessLogContext(request, context);
        logContext.setResponse(new HttpResponse(200) {
            public ResourceLocator getContentPath() {
                return null;
            }
        });
        
        HttpAccessLogHandler handler = new HttpAccessLogHandler();
        
        HttpResponse response = (HttpResponse) handler.handle(request, context);
        assertThat(response.getStatusCode(), is(200));
        
        List<String> messages = OnMemoryLogWriter.getMessages("writer.accessLog");
        
        String[] splitMsg = messages.get(0).split(Logger.LS);
        int index = 0;
        
        assertThat(splitMsg[index++], is("INFO ACC @@@@ BEGIN @@@@ rid = [R0001] uid = [U12345678] sid = [session_id_test]"));
        assertThat(splitMsg[index++], is("\turl         = [request_url_test]"));
        assertThat(splitMsg[index++], is("\tmethod      = [POST]"));
        assertThat(splitMsg[index++], is("\tport        = [9999]"));
        assertThat(splitMsg[index++], is("\tclient_ip   = [remote_addr_test]"));
        assertThat(splitMsg[index++], is("\tclient_host = [remote_host_test]"));
        
        splitMsg = messages.get(1).split(Logger.LS);
        index = 0;
        
        assertThat(splitMsg[index++], is("INFO ACC @@@@ PARAMETERS @@@@"));
        assertThat(splitMsg[index++], is("\tparameters  = [{"));
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\]}]").matcher(splitMsg[index++]).matches());
        
        splitMsg = messages.get(2).split(Logger.LS);
        index = 0;
        assertThat(splitMsg[index++], is("INFO ACC @@@@ DISPATCHING CLASS @@@@ class = [nablarch.common.web.handler.ReturnNoResponseLocatorHandler]"));
        
        splitMsg = messages.get(3).split(Logger.LS);
        index = 0;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("^\\[(.+)\\]");
        
        String[] resLine = splitMsg[index++].split(" ");
        int resLineIndex = 0;
        assertThat(resLine[resLineIndex++], is("INFO"));
        assertThat(resLine[resLineIndex++], is("ACC"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("END"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("rid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[R0001]"));
        assertThat(resLine[resLineIndex++], is("uid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[U12345678]"));
        assertThat(resLine[resLineIndex++], is("sid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[session_id_test]"));
        assertThat(resLine[resLineIndex++], is("url"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[request_url_test]"));
        assertThat(resLine[resLineIndex++], is("status_code"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[200]"));
        assertThat(resLine[resLineIndex++], is("content_path"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[]"));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("start_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("end_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("execution_time"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("max_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("free_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
    }
    
    /**
     * フォーマットでメモリ関連の項目を指定しない場合に正しく出力されること。
     */
    @Test
    public void testWithoutMemoryItem() {
        
        ClassLoader defaultCL = Thread.currentThread().getContextClassLoader();
        ClassLoader customCL = new CustomClassLoader(defaultCL);
        Thread.currentThread().setContextClassLoader(customCL);
        
        System.setProperty("httpAccessLogFormatter.endFormat", "< sid = [$sessionId$] @@@@ END @@@@");
        
        try {
            init("POST", "/handler/NormalHandler/index.html");
            
            HttpAccessLogHandler handler = new HttpAccessLogHandler();
            
            
            // HTTPアクセスログの設定を変更するために、クラスローダを差し替えている影響で、
            // JavaPackageMappingでクラスが見つからないためNotFoundエラーが返る。
            try {
                HttpResponse response = (HttpResponse) handler.handle(request, context);
                fail();
            } catch (RuntimeException e) {
                assertTrue(e instanceof Result.NotFound);
            }
            
            List<String> messages = OnMemoryLogWriter.getMessages("writer.accessLog");
            
            String[] splitMsg = messages.get(0).split(Logger.LS);
            int index = 0;
            
            assertThat(splitMsg[index++], is("INFO ACC @@@@ BEGIN @@@@ rid = [R0001] uid = [U12345678] sid = [session_id_test]"));
            assertThat(splitMsg[index++], is("\turl         = [request_url_test]"));
            assertThat(splitMsg[index++], is("\tmethod      = [POST]"));
            assertThat(splitMsg[index++], is("\tport        = [9999]"));
            assertThat(splitMsg[index++], is("\tclient_ip   = [remote_addr_test]"));
            assertThat(splitMsg[index++], is("\tclient_host = [remote_host_test]"));
            
            splitMsg = messages.get(1).split(Logger.LS);
            index = 0;
            
            assertThat(splitMsg[index++], is("INFO ACC @@@@ PARAMETERS @@@@"));
            assertThat(splitMsg[index++], is("\tparameters  = [{"));
            assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
            assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
            assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\]}]").matcher(splitMsg[index++]).matches());
            
            splitMsg = messages.get(2).split(Logger.LS);
            index = 0;
            assertThat(splitMsg[index++], is("INFO ACC @@@@ DISPATCHING CLASS @@@@ class = [nablarch.common.web.handler.NormalHandler]"));
            
            splitMsg = messages.get(3).split(Logger.LS);
            index = 0;
            
            String[] resLine = splitMsg[index++].split(" ");
            int resLineIndex = 0;
            
            assertThat(resLine[resLineIndex++], is("INFO"));
            assertThat(resLine[resLineIndex++], is("ACC"));
            assertThat(resLine[resLineIndex++], is("<"));
            assertThat(resLine[resLineIndex++], is("sid"));
            assertThat(resLine[resLineIndex++], is("="));
            assertThat(resLine[resLineIndex++], is("[session_id_test]"));
            assertThat(resLine[resLineIndex++], is("@@@@"));
            assertThat(resLine[resLineIndex++], is("END"));
            assertThat(resLine[resLineIndex++], is("@@@@"));
        } finally {
            Thread.currentThread().setContextClassLoader(defaultCL);
        }
    }
    
    /**
     * リクエスト処理開始時のみ出力する場合に正しく出力できること。
     */
    @Test
    public void testBeginLogOnly() {
        
        ClassLoader defaultCL = Thread.currentThread().getContextClassLoader();
        ClassLoader customCL = new CustomClassLoader(defaultCL);
        Thread.currentThread().setContextClassLoader(customCL);
        
        System.setProperty("httpAccessLogFormatter.parametersOutputEnabled", "false");
        System.setProperty("httpAccessLogFormatter.dispatchingClassOutputEnabled", "false");
        System.setProperty("httpAccessLogFormatter.endOutputEnabled", "false");
        
        try {
            init("POST", "/handler/NormalHandler/index.html");
            
            HttpAccessLogHandler handler = new HttpAccessLogHandler();
            
            // HTTPアクセスログの設定を変更するために、クラスローダを差し替えている影響で、
            // JavaPackageMappingでクラスが見つからないためNotFoundエラーが返る
            try {
                HttpResponse response = (HttpResponse) handler.handle(request, context);
                fail();
            } catch (RuntimeException e) {
                assertTrue(e instanceof Result.NotFound);
            }
            
            
            List<String> messages = OnMemoryLogWriter.getMessages("writer.accessLog");
            
            String[] splitMsg = messages.get(0).split(Logger.LS);
            int index = 0;
            
            assertThat(splitMsg[index++], is("INFO ACC @@@@ BEGIN @@@@ rid = [R0001] uid = [U12345678] sid = [session_id_test]"));
            assertThat(splitMsg[index++], is("\turl         = [request_url_test]"));
            assertThat(splitMsg[index++], is("\tmethod      = [POST]"));
            assertThat(splitMsg[index++], is("\tport        = [9999]"));
            assertThat(splitMsg[index++], is("\tclient_ip   = [remote_addr_test]"));
            assertThat(splitMsg[index++], is("\tclient_host = [remote_host_test]"));
        } finally {
            Thread.currentThread().setContextClassLoader(defaultCL);
        }
    }
    
    /**
     * リクエスト処理終了時のみ出力する場合に正しく出力できること。
     */
    @Test
    public void testEndLogOnly() {
        
        ClassLoader defaultCL = Thread.currentThread().getContextClassLoader();
        ClassLoader customCL = new CustomClassLoader(defaultCL);
        Thread.currentThread().setContextClassLoader(customCL);
        
        System.setProperty("httpAccessLogFormatter.endFormat", "< sid = [$sessionId$] @@@@ END @@@@");
        System.setProperty("httpAccessLogFormatter.beginOutputEnabled", "false");
        System.setProperty("httpAccessLogFormatter.parametersOutputEnabled", "false");
        System.setProperty("httpAccessLogFormatter.dispatchingClassOutputEnabled", "false");
        
        try {
            init("POST", "/handler/NormalHandler/index.html");
            
            HttpAccessLogHandler handler = new HttpAccessLogHandler();
            
            // HTTPアクセスログの設定を変更するために、クラスローダを差し替えている影響で、
            // JavaPackageMappingでクラスが見つからないため404が返る。
            try {
                HttpResponse response = (HttpResponse) handler.handle(request, context);
                fail();
            } catch (RuntimeException e) {
                assertTrue(e instanceof Result.NotFound);
            }
            
            List<String> messages = OnMemoryLogWriter.getMessages("writer.accessLog");
            
            String[] splitMsg = messages.get(0).split(Logger.LS);
            int index = 0;
            
            String[] resLine = splitMsg[index++].split(" ");
            int resLineIndex = 0;
            assertThat(resLine[resLineIndex++], is("INFO"));
            assertThat(resLine[resLineIndex++], is("ACC"));
            assertThat(resLine[resLineIndex++], is("<"));
            assertThat(resLine[resLineIndex++], is("sid"));
            assertThat(resLine[resLineIndex++], is("="));
            assertThat(resLine[resLineIndex++], is("[session_id_test]"));
            assertThat(resLine[resLineIndex++], is("@@@@"));
            assertThat(resLine[resLineIndex++], is("END"));
            assertThat(resLine[resLineIndex++], is("@@@@"));
        } finally {
            Thread.currentThread().setContextClassLoader(defaultCL);
        }
    }
    
    /**
     * 後続ハンドラでHttpErrorResponseが発生した場合に正しく出力できること。
     */
    @Test
    public void testHttpErrorResponseOccurred() {
        
        init("POST", "/handler/OccurHttpErrorResponseHandler/index.html");
        
        HttpAccessLogHandler handler = new HttpAccessLogHandler();
        
        try {
            handler.handle(request, context);
            fail();
        } catch (RuntimeException e) {
            assertTrue(e instanceof HttpErrorResponse);
            assertEquals(400, ((HttpErrorResponse)e).getResponse().getStatusCode());
        }

        List<String> messages = OnMemoryLogWriter.getMessages("writer.accessLog");
        
        String[] splitMsg = messages.get(0).split(Logger.LS);
        int index = 0;
        assertThat(splitMsg[index++], is("INFO ACC @@@@ BEGIN @@@@ rid = [R0001] uid = [U12345678] sid = [session_id_test]"));
        assertThat(splitMsg[index++], is("\turl         = [request_url_test]"));
        assertThat(splitMsg[index++], is("\tmethod      = [POST]"));
        assertThat(splitMsg[index++], is("\tport        = [9999]"));
        assertThat(splitMsg[index++], is("\tclient_ip   = [remote_addr_test]"));
        assertThat(splitMsg[index++], is("\tclient_host = [remote_host_test]"));
        
        splitMsg = messages.get(1).split(Logger.LS);
        index = 0;
        
        assertThat(splitMsg[index++], is("INFO ACC @@@@ PARAMETERS @@@@"));
        assertThat(splitMsg[index++], is("\tparameters  = [{"));
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\]}]").matcher(splitMsg[index++]).matches());
        
        splitMsg = messages.get(2).split(Logger.LS);
        index = 0;
        assertThat(splitMsg[index++], is("INFO ACC @@@@ DISPATCHING CLASS @@@@ class = [nablarch.common.web.handler.OccurHttpErrorResponseHandler]"));
        
        splitMsg = messages.get(3).split(Logger.LS);
        index = 0;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("^\\[(.+)\\]");
        
        String[] resLine = splitMsg[index++].split(" ");
        int resLineIndex = 0;
        assertThat(resLine[resLineIndex++], is("INFO"));
        assertThat(resLine[resLineIndex++], is("ACC"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("END"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("rid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[R0001]"));
        assertThat(resLine[resLineIndex++], is("uid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[U12345678]"));
        assertThat(resLine[resLineIndex++], is("sid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[session_id_test]"));
        assertThat(resLine[resLineIndex++], is("url"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[request_url_test]"));
        assertThat(resLine[resLineIndex++], is("status_code"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[400]"));
        assertThat(resLine[resLineIndex++], is("content_path"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[servlet:///error.jsp]"));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("start_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("end_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("execution_time"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("max_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("free_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
    }
    
    /**
     * 後続ハンドラでRuntimeExceptionが発生した場合に正しく出力できること。
     */
    @Test
    public void testRuntimeExceptionOccurred() {
        
        init("POST", "/handler/OccurRuntimeExceptionHandler/index.html");
        
        HttpAccessLogHandler handler = new HttpAccessLogHandler();
        
        try {
            handler.handle(request, context);
            fail("must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("dummy error"));
        }
        
        List<String> messages = OnMemoryLogWriter.getMessages("writer.accessLog");
        
        String[] splitMsg = messages.get(0).split(Logger.LS);
        int index = 0;
        assertThat(splitMsg[index++], is("INFO ACC @@@@ BEGIN @@@@ rid = [R0001] uid = [U12345678] sid = [session_id_test]"));
        assertThat(splitMsg[index++], is("\turl         = [request_url_test]"));
        assertThat(splitMsg[index++], is("\tmethod      = [POST]"));
        assertThat(splitMsg[index++], is("\tport        = [9999]"));
        assertThat(splitMsg[index++], is("\tclient_ip   = [remote_addr_test]"));
        assertThat(splitMsg[index++], is("\tclient_host = [remote_host_test]"));

        splitMsg = messages.get(1).split(Logger.LS);
        index = 0;
        
        assertThat(splitMsg[index++], is("INFO ACC @@@@ PARAMETERS @@@@"));
        assertThat(splitMsg[index++], is("\tparameters  = [{"));
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\],").matcher(splitMsg[index++]).matches());
        assertTrue(Pattern.compile("\t\treq_param[1-3] = \\[req_param[1-3]_test\\]}]").matcher(splitMsg[index++]).matches());
        
        splitMsg = messages.get(2).split(Logger.LS);
        index = 0;
        assertThat(splitMsg[index++], is("INFO ACC @@@@ DISPATCHING CLASS @@@@ class = [nablarch.common.web.handler.OccurRuntimeExceptionHandler]"));
        
        splitMsg = messages.get(3).split(Logger.LS);
        index = 0;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("^\\[(.+)\\]");
        
        String[] resLine = splitMsg[index++].split(" ");
        int resLineIndex = 0;
        assertThat(resLine[resLineIndex++], is("INFO"));
        assertThat(resLine[resLineIndex++], is("ACC"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("END"));
        assertThat(resLine[resLineIndex++], is("@@@@"));
        assertThat(resLine[resLineIndex++], is("rid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[R0001]"));
        assertThat(resLine[resLineIndex++], is("uid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[U12345678]"));
        assertThat(resLine[resLineIndex++], is("sid"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[session_id_test]"));
        assertThat(resLine[resLineIndex++], is("url"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[request_url_test]"));
        assertThat(resLine[resLineIndex++], is("status_code"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[]"));
        assertThat(resLine[resLineIndex++], is("content_path"));
        assertThat(resLine[resLineIndex++], is("="));
        assertThat(resLine[resLineIndex++], is("[]"));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("start_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("end_time"));
        assertNotNull(LogTestUtil.parseDate(resLine[resLineIndex++].trim(), dateFormat, pattern));
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("execution_time"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("max_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
        
        resLine = splitMsg[index++].split("=");
        resLineIndex = 0;
        assertThat(resLine[resLineIndex++].trim(), is("free_memory"));
        assertTrue(Pattern.compile("\\[[0-9]+\\]").matcher(resLine[resLineIndex++].trim()).matches());
    }
}
