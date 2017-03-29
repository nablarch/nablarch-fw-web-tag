package nablarch.common.web.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nablarch.core.repository.SystemRepository;
import nablarch.test.core.log.LogVerifier;
import nablarch.test.support.SystemRepositoryResource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class ServiceAvailabilityDisplayControlCheckerTest {

    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource(
            "nablarch/common/web/tag/ServiceAvailabilityDisplayControlCheckerTest.xml");

    private static final String[][] APPLIED_REQUEST = {
            {"REQ0000001", "1"}
    };

    @Test
    public void testServiceAvailabilityDisplayControlChecker() {
        LogVerifier.clear();
        LogVerifier.setExpectedLogMessages(new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("logLevel", "INFO");
                put("message1",
                        "Failed to pre-check the availability of business services. "
                                + "It is needed for determining appearance of some buttons and links. "
                                + "(They are showed in ordinal appearance.)");
            }});
        }});

        repositoryResource.getComponentByType(MockServiceAvailability.class)
                          .setRequest(APPLIED_REQUEST);

        ServiceAvailabilityDisplayControlChecker checker = new ServiceAvailabilityDisplayControlChecker();
        Assert.assertFalse(checker.needsDisplayControl("REQ0000001"));
        Assert.assertTrue(checker.needsDisplayControl("unknown"));
        repositoryResource.getComponentByType(MockServiceAvailability.class)
                          .dropRequest();
        LogVerifier.clear();
        Assert.assertTrue(checker.needsDisplayControl("REQ0000001"));
        LogVerifier.verify("開閉局状態の事前チェックに失敗した旨のログがでる。(INFOレベル)");
    }


    /** connection */

    @BeforeClass
    public static void classSetup() throws Exception {
    }

    @AfterClass
    public static void classDown() throws Exception {

        SystemRepository.clear();
    }
}
