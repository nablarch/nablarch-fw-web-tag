package nablarch.common.web.hiddenencryption;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.jsp.PageContext;

import nablarch.common.web.handler.MockPageContext;
import nablarch.common.web.handler.WebTestUtil;
import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;
import nablarch.core.util.Base64Util;
import nablarch.fw.ExecutionContext;

import org.junit.Test;

/**
 * @author Kiyohito Itoh
 */
public class HiddenEncryptionUtilTest {

    @SuppressWarnings("serial")
    @Test
    public void testEncryptAndDecrypt() {
        
        MockPageContext pageContext = new MockPageContext();
        List<String> requestIds = new ArrayList<String>() {{add("R0001"); add("R0002");}};
        Map<String, List<String>> values = new HashMap<String, List<String>>() {
            {
                put("param1", new ArrayList<String>() {{add("param1");}});
                put("param2", new ArrayList<String>() {{add("param2a"); add("param2b");}});
                put("param3", new ArrayList<String>() {{add("param3a"); add("param3b"); add("param3c");}});
            }
        };
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext, requestIds, values);
        
        ExecutionContext context = new ExecutionContext();
        context.setSessionScopeMap(pageContext.getAttributes(PageContext.SESSION_SCOPE));
        
        ThreadContext.setRequestId("R0001");
        
        Map<String, List<String>> actuals = HiddenEncryptionUtil.decryptHiddenValues(context, hiddenValue);
        Map<String, List<String>> expecteds = new HashMap<String, List<String>>(values);
        expecteds.remove(HiddenEncryptionUtil.KEY_HIDDEN_REQUEST_IDS_NAME);
        WebTestUtil.assertParams(actuals, expecteds);
    }
    
    @SuppressWarnings("serial")
    @Test
    public void testDecryptForInvalidParam() throws Throwable {
        
        // decryption failed for different key
        MockPageContext pageContext = new MockPageContext();
        List<String> requestIds = new ArrayList<String>() {{add("R0001"); add("R0002");}};
        Map<String, List<String>> values = new HashMap<String, List<String>>() {
            {
                put("param1", new ArrayList<String>() {{add("param1");}});
            }
        };
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext, requestIds, values);
        
        ExecutionContext context = new ExecutionContext();
        context.setSessionScopeMap(pageContext.getAttributes(PageContext.SESSION_SCOPE));
        
        ThreadContext.setRequestId("R0001");

        // value is null
        try {
            HiddenEncryptionUtil.decryptHiddenValues(context, null);
            fail();
        } catch (TamperingDetectedException e) {
            assertThat(e.getMessage(), is("decryption failed."));
            assertThat(e.getCause().getMessage(), is("context or src is null."));
        }
        
        // value is blank
        try {
            HiddenEncryptionUtil.decryptHiddenValues(context, "");
            fail();
        } catch (TamperingDetectedException e) {
            assertThat(e.getMessage(), is("hash was invalid."));
        }
        
        // base64 decoding failed
        try {
            HiddenEncryptionUtil.decryptHiddenValues(context, "value1");
            fail();
        } catch (TamperingDetectedException e) {
            assertThat(e.getMessage(), is("base64 decoding failed."));
        }

        // 複合に失敗するケース。
        try {
            HiddenEncryptionUtil.decryptHiddenValues(context, Base64Util.encode("あいうえお".getBytes("MS932")));
            fail();
        } catch (TamperingDetectedException e) {
            assertThat(e.getMessage(), is("decryption failed."));
            assertThat(e.getCause().getMessage(), is("decryption failed. transformation = [AES/CBC/PKCS5Padding]"));
        }
        
        // hash was invalid
        char[] c = hiddenValue.toCharArray();
        c[0] = c[0] == 'A' ? 'B' : 'A';
        context.setSessionScopeMap(pageContext.getAttributes(PageContext.SESSION_SCOPE));
        try {
            HiddenEncryptionUtil.decryptHiddenValues(context, String.valueOf(c));
            fail();
        } catch (TamperingDetectedException e) {
            assertThat(e.getMessage(), is("hash was invalid."));
        }
        
        // requestId was invalid
        requestIds = new ArrayList<String>() {{add("R0001"); add("R0002");}};
        hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext, requestIds, values);
        ThreadContext.setRequestId("R999");
        try {
            HiddenEncryptionUtil.decryptHiddenValues(context, hiddenValue);
            fail();
        } catch (TamperingDetectedException e) {
            assertThat(e.getMessage(), is("requestId was invalid."));
        }
    }
    
    @SuppressWarnings("serial")
    @Test
    public void testUsingCustomEncpryptor() {
        
        XmlComponentDefinitionLoader loader = new XmlComponentDefinitionLoader("nablarch/common/web/hiddenencryption/custom-encryptor-test.xml");
        DiContainer container = new DiContainer(loader);
        SystemRepository.load(container);
        
        MockPageContext pageContext = new MockPageContext();
        List<String> requestIds = new ArrayList<String>() {{add("R0001"); add("R0002");}};
        Map<String, List<String>> values = new HashMap<String, List<String>>() {
            {
                put("param1", new ArrayList<String>() {{add("param1");}});
            }
        };
        
        String hiddenValue = HiddenEncryptionUtil.encryptHiddenValues(pageContext, requestIds, values);
        
        String base64DecodedValue = new String(Base64Util.decode(hiddenValue));
        assertTrue(base64DecodedValue.indexOf("R0001") != -1);
        assertTrue(base64DecodedValue.indexOf("R0002") != -1);
        assertTrue(base64DecodedValue.indexOf("param1") != -1);
        
        ExecutionContext context = new ExecutionContext();
        context.setSessionScopeMap(pageContext.getAttributes(PageContext.SESSION_SCOPE));
        
        ThreadContext.setRequestId("R0001");
        
        Map<String, List<String>> actuals = HiddenEncryptionUtil.decryptHiddenValues(context, hiddenValue);
        Map<String, List<String>> expecteds = new HashMap<String, List<String>>(values);
        expecteds.remove(HiddenEncryptionUtil.KEY_HIDDEN_REQUEST_IDS_NAME);
        WebTestUtil.assertParams(actuals, expecteds);
        
        SystemRepository.clear();
    }
}
