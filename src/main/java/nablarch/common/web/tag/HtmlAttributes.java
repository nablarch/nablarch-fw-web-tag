package nablarch.common.web.tag;

import nablarch.core.util.annotation.Published;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HTMLの属性を保持するクラス。<br>
 * {@link HtmlAttribute}をキーにして属性の値を保持する。
 * @author Kiyohito Itoh
 */
@Published(tag = "architect")
public class HtmlAttributes {

    /** 空の属性。属性を何も指定しない場合に使用する。 */
    public static final HtmlAttributes EMPTY_ATTRIBUTES = new HtmlAttributes() {
        /** {@inheritDoc}} */
        public void put(HtmlAttribute attribute, Object value) {
            throw new UnsupportedOperationException("this object is immutable attributes.");
        }
    };

    /** 属性を保持するマップ */
    private Map<HtmlAttribute, Object> attributes = new HashMap<HtmlAttribute, Object>();

    /** 動的属性を保持するマップ */
    private Map<String, Object> dynamicAttributes = new HashMap<String, Object>();

    /**
     * 動的属性を設定する。<br>
     * <br>
     * 既に存在する場合は上書き。
     *
     * @param attribute 属性の名前
     * @param value 属性の値
     */
    public void put(String attribute, Object value) {
        dynamicAttributes.put(attribute, value);
    }

    /**
     * 属性を設定する。<br>
     * <br>
     * 既に存在する場合は上書き。
     *
     * @param attribute 属性の名前
     * @param value 属性の値
     */
    public void put(HtmlAttribute attribute, Object value) {
        attributes.put(attribute, value);
    }


    /**
     * 属性を設定する。<br>
     * <br>
     * 既に存在する場合は上書き。
     *
     * @param other 属性
     */
    public void putAll(HtmlAttributes other) {
        attributes.putAll(other.attributes);
        dynamicAttributes.putAll(other.dynamicAttributes);
    }

    /**
     * 属性を取得する。
     *
     * @param <T> 属性の型
     * @param attribute 属性の名前
     * @return 属性の値。属性に対応する値が存在しない場合はnull
     */
    @SuppressWarnings("unchecked")
    public <T> T get(HtmlAttribute attribute) {
        return attributes.containsKey(attribute) ? (T) attributes.get(attribute) : null;
    }

    /**
     * 全属性のキーのセットを取得する。
     *
     * @return 全属性のキーのセット
     */
    public Set<HtmlAttribute> keys() {
        return attributes.keySet();
    }

    /**
     * 属性をクリアする。
     */
    public void clear() {
        attributes.clear();
    }

    /**
     * 空か判定する。
     * @return 空の場合はtrue
     */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    /**
     * HTMLタグの属性に指定できる形式の文字列を取得する。
     * <pre>
     * 保持している属性をスペース区切りで連結した文字列を返す。
     *
     * 属性の値がBoolean型の場合を除き、属性の書式は「名前="値"」となる。
     *
     * 属性の値がBoolean型の場合は、下記の通り処理する。
     * 属性の値がtrueの場合：「名前="名前"」の書式で文字列に含める。
     * 属性の値がfalseの場合：文字列に含めない。
     * </pre>
     * @param tagName タグ名称
     * @return XHTMLタグの属性に指定できる形式の文字列
     */
    public String toHTML(String tagName) {
        if (attributes.isEmpty() && dynamicAttributes.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder((dynamicAttributes.size() + attributes.size()) * 20);
        for (HtmlAttribute attr : HtmlAttribute.values()) {
            if (!attributes.containsKey(attr)) {
                continue;
            }
            Object o = attributes.get(attr);
            if (o == null) {
                continue;
            }
            if (o instanceof Boolean) {
                if (((Boolean) o)) {
                    o = attr.getXHtmlName();
                } else {
                    continue;
                }
            }
            if (sb.length() != 0) {
                sb.append(' ');
            }
            String escapeValue = TagUtil.escapeHtml(o, false);
            if (isStaticResourceLink(tagName, attr)) {
                // 静的リソースの場合は、タイムスタンプをGETパラメータに追加
                // これにより、サーバを再起動したタイミングでクライアントのキャッシュを無視して強制的にリソースの最新化が可能となる。
                escapeValue = TagUtil.addStaticContentVersion(escapeValue);
            }
            sb.append(String.format("%s=\"%s\"", attr.getXHtmlName(), escapeValue));
        }
        for (Map.Entry<String, Object> dynamicAttr : dynamicAttributes.entrySet()) {
            Object o = dynamicAttr.getValue();
            if (o == null) {
                continue;
            }
            if (o instanceof Boolean) {
                if (((Boolean) o)) {
                    o = dynamicAttr.getKey();
                } else {
                    continue;
                }
            }
            if (sb.length() != 0) {
                sb.append(' ');
            }
            String escapeValue = TagUtil.escapeHtml(o, false);
            sb.append(String.format("%s=\"%s\"", dynamicAttr.getKey(), escapeValue));
        }
        return sb.toString();
    }

    /**
     * 静的リソースへのリンク属性かを判定する。
     * <p/>
     * 以下の条件を満たす場合、静的リソースへのリンクと判断する。<br/>
     * <ul>
     *     <li>linkタグのhref属性</li>
     *     <li>inputタグのsrc属性</li>
     *     <li>imgタグのsrc属性</li>
     *     <li>scriptタグのsrc属性</li>
     * </ul>
     *
     * @param tagName タグ名
     * @param htmlAttribute HTMLの属性情報
     * @return 静的リソースへのリンクの場合はtrue
     */
    private boolean isStaticResourceLink(String tagName, HtmlAttribute htmlAttribute) {
        if (htmlAttribute != HtmlAttribute.SRC && htmlAttribute != HtmlAttribute.HREF) {
            // src、href属性以外は静的リソースへのリンクではない
            return false;
        }
        // link,img,input,scriptタグが対象
        return ("link".equals(tagName) || "img".equals(tagName) || "input".equals(tagName) || "script".equals(tagName));
    }
}
