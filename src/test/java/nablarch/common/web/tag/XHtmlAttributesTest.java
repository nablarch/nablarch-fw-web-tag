package nablarch.common.web.tag;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Kiyohito Itoh
 */
public class XHtmlAttributesTest {

    @Before
    public void setup() {
        SystemRepository.load(new ObjectLoader() {
            public Map<String, Object> load() {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("static_content_version", "<1.0>");
                return data;
            }
        });
    }

    @After
    public void tearDown() {
        SystemRepository.clear();
    }

    @Test
    public void testToHTML() throws UnsupportedEncodingException {

        HtmlAttributes attributes = new HtmlAttributes();
        attributes.put(HtmlAttribute.NAME, "name_test");
        attributes.put(HtmlAttribute.CLASS, "class_test");
        attributes.put(HtmlAttribute.TYPE, null);
        attributes.put(HtmlAttribute.SRC, "image.png");
        
        assertThat(attributes.toHTML("input"), is("class=\"class_test\" name=\"name_test\" src=\"image.png?nablarch_static_content_version=&lt;1.0&gt;\""));
    }

    @Test
    public void testToHTMLWithDynamicAttribute() throws UnsupportedEncodingException {

        HtmlAttributes attributes = new HtmlAttributes();
        attributes.put(HtmlAttribute.NAME, "name_test");
        attributes.put(HtmlAttribute.CLASS, "class_test");
        attributes.put(HtmlAttribute.TYPE, null);
        attributes.put(HtmlAttribute.SRC, "image.png");

        attributes.putDynamicAttribute("dyna-name", "dyna-value");

        assertThat(attributes.toHTML("input"), is("class=\"class_test\" name=\"name_test\" src=\"image.png?nablarch_static_content_version=&lt;1.0&gt;\" dyna-name=\"dyna-value\""));
    }

    @Test
    public void testToHTMLWithDynamicAttributeOnly() throws UnsupportedEncodingException {

        HtmlAttributes attributes = new HtmlAttributes();

        attributes.putDynamicAttribute("dyna-name", "dyna-value");
        attributes.putDynamicAttribute("dyna-name2", "dyna-value2");

        assertThat(attributes.toHTML("input"), is("dyna-name=\"dyna-value\" dyna-name2=\"dyna-value2\""));
    }
}
