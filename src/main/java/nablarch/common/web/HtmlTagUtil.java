package nablarch.common.web;

import java.util.List;
import java.util.Stack;

import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;

/**
 * HTMLタグの作成、編集に必要となる共通機能を提供するユーティリティ。
 * @author tani takanori
 */
public final class HtmlTagUtil {

    /** 隠蔽コンストラクタ */
    private HtmlTagUtil() {
    }

    /**
     * HTMLエスケープを行う。
     * <p/>
     * このメソッドは、セキュリティ上問題のある文字列のHTMLエスケープ行う。<br/>
     * 文字列がnullであった場合、nullを返す。
     * <pre>
     * HTMLエスケープ例：
     *
     * {@literal
     * & -> &amp;
     * < -> &lt;
     * > -> &gt;
     * " -> &#034;
     * ' -> &#039;
     * }
     * 実装例：
     *
     * {@code
     * String str = "&<>\"\'";
     * HtmlTagUtil.escapeHtml(str); //-->&amp;&lt;&gt;&#034;&#039;
     * }
     * </pre>
     * @param s エスケープ対象のオブジェクト
     * @return エスケープ後の文字列
     */
    @Published(tag = "architect")
    public static String escapeHtml(Object s) {
        return escapeHtml(s, false);
    }

    /**
     * HTMLエスケープ及び半角スペースと改行の変換を行う。
     * <p/>
     * このメソッドは、下記の処理を行う。
     * <ul>
     *     <li>セキュリティ上問題のある文字列のHTMLエスケープ処理</li>
     *     <li>改行、半角スペースの変換</li>
     * </ul>
     * 文字列がnullであった場合、nullを返す。
     * <pre>
     * HTMLエスケープ例：
     *
     * {@literal
     * &  -> &amp;
     * <  -> &lt;
     * >  -> &gt;
     * "  -> &#034;
     * '  -> &#039;
     * }
     * </pre>
     * withHtmlFormatにtrueが指定された場合は、半角スペースと改行の変換を行う。
     * <pre>
     * 半角スペース、改行の変換例：
     *
     * " "(半角スペース) -> {@literal &nbsp;}
     * \n、\r、\r\n -> {@literal <br/>}
     * </pre>
     * <pre>
     * 実装例：
     *
     * {@code
     * String str = " \r\n\n\r";
     * HtmlTagUtil.escapeHtml(str, true) //-->&nbsp;<br/><br/><br/>
     * }
     * </pre>
     * @param s エスケープ対象のオブジェクト
     * @param withHtmlFormat 改行変換と半角スペース変換を行う場合はtrue
     * @return エスケープ後の文字列
     */
    @Published(tag = "architect")
    public static String escapeHtml(Object s, boolean withHtmlFormat) {
        return escapeHtml(s, withHtmlFormat, null, null);
    }

    /**
     * HTMLエスケープ、半角スペース・改行の変換を行う。
     * HTMLエスケープ対象外のタグを指定した場合は、指定したタグにのみHTMLエスケープを行わない。
     * <p/>
     * このメソッドでは、下記の処理を行う。
     * <ul>
     *     <li>セキュリティ上問題のある文字列のHTMLエスケープ</li>
     *     <li>改行、半角スペースの変換</li>
     *     <li>HTMLエスケープ対象外に指定したタグのエスケープのスキップ</li>
     * </ul>
     * 文字列がnullであった場合、nullを返す。
     * <pre>
     * HTMLエスケープ例：
     * {@literal
     * & -> &amp;
     * < -> &lt;
     * > -> &gt;
     * " -> &#034;
     * ' -> &#039;
     * }
     * </pre>
     * withHtmlFormatにtrueが指定された場合は、半角スペースと改行の変換を行う。
     * <pre>
     * 半角スペース、改行の変換例：
     *
     * " "(半角スペース) -> {@literal &nbsp;}<br/>
     * \n、\r、\r\n -> {@literal <br/>}
     * </pre>
     * <pre>
     * 実装例：
     *
     * {@code
     * String str = " \r\n\n\r";
     * HtmlTagUtil.escapeHtml(str, true, null, null) //--> &nbsp;<br/><br/><br/>
     * }
     * </pre>
     * エスケープ対象外のタグとその中で使用できるタグの属性を設定した場合は、さらに下記の変換を行う。
     * <pre>
     * 実装例：
     *
     * {@code
     * String str1 = "<a href=\"javascript:alert();\" name=\"サンプル\">あいう</a>";
     * String str2 = "<a href=\"xxx.html\" name=\"サンプル\">あいう</a>";
     * List<String> list1 = Arrays.asList("a");
     * List<String> list2 = Arrays.asList("href", "name");
     *
     * //属性値の内容がセキュリティ上問題のあるものだった場合、設定と関係なくエスケープされる
     * HtmlTagUtil.escapeHtml(str1.toString(), false, list1, list2);
     *             //-->&lt;a href=&#034;javascript:alert();&#034; name=&#034;サンプル&#034;&gt;あいう</a>
     *
     * //属性地の内容がセキュリティ上問題のないものだった場合、設定したタグはエスケープされない
     * HtmlTagUtil.escapeHtml(str2.toString(), false, list1, list2);
     *             //--><a href="xxx.html" name="サンプル">あいう</a>
     * }
     * </pre>
     * @param s エスケープ対象のオブジェクト
     * @param withHtmlFormat 改行変換と半角スペース変換を行う場合はtrue
     * @param safeTags       エスケープ対象外のタグ
     * @param safeAttributes
     *     エスケープ対象外のタグの中で使用することができる属性。
     *     (ここに無い属性が使用されていた場合は、エスケープ対象外のタグでも、
     *      エスケープされる。)
     * @return エスケープ後の文字列
     */
    @Published(tag = "architect")
    public static String escapeHtml(Object       s,
                                    boolean      withHtmlFormat,
                                    List<String> safeTags,
                                    List<String> safeAttributes) {
        if (s == null) {
            return null;
        }

        String str = StringUtil.toString(s);
        int length = str.length();
        boolean emitsSafeTags = (safeTags != null && safeTags.size() > 0);

        StringBuffer sb = new StringBuffer();
        HtmlTagConsumer consumer = emitsSafeTags
                                  ? new HtmlTagConsumer(sb, str, safeTags, safeAttributes)
                                  : null;

        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            switch (c) {
                case '&' : sb.append("&amp;"); break;
                case '<' :
                    if (emitsSafeTags) {
                        int chars = consumer.consumeTagBeginsAt(i);
                        if (chars == 0) {
                            sb.append("&lt;");
                        } else {
                            i += chars - 1;
                        }
                    } else {
                        sb.append("&lt;");
                    }
                    break;
                case '>' : sb.append("&gt;"); break;
                case '"' : sb.append("&#034;"); break;
                case '\'': sb.append("&#039;"); break;
                case ' ' : sb.append(withHtmlFormat ? "&nbsp;" : c); break;
                case '\n': sb.append(withHtmlFormat ? "<br />" : c); break;
                case '\r':
                    sb.append(withHtmlFormat ? "<br />" : c);
                    // \r\nの場合はiを1進める。
                    if (withHtmlFormat && ((i + 1) != length && str.charAt(i + 1) == '\n')) {
                        i++;
                    }
                    break;
                default :
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * HTMLタグの読込み処理を行うユーティリティクラス。
     *
     */
    private static class HtmlTagConsumer {
        /** 連結バッファ */
        private final StringBuffer sb;
        /** 探索対象文字列 */
        private final String src;
        /** 許容されるHTMLタグ */
        private final List<String> tags;
        /** 許容タグの中で使用できる属性 */
        private final List<String> attrs;
        /** 探索範囲 */
        private final Stack<Range> stack;
        /** キャプチャ文字 */
        private String image;


        /** 走査領域 */
        private class Range {
            /** 開始位置(0起点) */
            private final int begin;
            /** 終了位置(=最後の文字の位置) */
            private final int end;
            /** 現在の走査位置 */
            private int pos;
            /**
             * コンストラクタ
             * @param begin 開始位置
             * @param end   終了位置
             */
            public Range(int begin, int end) {
                this.begin = begin;
                this.end   = end;
                this.pos   = begin;
            }
            /**
             * 開始位置から現在の走査位置までの部分文字列を返す。
             * @return 部分文字列
             */
            @Override
            public String toString() {
                return src.substring(begin, pos);
            }
        }

        /** デフォルトコンストラクタ
         * @param sb    連結バッファ
         * @param src   元文字列
         * @param tags  許容されるHTMLタグ
         * @param attrs 許容タグの中で使用できる属性
         */
        public HtmlTagConsumer(StringBuffer sb, String src, List<String> tags, List<String> attrs) {
            this.sb    = sb;
            this.src   = src;
            this.tags  = tags;
            this.attrs = attrs;
            this.stack = new Stack<Range>();

        }

        /**
         * offsetで指定された位置から許容されたタグが開始していた場合は
         * その内容をそのままバッファに連結し、その文字数を返却する。
         *
         * @param  offset 読込み開始位置(0起算)
         * @return        読み込んだ(バッファに連結した)文字列数
         */
        public int consumeTagBeginsAt(int offset) {
            Range r = new Range(offset, src.length());
            stack.push(r);
            if (endTag()) {
                return consume();
            }
            if (beginTag()) {
                return consume();
            }
            return 0;
        }

        /**
         * 読み込まれたタグ文字列をバッファに出力する。
         * @return バッファに出力した文字数
         */
        private int consume() {
            String read = stack.pop().toString();
            sb.append(read);
            return read.length();
        }

        /**
         * 後続するHTML終了タグを走査する。
         * @return 終了タグが後続していればtrue
         */
        private boolean endTag() {
            return between("</", ">")
                && ws()
                && identifier()
                && tags.contains(image)
                && ws()
                && ends();
        }

        /**
         * 後続するHTML開始タグを走査する。
         * @return 開始タグが後続していればtrue
         */
        private boolean beginTag() {
            return between("<", ">")
                && ws()
                && identifier()
                && tags.contains(image)
                && ws()
                && attributes()
                && ws()
                && ends();
        }

        /**
         * 属性値の任意回の繰り返しを走査する。
         * @return 属性値が存在しない、もしくは、1つ以上の属性値が後続
         *          している場合はtrueを返す。
         */
        private boolean attributes() {
            Range r = stack.peek();
            while (r.pos < r.end) {
                if (slash()) {
                    return (r.pos == r.end);
                }
                if (!attribute()) {
                    return false;
                }
                ws();
            }
            return true;
        }

        /**
         * 属性値を走査する。
         * @return 属性値が後続していればtrue
         */
        private boolean attribute() {
            return identifier()
                && attrs.contains(image)
                && ws()
                && (
                    !eq() && ws() // 属性値なし
                    ||
                    ws() && between("'", "'")   && attrValue() && ends()
                    ||
                    ws() && between("\"", "\"") && attrValue() && ends()
                );
        }

        /**
         * '='を走査する。
         * @return '='が後続していればtrue
         */
        private boolean eq() {
            Range r = stack.peek();
            boolean result = (src.charAt(r.pos) == '=');
            if (result) {
                r.pos++;
            }
            return result;
        }

        /**
         * '/'を走査する。
         * @return '/'が後続していればtrue
         */
        private boolean slash() {
            Range r = stack.peek();
            boolean result = (src.charAt(r.pos) == '/');
            if (result) {
                r.pos++;
            }
            return result;
        }

        /**
         * 属性値の内容を走査し、キャプチャに格納する。
         * @return 属性値の内容がセキュリティ上の問題となりうるものであった
         *          場合はfalseを返す。
         */
        private boolean attrValue() {
            image = null;
            Range r = stack.peek();
            image = src.substring(r.pos, r.end);
            // href属性などで javascript スキームが設定されているとまずいので、
            // ここではねておく。
            if (image.trim().toLowerCase().startsWith(JS_SCHEME)) {
                return false;
            }
            r.pos = r.end;
            return true;
        }
        /** javascriptスキーム */
        private static final String JS_SCHEME = "javascript:";

        /**
         * 後続する識別子文字列を走査し、その値をキャプチャする。
         * @return 識別子文字列が後続していればtrue
         */
        private boolean identifier() {
            image = null;
            Range r = stack.peek();
            int pos = r.pos;
            while (pos < r.end) {
                if (!isIdentifierChar(src.charAt(pos))) {
                    break;
                }
                pos++;
            }
            image = src.substring(r.pos, pos);
            r.pos = pos;
            return (image.length() != 0);
        }

        /**
         * 指定された文字が識別子に使用できる文字かどうかを判定する。
         * @param c 文字
         * @return 識別子に使用できる文字であればtrue
         */
        private boolean isIdentifierChar(char c) {
            return ('0' <= c && c <= '9')
                || ('a' <= c && c <= 'z')
                || ('A' <= c && c <= 'Z')
                || ("_-".indexOf(c) != -1);
        }

        /**
         * 指定された文字列で挟まれた最も狭い領域をサブパターンとして取得する。
         * @param from 開始文字列
         * @param to   終了文字列
         * @return 領域が取得できた場合はtrue
         */
        private boolean between(String from, String to) {
            Range r = stack.peek();
            int contentBegin = r.pos + from.length();
            if (contentBegin > r.end) {
                return false;
            }
            if (!from.equals(src.substring(r.pos, contentBegin))) {
                return false;
            }
            int contentEnd = src.indexOf(to, contentBegin);
            if (contentEnd == -1) {
                return false;
            }
            int end = contentEnd + to.length();
            if (end > r.end) {
                return false;
            }
            r.pos = end;
            stack.push(new Range(contentBegin, contentEnd)); // 中身
            return true;
        }

        /**
         * 現在のサブパターンが終端に達しているかどうかを確認し、
         * サブパターンの走査を終了する。
         * @return 終端に達していればtrue
         */
        private boolean ends() {
            Range r = stack.pop();
            return (r.pos == r.end);
        }

        /**
         * 現在位置から任意個の空白文字を走査する。
         * @return 常にreturnを返す。
         */
        private boolean ws() {
            Range r = stack.peek();
            for (int i = r.pos; i <= r.end; i++) {
                char c = src.charAt(i);
                if (WS.indexOf(c) == -1) {
                    r.pos = i;
                    return true;
                }
            }
            return true;
        }
        /**空白文字*/
        private static final String WS = " \t\n\r";
    }

}
