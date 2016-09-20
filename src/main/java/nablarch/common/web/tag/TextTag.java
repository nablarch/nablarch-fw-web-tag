package nablarch.common.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import nablarch.core.ThreadContext;
import nablarch.core.util.FormatSpec;
import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;

import java.util.Locale;

/**
 * 入力データ復元とHTMLエスケープを行うinputタグ(type="text")を出力するクラス。
 * @author Kiyohito Itoh
 */
public class TextTag extends InputTagSupport {
    
    /** textタグのサポートクラス */
    private TextTagWriter writer = new TextTagWriter();

    /**
     * HTML5のautocomplete属性を設定する。
     * @param autocomplete HTML5のautocomplete属性
     */
    public void setAutocomplete(String autocomplete) {
        getAttributes().put(HtmlAttribute.AUTOCOMPLETE, autocomplete);
    }

    /**
     * XHTMLのreadonly属性を設定する。
     * @param readonly XHTMLのreadonly属性
     */
    public void setReadonly(boolean readonly) {
        getAttributes().put(HtmlAttribute.READONLY, readonly);
    }

    /**
     * XHTMLのsize属性を設定する。
     * @param size XHTMLのsize属性
     */
    public void setSize(int size) {
        getAttributes().put(HtmlAttribute.SIZE, size);
    }

    /**
     * XHTMLのmaxlength属性を設定する。
     * @param maxlength XHTMLのmaxlength属性
     */
    public void setMaxlength(int maxlength) {
        getAttributes().put(HtmlAttribute.MAXLENGTH, maxlength);
    }
    
    /**
     * XHTMLのonselect属性を設定する。
     * @param onselect XHTMLのonselect属性
     */
    public void setOnselect(String onselect) {
        getAttributes().put(HtmlAttribute.ONSELECT, onselect);
    }
    
    /**
     * XHTMLのonchange属性を設定する。
     * @param onchange XHTMLのonchange属性
     */
    public void setOnchange(String onchange) {
        getAttributes().put(HtmlAttribute.ONCHANGE, onchange);
    }

    /**
     * HTML5のplaceholder属性を設定する。
     * @param placeholder HTML5のplaceholder属性
     */
    public void setPlaceholder(String placeholder) {
        getAttributes().put(HtmlAttribute.PLACEHOLDER, placeholder);
    }

    /**
     * エラーレベルのメッセージに使用するCSSクラス名を設定する。<br>
     * デフォルトは"nablarch_error"。
     * @param errorCss エラーレベルのメッセージに使用するCSSクラス名
     */
    public void setErrorCss(String errorCss) {
        writer.setErrorCss(errorCss);
    }
    
    /**
     * name属性のエイリアスを設定する。<br>
     * 複数指定する場合はカンマ区切り。
     * @param nameAlias name属性のエイリアス
     */
    public void setNameAlias(String nameAlias) {
        writer.setNameAlias(nameAlias);
    }
    
    /**
     * 出力時のフォーマットを設定する。
     * <pre>
     * フォーマットは、"データタイプ{パターン}"形式で指定する。
     * 
     * フレームワークがデフォルトでサポートしているフォーマットを下記に示す。
     * 
     * dateString:
     *   日付文字列のフォーマット。
     *   値は、yyyyMMdd形式の文字列を指定する。
     *   パターンには、java.text.SimpleDateFormatが規定している構文を指定する。
     *   {@link CustomTagConfig}を使用してパターンのデフォルト値を設定することができる。
     *   例：dateString{yyyy/MM/dd}
     * 
     * decimal:
     *   10進数のフォーマット。
     *   値は、java.lang.Number型又は数字の文字列を指定する。
     *   数字の文字列の場合は、カンマを取り除いた後でフォーマットする。
     *   パターンには、java.text.DecimalFormatが規定している構文を指定する。
     *   例：decimal{###.##%}
     * </pre>
     * @param valueFormat 出力時のフォーマット
     */
    public void setValueFormat(String valueFormat) {
        writer.setValueFormat(valueFormat);
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * 入力画面と確認画面で出力内容が異なる。
     * 
     * 入力画面：
     * 指定された属性を使用してボディを持たないタグを出力する。
     * value属性に指定されたvalueを設定する。
     * type属性に"text"を設定する。
     * 属性はHTMLエスケープして出力する。
     * {@link nablarch.common.web.tag.FormContext}にname属性を設定する。
     * 
     * 確認画面：
     * フォーマットが指定されている場合は、入力データを指定されたフォーマット後にHTMLエスケープして出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        checkChildElementsOfForm();
        getAttributes().put(HtmlAttribute.TYPE, "text");
        writer.writeTag(pageContext, getAttributes());
        TagUtil.setNameToFormContext(pageContext, getAttributes());
        return SKIP_BODY;
    }
    
    /**
     * textタグを出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class TextTagWriter extends SinglevaluedInputTagWriterSupport {
        
        /** 出力時のフォーマット */
        private String valueFormat;
        
        /** デフォルトコンストラクタ。 */
        public TextTagWriter() {
            super("input");
        }
        
        /**
         * 出力時のフォーマットを設定する。
         * <pre>
         * フォーマットは、"データタイプ{パターン}"形式で指定する。
         * 
         * フレームワークがデフォルトでサポートしているフォーマットを下記に示す。
         * 
         * dateString:
         *   日付文字列のフォーマット。
         *   値は、yyyyMMdd形式の文字列を指定する。
         *   パターンには、java.text.SimpleDateFormatが規定している構文を指定する。
         *   {@link CustomTagConfig}を使用してパターンのデフォルト値を設定することができる。
         *   例：dateString{yyyy/MM/dd}
         * 
         * decimal:
         *   10進数のフォーマット。
         *   値は、java.lang.Number型又は数字の文字列を指定する。
         *   数字の文字列の場合は、カンマを取り除いた後でフォーマットする。
         *   パターンには、java.text.DecimalFormatが規定している構文を指定する。
         *   例：decimal{###.##%}
         * </pre>
         * @param valueFormat 出力時のフォーマット
         */
        private void setValueFormat(String valueFormat) {
            this.valueFormat = valueFormat;
        }

        /**
         * {@inheritDoc}
         * <pre>
         * valueFormat属性が指定されている場合はvalue属性の値をフォーマットし、
         * フォーマット仕様をhiddenタグに追加する。
         * </pre>
         */
        @Override
        protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
            if (valueFormat != null) {
                String paramName = createFormatSpecParamName(attributes);
                // valueのフォーマット
                FormatSpec formatSpec = TagUtil.createFormatSpec(valueFormat);
                value = TagUtil.formatValue(
                        pageContext, attributes.<String>get(HtmlAttribute.NAME), formatSpec, value);
                if (pageContext.getRequest().getParameter(paramName) == null) {
                    // フォーマット仕様をhiddenに追加する。
                    // 複数回hiddenに追加されないように、リクエストパラメータに値が含まれていない時のみ追加する。
                    addFormatSpecParam(pageContext, attributes, formatSpec, paramName);
                }
            }
            if (attributes.get(HtmlAttribute.AUTOCOMPLETE) == null) {
                AutocompleteDisableTarget defaultTarget = TagUtil.getCustomTagConfig().getAutocompleteDisableTarget();
                if (defaultTarget == AutocompleteDisableTarget.ALL) {
                    attributes.put(HtmlAttribute.AUTOCOMPLETE, "off");
                }
            }
            return super.createInputTag(pageContext, attributes, value);
        }

        /**
         * フォーマット仕様をhiddenタグに追加する。
         * <pre>
         * hiddenタグに追加する内容は下記のとおり。
         * フォーマット仕様パラメータ
         *   パラメータの名前: 指定されたパラメータ名
         *   パラメータの値:   "データタイプ{パターン}"形式のフォーマット文字列
         * パターンのセパレータパラメータ
         *   パラメータの名前: 指定されたパラメータ名＋"_separator"
         *   パラメータの値:   パターンのセパレータ
         * </pre>
         * @param pageContext ページコンテキスト
         * @param attributes 属性
         * @param spec フォーマット仕様
         * @param paramName フォーマット仕様のパラメータ名
         */
        protected void addFormatSpecParam(PageContext pageContext, HtmlAttributes attributes, FormatSpec spec, String paramName) {
            String dataType = spec.getDataType();
            String pattern = spec.getFormatOfPattern();
            String separator = spec.getPatternSeparator();
            String additionalInfo = spec.getAdditionalInfoOfPattern();
            if (additionalInfo == null) {
                if ("decimal".equals(dataType) || "yyyymmdd".equals(dataType)
                        || "yyyymm".equals(dataType) || "dateString".equals(dataType)) {
                    final Locale locale = ThreadContext.getLanguage() != null ? ThreadContext.getLanguage() : Locale.getDefault();
                    additionalInfo = locale.getLanguage();
                }
            } else {
                if ("yyyymmdd".equals(dataType)
                        || "yyyymm".equals(dataType) || "dateString".equals(dataType)) {
                    additionalInfo = eliminateExtraInfoForDateString(additionalInfo, separator);
                }
            }
            FormContext formContext = TagUtil.getFormContext(pageContext);
            formContext.addHiddenTagInfo(
                paramName,
                String.format("%s{%s%s%s}",
                              dataType,
                              StringUtil.hasValue(pattern) ? pattern : "",
                              StringUtil.hasValue(separator) ? separator : "",
                              StringUtil.hasValue(additionalInfo) ? additionalInfo : ""));
            formContext.addHiddenTagInfo(
                paramName + "_separator",
                StringUtil.hasValue(separator) ? separator : "");
        }

        /**
         * 日付文字列フォーマットで指定されたパターンの付加情報から余分な情報を取り除く。
         * <pre>
         * 年月日フォーマットにタイムゾーンを指定している。
         * 
         * 
         * 年月日バリデーションでパターンの付加情報をロケールとして扱うようになると動作しなくなるので、
         * 本メソッドにより余分な情報を取り除く。
         * 
         * ロケールはパターンの付加情報で先頭に指定されるので、
         * パターンの付加情報に区切り文字が含まれている場合に、1つ目の区切り文字から後ろを削除する。
         * </pre>
         * @param info パターンの付加情報
         * @param separator 区切り文字
         * @return 余分な情報を取り除いたパターンの付加情報
         */
        private String eliminateExtraInfoForDateString(String info, String separator) {
            int separatorIndex = info.indexOf(separator);
            return separatorIndex != -1 ? info.substring(0, separatorIndex) : info;
        }

        /**
         * フォーマット仕様のパラメータ名を作成する。
         * <pre>
         * 作成するパラメータ名は下記のとおり。
         * name属性の値＋"_nablarch_formatSpec"
         * </pre>
         * @param attributes 属性
         * @return フォーマット仕様のパラメータ名
         */
        protected String createFormatSpecParamName(HtmlAttributes attributes) {
            String name = attributes.get(HtmlAttribute.NAME) + "_" + ExecutionContext.FW_PREFIX + "formatSpec";
            return name;
        }

        /**
         * {@inheritDoc}
         * <pre>
         * フォーマットが指定されている場合は、入力データを指定されたフォーマット後にHTMLエスケープして出力する。
         * </pre>
         */
        @Override
        protected String createOutputTag(PageContext pageContext, HtmlAttributes attributes, Object value) {
            if (valueFormat != null) {
                value = TagUtil.formatValue(pageContext, attributes.<String>get(HtmlAttribute.NAME),
                                            TagUtil.createFormatSpec(valueFormat), value);
            }
            return super.createOutputTag(pageContext, attributes, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected String getTagName() {
        return "text";
    }
}
