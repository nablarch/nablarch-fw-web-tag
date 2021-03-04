package nablarch.common.web.tag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import nablarch.core.util.StringUtil;
import nablarch.fw.web.i18n.DirectoryBasedResourcePathRule;
import nablarch.fw.web.i18n.ResourcePathRule;

import static nablarch.fw.ExecutionContext.FW_PREFIX;

/**
 * カスタムタグのデフォルト値を保持するクラス。
 * @author Kiyohito Itoh
 */
public class CustomTagConfig {
    
    /** errorタグと入力項目タグのerrorCss属性のデフォルト値 */
    private String errorCss = FW_PREFIX + "error";
    
    /** errorタグのmessageFormat属性のデフォルト値 */
    private String messageFormat = "div";
    
    /** selectタグ、radioButtonsタグ、checkboxesタグのelementLabelPattern属性のデフォルト値 */
    private String elementLabelPattern = "$LABEL$";
    
    /** selectタグ、radioButtonsタグ、checkboxesタグのlistFormat属性のデフォルト値 */
    private ListFormat listFormat = ListFormat.BR;
    
    /** codeSelectタグ、codeRadioButtonsタグ、codeCheckboxesタグのlabelPattern属性のデフォルト値 */
    private String codeLabelPattern = "$NAME$";
    
    /** codeSelectタグ、codeRadioButtonsタグ、codeCheckboxesタグのcodeListFormat属性のデフォルト値 */
    private ListFormat codeListFormat = ListFormat.BR;
    
    /** 出力時に使用する改行コード */
    private String lineSeparator = "\n";
    
    /** URI指定でhttpとhttpsを切り替える際に使用するhttp用のポート番号 */
    private int port = PORT_NOT_SPECIFIED;
    
    /** URI指定でhttpとhttpsを切り替える際に使用するhttps用のポート番号 */
    private int securePort = PORT_NOT_SPECIFIED;
    
    /** URI指定でhttpとhttpsを切り替える際に使用するホストのデフォルト値 */
    private String host;
    
    /** 日付文字列のフォーマットに使用するデフォルトのパターン */
    private String datePattern;

    /** 年月日のフォーマットに使用するデフォルトのパターン */
    private String yyyymmddPattern;

    /** 年月のフォーマットに使用するデフォルトのパターン */
    private String yyyymmPattern;

    /** 日時のフォーマットに使用するデフォルトのパターン */
    private String dateTimePattern;

    /** フォーマットに使用するパターンの区切り文字 */
    private String patternSeparator = "|";

    /** hiddenタグの暗号化機能を使用するか否か。 */
    private boolean useHiddenEncryption = true;
    
    /** hiddenタグを暗号化しないリクエストID */
    private Set<String> noHiddenEncryptionRequestIds = new HashSet<String>();
    
    /** listSearchResultタグのlistSearchResultWrapperCss属性のデフォルト値。 */
    private String listSearchResultWrapperCss = FW_PREFIX + "listSearchResultWrapper";
    
    /** listSearchResultタグのuseResultCount属性のデフォルト値。 */
    private boolean useResultCount = true;
    
    /** listSearchResultタグのresultCountCss属性のデフォルト値。 */
    private String resultCountCss = FW_PREFIX + "resultCount";
    
    /** listSearchResultタグのusePaging属性のデフォルト値。 */
    private boolean usePaging = true;
    
    /** listSearchResultタグのpagingPosition属性のデフォルト値。 */
    private String pagingPosition = "top";
    
    /** listSearchResultタグのpagingCss属性のデフォルト値。 */
    private String pagingCss = FW_PREFIX + "paging";
    
    /** listSearchResultタグのuseCurrentPageNumber属性のデフォルト値。 */
    private boolean useCurrentPageNumber = true;
    
    /** listSearchResultタグのcurrentPageNumberCss属性のデフォルト値。 */
    private String currentPageNumberCss = FW_PREFIX + "currentPageNumber";
    
    /** listSearchResultタグのuseFirstSubmit属性のデフォルト値。 */
    private boolean useFirstSubmit = false;
    
    /** listSearchResultタグのfirstSubmitTag属性のデフォルト値。 */
    private String firstSubmitTag = "submitLink";
    
    /** listSearchResultタグのfirstSubmitType属性のデフォルト値。 */
    private String firstSubmitType = "";
    
    /** listSearchResultタグのfirstSubmitCss属性のデフォルト値。 */
    private String firstSubmitCss = FW_PREFIX + "firstSubmit";
    
    /** listSearchResultタグのfirstSubmitLabel属性のデフォルト値。 */
    private String firstSubmitLabel = "最初";
    
    /** listSearchResultタグのfirstSubmitName属性のデフォルト値。 */
    private String firstSubmitName = "firstSubmit";
    
    /** listSearchResultタグのusePrevSubmit属性のデフォルト値。 */
    private boolean usePrevSubmit = true;
    
    /** listSearchResultタグのprevSubmitTag属性のデフォルト値。 */
    private String prevSubmitTag = "submitLink";
    
    /** listSearchResultタグのprevSubmitType属性のデフォルト値。 */
    private String prevSubmitType = "";
    
    /** listSearchResultタグのprevSubmitCss属性のデフォルト値。 */
    private String prevSubmitCss = FW_PREFIX + "prevSubmit";
    
    /** listSearchResultタグのprevSubmitLabel属性のデフォルト値。 */
    private String prevSubmitLabel = "前へ";
    
    /** listSearchResultタグのprevSubmitName属性のデフォルト値。 */
    private String prevSubmitName = "prevSubmit";
    
    /** listSearchResultタグのusePageNumberSubmit属性のデフォルト値。 */
    private boolean usePageNumberSubmit = false;
    
    /** listSearchResultタグのpageNumberSubmitWrapperCss属性のデフォルト値。 */
    private String pageNumberSubmitWrapperCss = FW_PREFIX + "pageNumberSubmitWrapper";
    
    /** listSearchResultタグのpageNumberSubmitTag属性のデフォルト値。 */
    private String pageNumberSubmitTag = "submitLink";
    
    /** listSearchResultタグのpageNumberSubmitType属性のデフォルト値。 */
    private String pageNumberSubmitType = "";
    
    /** listSearchResultタグのpageNumberSubmitCss属性のデフォルト値。 */
    private String pageNumberSubmitCss = FW_PREFIX + "pageNumberSubmit";
    
    /** listSearchResultタグのpageNumberSubmitName属性のデフォルト値。 */
    private String pageNumberSubmitName = "pageNumberSubmit";
    
    /** listSearchResultタグのuseNextSubmit属性のデフォルト値。 */
    private boolean useNextSubmit = true;
    
    /** listSearchResultタグのnextSubmitTag属性のデフォルト値。 */
    private String nextSubmitTag = "submitLink";
    
    /** listSearchResultタグのnextSubmitType属性のデフォルト値。 */
    private String nextSubmitType = "";
    
    /** listSearchResultタグのnextSubmitCss属性のデフォルト値。 */
    private String nextSubmitCss = FW_PREFIX + "nextSubmit";
    
    /** listSearchResultタグのnextSubmitLabel属性のデフォルト値。 */
    private String nextSubmitLabel = "次へ";
    
    /** listSearchResultタグのnextSubmitName属性のデフォルト値。 */
    private String nextSubmitName = "nextSubmit";
    
    /** listSearchResultタグのuseLastSubmit属性のデフォルト値。 */
    private boolean useLastSubmit = false;
    
    /** listSearchResultタグのlastSubmitTag属性のデフォルト値。 */
    private String lastSubmitTag = "submitLink";
    
    /** listSearchResultタグのlastSubmitType属性のデフォルト値。 */
    private String lastSubmitType = "";
    
    /** listSearchResultタグのlastSubmitCss属性のデフォルト値。 */
    private String lastSubmitCss = FW_PREFIX + "lastSubmit";
    
    /** listSearchResultタグのlastSubmitLabel属性のデフォルト値。 */
    private String lastSubmitLabel = "最後";
    
    /** listSearchResultタグのlastSubmitName属性のデフォルト値。 */
    private String lastSubmitName = "lastSubmit";
    
    /** listSearchResultタグのresultSetCss属性のデフォルト値。 */
    private String resultSetCss = FW_PREFIX + "resultSet";
    
    /** listSearchResultタグのvarRowName属性のデフォルト値。 */
    private String varRowName = "row";
    
    /** listSearchResultタグのvarStatusName属性のデフォルト値。 */
    private String varStatusName = "status";
    
    /** listSearchResultタグのvarCount属性のデフォルト値。 */
    private String varCountName = "count";
    
    /** listSearchResultタグのvarRowCount属性のデフォルト値。 */
    private String varRowCountName = "rowCount";
    
    /** listSearchResultタグのvarOddEvenName属性のデフォルト値。 */
    private String varOddEvenName = "oddEvenCss";
    
    /** listSearchResultタグのoddValue属性のデフォルト値。 */
    private String oddValue = FW_PREFIX + "odd";
    
    /** listSearchResultタグのevenValue属性のデフォルト値。 */
    private String evenValue = FW_PREFIX + "even";
    
    /** pagingSortSubmitタグのsortSubmitTag属性のデフォルト値 */
    private String sortSubmitTag = "submitLink";
    
    /** pagingSortSubmitタグのsortSubmitType属性のデフォルト値 */
    private String sortSubmitType = "";

    /** pagingSortSubmitタグのsortSubmitCss属性のデフォルト値 */
    private String sortSubmitCss = FW_PREFIX + "sort";
    
    /** pagingSortSubmitタグのascSortSubmitCss属性のデフォルト値 */
    private String ascSortSubmitCss = FW_PREFIX + "asc";
    
    /** pagingSortSubmitタグのdescSortSubmitCss属性のデフォルト値 */
    private String descSortSubmitCss = FW_PREFIX + "desc";
    
    /** pagingSortSubmitタグのdefaultSort属性のデフォルト値 */
    private String defaultSort = "asc";
    
    /** checkboxタグのチェックありに対する値のデフォルト値 */
    private String checkboxOnValue = "1";
    
    /** checkboxタグのチェックなしに対する値のデフォルト値 */
    private String checkboxOffValue = "0";
    
    /** scriptタグのボディに対するプレフィックス */
    private String scriptBodyPrefix = "<!--";
    
    /** scriptタグのボディに対するサフィックス */
    private String scriptBodySuffix = "-->";

    /** サブミットを行う表示制御判定を行う条件リスト */
    private List<DisplayControlChecker> displayControlCheckers;

    /** サブミットを行う表示方法デフォルト値 */
    private DisplayMethod displayMethod = DisplayMethod.NORMAL;

    /** サブミット表示制御判定が「非活性」時に使用するJSPのURL */
    private String submitLinkDisabledJsp;

    /** 修飾付き出力タグ(n:prettyPrint) で利用可能なタグ */
    private String[] safeTags = null;

    /** 修飾付き出力タグ(n:prettyPrint) で利用可能な属性 */
    private String[] safeAttributes = null;

    /** ポップアップのウィンドウ名 */
    private String popupWindowName = null;

    /** ポップアップのオプション情報 */
    private String popupOption = null;

    /** 動的属性でBooleanとして扱う属性 */
    private Set<String> dynamicBooleanAttributes = new HashSet<String>(
            Arrays.asList(
                    "async",
                    "autofocus",
                    "checked",
                    "disabled",
                    "formnovalidate",
                    "hidden",
                    "ismap",
                    "itemscope",
                    "multiple",
                    "nomodule",
                    "novalidate",
                    "readonly",
                    "required",
                    "reversed",
                    "selected"
            )
    );

    /**
     * 言語対応のリソースパスを取得する際に使用する{@link ResourcePathRule}。
     */
    private ResourcePathRule resourcePathRule = new DirectoryBasedResourcePathRule();
    
    /**
     * 入力系のタグで name 属性に指定した名称に対応する値を取得する際に、値を保持するオブジェクトが 
     * null であれば null を設定されたものとして動作するか否か。
     * @see TagUtil#getValue(javax.servlet.jsp.PageContext, String, boolean)
     */
    private boolean useValueAsNullIfObjectExists = true;

    /** autocomplete属性をOFFにする対象のデフォルト値 */
    private AutocompleteDisableTarget autocompleteDisableTarget = AutocompleteDisableTarget.NONE;
    
    /** GETメソッドによるリクエストを使用するか否か */
    private boolean useGetRequest = true;

    /**
     * errorタグと入力項目タグのerrorCss属性のデフォルト値を取得する。
     * @return エラーレベルのメッセージに使用するCSSクラス名
     */
    public String getErrorCss() {
        return errorCss;
    }

    /**
     * errorタグと入力項目タグのerrorCss属性のデフォルト値を設定する。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     * @see ErrorTag#setErrorCss(String)
     */
    public void setErrorCss(String errorCss) {
        this.errorCss = errorCss;
    }

    /**
     * errorタグのmessageFormat属性のデフォルト値を取得する。
     * @return フォーマット
     */
    public String getMessageFormat() {
        return messageFormat;
    }

    /**
     * errorタグのmessageFormat属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>div</li>
     *     <li>span</li>
     * </ul>
     * @param messageFormat フォーマット
     * @throws IllegalArgumentException フォーマットが不正な場合
     * @see ErrorTag#setMessageFormat(String)
     */
    public void setMessageFormat(String messageFormat) {
        if (messageFormat == null || !ErrorTag.MESSAGE_FORMATS.contains(messageFormat)) {
            throw new IllegalArgumentException(
                String.format("messageFormat was invalid. messageFormat must specify the following values. values = %s messageFormat = [%s]",
                              ErrorTag.MESSAGE_FORMATS, messageFormat));
        }
        this.messageFormat = messageFormat;
    }

    /**
     * selectタグ、radioButtonsタグ、checkboxesタグのelementLabelPattern属性のデフォルト値を取得する。
     * @return パターン文字列
     */
    public String getElementLabelPattern() {
        return elementLabelPattern;
    }

    /**
     * selectタグ、radioButtonsタグ、checkboxesタグのelementLabelPattern属性のデフォルト値を設定する。
     * @param elementLabelPattern パターン文字列
     * @see ListSelectTag#setElementLabelPattern(String)
     * @see ListRadioButtonsTag#setElementLabelPattern(String)
     * @see ListCheckboxesTag#setElementLabelPattern(String)
     */
    public void setElementLabelPattern(String elementLabelPattern) {
        this.elementLabelPattern = elementLabelPattern;
    }

    /**
     * selectタグ、radioButtonsタグ、checkboxesタグのlistFormat属性のデフォルト値を取得する。
     * @return フォーマット
     */
    public ListFormat getListFormat() {
        return listFormat;
    }

    /**
     * selectタグ、radioButtonsタグ、checkboxesタグのlistFormat属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>br(brタグ)</li>
     *     <li>div(divタグ)</li>
     *     <li>span(spanタグ)</li>
     *     <li>ul(ulタグ)</li>
     *     <li>ol(olタグ)</li>
     *     <li>sp(スペース区切り)</li>
     * </ul>
     * @param listFormat フォーマット
     * @throws IllegalArgumentException フォーマットが不正な場合
     * @see ListSelectTag#setListFormat(String)
     * @see ListRadioButtonsTag#setListFormat(String)
     * @see ListCheckboxesTag#setListFormat(String)
     */
    public void setListFormat(String listFormat) {
        if (listFormat == null || !ListFormat.getFormats().contains(listFormat)) {
            throw new IllegalArgumentException(
                String.format("listFormat was invalid. listFormat must specify the following values. values = %s listFormat = [%s]",
                              ListFormat.getFormats(), listFormat));
        }
        this.listFormat = ListFormat.getFormatByTagName(listFormat);
    }

    /**
     * codeSelectタグ、codeRadioButtonsタグ、codeCheckboxesタグのlabelPattern属性のデフォルト値を取得する。
     * @return パターン
     */
    public String getCodeLabelPattern() {
        return codeLabelPattern;
    }

    /**
     * codeSelectタグ、codeRadioButtonsタグ、codeCheckboxesタグのlabelPattern属性のデフォルト値を設定する。
     * @param codeLabelPattern パターン
     * @see CodeSelectTag#setLabelPattern(String)
     * @see CodeRadioButtonsTag#setLabelPattern(String)
     * @see CodeCheckboxesTag#setLabelPattern(String)
     */
    public void setCodeLabelPattern(String codeLabelPattern) {
        this.codeLabelPattern = codeLabelPattern;
    }

    /**
     * codeSelectタグ、codeRadioButtonsタグ、codeCheckboxesタグのcodeListFormat属性のデフォルト値を取得する。
     * @return フォーマット
     */
    public ListFormat getCodeListFormat() {
        return codeListFormat;
    }

    /**
     * codeSelectタグ、codeRadioButtonsタグ、codeCheckboxesタグのcodeListFormat属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>br(brタグ)</li>
     *     <li>div(divタグ)</li>
     *     <li>span(spanタグ)</li>
     *     <li>ul(ulタグ)</li>
     *     <li>ol(olタグ)</li>
     *     <li>sp(スペース区切り)</li>
     * </ul>
     * @param codeListFormat フォーマット
     * @throws IllegalArgumentException フォーマットが不正な場合
     * @see CodeSelectTag#setListFormat(String)
     * @see CodeRadioButtonsTag#setListFormat(String)
     * @see CodeCheckboxesTag#setListFormat(String)
     */
    public void setCodeListFormat(String codeListFormat) {
        if (codeListFormat == null || !ListFormat.getFormats().contains(codeListFormat)) {
            throw new IllegalArgumentException(
                String.format("codeListFormat was invalid. codeListFormat must specify the following values. values = %s codeListFormat = [%s]",
                              ListFormat.getFormats(), codeListFormat));
        }
        this.codeListFormat = ListFormat.getFormatByTagName(codeListFormat);
    }

    /**
     * カスタムタグが出力時に使用する改行コードのデフォルト値を取得する。
     * @return 改行コード
     */
    public String getLineSeparator() {
        return lineSeparator;
    }
    
    /**
     * カスタムタグが出力時に使用する改行コードを設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>LF(Line Feed)</li>
     *     <li>CR(Carriage Return)</li>
     *     <li>CRLF</li>
     * </ul>
     * デフォルトはLF。
     * @param lineSeparator 改行コード
     * @throws IllegalArgumentException 改行コードが不正な場合
     */
    public void setLineSeparator(String lineSeparator) {
        if ("LF".equals(lineSeparator)) {
            this.lineSeparator = "\n";
        } else if ("CR".equals(lineSeparator)) {
            this.lineSeparator = "\r";
        } else if ("CRLF".equals(lineSeparator)) {
            this.lineSeparator = "\r\n";
        } else {
            throw new IllegalArgumentException(
                    String.format("lineSeparator was invalid. lineSeparator must specify the following values. values = %s lineSeparator = [%s]",
                                  "[LF, CR, CRLF]", lineSeparator));
        }
    }

    /**
     * ホストとポート番号を組み合わせたベースパスを取得する。
     * @param secure httpsの場合は{@code true}、httpの場合は{@code false}
     * @return ホストとポート番号を組み合わせたベースパス。ホストが{@code null}または空文字の場合は{@code null}
     */
    public String getBasePath(boolean secure) {
        if (StringUtil.isNullOrEmpty(host)) {
            return null;
        }
        return host + getPortExpression(secure ? securePort : port);
    }
    
    /** ポート番号が指定されていないことを示す定数 */
    private static final int PORT_NOT_SPECIFIED = -1;
    
    /**
     * ポート番号(コロン付き)を取得する。
     * @param port ポート番号
     * @return ポート番号(コロン付き)。ポート番号が指定されてない場合は空文字
     */
    private String getPortExpression(int port) {
        return port != PORT_NOT_SPECIFIED ? ":" + port : "";
    }

    /**
     * URI指定でhttpとhttpsを切り替える際に使用するhttp用のポート番号を設定する。
     * @param port ポート番号
     */
    public void setPort(int port) {
        this.port = port;
    }
    
    /**
     * URI指定でhttpとhttpsを切り替える際に使用するhttps用のポート番号を設定する。
     * @param securePort ポート番号
     */
    public void setSecurePort(int securePort) {
        this.securePort = securePort;
    }
    
    /**
     * URI指定でhttpとhttpsを切り替える際に使用するホストのデフォルト値を設定する。
     * @param host ホスト名
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 日付文字列のフォーマットに使用するパターンのデフォルト値を取得する。
     * @return パターン
     */
    public String getDatePattern() {
        return datePattern;
    }

    /**
     * 日付文字列のフォーマットに使用するパターンのデフォルト値を設定する。
     * @param datePattern パターン
     */
    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    /**
     * 年月日のフォーマットに使用するパターンのデフォルト値を取得する。
     * @return パターン
     */
    public String getYyyymmddPattern() {
        return yyyymmddPattern;
    }

    /**
     * 年月日のフォーマットに使用するパターンのデフォルト値を設定する。
     * @param yyyymmddPattern パターン
     */
    public void setYyyymmddPattern(String yyyymmddPattern) {
        this.yyyymmddPattern = yyyymmddPattern;
    }

    /**
     * 年月のフォーマットに使用するパターンのデフォルト値を取得する。
     * @return パターン
     */
    public String getYyyymmPattern() {
        return yyyymmPattern;
    }

    /**
     * 年月のフォーマットに使用するパターンのデフォルト値を設定する。
     * @param yyyymmPattern パターン
     */
    public void setYyyymmPattern(String yyyymmPattern) {
        this.yyyymmPattern = yyyymmPattern;
    }

    /**
     * 日時のフォーマットに使用するパターンのデフォルト値を取得する。
     * @return パターン
     */
    public String getDateTimePattern() {
        return dateTimePattern;
    }

    /**
     * 日時のフォーマットに使用するパターンのデフォルト値を設定する。
     * @param dateTimePattern パターン
     */
    public void setDateTimePattern(String dateTimePattern) {
        this.dateTimePattern = dateTimePattern;
    }

    /**
     * フォーマットに使用するパターン区切り文字のデフォルト値を取得する。
     * @return 区切り文字
     */
    public String getPatternSeparator() {
        return patternSeparator;
    }

    /**
     * フォーマットに使用するパターン区切り文字のデフォルト値を設定する。
     * @param patternSeparator 区切り文字
     */
    public void setPatternSeparator(String patternSeparator) {
        this.patternSeparator = patternSeparator;
    }

    /**
     * hiddenタグの暗号化機能を使用するか否かのデフォルト値を取得する。
     * @return 使用する場合は{@code true}
     */
    public boolean getUseHiddenEncryption() {
        return useHiddenEncryption;
    }

    /**
     * hiddenタグの暗号化機能を使用するか否かのデフォルト値を設定する。
     * <p/>
     * デフォルトは{@code true}。
     * @param useHiddenEncryption 使用する場合は{@code true}
     */
    public void setUseHiddenEncryption(boolean useHiddenEncryption) {
        this.useHiddenEncryption = useHiddenEncryption;
    }
    
    /**
     * hiddenタグを暗号化しないリクエストIDのデフォルト値を取得する。
     * @return リクエストID
     */
    public Set<String> getNoHiddenEncryptionRequestIds() {
        return noHiddenEncryptionRequestIds;
    }
    
    /**
     * hiddenタグを暗号化しないリクエストIDのデフォルト値を設定する。
     * <p/>
     * list要素で指定する。
     * @param noHiddenEncryptionRequestIds リクエストID
     */
    public void setNoHiddenEncryptionRequestIds(List<String> noHiddenEncryptionRequestIds) {
        this.noHiddenEncryptionRequestIds.addAll(noHiddenEncryptionRequestIds);
    }

    /**
     * listSearchResultタグのlistSearchResultWrapperCss属性のデフォルト値を取得する。
     * @return ページングテーブル全体をラップするdivタグのclass属性
     */
    public String getListSearchResultWrapperCss() {
        return listSearchResultWrapperCss;
    }

    /**
     * listSearchResultタグのlistSearchResultWrapperCss属性のデフォルト値を設定する。
     * @param listSearchResultWrapperCss ページングテーブル全体をラップするdivタグのclass属性
     */
    public void setListSearchResultWrapperCss(String listSearchResultWrapperCss) {
        this.listSearchResultWrapperCss = listSearchResultWrapperCss;
    }

    /**
     * listSearchResultタグのuseResultCount属性のデフォルト値を取得する。
     * @return 検索結果件数を表示する場合は{@code true}
     */
    public boolean getUseResultCount() {
        return useResultCount;
    }

    /**
     * listSearchResultタグのuseResultCount属性のデフォルト値を設定する。
     * @param useResultCount 検索結果件数を表示する場合は{@code true}
     */
    public void setUseResultCount(boolean useResultCount) {
        this.useResultCount = useResultCount;
    }

    /**
     * listSearchResultタグのresultCountCss属性のデフォルト値を取得する。
     * @return 検索結果件数をラップするdivタグのclass属性
     */
    public String getResultCountCss() {
        return resultCountCss;
    }

    /**
     * listSearchResultタグのresultCountCss属性のデフォルト値を設定する。
     * @param resultCountCss 検索結果件数をラップするdivタグのclass属性
     */
    public void setResultCountCss(String resultCountCss) {
        this.resultCountCss = resultCountCss;
    }

    /**
     * listSearchResultタグのusePaging属性のデフォルト値を取得する。
     * @return ページングを使用する場合は{@code true}
     */
    public boolean getUsePaging() {
        return usePaging;
    }

    /**
     * listSearchResultタグのusePaging属性のデフォルト値を設定する。
     * @param usePaging ページングを使用する場合は{@code true}
     */
    public void setUsePaging(boolean usePaging) {
        this.usePaging = usePaging;
    }

    /**
     * listSearchResultタグのpagingPosition属性のデフォルト値を取得する。
     * @return ページングの表示位置
     */
    public String getPagingPosition() {
        return pagingPosition;
    }

    /**
     * listSearchResultタグのpagingPosition属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを設定する。
     * <ul>
     *     <li>top</li>
     *     <li>bottom</li>
     *     <li>both</li>
     *     <li>none</li>
     * </ul>
     * @param pagingPosition ページングの表示位置
     * @throws IllegalArgumentException 表示位置が不正な場合
     */
    public void setPagingPosition(String pagingPosition) {
        if (!Pattern.compile("top|bottom|both|none").matcher(pagingPosition).matches()) {
            throw new IllegalArgumentException(
                String.format("pagingPosition was invalid. pagingPosition must specify the following values."
                        + " values = [top, bottom, both, none] pagingPosition = [%s]", pagingPosition));
        }
        this.pagingPosition = pagingPosition;
    }

    /**
     * listSearchResultタグのpagingCss属性のデフォルト値を取得する。
     * @return ページングのサブミット要素をラップするdivタグのclass属性
     */
    public String getPagingCss() {
        return pagingCss;
    }

    /**
     * listSearchResultタグのpagingCss属性のデフォルト値を設定する。
     * @param pagingCss ページングのサブミット要素をラップするdivタグのclass属性
     */
    public void setPagingCss(String pagingCss) {
        this.pagingCss = pagingCss;
    }

    /**
     * listSearchResultタグのuseCurrentPageNumber属性のデフォルト値を取得する。
     * @return 現在のページ番号を使用する場合は{@code true}
     */
    public boolean getUseCurrentPageNumber() {
        return useCurrentPageNumber;
    }

    /**
     * listSearchResultタグのuseCurrentPageNumber属性のデフォルト値を設定する。
     * @param useCurrentPageNumber 現在のページ番号を使用する場合は{@code true}
     */
    public void setUseCurrentPageNumber(boolean useCurrentPageNumber) {
        this.useCurrentPageNumber = useCurrentPageNumber;
    }

    /**
     * listSearchResultタグのcurrentPageNumberCss属性のデフォルト値を取得する。
     * @return 現在のページ番号をラップするdivタグのclass属性
     */
    public String getCurrentPageNumberCss() {
        return currentPageNumberCss;
    }

    /**
     * listSearchResultタグのcurrentPageNumberCss属性のデフォルト値を設定する。
     * @param currentPageNumberCss 現在のページ番号をラップするdivタグのclass属性
     */
    public void setCurrentPageNumberCss(String currentPageNumberCss) {
        this.currentPageNumberCss = currentPageNumberCss;
    }

    /**
     * listSearchResultタグのuseFirstSubmit属性のデフォルト値を取得する。
     * @return 最初のページ遷移するサブミットを使用する場合は{@code true}
     */
    public boolean getUseFirstSubmit() {
        return useFirstSubmit;
    }

    /**
     * listSearchResultタグのuseFirstSubmit属性のデフォルト値を設定する。
     * @param useFirstSubmit 最初のページ遷移するサブミットを使用する場合は{@code true}
     */
    public void setUseFirstSubmit(boolean useFirstSubmit) {
        this.useFirstSubmit = useFirstSubmit;
    }

    /**
     * listSearchResultタグのfirstSubmitTag属性のデフォルト値を取得する。
     * @return firstSubmitTag属性に設定されているタグ名
     */
    public String getFirstSubmitTag() {
        return firstSubmitTag;
    }
    
    /**
     * サブミットのタグ名を正しいことを表明する。
     * @param tag タグ名
     * @param prop プロパティ名
     * @throws IllegalArgumentException タグ名が不正な場合
     */
    private void assertSubmitTag(String tag, String prop) {
        if (!SUBMIT_TAG_PATTERN.matcher(tag).matches()) {
            throw new IllegalArgumentException(
                String.format("%s was invalid. %s must specify the following values. values = [submitLink, submit, button] %s = [%s]",
                              prop, prop, prop, tag));
        }
    }
    
    /** サブミットのタグ名パターン */
    private static final Pattern SUBMIT_TAG_PATTERN = Pattern.compile("submitLink|submit|button");
    
    /**
     * listSearchResultタグのfirstSubmitTag属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submitLink</li>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param firstSubmitTag firstSubmitTag属性に設定するタグ名
     * @throws IllegalArgumentException タグ名が不正な場合
     */
    public void setFirstSubmitTag(String firstSubmitTag) {
        assertSubmitTag(firstSubmitTag, "firstSubmitTag");
        this.firstSubmitTag = firstSubmitTag;
    }

    /**
     * listSearchResultタグのfirstSubmitType属性のデフォルト値を取得する。
     * @return サブミットに使用するタグのtype属性
     */
    public String getFirstSubmitType() {
        return firstSubmitType;
    }

    /**
     * listSearchResultタグのfirstSubmitType属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param firstSubmitType サブミットに使用するタグのtype属性
     */
    public void setFirstSubmitType(String firstSubmitType) {
        this.firstSubmitType = firstSubmitType;
    }

    /**
     * listSearchResultタグのfirstSubmitCss属性のデフォルト値を取得する。
     * @return 最初のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public String getFirstSubmitCss() {
        return firstSubmitCss;
    }

    /**
     * listSearchResultタグのfirstSubmitCss属性のデフォルト値を設定する。
     * @param firstSubmitCss 最初のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public void setFirstSubmitCss(String firstSubmitCss) {
        this.firstSubmitCss = firstSubmitCss;
    }

    /**
     * listSearchResultタグのfirstSubmitLabel属性のデフォルト値を取得する。
     * @return 最初のページに遷移するサブミットに使用するラベル
     */
    public String getFirstSubmitLabel() {
        return firstSubmitLabel;
    }

    /**
     * listSearchResultタグのfirstSubmitLabel属性のデフォルト値を設定する。
     * @param firstSubmitLabel 最初のページに遷移するサブミットに使用するラベル
     */
    public void setFirstSubmitLabel(String firstSubmitLabel) {
        this.firstSubmitLabel = firstSubmitLabel;
    }

    /**
     * listSearchResultタグのfirstSubmitName属性のデフォルト値を取得する。
     * @return 最初のページに遷移するサブミットに使用するタグのname属性
     */
    public String getFirstSubmitName() {
        return firstSubmitName;
    }

    /**
     * listSearchResultタグのfirstSubmitName属性のデフォルト値を設定する。
     * @param firstSubmitName 最初のページに遷移するサブミットに使用するタグのname属性
     */
    public void setFirstSubmitName(String firstSubmitName) {
        this.firstSubmitName = firstSubmitName;
    }

    /**
     * listSearchResultタグのusePrevSubmit属性のデフォルト値を取得する。
     * @return 前のページに遷移するサブミットを使用する場合は{@code true}
     */
    public boolean getUsePrevSubmit() {
        return usePrevSubmit;
    }

    /**
     * listSearchResultタグのusePrevSubmit属性のデフォルト値を設定する。
     * @param usePrevSubmit 前のページに遷移するサブミットを使用する場合は{@code true}
     */
    public void setUsePrevSubmit(boolean usePrevSubmit) {
        this.usePrevSubmit = usePrevSubmit;
    }

    /**
     * listSearchResultタグのprevSubmitTag属性のデフォルト値を取得する。
     * @return prevSubmitTag属性に設定するタグ名
     */
    public String getPrevSubmitTag() {
        return prevSubmitTag;
    }

    /**
     * listSearchResultタグのprevSubmitTag属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submitLink</li>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param prevSubmitTag prevSubmitTag属性に設定するタグ名
     * @throws IllegalArgumentException タグ名が不正な場合
     */
    public void setPrevSubmitTag(String prevSubmitTag) {
        assertSubmitTag(prevSubmitTag, "prevSubmitTag");
        this.prevSubmitTag = prevSubmitTag;
    }

    /**
     * listSearchResultタグのprevSubmitType属性のデフォルト値を取得する。
     * @return 前のページに遷移するサブミットに使用するタグのtype属性
     */
    public String getPrevSubmitType() {
        return prevSubmitType;
    }

    /**
     * listSearchResultタグのprevSubmitType属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param prevSubmitType 前のページに遷移するサブミットに使用するタグのtype属性
     */
    public void setPrevSubmitType(String prevSubmitType) {
        this.prevSubmitType = prevSubmitType;
    }

    /**
     * listSearchResultタグのprevSubmitCss属性のデフォルト値を取得する。
     * @return 前のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public String getPrevSubmitCss() {
        return prevSubmitCss;
    }

    /**
     * listSearchResultタグのprevSubmitCss属性のデフォルト値を設定する。
     * @param prevSubmitCss 前のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public void setPrevSubmitCss(String prevSubmitCss) {
        this.prevSubmitCss = prevSubmitCss;
    }

    /**
     * listSearchResultタグのprevSubmitLabel属性のデフォルト値を取得する。
     * @return 前のページに遷移するサブミットに使用するラベル
     */
    public String getPrevSubmitLabel() {
        return prevSubmitLabel;
    }

    /**
     * listSearchResultタグのprevSubmitLabel属性のデフォルト値を設定する。
     * @param prevSubmitLabel 前のページに遷移するサブミットに使用するラベル
     */
    public void setPrevSubmitLabel(String prevSubmitLabel) {
        this.prevSubmitLabel = prevSubmitLabel;
    }

    /**
     * listSearchResultタグのprevSubmitName属性のデフォルト値を取得する。
     * @return 前のページに遷移するサブミットに使用するタグのname属性
     */
    public String getPrevSubmitName() {
        return prevSubmitName;
    }

    /**
     * listSearchResultタグのprevSubmitName属性のデフォルト値を設定する。
     * @param prevSubmitName 前のページに遷移するサブミットに使用するタグのname属性
     */
    public void setPrevSubmitName(String prevSubmitName) {
        this.prevSubmitName = prevSubmitName;
    }

    /**
     * listSearchResultタグのusePageNumberSubmit属性のデフォルト値を取得する。
     * @return ページ番号のページに遷移するサブミットを使用する場合は{@code true}
     */
    public boolean getUsePageNumberSubmit() {
        return usePageNumberSubmit;
    }

    /**
     * listSearchResultタグのusePageNumberSubmit属性のデフォルト値を設定する。
     * @param usePageNumberSubmit ページ番号のページに遷移するサブミットを使用する場合は{@code true}
     */
    public void setUsePageNumberSubmit(boolean usePageNumberSubmit) {
        this.usePageNumberSubmit = usePageNumberSubmit;
    }

    /**
     * listSearchResultタグのpageNumberSubmitWrapperCss属性のデフォルト値を取得する。
     * @return ページ番号全体をラップするdivタグのclass属性
     */
    public String getPageNumberSubmitWrapperCss() {
        return pageNumberSubmitWrapperCss;
    }

    /**
     * listSearchResultタグのpageNumberSubmitWrapperCss属性のデフォルト値を設定する。
     * @param pageNumberSubmitWrapperCss ページ番号全体をラップするdivタグのclass属性
     */
    public void setPageNumberSubmitWrapperCss(String pageNumberSubmitWrapperCss) {
        this.pageNumberSubmitWrapperCss = pageNumberSubmitWrapperCss;
    }

    /**
     * listSearchResultタグのpageNumberSubmitTag属性のデフォルト値を取得する。
     * @return pageNumberSubmitTag属性に設定するタグ名
     */
    public String getPageNumberSubmitTag() {
        return pageNumberSubmitTag;
    }

    /**
     * listSearchResultタグのpageNumberSubmitTag属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submitLink</li>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param pageNumberSubmitTag pageNumberSubmitTag属性に設定するタグ名
     * @throws IllegalArgumentException タグ名が不正な場合
     */
    public void setPageNumberSubmitTag(String pageNumberSubmitTag) {
        assertSubmitTag(pageNumberSubmitTag, "pageNumberSubmitTag");
        this.pageNumberSubmitTag = pageNumberSubmitTag;
    }

    /**
     * listSearchResultタグのpageNumberSubmitType属性のデフォルト値を取得する。
     * @return ページ番号のページに遷移するサブミットに使用するタグのtype属性
     */
    public String getPageNumberSubmitType() {
        return pageNumberSubmitType;
    }

    /**
     * listSearchResultタグのpageNumberSubmitType属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param pageNumberSubmitType ページ番号のページに遷移するサブミットに使用するタグのtype属性
     */
    public void setPageNumberSubmitType(String pageNumberSubmitType) {
        this.pageNumberSubmitType = pageNumberSubmitType;
    }

    /**
     * listSearchResultタグのpageNumberSubmitCss属性のデフォルト値を取得する。
     * @return ページ番号のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public String getPageNumberSubmitCss() {
        return pageNumberSubmitCss;
    }

    /**
     * listSearchResultタグのpageNumberSubmitCss属性のデフォルト値を設定する。
     * @param pageNumberSubmitCss ページ番号のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public void setPageNumberSubmitCss(String pageNumberSubmitCss) {
        this.pageNumberSubmitCss = pageNumberSubmitCss;
    }

    /**
     * listSearchResultタグのpageNumberSubmitName属性のデフォルト値を取得する。
     * @return ページ番号のページに遷移するサブミットに使用するタグのname属性
     */
    public String getPageNumberSubmitName() {
        return pageNumberSubmitName;
    }

    /**
     * listSearchResultタグのpageNumberSubmitName属性のデフォルト値を設定する。
     * @param pageNumberSubmitName ページ番号のページに遷移するサブミットに使用するタグのname属性
     */
    public void setPageNumberSubmitName(String pageNumberSubmitName) {
        this.pageNumberSubmitName = pageNumberSubmitName;
    }

    /**
     * listSearchResultタグのuseNextSubmit属性のデフォルト値を取得する。
     * @return 次のページに遷移するサブミットを使用する場合は{@code true}
     */
    public boolean getUseNextSubmit() {
        return useNextSubmit;
    }

    /**
     * listSearchResultタグのuseNextSubmit属性のデフォルト値を設定する。
     * @param useNextSubmit 次のページに遷移するサブミットを使用する場合は{@code true}
     */
    public void setUseNextSubmit(boolean useNextSubmit) {
        this.useNextSubmit = useNextSubmit;
    }

    /**
     * listSearchResultタグのnextSubmitTag属性のデフォルト値を取得する。
     * @return nextSubmitTag属性に設定されたタグ名
     */
    public String getNextSubmitTag() {
        return nextSubmitTag;
    }

    /**
     * listSearchResultタグのnextSubmitTag属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submitLink</li>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param nextSubmitTag nextSubmitTag属性に設定するタグ名
     * @throws IllegalArgumentException タグ名が不正な場合
     */
    public void setNextSubmitTag(String nextSubmitTag) {
        assertSubmitTag(nextSubmitTag, "nextSubmitTag");
        this.nextSubmitTag = nextSubmitTag;
    }

    /**
     * listSearchResultタグのnextSubmitType属性のデフォルト値を取得する。
     * @return 次のページに遷移するサブミットに使用するタグのtype属性
     */
    public String getNextSubmitType() {
        return nextSubmitType;
    }

    /**
     * listSearchResultタグのnextSubmitType属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param nextSubmitType 次のページに遷移するサブミットに使用するタグのtype属性
     */
    public void setNextSubmitType(String nextSubmitType) {
        this.nextSubmitType = nextSubmitType;
    }

    /**
     * listSearchResultタグのnextSubmitCss属性のデフォルト値を取得する。
     * @return 次のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public String getNextSubmitCss() {
        return nextSubmitCss;
    }

    /**
     * listSearchResultタグのnextSubmitCss属性のデフォルト値を設定する。
     * @param nextSubmitCss 次のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public void setNextSubmitCss(String nextSubmitCss) {
        this.nextSubmitCss = nextSubmitCss;
    }

    /**
     * listSearchResultタグのnextSubmitLabel属性のデフォルト値を取得する。
     * @return 次のページに遷移するサブミットに使用するラベル
     */
    public String getNextSubmitLabel() {
        return nextSubmitLabel;
    }

    /**
     * listSearchResultタグのnextSubmitLabel属性のデフォルト値を設定する。
     * @param nextSubmitLabel 次のページに遷移するサブミットに使用するラベル
     */
    public void setNextSubmitLabel(String nextSubmitLabel) {
        this.nextSubmitLabel = nextSubmitLabel;
    }

    /**
     * listSearchResultタグのnextSubmitName属性のデフォルト値を取得する。
     * @return 次のページに遷移するサブミットに使用するタグのname属性
     */
    public String getNextSubmitName() {
        return nextSubmitName;
    }

    /**
     * listSearchResultタグのnextSubmitName属性のデフォルト値を設定する。
     * @param nextSubmitName 次のページに遷移するサブミットに使用するタグのname属性
     */
    public void setNextSubmitName(String nextSubmitName) {
        this.nextSubmitName = nextSubmitName;
    }

    /**
     * listSearchResultタグのuseLastSubmit属性のデフォルト値を取得する。
     * @return 最後のページに遷移するサブミットを使用する場合は{@code true}
     */
    public boolean getUseLastSubmit() {
        return useLastSubmit;
    }

    /**
     * listSearchResultタグのuseLastSubmit属性のデフォルト値を設定する。
     * @param useLastSubmit 最後のページに遷移するサブミットを使用する場合は{@code true}
     */
    public void setUseLastSubmit(boolean useLastSubmit) {
        this.useLastSubmit = useLastSubmit;
    }

    /**
     * listSearchResultタグのlastSubmitTag属性のデフォルト値を取得する。
     * @return lastSubmitTag属性に設定されたタグ名
     */
    public String getLastSubmitTag() {
        return lastSubmitTag;
    }

    /**
     * listSearchResultタグのlastSubmitTag属性のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>submitLink</li>
     *     <li>submit</li>
     *     <li>button</li>
     * </ul>
     * @param lastSubmitTag lastSubmitTag属性に設定するタグ名
     * @throws IllegalArgumentException タグ名が不正な場合
     */
    public void setLastSubmitTag(String lastSubmitTag) {
        assertSubmitTag(lastSubmitTag, "lastSubmitTag");
        this.lastSubmitTag = lastSubmitTag;
    }

    /**
     * listSearchResultタグのlastSubmitType属性のデフォルト値を取得する。
     * @return 最後のページに遷移するサブミットに使用するタグのtype属性
     */
    public String getLastSubmitType() {
        return lastSubmitType;
    }

    /**
     * listSearchResultタグのlastSubmitType属性のデフォルト値を設定する。
     * @param lastSubmitType 最後のページに遷移するサブミットに使用するタグのtype属性
     */
    public void setLastSubmitType(String lastSubmitType) {
        this.lastSubmitType = lastSubmitType;
    }

    /**
     * listSearchResultタグのlastSubmitCss属性のデフォルト値を取得する。
     * @return 最後のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public String getLastSubmitCss() {
        return lastSubmitCss;
    }

    /**
     * listSearchResultタグのlastSubmitCss属性のデフォルト値を設定する。
     * @param lastSubmitCss 最後のページに遷移するサブミットをラップするdivタグのclass属性
     */
    public void setLastSubmitCss(String lastSubmitCss) {
        this.lastSubmitCss = lastSubmitCss;
    }

    /**
     * listSearchResultタグのlastSubmitLabel属性のデフォルト値を取得する。
     * @return 最後のページに遷移するサブミットに使用するラベル
     */
    public String getLastSubmitLabel() {
        return lastSubmitLabel;
    }

    /**
     * listSearchResultタグのlastSubmitLabel属性のデフォルト値を設定する。
     * @param lastSubmitLabel 最後のページに遷移するサブミットに使用するラベル
     */
    public void setLastSubmitLabel(String lastSubmitLabel) {
        this.lastSubmitLabel = lastSubmitLabel;
    }

    /**
     * listSearchResultタグのlastSubmitName属性のデフォルト値を取得する。
     * @return 最後のページに遷移するサブミットに使用するタグのname属性
     */
    public String getLastSubmitName() {
        return lastSubmitName;
    }

    /**
     * listSearchResultタグのlastSubmitName属性のデフォルト値を設定する。
     * @param lastSubmitName 最後のページに遷移するサブミットに使用するタグのname属性
     */
    public void setLastSubmitName(String lastSubmitName) {
        this.lastSubmitName = lastSubmitName;
    }

    /**
     * listSearchResultタグのresultSetCss属性のデフォルト値を取得する。
     * @return 検索結果テーブルのclass属性
     */
    public String getResultSetCss() {
        return resultSetCss;
    }

    /**
     * listSearchResultタグのresultSetCss属性のデフォルト値を設定する。
     * @param resultSetCss 検索結果テーブルのclass属性
     */
    public void setResultSetCss(String resultSetCss) {
        this.resultSetCss = resultSetCss;
    }

    /**
     * listSearchResultタグのvarRowName属性のデフォルト値を取得する。
     * @return ボディ行のフラグメントで行データ(c:forEachタグのvar属性)を参照する際に使用する変数名
     */
    public String getVarRowName() {
        return varRowName;
    }

    /**
     * listSearchResultタグのvarRowName属性のデフォルト値を設定する。
     * @param varRowName ボディ行のフラグメントで行データ(c:forEachタグのvar属性)を参照する際に使用する変数名
     */
    public void setVarRowName(String varRowName) {
        this.varRowName = varRowName;
    }

    /**
     * listSearchResultタグのvarStatusName属性のデフォルト値を取得する。
     * @return ボディ行のフラグメントでステータス(c:forEachタグのstatus属性)を参照する際に使用する変数名
     */
    public String getVarStatusName() {
        return varStatusName;
    }

    /**
     * listSearchResultタグのvarStatusName属性のデフォルト値を設定する。
     * @param varStatusName ボディ行のフラグメントでステータス(c:forEachタグのstatus属性)を参照する際に使用する変数名
     */
    public void setVarStatusName(String varStatusName) {
        this.varStatusName = varStatusName;
    }

    /**
     * listSearchResultタグのvarCount属性のデフォルト値を取得する。
     * @return ステータス(c:forEachタグのstatus属性)のcountプロパティを参照する際に使用する変数名
     */
    public String getVarCountName() {
        return varCountName;
    }

    /**
     * listSearchResultタグのvarCount属性のデフォルト値を設定する。
     * @param varCountName ステータス(c:forEachタグのstatus属性)のcountプロパティを参照する際に使用する変数名
     */
    public void setVarCountName(String varCountName) {
        this.varCountName = varCountName;
    }

    /**
     * listSearchResultタグのvarRowCount属性のデフォルト値を取得する。
     * @return 検索結果のカウント(検索結果の取得開始位置＋ステータスのカウント)を参照する際に使用する変数名
     */
    public String getVarRowCountName() {
        return varRowCountName;
    }

    /**
     * listSearchResultタグのvarRowCount属性のデフォルト値を設定する。
     * @param varRowCountName 検索結果のカウント(検索結果の取得開始位置＋ステータスのカウント)を参照する際に使用する変数名
     */
    public void setVarRowCountName(String varRowCountName) {
        this.varRowCountName = varRowCountName;
    }

    /**
     * listSearchResultタグのvarOddEvenName属性のデフォルト値を取得する。
     * @return ボディ行のclass属性を参照する際に使用する変数名
     */
    public String getVarOddEvenName() {
        return varOddEvenName;
    }

    /**
     * listSearchResultタグのvarOddEvenName属性のデフォルト値を設定する。
     * @param varOddEvenName ボディ行のclass属性を参照する際に使用する変数名
     */
    public void setVarOddEvenName(String varOddEvenName) {
        this.varOddEvenName = varOddEvenName;
    }

    /**
     * listSearchResultタグのoddValue属性のデフォルト値を取得する。
     * @return ボディ行の奇数行に使用するclass属性
     */
    public String getOddValue() {
        return oddValue;
    }

    /**
     * listSearchResultタグのoddValue属性のデフォルト値を設定する。
     * @param oddValue ボディ行の奇数行に使用するclass属性
     */
    public void setOddValue(String oddValue) {
        this.oddValue = oddValue;
    }

    /**
     * listSearchResultタグのevenValue属性のデフォルト値を取得する。
     * @return ボディ行の偶数行に使用するclass属性
     */
    public String getEvenValue() {
        return evenValue;
    }

    /**
     * listSearchResultタグのevenValue属性のデフォルト値を設定する。
     * @param evenValue ボディ行の偶数行に使用するclass属性
     */
    public void setEvenValue(String evenValue) {
        this.evenValue = evenValue;
    }

    /**
     * pagingSortSubmitタグのsortSubmitCss属性のデフォルト値を取得する。
     * @return pagingSortSubmitタグのsortSubmitCss属性のデフォルト値
     */
    public String getSortSubmitCss() {
        return sortSubmitCss;
    }

    /**
     * pagingSortSubmitタグのsortSubmitCss属性のデフォルト値を設定する。
     * @param sortSubmitCss pagingSortSubmitタグのsortSubmitCss属性のデフォルト値
     */
    public void setSortSubmitCss(String sortSubmitCss) {
        this.sortSubmitCss = sortSubmitCss;
    }

    /**
     * pagingSortSubmitタグのsortSubmitTag属性のデフォルト値を取得する。
     * @return pagingSortSubmitタグのsortSubmitTag属性のデフォルト値
     */
    public String getSortSubmitTag() {
        return sortSubmitTag;
    }

    /**
     * pagingSortSubmitタグのsortSubmitTag属性のデフォルト値を設定する。
     * @param sortSubmitTag pagingSortSubmitタグのsortSubmitTag属性のデフォルト値
     */
    public void setSortSubmitTag(String sortSubmitTag) {
        this.sortSubmitTag = sortSubmitTag;
    }

    /**
     * pagingSortSubmitタグのsortSubmitType属性のデフォルト値を取得する。
     * @return pagingSortSubmitタグのsortSubmitType属性のデフォルト値
     */
    public String getSortSubmitType() {
        return sortSubmitType;
    }

    /**
     * pagingSortSubmitタグのsortSubmitType属性のデフォルト値を設定する。
     * @param sortSubmitType pagingSortSubmitタグのsortSubmitType属性のデフォルト値
     */
    public void setSortSubmitType(String sortSubmitType) {
        this.sortSubmitType = sortSubmitType;
    }

    /**
     * pagingSortSubmitタグのascSortSubmitCss属性のデフォルト値を取得する。
     * @return pagingSortSubmitタグのascSortSubmitCss属性のデフォルト値
     */
    public String getAscSortSubmitCss() {
        return ascSortSubmitCss;
    }

    /**
     * pagingSortSubmitタグのascSortSubmitCss属性のデフォルト値を設定する。
     * @param ascSortSubmitCss pagingSortSubmitタグのascSortSubmitCss属性のデフォルト値
     */
    public void setAscSortSubmitCss(String ascSortSubmitCss) {
        this.ascSortSubmitCss = ascSortSubmitCss;
    }

    /**
     * pagingSortSubmitタグのdescSortSubmitCss属性のデフォルト値を取得する。
     * @return pagingSortSubmitタグのdescSortSubmitCss属性のデフォルト値
     */
    public String getDescSortSubmitCss() {
        return descSortSubmitCss;
    }

    /**
     * pagingSortSubmitタグのdescSortSubmitCss属性のデフォルト値を設定する。
     * @param descSortSubmitCss pagingSortSubmitタグのdescSortSubmitCss属性のデフォルト値
     */
    public void setDescSortSubmitCss(String descSortSubmitCss) {
        this.descSortSubmitCss = descSortSubmitCss;
    }

    /**
     * pagingSortSubmitタグのdefaultSort属性のデフォルト値を取得する。
     * @return pagingSortSubmitタグのdefaultSort属性のデフォルト値
     */
    public String getDefaultSort() {
        return defaultSort;
    }

    /**
     * pagingSortSubmitタグのdefaultSort属性のデフォルト値を設定する。
     * @param defaultSort pagingSortSubmitタグのdefaultSort属性のデフォルト値
     * @throws IllegalArgumentException デフォルト値が不正な場合
     */
    public void setDefaultSort(String defaultSort) {
        if (!Pattern.compile("asc|desc").matcher(defaultSort).matches()) {
            throw new IllegalArgumentException(
                String.format("defaultSort was invalid. defaultSort must specify the following values. values = [asc, desc] defaultSort = [%s]",
                              defaultSort));
        }
        this.defaultSort = defaultSort;
    }

    /**
     * checkboxタグのチェックありに対する値のデフォルト値を取得する。
     * @return デフォルト値
     */
    public String getCheckboxOnValue() {
        return checkboxOnValue;
    }

    /**
     * checkboxタグのチェックありに対する値のデフォルト値を設定する。
     * <p/>
     * デフォルトは1。
     * @param checkboxOnValue デフォルト値
     */
    public void setCheckboxOnValue(String checkboxOnValue) {
        this.checkboxOnValue = checkboxOnValue;
    }

    /**
     * checkboxタグのチェックなしに対する値のデフォルト値を取得する。
     * @return デフォルト値
     */
    public String getCheckboxOffValue() {
        return checkboxOffValue;
    }

    /**
     * checkboxタグのチェックなしに対する値のデフォルト値を設定する。
     * <p/>
     * デフォルトは0。
     * @param checkboxOffValue デフォルト値
     */
    public void setCheckboxOffValue(String checkboxOffValue) {
        this.checkboxOffValue = checkboxOffValue;
    }

    /**
     * scriptタグのボディに対するプレフィックスのデフォルト値を取得する。
     * @return scriptタグのボディに対するプレフィックス
     */
    public String getScriptBodyPrefix() {
        return scriptBodyPrefix;
    }

    /**
     * scriptタグのボディに対するプレフィックスのデフォルト値を設定する。
     * <p/>
     * デフォルト値を下記に示す。
     * <pre>
     * {@literal <!--}
     * </pre>
     * @param scriptBodyPrefix scriptタグのボディに対するプレフィックス
     */
    public void setScriptBodyPrefix(String scriptBodyPrefix) {
        this.scriptBodyPrefix = scriptBodyPrefix;
    }

    /**
     * scriptタグのボディに対するサフィックスのデフォルト値を取得する。
     * @return scriptタグのボディに対するサフィックス
     */
    public String getScriptBodySuffix() {
        return scriptBodySuffix;
    }

    /**
     * scriptタグのボディに対するサフィックスのデフォルト値を設定する。
     * <p/>
     * デフォルト値を下記に示す。
     * <pre>
     * {@literal -->}
     * </pre>
     * @param scriptBodySuffix scriptタグのボディに対するサフィックス
     */
    public void setScriptBodySuffix(String scriptBodySuffix) {
        this.scriptBodySuffix = scriptBodySuffix;
    }

    /**
     * 言語対応のリソースパスを取得する際に使用するリソースパスルールのデフォルト値を取得する。
     * @return {@code ResourcePathRule}インタフェースを実装したクラスのインスタンス
     * @see ResourcePathRule
     */
    public ResourcePathRule getResourcePathRule() {
        return resourcePathRule;
    }

    /**
     * 言語対応のリソースパスを取得する際に使用するリソースパスルールのデフォルト値を設定する。
     * <p/>
     * デフォルトでは{@link DirectoryBasedResourcePathRule}インスタンスが設定されている。
     * @param resourcePathRule {@code ResourcePathRule}インタフェースを実装したクラスのインスタンス
     */
    public void setResourcePathRule(ResourcePathRule resourcePathRule) {
        this.resourcePathRule = resourcePathRule;
    }

    /**
     * サブミットを行うタグの表示制御を行う際に使用する条件のデフォルト値を設定する。
     * 
     * @param displayControlCheckers {@link DisplayControlChecker}インタフェースを実装したクラスのリスト
     */
    public void setDisplayControlCheckers(List<DisplayControlChecker> displayControlCheckers) {
        this.displayControlCheckers = displayControlCheckers;
    }

    /**
     * サブミットを行うタグの表示制御を行う際に使用する条件のデフォルト値を取得する。
     * 
     * @return {@code DisplayControlChecker}インタフェースを実装したクラスのリスト
     * @see DisplayControlChecker
     */
    public List<DisplayControlChecker> getDisplayControlCheckers() {
        return displayControlCheckers;
    }

    /**
     * サブミットを行うタグの表示制御方法のデフォルト値を設定する。
     * <p/>
     * 表示制御方法をもとに、認可判定と開閉局判定の結果に応じた表示制御を行う。
     * この値は各タグでカスタマイズされない限り、システム全体で使用される。<br/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>NODISPLAY : タグを非表示にする。</li>
     *     <li>DISABLED  : タグを使用不可能にする。</li>
     *     <li>NORMAL    : 通常表示（非表示にも使用不可能にもしない）。</li>
     * </ul>
     * 
     * @param displayMethod 表示制御方法
     * @throws IllegalArgumentException 表示制御方法が不正な場合
     */
    public void setDisplayMethod(String displayMethod) {

        DisplayMethod.getDisplayMethod(displayMethod);
        this.displayMethod = DisplayMethod.valueOf(displayMethod);
    }

    /**
     * サブミットを行うタグの表示制御方法のデフォルト値を取得する。
     * 
     * @return 表示制御方法
     */
    public DisplayMethod getDisplayMethod() {
        return displayMethod;
    }

    /**
     * SubmitLink描画時の表示制御判定が「非活性」の場合に使用するJSPが設定されているかどうかを判定する。
     * @return 設定されている場合は{@code true}
     */
    public boolean isSubmitLinkDisabledJspSpecified() {
        return submitLinkDisabledJsp != null;
    }

    /**
     * SubmitLink描画時の表示制御判定が「非活性」の場合に使用するJSPのURLを取得する。
     *
     * @return JSPのURL（設定されていない場合は{@code null}）
     */
    public String getSubmitLinkDisabledJsp() {
        return submitLinkDisabledJsp;
    }

    /**
     * SubmitLink描画時の表示制御判定が「非活性」の場合に使用するJSPのURLを設定する。
     * <p/>
     * SubmitLinkのボディ部の値は、"nablarch_link_body"というキーでリクエストスコープに格納されている。
     * また、活性時にaタグに出力されるidやclassといった属性は、nablarch_link_attributes_<属性名>
     * というキーでリクエストスコープに格納されている。<br/>
     * 下記に例を示す。
     * <pre>
     * {@literal
     * <%@ page contentType="text/html;charset=UTF-8" %>
     * <%@ taglib prefix="n" uri="http://tis.co.jp/nablarch" %>
     * <span class="<n:write name="nablarch_link_attributes_class" withHtmlFormat="false"/>" style="text-decoration: line-through;">
     *     <n:write name="nablarch_link_body" />
     * </span>}
     * </pre>
     * この例では、非活性項目はボディ部の内容が打ち消し線付きで描画され、n:SubmitLinkタグのcssClass属性
     * に指定した属性(活性時はaタグのclass属性に出力される)がそのままspanタグのclassに出力される。
     *
     * @param submitLinkDisabledJsp JSPのURL
     */
    public void setSubmitLinkDisabledJsp(String submitLinkDisabledJsp) {
        this.submitLinkDisabledJsp = submitLinkDisabledJsp;
    }


    /**
     *  修飾付き出力(n:prettyPrint)タグにおいて、
     *  HTMLエスケープの対象とならずにHTMLタグとしてそのまま出力するタグ名のリスト
     *  をカンマ区切りで設定する。
     *  <p/>
     *  下記にデフォルトの設定を示す。
     *  <pre>
     *  b big blockquote br caption center dd del
     *  dl dt em font h1 h2 h3 hr i ins li ol p small
     *  strong sub sup table td th tr u ul
     *  </pre>
     *  
     * @param tagNames HTMLエスケープの対象外のタグリスト
     */
    public void setSafeTags(String[] tagNames) {
        this.safeTags = tagNames;
    }
    
    /**
     * 修飾付き出力(n:prettyPrint)タグにおいて、
     * HTMLエスケープの対象外のHTMLタグの中で使用することができる属性値のリスト
     * をカンマ区切りで設定する。
     * <p/>
     * 下記にデフォルトの設定を示す。
     * <pre>
     * color size border colspan rowspan bgcolor
     * </pre>
     * 
     * @param attributes 属性値のリスト
     */
    public void setSafeAttributes(String[] attributes) {
        this.safeAttributes = attributes;
    }
    
    /**
     *  修飾付き出力(n:prettyPrint)タグにおいて、
     *  HTMLエスケープの対象とならずにHTMLタグとしてそのまま出力するタグ名のリストを取得する。
     * @return HTMLエスケープの対象外のタグリスト
     */
    public String[] getSafeTags() {
        return this.safeTags;
    }
    
    /**
     * 修飾付き出力(n:prettyPrint)タグにおいて、
     * HTMLエスケープの対象外のHTMLタグの中で使用することができる属性値のリストを取得する。
     * @return 属性値のリスト
     */
    public String[] getSafeAttributes() {
        return this.safeAttributes;
    }

    /**
     * 入力系のタグでname属性に指定した名称に対応する値を取得する際に、値を保持するオブジェクトが
     * {@code null}であれば{@code null}を設定されたものとして動作するか否かを設定する。
     * <p/>
     * 本設定値は後方互換性のために存在する。
     * {@code false}を指定することで、リクエストスコープに設定されたオブジェクトが存在した場合も、
     * プロパティが {@code null}であればリクエストパラメータの値を優先的に使用するよう動作する。
     * デフォルトは{@code true}。
     * 
     * @param useValueAsNullIfObjectExists 値を保持するオブジェクトが{@code null}の際に、{@code null}として動作させる場合は{@code true}
     * @see TagUtil#getValue(javax.servlet.jsp.PageContext, String, boolean)
     */
    public void setUseValueAsNullIfObjectExists(boolean useValueAsNullIfObjectExists) {
        this.useValueAsNullIfObjectExists = useValueAsNullIfObjectExists;
    }
    
    /**
     * 入力系のタグでname属性に指定した名称に対応する値を取得する際に、値を保持するオブジェクトが
     * {@code null}であれば{@code null}を設定されたものとして動作するか否かを取得する。
     * @return 値を保持するオブジェクトが {@code null}の際に、{@code null}として動作させる場合は{@code true}
     */
    public boolean getUseValueAsNullIfObjectExists() {
        return useValueAsNullIfObjectExists;
    }

    /**
     * ポップアップのウィンドウ名を取得する。
     * @return ポップアップのウィンドウ名
     */
    public String getPopupWindowName() {
        return popupWindowName;
    }

    /**
     * ポップアップのウィンドウ名を設定する。
     * <p/>
     * 設定した値は新規ポップアップウィンドウを開く際、window.open関数の第2引数に渡される。
     * @param popupWindowName ポップアップのウィンドウ名
     */
    public void setPopupWindowName(String popupWindowName) {
        this.popupWindowName = popupWindowName;
    }

    /**
     * ポップアップのオプション情報（window.open関数の第3引数の値）を取得する。
     * @return ポップアップのオプション情報
     */
    public String getPopupOption() {
        return popupOption;
    }

    /**
     * ポップアップのオプション情報（window.open関数の第3引数の値）を設定する。
     * <p/>
     * 新規ポップアップウィンドウを開くとき、window.open関数の第3引数に渡される。<br/>
     * {@literal "width=200,height=100"}のように指定する。
     *
     * @param popupOption ポップアップのオプション情報
     */
    public void setPopupOption(String popupOption) {
        this.popupOption = popupOption;
    }

    /**
     * autocomplete属性をOFFにする対象のデフォルト値を取得する。
     * @return デフォルト値
     */
    public AutocompleteDisableTarget getAutocompleteDisableTarget() {
        return autocompleteDisableTarget;
    }

    /**
     * autocomplete属性をOFFにする対象のデフォルト値を設定する。
     * <p/>
     * 下記のいずれかを指定する。
     * <ul>
     *     <li>all(すべてのタグ)</li>
     *     <li>password(パスワードのみ)</li>
     *     <li>none(対象なし)</li>
     * </ul>
     * デフォルトはnone。
     * @param target デフォルト値
     * @throws IllegalArgumentException デフォルト値が不正な場合
     */
    public void setAutocompleteDisableTarget(String target) {
        if (!AutocompleteDisableTarget.contains(target)) {
            throw new IllegalArgumentException(
                String.format("autocompleteDisableTarget was invalid. "
                            + "autocompleteDisableTarget must specify the following values. "
                            + "values = %s autocompleteDisableTarget = [%s]",
                              AutocompleteDisableTarget.getTargets(), target));
        }
        autocompleteDisableTarget = AutocompleteDisableTarget.valueOf(target.toUpperCase());
    }

    /**
     * GETメソッドによるリクエストを使用するか否かのデフォルト値を取得する。
     * 
     * @return GETメソッドによるリクエストを使用する場合は{@code true}
     */
    public boolean getUseGetRequest() {
        return useGetRequest;
    }

    /**
     * GETメソッドによるリクエストを使用するか否かを設定する。
     * 
     * @param useGetRequest GETメソッドによるリクエストを使用する場合は{@code true}
     */
    public void setUseGetRequest(boolean useGetRequest) {
        this.useGetRequest = useGetRequest;
    }

    /**
     * 動的属性でBooleanとして扱う属性を取得する。
     * @return 動的属性でBooleanとして扱う属性
     */
    public Set<String> getDynamicBooleanAttributes() {
        return dynamicBooleanAttributes;
    }

    /**
     * 動的属性でBooleanとして扱う属性を設定する。
     * @param dynamicBooleanAttributes 動的属性でBooleanとして扱う属性
     */
    public void setDynamicBooleanAttributes(List<String> dynamicBooleanAttributes) {
        this.dynamicBooleanAttributes = new HashSet<String>(dynamicBooleanAttributes);
    }
}
