package nablarch.common.web.tag;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;

public class PrettyPrintTag_InternalTest extends TagTestSupport<WriteTag> {

    public PrettyPrintTag_InternalTest() {
        super(new WriteTag());
    }
    
    public static final class Entity {
        private String bbb;
        private Date date;
        public Entity(String bbb) {
            this.bbb = bbb;
        }
        public String getBbb() {
            return bbb;
        }
        public void setBbb(String bbb) {
            this.bbb = bbb;
        }
        public Date getDate() {
            return date;
        }
        public void setDate(Date date) {
            this.date = date;
        }
    }
    
    @Test public void testPrettyPrint() throws Exception {

        specPrettyPrint(
            "ol,li,p", "",
            "<ol><li><p>hoge</p></li></ol>",
            "<ol><li><p>hoge</p></li></ol>"
        );

        specPrettyPrint(
            "ol,li", "",
            "<ol><li><p>hoge</p></li></ol>",
            "<ol><li>&lt;p&gt;hoge&lt;/p&gt;</li></ol>"
        );
        
        specPrettyPrint(
            "ol,li,p", "class,id",
            "<ol class='item_list' id='item_list1'><li id='item1'><p>hoge</p></li></ol>",
            "<ol class='item_list' id='item_list1'><li id='item1'><p>hoge</p></li></ol>"
        );
        
        specPrettyPrint(
            "ol, li, p", "class,id",
            "<ol class='item_list' id='item_list1' onclick='maliciousScript();'><li id='item1'><p>hoge</p></li></ol>",
            "&lt;ol class=&#039;item_list&#039; id=&#039;item_list1&#039; onclick=&#039;maliciousScript();&#039;&gt;<li id='item1'><p>hoge</p></li></ol>"
        );
        
        specPrettyPrint(
            "ol, li, p", "class",
            "<ol class='item_list' id='item_list1' onclick='maliciousScript();'><li id='item1'><p>hoge</p></li></ol>",
            "&lt;ol class=&#039;item_list&#039; id=&#039;item_list1&#039; onclick=&#039;maliciousScript();&#039;&gt;&lt;li id=&#039;item1&#039;&gt;<p>hoge</p></li></ol>"
        );
        
        specPrettyPrint(
            "ol,li", "class,id",
            "<ol class='item_list' id='item_list1'>\n" +
            "  <li id='item1'>\n"+
            "    <p>\n"+
            "      hoge\n"+
            "    </p>\n"+
            "  </li>\n"  +
            "</ol>",
            "<ol class='item_list' id='item_list1'>\n" +
            "  <li id='item1'>\n"+
            "    &lt;p&gt;\n"+
            "      hoge\n"+
            "    &lt;/p&gt;\n"+
            "  </li>\n" +
            "</ol>"
        );
    }
    
    @Test public void testPrettyPrint2() throws Exception {
        specPrettyPrint(
            "ol, li", "id, class",
            "<ol>\n"+
            "  <li\n"+
            "    id    = 'id1'\n"+
            "    class = \"class1\"\n"+
            "  >hoge</li>\n"+
            "</ol>",
            "<ol>\n"+
            "  <li\n"+
            "    id    = 'id1'\n"+
            "    class = \"class1\"\n"+
            "  >hoge</li>\n"+
            "</ol>"
        );
    }
    
    @Test public void testPrettyPrint3() throws Exception {
        specPrettyPrint(
            "input", "checked",
            "<input />",
            "<input />"
        );
        
        specPrettyPrint(
            "input", "checked",
            "<input >",
            "<input >"
        );
        
        specPrettyPrint(
            "input", "checked",
            "<input checked/>",
            "<input checked/>"
        );
        
        specPrettyPrint(
            "input", "checked, readonly",
            "<input checked='checked'\n"+
            "       readonly/>",
            "<input checked='checked'\n"+
            "       readonly/>"
        );
        
        specPrettyPrint(
            "input", "checked",
            "<input checked='checked' readonly/>",
            "&lt;input checked=&#039;checked&#039; readonly/&gt;"
        );
    }
    
    @Test public void testPrettyPrint4() throws Exception {
        specPrettyPrint(
            "ol, li", "",
            "<ol></ol>",
            "<ol></ol>"
        );
        specPrettyPrint(
            "ol, li", "",
            "<ol</ol>>",
            "&lt;ol</ol>&gt;"
        );
    }
    
    @Test public void testPrettyPrint_IllegalAttrContent()
    throws Exception {
        specPrettyPrint(
            "a", "href",
            "<a href='http://www.example.com/' />ここをクリック</a>",
            "<a href='http://www.example.com/' />ここをクリック</a>"
        );
        specPrettyPrint(
            "a", "href",
            "<a href='javascript://maliciousScript();' />ここをクリック</a>",
            "&lt;a href=&#039;javascript://maliciousScript();&#039; /&gt;ここをクリック</a>"
        );
    }
    
    private void specPrettyPrint(String safeTags, String safeAttrs, String html, String printed) throws Exception {
        
        pageContext.getAttributes(PageContext.REQUEST_SCOPE)
                   .put("entity", new Entity(html));
        target.setName("entity.bbb");
        target.setWithHtmlFormat(false);
        target.setSafeTags((safeTags.length() == 0) ? new String[]{} : safeTags.split("\\s*,\\s*"));
        target.setSafeAttributes((safeAttrs.length() == 0) ? new String[]{} : safeAttrs.split("\\s*,\\s*"));
        
        assertThat(target.doStartTag(), is(Tag.SKIP_BODY));
        assertThat(target.doEndTag(), is(Tag.EVAL_PAGE));
        
        String actual = TagTestUtil.getOutput(pageContext);
        TagTestUtil.assertTag(actual, printed, " ");
        TagTestUtil.clearOutput(pageContext);
    }
}
