package nablarch.common.web.tag;

import java.util.Collection;
import java.util.List;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * コード値を出力するクラス。
 * @author Kiyohito Itoh
 */
public class CodeTag extends HtmlTagSupport {
    
    /** コード値を出力するクラスの実装をサポートするクラス */
    private CodeTagWriter writer = new CodeTagWriter();
    
    /**
     * XHTMLのname属性を設定する。
     * @param name XHTMLのname属性
     */
    public void setName(String name) {
        getAttributes().put(HtmlAttribute.NAME, name);
    }
    
    /**
     * コードIDを設定する。
     * @param codeId コードID
     */
    public void setCodeId(String codeId) {
        writer.setCodeId(codeId);
    }

    /**
     * 使用するパターンのカラム名を設定する。
     * @param pattern 使用するパターンのカラム名
     */
    public void setPattern(String pattern) {
        writer.setPattern(pattern);
    }

    /**
     * 取得するオプション名称のカラム名を設定する。
     * @param optionColumnName 取得するオプション名称のカラム名
     */
    public void setOptionColumnName(String optionColumnName) {
        writer.setOptionColumnName(optionColumnName);
    }

    /**
     * ラベルを整形するパターンを設定する。
     * <pre>
     * プレースホルダを下記に示す。
     * 
     * $NAME$: コード値に対応するコード名称
     * $SHORTNAME$: コード値に対応するコードの略称
     * $OPTIONALNAME$: コード値に対応するコードのオプション名称
     *                 $OPTIONALNAME$を使用する場合は、optionColumnName属性の指定が必須となる。
     * $VALUE$: コード値
     * 
     * デフォルトは"$NAME$"。
     * </pre>
     * @param labelPattern ラベルを整形するパターン
     */
    public void setLabelPattern(String labelPattern) {
        writer.setLabelPattern(labelPattern);
    }
    
    /**
     * リスト表示時に使用するフォーマットを設定する。
     * <pre>
     * 下記のいずれかを指定する。
     * br(brタグ)
     * div(divタグ)
     * span(spanタグ)
     * ul(ulタグ)
     * ol(olタグ)
     * sp(スペース区切り)
     * 
     * デフォルトはbr。
     * </pre>
     * @param listFormat リスト表示時に使用するフォーマット
     */
    public void setListFormat(String listFormat) {
        if (listFormat == null || !ListFormat.getFormats().contains(listFormat)) {
            throw new IllegalArgumentException(
                String.format("listFormat was invalid. listFormat must specify the following values. values = %s listFormat = [%s]",
                              ListFormat.getFormats(), listFormat));
        }
        writer.setListFormat(ListFormat.getFormatByTagName(listFormat));
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * name属性に対応する入力データが存在する場合は指定されたフォーマットで出力する。
     * ラベルはHTMLエスケープして出力する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        writer.writeTag(pageContext, getAttributes());
        return SKIP_BODY;
    }
    
    /**
     * コード値を出力するクラスの実装をサポートするクラス。
     * @author Kiyohito Itoh
     */
    private static final class CodeTagWriter extends CodeTagWriterSupport {
        
        /**
         * {@inheritDoc}
         * </br>
         * </br>
         * 表示専用タグのため、取得先は変数スコープのみとする。
         * name属性が指定された場合は、表示専用タグのため、変数スコープから取得した値を返す。
         * name属性が指定されない場合は、{@link CodeTagWriterSupport#getList(jakarta.servlet.jsp.PageContext)}に処理を委譲し、すべてのコード値を返す。
         */
        protected Collection<?> getValues(PageContext pageContext, HtmlAttributes attributes) {
            String name = attributes.get(HtmlAttribute.NAME);
            if (name == null) {
                return getList(pageContext);
            } else {
                return TagUtil.getMultipleValues(pageContext, name);
            }
        }

        /**
         * {@inheritDoc}<br>
         * 表示専用タグのため常にtrueを返す。
         */
        protected boolean isConfirmationPage(PageContext pageContext) {
            return true;
        }

        /**
         * {@inheritDoc}<br>
         * 表示専用タグのため呼ばれることはない。
         */
        protected String createInputTag(PageContext pageContext, HtmlAttributes attributes, List<?> list, Collection<?> values,
                                         ListFormat listFormat, HtmlAttributes listAttributes) {
            return "";
        }
    }
}
