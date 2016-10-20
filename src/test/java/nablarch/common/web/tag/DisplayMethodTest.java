package nablarch.common.web.tag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.junit.Test;


/**
 * {@link DisplayMethod}の単体テスト。
 *
 * @author takanori tani
 */
public class DisplayMethodTest {

    /**
     * DisplayMethod#getDisplayMethod(String)のテスト。
     */
    @Test
    public void testGetDisplayMethod() {
        // 存在するDisplayMethodを指定した場合。
        assertThat("存在しているので、一致しているInstanceが取得できる。", DisplayMethod.getDisplayMethod("NORMAL"), is(DisplayMethod.NORMAL));
        assertThat("パラメータを変更して確認", DisplayMethod.getDisplayMethod("DISABLED"), is(DisplayMethod.DISABLED));

        try {
             DisplayMethod.getDisplayMethod("NOT_IN_DISPLAY_METHOD");
             fail("存在しない表示方法の場合は例外が発生すべきだが、スルーされた。");
        } catch (IllegalArgumentException e) {
             String message = e.getMessage();
             assertThat("エラーメッセージの確認(呼び出し時のパラメータがあるか。)", message, containsString("[NOT_IN_DISPLAY_METHOD]"));
        }

        try {
            DisplayMethod.getDisplayMethod(null);
            fail("nullの場合もスルー。");
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            assertThat("エラーメッセージの確認(呼び出し時のパラメータがあるか。)", message, containsString("[null]"));
        }
    }
}
