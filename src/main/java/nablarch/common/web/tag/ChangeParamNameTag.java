package nablarch.common.web.tag;

import jakarta.servlet.jsp.JspException;

/**
 * ポップアップ用のサブミット時にパラメータ名を変更するクラス。
 * <p/>
 * ポップアップ用のサブミットでは、元画面のフォームに含まれるinput要素を動的に追加して送信する。
 * このカスタムタグを指定することで、元画面のフォームに含まれるinput要素の値を、
 * 指定されたパラメータ名で送信することが可能となる。
 * @author Kiyohito Itoh
 */
public class ChangeParamNameTag extends CustomTagSupport {
    
    /** サブミット時に使用するパラメータの名前 */
    private String paramName;
    
    /** 変更元となる元画面のinput要素のname属性 */
    private String inputName;

    /**
     * サブミット時に使用するパラメータの名前を設定する。
     * @param paramName サブミット時に使用するパラメータの名前
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * 変更元となる元画面のinput要素のname属性を設定する。
     * @param inputName 変更元となる元画面のinput要素のname属性
     */
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * カレントのサブミット情報に指定されたパラメータの変更情報を設定する。
     * </pre>
     */
    public int doStartTag() throws JspException {
        
        FormContext formContext = TagUtil.getFormContext(pageContext);
        if (formContext == null || formContext.getCurrentSubmissionInfo() == null) {
            throw new IllegalStateException(
                "invalid location of the changeParamName tag."
                    + " the changeParamName tag must locate in the tag to submit(popupSubmit or popupLink or popupButton).");
        }
        
        formContext.getCurrentSubmissionInfo().addChangeParamName(paramName, inputName);
        
        return SKIP_BODY;
    }
    
    /**
     * パラメータ名の変更情報を保持するクラス。
     * @author Kiyohito Itoh
     */
    public static final class ChangeParamName {
        
        /** サブミット時に使用するパラメータの名前 */
        private String paramName;
        
        /** 変更元となる元画面のinput要素のname属性 */
        private String inputName;
        
        /**
         * コンストラクタ。
         * @param paramName サブミット時に使用するパラメータの名前
         * @param inputName 変更元となる元画面のinput要素のname属性
         */
        public ChangeParamName(String paramName, String inputName) {
            super();
            this.paramName = paramName;
            this.inputName = inputName;
        }

        /**
         * サブミット時に使用するパラメータの名前を取得する。
         * @return サブミット時に使用するパラメータの名前
         */
        public String getParamName() {
            return paramName;
        }

        /**
         * 変更元となる元画面のinput要素のname属性を取得する。
         * @return 変更元となる元画面のinput要素のname属性
         */
        public String getInputName() {
            return inputName;
        }
    }
}
