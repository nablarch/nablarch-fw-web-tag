package nablarch.common.web.tag;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.tagext.Tag;

import org.junit.Before;
import org.junit.Test;

import nablarch.common.web.WebConfig;
import nablarch.common.web.handler.MockPageContext;
import nablarch.common.web.token.TokenGenerator;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;

/**
 * 二重送信トークンの設定をカスタマイズした{@link FormTag}のテスト。
 */
public class FormTagCustomizedDoubleSummitTokenTest extends TagTestSupport<FormTag> {

    public FormTagCustomizedDoubleSummitTokenTest() {
        super(new FormTag());
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        pageContext = new MockPageContext(true);
        target.setPageContext(pageContext);
    }

    /**
     * hidden要素にカスタマイズした名前でトークンが埋め込まれていることを確認するテスト。
     * 
     * @throws Exception
     */
    @Test
    public void testCustomizedConfiguration() throws Exception {

        //トークンをhidden要素へ埋め込むときのname属性値をカスタマイズ
        final WebConfig webConfig = new WebConfig();
        webConfig.setDoubleSubmissionTokenParameterName("customizedParameterName");

        final TokenGenerator tokenGenerator = new TokenGenerator() {
            @Override
            public String generate() {
                return "tokenTestValue";
            }
        };

        //hidden要素を暗号化しない
        final CustomTagConfig customTagConfig = new CustomTagConfig();
        customTagConfig.setUseHiddenEncryption(false);

        //スーパークラスでSystemRepository.clearしている
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                final Map<String, Object> objects = new HashMap<String, Object>();
                objects.put("webConfig", webConfig);
                objects.put("tokenGenerator", tokenGenerator);
                objects.put("customTagConfig", customTagConfig);
                return objects;
            }
        });

        target.setUseToken(true);

        assertThat(target.doStartTag(), is(Tag.EVAL_BODY_INCLUDE));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));

        final String actual = TagTestUtil.getOutput(pageContext);

        final String expected = "<input type=\"hidden\" name=\"nablarch_hidden\" value=\"customizedParameterName=tokenTestValue\" />";

        assertThat(actual, containsString(expected));
    }
}
