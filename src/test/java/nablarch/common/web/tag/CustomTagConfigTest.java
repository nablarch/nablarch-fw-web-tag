package nablarch.common.web.tag;

import nablarch.fw.web.i18n.DirectoryBasedResourcePathRule;
import nablarch.fw.web.i18n.FilenameBasedResourcePathRule;
import nablarch.fw.web.i18n.ResourcePathRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static nablarch.fw.ExecutionContext.FW_PREFIX;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Kiyohito Itoh
 */
public class CustomTagConfigTest {

    @Test
    public void testSpecifyInvalidProperty() {
        
        CustomTagConfig config = new CustomTagConfig();
        
        try {
            config.setMessageFormat(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("messageFormat was invalid. messageFormat must specify the following values. values = [div, span] messageFormat = [null]"));
        }
        
        try {
            config.setMessageFormat("divv");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("messageFormat was invalid. messageFormat must specify the following values. values = [div, span] messageFormat = [divv]"));
        }
        
        try {
            config.setListFormat(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("listFormat was invalid. listFormat must specify the following values. values = [br, div, span, ul, ol, sp] listFormat = [null]"));
        }
        
        try {
            config.setListFormat("li");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("listFormat was invalid. listFormat must specify the following values. values = [br, div, span, ul, ol, sp] listFormat = [li]"));
        }
        
        try {
            config.setCodeListFormat(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("codeListFormat was invalid. codeListFormat must specify the following values. values = [br, div, span, ul, ol, sp] codeListFormat = [null]"));
        }
        
        try {
            config.setCodeListFormat("li");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("codeListFormat was invalid. codeListFormat must specify the following values. values = [br, div, span, ul, ol, sp] codeListFormat = [li]"));
        }
        
        try {
            config.setLineSeparator(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("lineSeparator was invalid. lineSeparator must specify the following values. values = [LF, CR, CRLF] lineSeparator = [null]"));
        }
        
        try {
            config.setLineSeparator("\n");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("lineSeparator was invalid. lineSeparator must specify the following values. values = [LF, CR, CRLF] lineSeparator = [\n]"));
        }
        
        try {
            config.setPagingPosition("no");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("pagingPosition was invalid. pagingPosition must specify the following values. values = [top, bottom, both, none] pagingPosition = [no]"));
        }
        
        try {
            config.setFirstSubmitTag("input");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("firstSubmitTag was invalid. firstSubmitTag must specify the following values. values = [submitLink, submit, button] firstSubmitTag = [input]"));
        }
        
        try {
            config.setPrevSubmitTag("input");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("prevSubmitTag was invalid. prevSubmitTag must specify the following values. values = [submitLink, submit, button] prevSubmitTag = [input]"));
        }
        
        try {
            config.setPageNumberSubmitTag("input");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("pageNumberSubmitTag was invalid. pageNumberSubmitTag must specify the following values. values = [submitLink, submit, button] pageNumberSubmitTag = [input]"));
        }
        
        try {
            config.setNextSubmitTag("input");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("nextSubmitTag was invalid. nextSubmitTag must specify the following values. values = [submitLink, submit, button] nextSubmitTag = [input]"));
        }
        
        try {
            config.setLastSubmitTag("input");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("lastSubmitTag was invalid. lastSubmitTag must specify the following values. values = [submitLink, submit, button] lastSubmitTag = [input]"));
        }

        try {
            config.setDefaultSort("none");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("defaultSort was invalid. defaultSort must specify the following values. values = [asc, desc] defaultSort = [none]"));
        }

        try {
            config.setAutocompleteDisableTarget("text");
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("autocompleteDisableTarget was invalid. autocompleteDisableTarget must specify the following values. "
                 + "values = [all, password, none] autocompleteDisableTarget = [text]"));
        }
        try {
            config.setAutocompleteDisableTarget(null);
            fail("must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(),
                is("autocompleteDisableTarget was invalid. autocompleteDisableTarget must specify the following values. "
                 + "values = [all, password, none] autocompleteDisableTarget = [null]"));
        }
    }
    
    @Test
    public void testSetLineSeparator() {
        CustomTagConfig config = new CustomTagConfig();
        config.setLineSeparator("LF");
        assertThat(config.getLineSeparator(), is("\n"));
        config.setLineSeparator("CR");
        assertThat(config.getLineSeparator(), is("\r"));
        config.setLineSeparator("CRLF");
        assertThat(config.getLineSeparator(), is("\r\n"));
    }
    
    /**
     * タグファイルで使用するプロパティは単体テストできないので、ここでやる。
     */
    @Test
    public void testGetterSetter() {
        
        CustomTagConfig config = new CustomTagConfig();
        
        // デフォルト値を使用する場合
        assertThat(config.getListSearchResultWrapperCss(), is(FW_PREFIX + "listSearchResultWrapper"));
        assertThat(config.getPagingPosition(), is("top"));
        assertThat(config.getUseResultCount(), is(true));
        assertThat(config.getResultCountCss(), is(FW_PREFIX + "resultCount"));
        assertThat(config.getUsePaging(), is(true));
        assertThat(config.getPagingCss(), is(FW_PREFIX + "paging"));
        assertThat(config.getUseCurrentPageNumber(), is(true));
        assertThat(config.getCurrentPageNumberCss(), is(FW_PREFIX + "currentPageNumber"));
        
        assertThat(config.getUseFirstSubmit(), is(false));
        assertThat(config.getFirstSubmitTag(), is("submitLink"));
        assertThat(config.getFirstSubmitType(), is(""));
        assertThat(config.getFirstSubmitCss(), is(FW_PREFIX + "firstSubmit"));
        assertThat(config.getFirstSubmitLabel(), is("最初"));
        assertThat(config.getFirstSubmitName(), is("firstSubmit"));
        
        assertThat(config.getUsePrevSubmit(), is(true));
        assertThat(config.getPrevSubmitTag(), is("submitLink"));
        assertThat(config.getPrevSubmitType(), is(""));
        assertThat(config.getPrevSubmitCss(), is(FW_PREFIX + "prevSubmit"));
        assertThat(config.getPrevSubmitLabel(), is("前へ"));
        assertThat(config.getPrevSubmitName(), is("prevSubmit"));

        assertThat(config.getUsePageNumberSubmit(), is(false));
        assertThat(config.getPageNumberSubmitWrapperCss(), is(FW_PREFIX + "pageNumberSubmitWrapper"));
        assertThat(config.getPageNumberSubmitTag(), is("submitLink"));
        assertThat(config.getPageNumberSubmitType(), is(""));
        assertThat(config.getPageNumberSubmitCss(), is(FW_PREFIX + "pageNumberSubmit"));
        assertThat(config.getPageNumberSubmitName(), is("pageNumberSubmit"));
        
        assertThat(config.getUseNextSubmit(), is(true));
        assertThat(config.getNextSubmitTag(), is("submitLink"));
        assertThat(config.getNextSubmitType(), is(""));
        assertThat(config.getNextSubmitCss(), is(FW_PREFIX + "nextSubmit"));
        assertThat(config.getNextSubmitLabel(), is("次へ"));
        assertThat(config.getNextSubmitName(), is("nextSubmit"));

        assertThat(config.getUseLastSubmit(), is(false));
        assertThat(config.getLastSubmitTag(), is("submitLink"));
        assertThat(config.getLastSubmitType(), is(""));
        assertThat(config.getLastSubmitCss(), is(FW_PREFIX + "lastSubmit"));
        assertThat(config.getLastSubmitLabel(), is("最後"));
        assertThat(config.getLastSubmitName(), is("lastSubmit"));
        
        assertThat(config.getResultSetCss(), is(FW_PREFIX + "resultSet"));
        assertThat(config.getVarRowName(), is("row"));
        assertThat(config.getVarStatusName(), is("status"));
        assertThat(config.getVarCountName(), is("count"));
        assertThat(config.getVarRowCountName(), is("rowCount"));
        assertThat(config.getVarOddEvenName(), is("oddEvenCss"));
        assertThat(config.getOddValue(), is("nablarch_odd"));
        assertThat(config.getEvenValue(), is("nablarch_even"));
        
        assertThat(config.getSortSubmitTag(), is("submitLink"));
        assertThat(config.getSortSubmitType(), is(""));
        assertThat(config.getSortSubmitCss(), is(FW_PREFIX + "sort"));
        assertThat(config.getAscSortSubmitCss(), is(FW_PREFIX + "asc"));
        assertThat(config.getDescSortSubmitCss(), is(FW_PREFIX + "desc"));
        assertThat(config.getDefaultSort(), is("asc"));

        assertThat(config.getResourcePathRule().getClass().getName(),
                   is(DirectoryBasedResourcePathRule.class.getName()));
        
        assertThat(config.getDisplayMethod(), is(DisplayMethod.NORMAL));
        assertNull(config.getDisplayControlCheckers());

        assertThat(config.getPopupOption(), is(nullValue()));

        assertThat(config.getAutocompleteDisableTarget(), is(AutocompleteDisableTarget.NONE));
        
        assertThat(config.getUseGetRequest(), is(true));

        // カスタマイズする場合
        
        config.setListSearchResultWrapperCss("listSearchResultWrapperCss_test");
        assertThat(config.getListSearchResultWrapperCss(), is("listSearchResultWrapperCss_test"));
        
        config.setPagingPosition("top");
        assertThat(config.getPagingPosition(), is("top"));
        
        config.setPagingPosition("bottom");
        assertThat(config.getPagingPosition(), is("bottom"));
        
        config.setPagingPosition("both");
        assertThat(config.getPagingPosition(), is("both"));
        
        config.setPagingPosition("none");
        assertThat(config.getPagingPosition(), is("none"));

        config.setUseResultCount(true);
        assertThat(config.getUseResultCount(), is(true));

        config.setUseResultCount(false);
        assertThat(config.getUseResultCount(), is(false));

        config.setResultCountCss("resultCountCss_test");
        assertThat(config.getResultCountCss(), is("resultCountCss_test"));

        config.setUsePaging(true);
        assertThat(config.getUsePaging(), is(true));

        config.setUsePaging(false);
        assertThat(config.getUsePaging(), is(false));

        config.setPagingCss("pagingCss_test");
        assertThat(config.getPagingCss(), is("pagingCss_test"));
        
        config.setUseCurrentPageNumber(true);
        assertThat(config.getUseCurrentPageNumber(), is(true));
        
        config.setUseCurrentPageNumber(false);
        assertThat(config.getUseCurrentPageNumber(), is(false));
        
        config.setCurrentPageNumberCss("currentPageNumberCss_test");
        assertThat(config.getCurrentPageNumberCss(), is("currentPageNumberCss_test"));


        // first
        
        config.setUseFirstSubmit(true);
        assertThat(config.getUseFirstSubmit(), is(true));
        
        config.setUseFirstSubmit(false);
        assertThat(config.getUseFirstSubmit(), is(false));
        
        config.setFirstSubmitTag("submitLink");
        assertThat(config.getFirstSubmitTag(), is("submitLink"));

        config.setFirstSubmitTag("submit");
        assertThat(config.getFirstSubmitTag(), is("submit"));

        config.setFirstSubmitTag("button");
        assertThat(config.getFirstSubmitTag(), is("button"));
        
        config.setFirstSubmitType("submit");
        assertThat(config.getFirstSubmitType(), is("submit"));
        
        config.setFirstSubmitCss("firstSubmitCss_test");
        assertThat(config.getFirstSubmitCss(), is("firstSubmitCss_test"));
        
        config.setFirstSubmitLabel("firstSubmitLabel_test");
        assertThat(config.getFirstSubmitLabel(), is("firstSubmitLabel_test"));
        
        config.setFirstSubmitName("firstSubmitName_test");
        assertThat(config.getFirstSubmitName(), is("firstSubmitName_test"));

        // prev
        
        config.setUsePrevSubmit(true);
        assertThat(config.getUsePrevSubmit(), is(true));
        
        config.setUsePrevSubmit(false);
        assertThat(config.getUsePrevSubmit(), is(false));
        
        config.setPrevSubmitTag("submitLink");
        assertThat(config.getPrevSubmitTag(), is("submitLink"));

        config.setPrevSubmitTag("submit");
        assertThat(config.getPrevSubmitTag(), is("submit"));

        config.setPrevSubmitTag("button");
        assertThat(config.getPrevSubmitTag(), is("button"));
        
        config.setPrevSubmitType("submit");
        assertThat(config.getPrevSubmitType(), is("submit"));
        
        config.setPrevSubmitCss("prevSubmitCss_test");
        assertThat(config.getPrevSubmitCss(), is("prevSubmitCss_test"));
        
        config.setPrevSubmitLabel("prevSubmitLabel_test");
        assertThat(config.getPrevSubmitLabel(), is("prevSubmitLabel_test"));
        
        config.setPrevSubmitName("prevSubmitName_test");
        assertThat(config.getPrevSubmitName(), is("prevSubmitName_test"));

        // page number
        
        config.setUsePageNumberSubmit(true);
        assertThat(config.getUsePageNumberSubmit(), is(true));
        
        config.setUsePageNumberSubmit(false);
        assertThat(config.getUsePageNumberSubmit(), is(false));
        
        config.setPageNumberSubmitWrapperCss("pageNumberSubmitWrapperCss_test");
        assertThat(config.getPageNumberSubmitWrapperCss(), is("pageNumberSubmitWrapperCss_test"));
        
        config.setPageNumberSubmitTag("submitLink");
        assertThat(config.getPageNumberSubmitTag(), is("submitLink"));

        config.setPageNumberSubmitTag("submit");
        assertThat(config.getPageNumberSubmitTag(), is("submit"));

        config.setPageNumberSubmitTag("button");
        assertThat(config.getPageNumberSubmitTag(), is("button"));
        
        config.setPageNumberSubmitType("submit");
        assertThat(config.getPageNumberSubmitType(), is("submit"));
        
        config.setPageNumberSubmitCss("pageNumberSubmitCss_test");
        assertThat(config.getPageNumberSubmitCss(), is("pageNumberSubmitCss_test"));
        
        config.setPageNumberSubmitName("pageNumberSubmitName_test");
        assertThat(config.getPageNumberSubmitName(), is("pageNumberSubmitName_test"));

        // next
        
        config.setUseNextSubmit(true);
        assertThat(config.getUseNextSubmit(), is(true));
        
        config.setUseNextSubmit(false);
        assertThat(config.getUseNextSubmit(), is(false));
        
        config.setNextSubmitTag("submitLink");
        assertThat(config.getNextSubmitTag(), is("submitLink"));

        config.setNextSubmitTag("submit");
        assertThat(config.getNextSubmitTag(), is("submit"));

        config.setNextSubmitTag("button");
        assertThat(config.getNextSubmitTag(), is("button"));
        
        config.setNextSubmitType("submit");
        assertThat(config.getNextSubmitType(), is("submit"));
        
        config.setNextSubmitCss("nextSubmitCss_test");
        assertThat(config.getNextSubmitCss(), is("nextSubmitCss_test"));
        
        config.setNextSubmitLabel("nextSubmitLabel_test");
        assertThat(config.getNextSubmitLabel(), is("nextSubmitLabel_test"));
        
        config.setNextSubmitName("nextSubmitName_test");
        assertThat(config.getNextSubmitName(), is("nextSubmitName_test"));
        
        // last
        
        config.setUseLastSubmit(true);
        assertThat(config.getUseLastSubmit(), is(true));
        
        config.setUseLastSubmit(false);
        assertThat(config.getUseLastSubmit(), is(false));
        
        config.setLastSubmitTag("submitLink");
        assertThat(config.getLastSubmitTag(), is("submitLink"));

        config.setLastSubmitTag("submit");
        assertThat(config.getLastSubmitTag(), is("submit"));

        config.setLastSubmitTag("button");
        assertThat(config.getLastSubmitTag(), is("button"));
        
        config.setLastSubmitType("submit");
        assertThat(config.getLastSubmitType(), is("submit"));
        
        config.setLastSubmitCss("lastSubmitCss_test");
        assertThat(config.getLastSubmitCss(), is("lastSubmitCss_test"));
        
        config.setLastSubmitLabel("lastSubmitLabel_test");
        assertThat(config.getLastSubmitLabel(), is("lastSubmitLabel_test"));
        
        config.setLastSubmitName("lastSubmitName_test");
        assertThat(config.getLastSubmitName(), is("lastSubmitName_test"));
        
        config.setResultSetCss("resultSetCss_test");
        assertThat(config.getResultSetCss(), is("resultSetCss_test"));
        
        config.setVarRowName("varRowName_test");
        assertThat(config.getVarRowName(), is("varRowName_test"));
        
        config.setVarStatusName("varStatusName_test");
        assertThat(config.getVarStatusName(), is("varStatusName_test"));
        
        config.setVarCountName("varCountName_test");
        assertThat(config.getVarCountName(), is ("varCountName_test"));
        
        config.setVarRowCountName("varRowCountName_test");
        assertThat(config.getVarRowCountName(), is("varRowCountName_test"));
        
        config.setVarOddEvenName("varOddEvenName_test");
        assertThat(config.getVarOddEvenName(), is("varOddEvenName_test"));
        
        config.setOddValue("oddValue_test");
        assertThat(config.getOddValue(), is("oddValue_test"));
        
        config.setEvenValue("evenValue_test");
        assertThat(config.getEvenValue(), is("evenValue_test"));
        
        // sort
        
        config.setSortSubmitTag("sortSubmitTag_test");
        assertThat(config.getSortSubmitTag(), is("sortSubmitTag_test"));
        
        config.setSortSubmitType("sortSubmitType_test");
        assertThat(config.getSortSubmitType(), is("sortSubmitType_test"));
        
        config.setSortSubmitCss("sortSubmitCss_test");
        assertThat(config.getSortSubmitCss(), is("sortSubmitCss_test"));

        config.setAscSortSubmitCss("ascSortSubmitCss_test");
        assertThat(config.getAscSortSubmitCss(), is("ascSortSubmitCss_test"));
        
        config.setDescSortSubmitCss("descSortSubmitCss_test");
        assertThat(config.getDescSortSubmitCss(), is("descSortSubmitCss_test"));

        config.setDefaultSort("desc");
        assertThat(config.getDefaultSort(), is("desc"));
        
        // i18n

        ResourcePathRule resourcePathRule = new FilenameBasedResourcePathRule();
        config.setResourcePathRule(resourcePathRule);
        assertThat(config.getResourcePathRule(), is(resourcePathRule));
        
        // SubmissionDisplayDefault
        config.setDisplayMethod("NODISPLAY"); // 全て大文字
        assertThat(config.getDisplayMethod(), is(DisplayMethod.NODISPLAY));
        config.setDisplayMethod("DISABLED"); // 全て大文字
        assertThat(config.getDisplayMethod(), is(DisplayMethod.DISABLED));
        config.setDisplayMethod("NORMAL"); // 全て大文字
        assertThat(config.getDisplayMethod(), is(DisplayMethod.NORMAL));
        try {
            // 指定できない文字を指定した場合。
            config.setDisplayMethod("hoge");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("displayMethod was invalid. displayMethod must specify the following values. values = [NODISPLAY, DISABLED, NORMAL] displayMethod = [hoge]"));
        }

        // submissionDisplayRules
        // null
        config.setDisplayControlCheckers(null);
        assertNull(config.getDisplayControlCheckers());
        // 設定値あり
        List<DisplayControlChecker> submissionDisplayRules = new ArrayList<DisplayControlChecker>();
        DisplayControlChecker permission = new PermissionDisplayControlChecker();
        submissionDisplayRules.add(permission);
        DisplayControlChecker serviceAvailability = new ServiceAvailabilityDisplayControlChecker();
        submissionDisplayRules.add(serviceAvailability);
        config.setDisplayControlCheckers(submissionDisplayRules);
        List<DisplayControlChecker> actual = config.getDisplayControlCheckers(); 
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0), is(permission));
        assertThat(actual.get(1), is(serviceAvailability));

        // popup
        config.setPopupOption("width=350,height=250");
        assertThat(config.getPopupOption(), is("width=350,height=250"));

        // autocompleteDisableTarget
        config.setAutocompleteDisableTarget("all");
        assertThat(config.getAutocompleteDisableTarget(), is(AutocompleteDisableTarget.ALL));
        config.setAutocompleteDisableTarget("password");
        assertThat(config.getAutocompleteDisableTarget(), is(AutocompleteDisableTarget.PASSWORD));
        config.setAutocompleteDisableTarget("none");
        assertThat(config.getAutocompleteDisableTarget(), is(AutocompleteDisableTarget.NONE));

        // 日付
        config.setYyyymmPattern("yyyy/MM");
        assertThat(config.getYyyymmPattern(), is("yyyy/MM"));


        // safe tag
        config.setSafeTags(new String[]{"p"});
        assertThat(config.getSafeTags(), is(new String[] {"p"}));

        config.setSafeAttributes(new String[] {"class"});
        assertThat(config.getSafeAttributes(), is(new String[] {"class"}));
        
        // GETリクエスト使用有無
        config.setUseGetRequest(false);
        assertThat(config.getUseGetRequest(), is(false));
        config.setUseGetRequest(true);
        assertThat(config.getUseGetRequest(), is(true));
    }
}
