package nablarch.common.web.tag;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

/**
 * ブラウザのキャッシュを防ぐクラス。
 * @author Kiyohito Itoh
 */
public class NoCacheTag extends CustomTagSupport {
    
    /** ブラウザのキャッシュを防ぐmetaタグの定義 */
    private static String metaTags;
    
    /**
     * ブラウザのキャッシュを防ぐmetaタグの定義を取得する。<br>
     * リポジトリから改行コード（カスタムタグのデフォルト値設定）を取得するため、このメソッドを設けている。
     * @return ブラウザのキャッシュを防ぐmetaタグの定義
     */
    private static String getMetaTags() {
        if (metaTags != null) {
            return metaTags;
        }
        String ls = TagUtil.getCustomTagConfig().getLineSeparator();
        metaTags = new StringBuilder()
                            .append("<meta http-equiv=\"pragma\" content=\"no-cache\">").append(ls)
                            .append("<meta http-equiv=\"cache-control\" content=\"no-cache\">").append(ls)
                            .append("<meta http-equiv=\"expires\" content=\"0\">").toString();
        return metaTags;
    }
    
    /**
     * {@inheritDoc}
     * <pre>
     * ブラウザのキャッシュを防止するmetaタグの出力及びレスポンスヘッダの設定を行う。
     * 設定内容は下記の通り。
     * 
     * metaタグ
     *   pragma: no-cache
     *   cache-control: no-cache
     *   expires: 0
     * 
     * レスポンスヘッダ
     *   Pragma: no-cache
     *   Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0
     *   Expires: 0
     * </pre>
     */
    public int doStartTag() throws JspException {
        
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        
        // for IE
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        
        TagUtil.print(pageContext, getMetaTags());
        
        return SKIP_BODY;
    }
}
