package nablarch.common.web.tag;

import nablarch.common.web.handler.MockPageContext;
import nablarch.common.web.handler.MockPageContext.MockJspWriter;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.FileUtil;
import nablarch.fw.web.servlet.WebFrontController;
import nablarch.test.support.web.servlet.MockServletContext;
import nablarch.test.support.web.servlet.MockServletFilterConfig;
import org.junit.After;
import org.junit.Before;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TryCatchFinally;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kiyohito Itoh
 */
public abstract class TagTestSupport<T extends Tag> {

    protected MockPageContext pageContext;
    protected T target;

    protected TagTestSupport(T target) {
        this.target = target;
        if (target instanceof GenericAttributesTagSupport) {
            ((GenericAttributesTagSupport) target).getTagName();
        }
    }

    @Before
    public void setUp() throws Exception {
        pageContext = new MockPageContext();
        target.setPageContext(pageContext);

        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {

                WebFrontController controller = new WebFrontController();
                controller.setServletFilterConfig(new MockServletFilterConfig().setServletContext(new MockServletContext() {
                    @Override
                    public URL getResource(String arg0) throws MalformedURLException {
                        return FileUtil.getResourceURL("classpath:" + arg0.substring(1));
                    }
                }));

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("static_content_version", "1.0.0");
                data.put("webFrontController", controller);
                return data;
            }
        });

        if (SystemRepository.get("stringResourceHolder") == null) {
            SystemRepository.load(new ObjectLoader() {
                @Override
                public Map<String, Object> load() {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("stringResourceHolder", new TagTestUtil.MockMessageResource());
                    return data;
                }
            });
        }

        TagUtil.getCustomTagConfig().setAutocompleteDisableTarget("none");
    }

    @After
    public void tearDown() throws Exception {
        SystemRepository.clear();
        if (target instanceof TryCatchFinally) {
            ((TryCatchFinally) target).doFinally();
        }
        ((MockJspWriter) pageContext.getOut()).clearOutput();
    }
}
