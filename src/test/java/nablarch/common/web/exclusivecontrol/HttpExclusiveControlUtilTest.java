package nablarch.common.web.exclusivecontrol;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import nablarch.common.exclusivecontrol.ExCardMstPk3;
import nablarch.common.exclusivecontrol.ExCompMstPk;
import nablarch.common.exclusivecontrol.ExUserMstLowerPk;
import nablarch.common.exclusivecontrol.ExUserMstPk;
import nablarch.common.exclusivecontrol.ExUserMstPk2;
import nablarch.common.exclusivecontrol.ExUserMstPk3;
import nablarch.common.exclusivecontrol.ExclusiveCardMst3;
import nablarch.common.exclusivecontrol.ExclusiveCompMst;
import nablarch.common.exclusivecontrol.ExclusiveControlContext;
import nablarch.common.exclusivecontrol.ExclusiveControlTestSupport;
import nablarch.common.exclusivecontrol.ExclusiveTest1;
import nablarch.common.exclusivecontrol.ExclusiveTest1Pk;
import nablarch.common.exclusivecontrol.ExclusiveTest2;
import nablarch.common.exclusivecontrol.ExclusiveTest2Pk;
import nablarch.common.exclusivecontrol.ExclusiveUserMst;
import nablarch.common.exclusivecontrol.ExclusiveUserMst2;
import nablarch.common.exclusivecontrol.ExclusiveUserMst3;
import nablarch.common.exclusivecontrol.OptimisticLockException;
import nablarch.core.ThreadContext;
import nablarch.core.db.transaction.SimpleDbTransactionManager;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.MockHttpRequest;
import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.db.helper.DatabaseTestRunner;
import nablarch.test.support.db.helper.TargetDb;
import nablarch.test.support.db.helper.VariousDbTestHelper;

import nablarch.test.support.message.MockStringResourceHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * {@link HttpExclusiveControlUtil}テスト。
 * @author Kiyohito Itoh
 */
@RunWith(DatabaseTestRunner.class)
// PostgreSQLの場合、検索条件を文字列→数値に型変換しないため本機能を使用することが出来ない。
// このため、テスト自体をスキップする。
@TargetDb(exclude = TargetDb.Db.POSTGRE_SQL)
public class HttpExclusiveControlUtilTest extends ExclusiveControlTestSupport {

	@Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource("nablarch/common/exclusivecontrol/exclusivecontrol.xml");

    private static SimpleDbTransactionManager transactionManager;

    @Before
    public void setUp() {
        ThreadContext.setLanguage(Locale.JAPAN);
        repositoryResource.getComponentByType(MockStringResourceHolder.class).setMessages(MESSAGES);
        repositoryResource.addComponent("exclusiveControlManager", repositoryResource.getComponent("basicExclusiveControlManager"));
        transactionManager = repositoryResource.getComponent("dbManager-default");
        transactionManager.beginTransaction();
    }

    @After
    public void tearDown() {
        transactionManager.endTransaction();
    }

    /**
     * バージョン番号の取得、チェック、更新を一通りテストする。
     */
    @Test
    public void testAll() throws Exception {
        ExecutionContext exeContext;
        HttpRequest req;
        List<String> versions;
        List<ExclusiveUserMst> exclusiveUserMstList;
        List<ExclusiveCompMst> exclusiveCompMstList;

        /****************************************************************
        バージョン番号を取得する場合(1件)
        ****************************************************************/

        exeContext = new ExecutionContext();

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L));
        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001")));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(1));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001");

        /****************************************************************
        バージョン番号をチェックする場合(1件かつ更新なし)
        ****************************************************************/

        String userVersionString = versionString(
            "EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"
        );

        exeContext = new ExecutionContext();
        req = new MockHttpRequest();
        req.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString);

        HttpExclusiveControlUtil.checkVersions(req, exeContext);

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(1));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001");

        /****************************************************************
        バージョン番号をチェックする場合(1件かつ更新あり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst("uid001", "pk2001", "pk3001", 2L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString);

        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext);
            fail();
        } catch (OptimisticLockException e) {
            // success
        }

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(1件かつ更新なし)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L));

        userVersionString = versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001");

        req = new MockHttpRequest();
        req.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                     userVersionString);

        HttpExclusiveControlUtil.updateVersionsWithCheck(req);
        transactionManager.commitTransaction();

        exclusiveUserMstList = VariousDbTestHelper.findAll(ExclusiveUserMst.class);
        assertThat(exclusiveUserMstList.size(), is(1));
        assertThat(exclusiveUserMstList.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(0).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(0).pk3, is("pk3001"));
        assertThat(exclusiveUserMstList.get(0).version, is(2L));

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(1件かつ更新あり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst("uid001", "pk2001", "pk3001", 2L));

        userVersionString = versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001");

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString);

        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req);
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }


        /****************************************************************
        バージョン番号が存在しない場合(1件)
        ****************************************************************/

        exeContext = new ExecutionContext();

        assertFalse(HttpExclusiveControlUtil.prepareVersion(exeContext, new ExUserMstPk("uid999", "pk2001", "pk3001")));
        assertNull(exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME));

        /****************************************************************
        バージョン番号を取得する場合(複数件)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(
                new ExclusiveCompMst("com001", 1L),
                new ExclusiveCompMst("com002", 2L),
                new ExclusiveCompMst("com003", 3L),
                new ExclusiveCompMst("com004", 4L),
                new ExclusiveCompMst("com005", 5L));

        exeContext = new ExecutionContext();

        assertTrue(HttpExclusiveControlUtil.prepareVersions(exeContext, Arrays.asList(new ExCompMstPk("com001"),
                                                                     new ExCompMstPk("com003"),
                                                                     new ExCompMstPk("com005"),
                                                                     new ExCompMstPk("com004"),
                                                                     new ExCompMstPk("com002"))));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(5));
        assertVersionString(versions.get(0), "EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001");
        assertVersionString(versions.get(1), "EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003");
        assertVersionString(versions.get(2), "EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005");
        assertVersionString(versions.get(3), "EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004");
        assertVersionString(versions.get(4), "EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002");

        /****************************************************************
        バージョン番号をチェックする場合(複数件かつ更新なし)
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005"));

        HttpExclusiveControlUtil.checkVersions(req, exeContext);

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(5));
        assertVersionString(versions.get(0), "EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001");
        assertVersionString(versions.get(1), "EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002");
        assertVersionString(versions.get(2), "EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003");
        assertVersionString(versions.get(3), "EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004");
        assertVersionString(versions.get(4), "EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005");


        /****************************************************************
        バージョン番号をチェックする場合(複数件かつ更新あり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(
                new ExclusiveCompMst("com001", 1L),
                new ExclusiveCompMst("com002", 2L),
                new ExclusiveCompMst("com003", 3L),
                new ExclusiveCompMst("com004", 5L),
                new ExclusiveCompMst("com005", 5L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005"));

        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext);
            fail();
        } catch (OptimisticLockException e) {
            // success
        }

        // 更新対象リストを指定する場合。
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005"))
                    .setParam("user.targetUidList", "com001", "com002", "com004");

        // 更新済み項目("com004") が対象に含まれているので排他エラー
        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext, "user.targetUidList");
            fail();
        } catch (OptimisticLockException e) {
            // success
        }


        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005"))
                    .setParam("user.targetUidList", "com001", "com002", "com005");

       // 更新済み項目("com004") がチェック対象から外れたので論理排他チェックOK

        HttpExclusiveControlUtil.updateVersionsWithCheck(req, "user.targetUidList");
        transactionManager.commitTransaction();


        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005"));

        // 更新対象無し。
        HttpExclusiveControlUtil.checkVersions(req, exeContext, "user.targetUidList");

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(複数件かつ更新なし)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(
                new ExclusiveCompMst("com001", 1L),
                new ExclusiveCompMst("com002", 2L),
                new ExclusiveCompMst("com003", 3L),
                new ExclusiveCompMst("com004", 4L),
                new ExclusiveCompMst("com005", 5L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005"));

        HttpExclusiveControlUtil.updateVersionsWithCheck(req);
        transactionManager.commitTransaction();

        exclusiveCompMstList = VariousDbTestHelper.findAll(ExclusiveCompMst.class, "compId");
        assertThat(exclusiveCompMstList.size(), is(5));
        assertThat(exclusiveCompMstList.get(0).compId, is("com001"));
        assertThat(exclusiveCompMstList.get(0).version, is(2L));
        assertThat(exclusiveCompMstList.get(1).compId, is("com002"));
        assertThat(exclusiveCompMstList.get(1).version, is(3L));
        assertThat(exclusiveCompMstList.get(2).compId, is("com003"));
        assertThat(exclusiveCompMstList.get(2).version, is(4L));
        assertThat(exclusiveCompMstList.get(3).compId, is("com004"));
        assertThat(exclusiveCompMstList.get(3).version, is(5L));
        assertThat(exclusiveCompMstList.get(4).compId, is("com005"));
        assertThat(exclusiveCompMstList.get(4).version, is(6L));

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(複数件かつ更新あり)
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "1", "comp_id", "com001"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "2", "comp_id", "com002"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "3", "comp_id", "com003"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "4", "comp_id", "com004"),
                        versionString("EXCLUSIVE_COMP_MST", "VERSION", "5", "comp_id", "com005"));

        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req);
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }

        /****************************************************************
        バージョン番号が存在しない場合(複数件)
        ****************************************************************/

        exeContext = new ExecutionContext();
        assertFalse(HttpExclusiveControlUtil.prepareVersions(exeContext, Arrays.asList(new ExCompMstPk("com001"),
                                                                     new ExCompMstPk("com003"),
                                                                     new ExCompMstPk("com005"),
                                                                     new ExCompMstPk("com994"),
                                                                     new ExCompMstPk("com002"))));
        assertNull(exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME));

        /****************************************************************
        バージョン番号パラメータが存在しない場合
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest();

        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("version parameter was not found."));
        }

        req = new MockHttpRequest();

        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("version parameter was not found."));
        }

        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req, "pkList");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("version parameter was not found."));
        }



        /****************************************************************
        更新対象リスト変数名がnullだった場合は、実行時例外を送出する。
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest();

        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext, (String) null);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }

        req = new MockHttpRequest();
        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req, (String) null);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    /**
     * 複合キーを持つテーブルの一括更新についてテストする。
     */
    @Test
    public void testAllComplexKeyBulkUpdate() throws Exception {
        ExecutionContext exeContext;
        HttpRequest req;
        List<String> versions;
        List<ExclusiveUserMst> exclusiveUserMstList;

        VariousDbTestHelper.setUpTable(
                new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L),
                new ExclusiveUserMst("uid002", "pk2002", "pk3002", 2L),
                new ExclusiveUserMst("uid003", "pk2003", "pk3003", 3L),
                new ExclusiveUserMst("uid004", "pk2004", "pk3004", 4L),
                new ExclusiveUserMst("uid005", "pk2005", "pk3005", 5L));

        /****************************************************************
        バージョン番号をチェックする場合(複数件かつ更新なし)
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid002", "pk2002", "pk3002"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid003", "pk2003", "pk3003"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid004", "pk2004", "pk3004"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid005", "pk2005", "pk3005"));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(5));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST", "VERSION", "1", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3001");
        assertVersionString(versions.get(1), "EXCLUSIVE_USER_MST", "VERSION", "2", "user_id","uid002", "pk2", "pk2002", "pk3", "pk3002");
        assertVersionString(versions.get(2), "EXCLUSIVE_USER_MST", "VERSION", "3", "user_id","uid003", "pk2", "pk2003", "pk3", "pk3003");
        assertVersionString(versions.get(3), "EXCLUSIVE_USER_MST", "VERSION", "4", "user_id","uid004", "pk2", "pk2004", "pk3", "pk3004");
        assertVersionString(versions.get(4), "EXCLUSIVE_USER_MST", "VERSION", "5", "user_id","uid005", "pk2", "pk2005", "pk3", "pk3005");

        /****************************************************************
        バージョン番号をチェックする場合(複数件かつ更新なし、主キークラスに定義される主キーが小文字)
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstLowerPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstLowerPk("uid002", "pk2002", "pk3002"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstLowerPk("uid003", "pk2003", "pk3003"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstLowerPk("uid004", "pk2004", "pk3004"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstLowerPk("uid005", "pk2005", "pk3005"));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(5));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST", "VERSION", "1", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3001");
        assertVersionString(versions.get(1), "EXCLUSIVE_USER_MST", "VERSION", "2", "user_id","uid002", "pk2", "pk2002", "pk3", "pk3002");
        assertVersionString(versions.get(2), "EXCLUSIVE_USER_MST", "VERSION", "3", "user_id","uid003", "pk2", "pk2003", "pk3", "pk3003");
        assertVersionString(versions.get(3), "EXCLUSIVE_USER_MST", "VERSION", "4", "user_id","uid004", "pk2", "pk2004", "pk3", "pk3004");
        assertVersionString(versions.get(4), "EXCLUSIVE_USER_MST", "VERSION", "5", "user_id","uid005", "pk2", "pk2005", "pk3", "pk3005");

        /****************************************************************
        バージョン番号をチェックする場合(複数件かつ更新あり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(
                new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L),
                new ExclusiveUserMst("uid002", "pk2002", "pk3002", 2L),
                new ExclusiveUserMst("uid003", "pk2003", "pk3003", 3L),
                new ExclusiveUserMst("uid004", "pk2004", "pk3004", 5L),
                new ExclusiveUserMst("uid005", "pk2005", "pk3005", 5L));

        // 全行チェック
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        try {
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid002", "pk2002", "pk3002"));
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid003", "pk2003", "pk3003"));
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid004", "pk2004", "pk3004"));
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid005", "pk2005", "pk3005"));
            fail();
        } catch (OptimisticLockException e) {
            // success
        }

        // 更新対象を指定する場合。
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        // 更新済み項目("uid004") が対象に含まれているので排他エラー
        try {
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid002", "pk2002", "pk3002"));
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid004", "pk2004", "pk3004"));
            fail();
        } catch (OptimisticLockException e) {
            // success
        }


        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        // 更新済み項目("uid004") がチェック対象から外れたので論理排他チェックOK
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid002", "pk2002", "pk3002"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid005", "pk2005", "pk3005"));

        /****************************************************************
        バージョン番号のチェックを伴い更新する場合(複数件かつ更新なし)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(
                new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L),
                new ExclusiveUserMst("uid002", "pk2002", "pk3002", 2L),
                new ExclusiveUserMst("uid003", "pk2003", "pk3003", 3L),
                new ExclusiveUserMst("uid004", "pk2004", "pk3004", 4L),
                new ExclusiveUserMst("uid005", "pk2005", "pk3005", 5L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid002", "pk2002", "pk3002"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid003", "pk2003", "pk3003"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid004", "pk2004", "pk3004"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid005", "pk2005", "pk3005"));
        transactionManager.commitTransaction();

        exclusiveUserMstList = VariousDbTestHelper.findAll(ExclusiveUserMst.class, "userId");
        assertThat(exclusiveUserMstList.size(), is(5));
        assertThat(exclusiveUserMstList.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(0).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(0).pk3, is("pk3001"));
        assertThat(exclusiveUserMstList.get(0).version, is(2L));
        assertThat(exclusiveUserMstList.get(1).userId, is("uid002"));
        assertThat(exclusiveUserMstList.get(1).pk2, is("pk2002"));
        assertThat(exclusiveUserMstList.get(1).pk3, is("pk3002"));
        assertThat(exclusiveUserMstList.get(1).version, is(3L));
        assertThat(exclusiveUserMstList.get(2).userId, is("uid003"));
        assertThat(exclusiveUserMstList.get(2).pk2, is("pk2003"));
        assertThat(exclusiveUserMstList.get(2).pk3, is("pk3003"));
        assertThat(exclusiveUserMstList.get(2).version, is(4L));
        assertThat(exclusiveUserMstList.get(3).userId, is("uid004"));
        assertThat(exclusiveUserMstList.get(3).pk2, is("pk2004"));
        assertThat(exclusiveUserMstList.get(3).pk3, is("pk3004"));
        assertThat(exclusiveUserMstList.get(3).version, is(5L));
        assertThat(exclusiveUserMstList.get(4).userId, is("uid005"));
        assertThat(exclusiveUserMstList.get(4).pk2, is("pk2005"));
        assertThat(exclusiveUserMstList.get(4).pk3, is("pk3005"));
        assertThat(exclusiveUserMstList.get(4).version, is(6L));

        /****************************************************************
        バージョン番号のチェックを伴い更新する場合(複数件かつ更新あり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(
                new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L),
                new ExclusiveUserMst("uid002", "pk2002", "pk3002", 2L),
                new ExclusiveUserMst("uid003", "pk2003", "pk3003", 3L),
                new ExclusiveUserMst("uid004", "pk2004", "pk3004", 5L),
                new ExclusiveUserMst("uid005", "pk2005", "pk3005", 5L));

        // 全行チェック
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid002", "pk2002", "pk3002"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid003", "pk2003", "pk3003"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid004", "pk2004", "pk3004"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid005", "pk2005", "pk3005"));
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }

        // 更新対象を指定する場合
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        // 更新済み項目("uid004") が対象に含まれているので排他エラー
        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid002", "pk2002", "pk3002"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid004", "pk2004", "pk3004"));
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }

        // 更新済み項目("uid004") がチェック対象から外れたので論理排他チェックOK
        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid002", "pk2002", "pk3002"));
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid005", "pk2005", "pk3005"));
        } catch (OptimisticLockException e) {
            fail();
        } finally {
            transactionManager.rollbackTransaction();
        }

        /****************************************************************
       主キー条件にバージョン情報が含まれても正常に動作すること
        ****************************************************************/
        VariousDbTestHelper.setUpTable(
                new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L),
                new ExclusiveUserMst("uid002", "pk2002", "pk3002", 2L),
                new ExclusiveUserMst("uid003", "pk2003", "pk3003", 3L),
                new ExclusiveUserMst("uid004", "pk2004", "pk3004", 4L),
                new ExclusiveUserMst("uid005", "pk2005", "pk3005", 5L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001", "version", "1"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002", "version", "2"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003", "version", "3"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004", "version", "4"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005", "version", "5"));

        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid002", "pk2002", "pk3002"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid003", "pk2003", "pk3003"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid004", "pk2004", "pk3004"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid005", "pk2005", "pk3005"));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(5));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST", "VERSION", "1", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3001", "version", "1");
        assertVersionString(versions.get(1), "EXCLUSIVE_USER_MST", "VERSION", "2", "user_id","uid002", "pk2", "pk2002", "pk3", "pk3002", "version", "2");
        assertVersionString(versions.get(2), "EXCLUSIVE_USER_MST", "VERSION", "3", "user_id","uid003", "pk2", "pk2003", "pk3", "pk3003", "version", "3");
        assertVersionString(versions.get(3), "EXCLUSIVE_USER_MST", "VERSION", "4", "user_id","uid004", "pk2", "pk2004", "pk3", "pk3004", "version", "4");
        assertVersionString(versions.get(4), "EXCLUSIVE_USER_MST", "VERSION", "5", "user_id","uid005", "pk2", "pk2005", "pk3", "pk3005", "version", "5");

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001", "version", "1"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002", "version", "2"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003", "version", "3"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004", "version", "4"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005", "version", "5"));

        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid002", "pk2002", "pk3002"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid003", "pk2003", "pk3003"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid004", "pk2004", "pk3004"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid005", "pk2005", "pk3005"));
        transactionManager.commitTransaction();

        exclusiveUserMstList = VariousDbTestHelper.findAll(ExclusiveUserMst.class, "userId");
        assertThat(exclusiveUserMstList.size(), is(5));
        assertThat(exclusiveUserMstList.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(0).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(0).pk3, is("pk3001"));
        assertThat(exclusiveUserMstList.get(0).version, is(2L));
        assertThat(exclusiveUserMstList.get(1).userId, is("uid002"));
        assertThat(exclusiveUserMstList.get(1).pk2, is("pk2002"));
        assertThat(exclusiveUserMstList.get(1).pk3, is("pk3002"));
        assertThat(exclusiveUserMstList.get(1).version, is(3L));
        assertThat(exclusiveUserMstList.get(2).userId, is("uid003"));
        assertThat(exclusiveUserMstList.get(2).pk2, is("pk2003"));
        assertThat(exclusiveUserMstList.get(2).pk3, is("pk3003"));
        assertThat(exclusiveUserMstList.get(2).version, is(4L));
        assertThat(exclusiveUserMstList.get(3).userId, is("uid004"));
        assertThat(exclusiveUserMstList.get(3).pk2, is("pk2004"));
        assertThat(exclusiveUserMstList.get(3).pk3, is("pk3004"));
        assertThat(exclusiveUserMstList.get(3).version, is(5L));
        assertThat(exclusiveUserMstList.get(4).userId, is("uid005"));
        assertThat(exclusiveUserMstList.get(4).pk2, is("pk2005"));
        assertThat(exclusiveUserMstList.get(4).pk3, is("pk3005"));
        assertThat(exclusiveUserMstList.get(4).version, is(6L));

        /****************************************************************
        バージョン番号パラメータが存在しない場合
        ****************************************************************/

        req = new MockHttpRequest();
        exeContext = new ExecutionContext();

        try {
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("version parameter was not found."));
        }

        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("version parameter was not found."));
        }

        VariousDbTestHelper.setUpTable(
                new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3002", 2L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3003", 3L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3004", 4L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3005", 5L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001", "version", "1"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3002", "version", "2"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3003", "version", "3"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3004", "version", "4"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "USER_ID", "uid001", "pk2", "pk2001", "pk3", "pk3005", "version", "5"));

        try {
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3006"));
            fail();
        } catch (IllegalArgumentException e) {
            assertContainsAll(new String[]{
                    "version was not found. ",
                    "tableName = [EXCLUSIVE_USER_MST]",
                    "user_id=uid001",
                    "pk2=pk2001",
                    "pk3=pk3006",
            }, e.getMessage());
        }

        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3006"));
            fail();
        } catch (IllegalArgumentException e) {
            assertContainsAll(new String[]{
                    "version was not found. ",
                    "tableName = [EXCLUSIVE_USER_MST]",
                    "user_id=uid001",
                    "pk2=pk2001",
                    "pk3=pk3006",
            }, e.getMessage());
        }

        // 不正な排他制御コンテキストが指定された場合

        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", null, "pk3006"));
            fail();
        } catch (IllegalArgumentException e) {
            assertContainsAll(new String[]{
                    "version was not found. ",
                    "tableName = [EXCLUSIVE_USER_MST]",
                    "user_id=uid001",
                    "pk2=null",
                    "pk3=pk3006",
            }, e.getMessage());
        }

        /****************************************************************
        排他制御コンテキスト変数名がnullだった場合は、実行時例外を送出する。
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid002", "pk2", "pk2002", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid003", "pk2", "pk2003", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid004", "pk2", "pk2004", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid005", "pk2", "pk2005", "pk3", "pk3005"));

        try {
            HttpExclusiveControlUtil.checkVersion(req, exeContext, (ExclusiveControlContext) null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("exclusiveControlContext was null."));
        }

        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, (ExclusiveControlContext) null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("exclusiveControlContext was null."));
        }

        /****************************************************************
       キー値を取得できない場合は、実行時例外を送出する。
        ****************************************************************/

        // 対応する主キーのカラム名が見つからない
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "pk2", "pk2001", "pk3", "pk3001"));
        try {
            HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            fail();
        } catch (IllegalArgumentException e) {
            assertContainsAll(new String[]{
                    "version was not found. ",
                    "tableName = [EXCLUSIVE_USER_MST]",
                    "user_id=uid001",
                    "pk2=pk2001",
                    "pk3=pk3001",
            }, e.getMessage());
        }

        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
            fail();
        } catch (IllegalArgumentException e) {
            assertContainsAll(new String[]{
                    "version was not found. ",
                    "tableName = [EXCLUSIVE_USER_MST]",
                    "user_id=uid001",
                    "pk2=pk2001",
                    "pk3=pk3001",
            }, e.getMessage());
        }

        VariousDbTestHelper.setUpTable(
                new ExclusiveUserMst("uid001", "pk2001", "pk3001", 1L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3002", 2L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3003", 3L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3004", 4L),
                new ExclusiveUserMst("uid001", "pk2001", "pk3005", 5L));


        /****************************************************************
        バージョン番号をチェックする場合(複数件かつ更新なし、複合主キーの一項目のみ異なる)
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3005"));

        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3002"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3003"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3004"));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, new ExUserMstPk("uid001", "pk2001", "pk3005"));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(5));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST", "VERSION", "1", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3001");
        assertVersionString(versions.get(1), "EXCLUSIVE_USER_MST", "VERSION", "2", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3002");
        assertVersionString(versions.get(2), "EXCLUSIVE_USER_MST", "VERSION", "3", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3003");
        assertVersionString(versions.get(3), "EXCLUSIVE_USER_MST", "VERSION", "4", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3004");
        assertVersionString(versions.get(4), "EXCLUSIVE_USER_MST", "VERSION", "5", "user_id","uid001", "pk2", "pk2001", "pk3", "pk3005");

        /****************************************************************
        バージョン番号のチェックを伴い更新する場合(複数件かつ更新なし、複合主キーの一項目のみ異なる)
        ****************************************************************/
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "1", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3001"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "2", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3002"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "3", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3003"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "4", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3004"),
                        versionString("EXCLUSIVE_USER_MST", "VERSION", "5", "user_id", "uid001", "pk2", "pk2001", "pk3", "pk3005"));

        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3001"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3002"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3003"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3004"));
        HttpExclusiveControlUtil.updateVersionWithCheck(req, new ExUserMstPk("uid001", "pk2001", "pk3005"));
        transactionManager.commitTransaction();

        exclusiveUserMstList = VariousDbTestHelper.findAll(ExclusiveUserMst.class, "userId", "pk2", "pk3");
        assertThat(exclusiveUserMstList.size(), is(5));
        assertThat(exclusiveUserMstList.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(0).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(0).pk3, is("pk3001"));
        assertThat(exclusiveUserMstList.get(0).version, is(2L));
        assertThat(exclusiveUserMstList.get(1).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(1).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(1).pk3, is("pk3002"));
        assertThat(exclusiveUserMstList.get(1).version, is(3L));
        assertThat(exclusiveUserMstList.get(2).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(2).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(2).pk3, is("pk3003"));
        assertThat(exclusiveUserMstList.get(2).version, is(4L));
        assertThat(exclusiveUserMstList.get(3).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(3).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(3).pk3, is("pk3004"));
        assertThat(exclusiveUserMstList.get(3).version, is(5L));
        assertThat(exclusiveUserMstList.get(4).userId, is("uid001"));
        assertThat(exclusiveUserMstList.get(4).pk2, is("pk2001"));
        assertThat(exclusiveUserMstList.get(4).pk3, is("pk3005"));
        assertThat(exclusiveUserMstList.get(4).version, is(6L));
    }

    /**
     * バージョン番号の取得、チェック、更新を一通りテストする。
     */
    @Test
    public void testUpdatingEntitiesWithSinglePK() throws Exception {
        ExecutionContext exeContext;
        HttpRequest req;
        List<String> versions;
        List<ExclusiveUserMst2> exclusiveUserMst2List;

        /****************************************************************
        バージョン番号を取得する場合(1件)
        ****************************************************************/

        exeContext = new ExecutionContext();

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));

        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, new ExUserMstPk2("uid001")));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(1));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001");

        /****************************************************************
        バージョン番号をチェックする場合(1件かつ更新なし)
        ****************************************************************/

        String userVersionString = versionString(
            "EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001"
        );

        exeContext = new ExecutionContext();
        req = new MockHttpRequest();
        req.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString);

        HttpExclusiveControlUtil.checkVersions(req, exeContext);

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(1));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001");

        // 更新対象PKリストを指定した場合
        exeContext = new ExecutionContext();
        req = new MockHttpRequest();
        req.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString)
           .setParam("user.targetUidList", "uid001", "uid002"); // 更新対象

        // 更新対象かつ論理排他チェックOK
        HttpExclusiveControlUtil.checkVersions(req, exeContext, "user.targetUidList");


        /****************************************************************
        バージョン番号をチェックする場合(1件かつ更新あり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 2L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString);

        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext);
            fail();
        } catch (OptimisticLockException e) {
            // success
        }

        // 更新対象PKリストを指定した場合
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString)
             .setParam("user.targetUidList", "uid001", "uid002"); // 更新対象に入ってる

        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext, "user.targetUidList");
            fail();
        } catch (OptimisticLockException e) {
            // success
        }

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString)
             .setParam("user.targetUidList", "uid003", "uid002"); // 更新対象外

        // 更新可能
        HttpExclusiveControlUtil.checkVersions(req, exeContext, "user.targetUidList");

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(1件かつ更新なし)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));

        userVersionString = versionString("EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001");

        req = new MockHttpRequest();
        req.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME,
                     userVersionString);

        HttpExclusiveControlUtil.updateVersionsWithCheck(req);
        transactionManager.commitTransaction();

        exclusiveUserMst2List = VariousDbTestHelper.findAll(ExclusiveUserMst2.class);
        assertThat(exclusiveUserMst2List.size(), is(1));
        assertThat(exclusiveUserMst2List.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMst2List.get(0).version, is(2L));

        // 更新対象PKリストを指定した場合
        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString)
             .setParam("user.targetUidList", "uid001", "uid002"); // 更新対象に入ってる

        // 更新対象で論理排他チェックOK
        HttpExclusiveControlUtil.updateVersionsWithCheck(req, "user.targetUidList");
        transactionManager.commitTransaction();

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(1件かつ更新あり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 2L));

        userVersionString = versionString("EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001");

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString);

        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req);
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }


        // 更新対象PKリストを指定した場合
        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString)
             .setParam("user.targetUidList", "uid001", "uid002"); // 更新対象に入ってる

        // 論理排他エラーが発生する。
        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req, "user.targetUidList");
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }

        exeContext = new ExecutionContext();
        req = new MockHttpRequest()
                .setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, userVersionString)
             .setParam("user.targetUidList", "uid003", "uid002"); // 更新対象外


        // 更新した行がチェック対象外になったので論理排他チェックOK
        HttpExclusiveControlUtil.updateVersionsWithCheck(req, "user.targetUidList");


        /****************************************************************
        バージョン番号が存在しない場合(1件)
        ****************************************************************/

        exeContext = new ExecutionContext();

        assertFalse(HttpExclusiveControlUtil.prepareVersion(exeContext, new ExUserMstPk2("uid999")));
        assertNull(exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME));
    }

    /**
     * バージョン番号の取得、チェック、更新を一通りテストする。(複数の排他制御クラス、一括呼び出し）
     */
    @Test
    public void testUpdatingEntitiesWithMultipleExControlsForBulk() throws Exception {
        ExecutionContext exeContext;
        HttpRequest req;
        List<String> versions;
        List<ExclusiveUserMst2> exclusiveUserMst2List;
        List<ExclusiveCardMst3> exclusiveCardMst3List;

        /****************************************************************
        バージョン番号を取得する場合
        ****************************************************************/

        exeContext = new ExecutionContext();

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));
        VariousDbTestHelper.setUpTable(new ExclusiveCardMst3("cad005", 5L));

        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, new ExUserMstPk2("uid001")));
        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, new ExCardMstPk3("cad005")));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(2));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001");
        assertVersionString(versions.get(1), "EXCLUSIVE_CARD_MST3", "VERSION", "5", "card_id", "cad005");

        /****************************************************************
        バージョン番号をチェックする場合(排他エラーなし)
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        HttpExclusiveControlUtil.checkVersions(req, exeContext);

        /****************************************************************
        バージョン番号をチェックする場合(排他エラーあり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 2L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        try {
            HttpExclusiveControlUtil.checkVersions(req, exeContext);
            fail();
        } catch (OptimisticLockException e) {
            // success
        }

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(排他エラーなし)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        HttpExclusiveControlUtil.updateVersionsWithCheck(req);
        transactionManager.commitTransaction();

        exclusiveUserMst2List = VariousDbTestHelper.findAll(ExclusiveUserMst2.class);
        assertThat(exclusiveUserMst2List.size(), is(1));
        assertThat(exclusiveUserMst2List.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMst2List.get(0).version, is(2L));

        exclusiveCardMst3List = VariousDbTestHelper.findAll(ExclusiveCardMst3.class);
        assertThat(exclusiveCardMst3List.size(), is(1));
        assertThat(exclusiveCardMst3List.get(0).cardId, is("cad005"));
        assertThat(exclusiveCardMst3List.get(0).version, is(6L));

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(排他エラーあり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));
        VariousDbTestHelper.setUpTable(new ExclusiveCardMst3("cad005", 7L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        try {
            HttpExclusiveControlUtil.updateVersionsWithCheck(req);
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }

        /****************************************************************
        バージョン番号が存在しない場合
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        assertFalse(HttpExclusiveControlUtil.prepareVersion(exeContext, new ExUserMstPk2("uid999")));
        assertNull(exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME));
    }

    /**
     * バージョン番号の取得、チェック、更新を一通りテストする。(複数の排他制御クラス、個別呼び出し）
     */
    @Test
    public void testUpdatingEntitiesWithMultipleExControlsForSingle() throws Exception {
        ExecutionContext exeContext;
        HttpRequest req;
        List<String> versions;
        List<ExclusiveUserMst2> exclusiveUserMst2List;
        List<ExclusiveCardMst3> exclusiveCardMst3List;

        /****************************************************************
        バージョン番号を取得する場合
        ****************************************************************/

        exeContext = new ExecutionContext();

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));
        VariousDbTestHelper.setUpTable(new ExclusiveCardMst3("cad005", 5L));

        ExUserMstPk2 exUserMstPk2 = new ExUserMstPk2("uid001");
        ExCardMstPk3 exCardMstPk3 = new ExCardMstPk3("cad005");

        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, exUserMstPk2));
        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, exCardMstPk3));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(2));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001");
        assertVersionString(versions.get(1), "EXCLUSIVE_CARD_MST3", "VERSION", "5", "card_id", "cad005");

        /****************************************************************
        バージョン番号をチェックする場合(排他エラーなし)
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        // prepareVersionと異なる順で呼び出す。
        HttpExclusiveControlUtil.checkVersion(req, exeContext, exCardMstPk3);
        HttpExclusiveControlUtil.checkVersion(req, exeContext, exUserMstPk2);

        /****************************************************************
        バージョン番号をチェックする場合(排他エラーあり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 2L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        HttpExclusiveControlUtil.checkVersion(req, exeContext, exCardMstPk3);
        try {
            HttpExclusiveControlUtil.checkVersion(req, exeContext, exUserMstPk2);
            fail();
        } catch (OptimisticLockException e) {
            // success
        }

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(排他エラーなし)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        // prepareVersionと異なる順で呼び出す。
        HttpExclusiveControlUtil.updateVersionWithCheck(req, exCardMstPk3);
        HttpExclusiveControlUtil.updateVersionWithCheck(req, exUserMstPk2);
        transactionManager.commitTransaction();

        exclusiveUserMst2List = VariousDbTestHelper.findAll(ExclusiveUserMst2.class);
        assertThat(exclusiveUserMst2List.size(), is(1));
        assertThat(exclusiveUserMst2List.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMst2List.get(0).version, is(2L));

        exclusiveCardMst3List = VariousDbTestHelper.findAll(ExclusiveCardMst3.class);
        assertThat(exclusiveCardMst3List.size(), is(1));
        assertThat(exclusiveCardMst3List.get(0).cardId, is("cad005"));
        assertThat(exclusiveCardMst3List.get(0).version, is(6L));

        /****************************************************************
        バージョン番号をチェックを伴い更新する場合(排他エラーあり)
        ****************************************************************/

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));
        VariousDbTestHelper.setUpTable(new ExclusiveCardMst3("cad005", 7L));

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        HttpExclusiveControlUtil.updateVersionWithCheck(req, exUserMstPk2);
        try {
            HttpExclusiveControlUtil.updateVersionWithCheck(req, exCardMstPk3);
            fail();
        } catch (OptimisticLockException e) {
            // success
        } finally {
            transactionManager.rollbackTransaction();
        }
    }

    /**
     * MULで発生した不具合の再現。
     */
    @Test
    public void testForBugInMul() throws Exception {
        ExecutionContext exeContext;
        HttpRequest req;
        List<String> versions;
        List<ExclusiveUserMst2> exclusiveUserMst2List;
        List<ExclusiveCardMst3> exclusiveCardMst3List;

        exeContext = new ExecutionContext();

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));
        VariousDbTestHelper.setUpTable(new ExclusiveCardMst3("cad005", 5L));

        ExUserMstPk2 exUserMstPk2 = new ExUserMstPk2("uid001");
        ExCardMstPk3 exCardMstPk3 = new ExCardMstPk3("cad005");

        /****************************************************************
        GH5AAM1Action:初期表示処理
        ExUserMstPk2のみprepareVersionを呼び出す。･･･このリクエストで排他制御対象が確定する。
        ****************************************************************/

        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, exUserMstPk2));

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(1));
        assertVersionString(versions.get(0), "EXCLUSIVE_USER_MST2", "VERSION", "1", "user_id", "uid001");

        /****************************************************************
        GH5AAM1Action:確認処理(doRGH5AAM106)
        ExCardMstPk3はprepareVersionを呼び出す。･･･このリクエストで排他制御対象が確定する。
        ExUserMstPk2はcheckVersionを呼び出す。
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0));

        // 異なる排他制御クラスがprepareVersionされる。
        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, exCardMstPk3));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, exUserMstPk2);

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);

        /****************************************************************
        GH5AAM1Action:確認画面から入力画面へ戻る処理(doRGH5AAM108)
        ExUserMstPk2のみcheckVersionを呼び出す。
        排他制御対象が未確定となるためExCardMstPk3はcheckversionしない。
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        // 戻る処理は個別にcheckVersionするため、ここで #5452 によりIllegalArgumentExceptionが発生していた。
        HttpExclusiveControlUtil.checkVersion(req, exeContext, exUserMstPk2);

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);

        /****************************************************************
        GH5AAM1Action:確認処理(doRGH5AAM106)
        ExCardMstPk3はprepareVersionを呼び出す。･･･このリクエストで排他制御対象が確定する。
        ExUserMstPk2はcheckVersionを呼び出す。
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0));

        assertTrue(HttpExclusiveControlUtil.prepareVersion(exeContext, exCardMstPk3));
        HttpExclusiveControlUtil.checkVersion(req, exeContext, exUserMstPk2);

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);

        /****************************************************************
        updateVersionsWithCheckを呼び出す。
        ****************************************************************/

        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        HttpExclusiveControlUtil.updateVersionsWithCheck(req);
        transactionManager.commitTransaction();

        exclusiveUserMst2List = VariousDbTestHelper.findAll(ExclusiveUserMst2.class);
        assertThat(exclusiveUserMst2List.size(), is(1));
        assertThat(exclusiveUserMst2List.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMst2List.get(0).version, is(2L));

        exclusiveCardMst3List = VariousDbTestHelper.findAll(ExclusiveCardMst3.class);
        assertThat(exclusiveCardMst3List.size(), is(1));
        assertThat(exclusiveCardMst3List.get(0).cardId, is("cad005"));
        assertThat(exclusiveCardMst3List.get(0).version, is(6L));
    }

    /**
     * 複数テーブルを使用して排他制御を行う場合で、それらのテーブルの主キーが全く同一の定義の場合のテスト。
     *
     * 指定した排他制御テーブルを使用して排他制御が行われること。
     *
     * 本テストは、不具合「#5554」対応のために追加したテスト。
     */
    @Test
    public void testMultiTableAndSamePks() throws Exception {
        HttpRequest req;
        List<String> versions;
        List<ExclusiveUserMst2> exclusiveUserMst2List;
        List<ExclusiveUserMst3> exclusiveUserMst3List;

        VariousDbTestHelper.setUpTable(new ExclusiveUserMst2("uid001", 1L));
        VariousDbTestHelper.setUpTable(new ExclusiveUserMst3("uid001", 5L));

        ExUserMstPk2 exUserMstPk2 = new ExUserMstPk2("uid001");
        ExUserMstPk3 exUserMstPk3 = new ExUserMstPk3("uid001");

        ExecutionContext exeContext = new ExecutionContext();

        /****************************************************************
         複数テーブルのバージョン番号を準備する。
         ****************************************************************/

        HttpExclusiveControlUtil.prepareVersion(exeContext, exUserMstPk2);
        HttpExclusiveControlUtil.prepareVersion(exeContext, exUserMstPk3);

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);

        /****************************************************************
         複数テーブルのバージョン番号をチェックする。
         ****************************************************************/
        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        HttpExclusiveControlUtil.checkVersion(req, exeContext, exUserMstPk3);
        HttpExclusiveControlUtil.checkVersion(req, exeContext, exUserMstPk2);
        transactionManager.commitTransaction();

        versions = exeContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);

        // EXCLUSIVE_USER_MST3
        assertVersionString(versions.get(0), exUserMstPk3.getTableName(), exUserMstPk3.getVersionColumnName(), "5", "user_id", "uid001");
        // EXCLUSIVE_USER_MST2
        assertVersionString(versions.get(1), exUserMstPk2.getTableName(), exUserMstPk2.getVersionColumnName(), "1", "user_id", "uid001");

        /****************************************************************
         複数テーブルのバージョン番号を更新する。
         ****************************************************************/
        exeContext = new ExecutionContext();
        req = new MockHttpRequest().setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0), versions.get(1));

        //*********************************************************************
        // EXCLUSIVE_USER_MST2
        //*********************************************************************
        HttpExclusiveControlUtil.updateVersionWithCheck(req, exUserMstPk2);
        transactionManager.commitTransaction();

        //*********************************************************************
        // 対応するテーブルのバージョン番号が正しく更新されていることを確認
        //*********************************************************************
        exclusiveUserMst2List = VariousDbTestHelper.findAll(ExclusiveUserMst2.class);
        assertThat(exclusiveUserMst2List.size(), is(1));
        assertThat(exclusiveUserMst2List.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMst2List.get(0).version, is(2L));

        //*********************************************************************
        // EXCLUSIVE_USER_MST3
        //*********************************************************************
        HttpExclusiveControlUtil.updateVersionWithCheck(req, exUserMstPk3);
        transactionManager.commitTransaction();

        exclusiveUserMst3List = VariousDbTestHelper.findAll(ExclusiveUserMst3.class);
        assertThat(exclusiveUserMst3List.size(), is(1));
        assertThat(exclusiveUserMst3List.get(0).userId, is("uid001"));
        assertThat(exclusiveUserMst3List.get(0).version, is(6L));
    }

    /**
     * バージョンの取得処理が行われずにチェックバージョンが呼ばれた場合のテスト。
     * <p/>
     * 例えば、ラウンドロビン方式やフェイルオーバ発生時にこのようなケースが想定される。
     * このようなケースの場合で、キャッシュ上に該当排他制御テーブルへアクセスするためのSQL文が存在しない場合でも、
     * 正しくSQL文が構築され排他制御処理が行わることを確認する。
     */
    @Test
    public void testCheckAndUpdate() throws Exception {

    	VariousDbTestHelper.setUpTable(new ExclusiveTest1(1L, 100L));

        ExclusiveTest1Pk exclusiveTest1 = new ExclusiveTest1Pk("1");
        ExecutionContext executionContext = new ExecutionContext();

        //----------------------------------------------------------------------
        // バージョンのチェック処理
        //----------------------------------------------------------------------
        String exclusiveParams = versionString(
                exclusiveTest1.getTableName(), exclusiveTest1.getVersionColumnName(), "100", "id", "1");
        HttpRequest request = new MockHttpRequest();
        request.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, exclusiveParams);
        HttpExclusiveControlUtil.checkVersions(request, executionContext);

        //----------------------------------------------------------------------
        // リクエストスコープに格納された排他制御の引き継ぎ情報をアサート
        //----------------------------------------------------------------------
        List<String> versions = executionContext.getRequestScopedVar(HttpExclusiveControlUtil.VERSIONS_VARIABLE_NAME);
        assertThat(versions.size(), is(1));
        String[] version = versions.get(0).split("\\|");
        assertThat(version.length, is(4));
        assertThat("table name", version[0], is("tableName=EXCLUSIVE_TEST_1"));
        assertThat("version column name", version[1], is("versionColumnName=VERSION"));
        assertThat("primary keys", version[2], is("primaryKeys=id\\=1"));
        assertThat("version no", version[3], is("version=100"));

        //----------------------------------------------------------------------
        // バージョンのアップデート処理
        //----------------------------------------------------------------------
        HttpRequest updateRequest = new MockHttpRequest();
        updateRequest.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versions.get(0));
        HttpExclusiveControlUtil.updateVersionWithCheck(request, exclusiveTest1);
        transactionManager.commitTransaction();

        List<ExclusiveTest1> exclusiveTest1List = VariousDbTestHelper.findAll(ExclusiveTest1.class);
        assertThat(exclusiveTest1List.size(), is(1));
        assertThat(exclusiveTest1List.get(0).id, is(1L));
        assertThat(exclusiveTest1List.get(0).version, is(101L));
    }

    /**
     * バージョンの取得処理が行われずにチェックバージョンが呼ばれた場合のテスト。
     * <p/>
     * 例えば、ラウンドロビン方式やフェイルオーバ発生時にこのようなケースが想定される。
     * このようなケースの場合で、キャッシュ上に該当排他制御テーブルへアクセスするためのSQL文が存在しない場合でも、
     * 正しくSQL文が構築され排他制御処理が行わることを確認する。
     */
    @Test
    public void testUpdateOnly() throws Exception {

    	VariousDbTestHelper.setUpTable(new ExclusiveTest2(100L, 500L));

        ExclusiveTest2Pk exclusiveTest2 = new ExclusiveTest2Pk("1");

        String versionParam = versionString(
                exclusiveTest2.getTableName(), exclusiveTest2.getVersionColumnName(), "500", "id", "100");
        HttpRequest request = new MockHttpRequest();
        request.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versionParam);
        HttpExclusiveControlUtil.updateVersionsWithCheck(request);
        transactionManager.commitTransaction();

        List<ExclusiveTest2> exclusiveTest2List = VariousDbTestHelper.findAll(ExclusiveTest2.class);
        assertThat(exclusiveTest2List.size(), is(1));
        assertThat(exclusiveTest2List.get(0).id, is(100L));
        assertThat(exclusiveTest2List.get(0).version, is(501L));

        versionParam = versionString(
                exclusiveTest2.getTableName(), exclusiveTest2.getVersionColumnName(), "501", "id", "100");

        request = new MockHttpRequest();
        request.setParam(HttpExclusiveControlUtil.VERSION_PARAM_NAME, versionParam);
        request.setParam("id", "100");
        HttpExclusiveControlUtil.updateVersionsWithCheck(request, "id");
        transactionManager.commitTransaction();

        exclusiveTest2List = VariousDbTestHelper.findAll(ExclusiveTest2.class);
        assertThat(exclusiveTest2List.size(), is(1));
        assertThat(exclusiveTest2List.get(0).id, is(100L));
        assertThat(exclusiveTest2List.get(0).version, is(502L));
    }

    private void assertContainsAll(String[] expected, String actual) {
        for (String e : expected) {
            assertThat(actual, containsString(e));
        }
    }

    private void assertVersionString(String actual, String tableName, String versionColumnName, String version, String... primaryKeys) {
        assertThat(actual, containsString("tableName=" + tableName));
        assertThat(actual, containsString("version=" + version));
        for (int i = 0; i < primaryKeys.length; i += 2) {
            assertThat(actual, containsString(primaryKeys[i] + "\\=" + primaryKeys[i + 1]));
        }
    }

    private String versionString(String tableName, String versionColumnName, String version, String... primaryKeys) {
        StringBuilder sb = new StringBuilder();
        sb.append("tableName=").append(tableName).append("|")
          .append("versionColumnName=").append(versionColumnName).append("|")
          .append("version=").append(version).append("|")
          .append("primaryKeys=");
        for (int i = 0; i < primaryKeys.length; i += 2) {
            if (i != 0) {
                sb.append("\\|");
            }
            sb.append(primaryKeys[i] + "\\=" + primaryKeys[i + 1]);
        }
        return sb.toString();
    }

}
