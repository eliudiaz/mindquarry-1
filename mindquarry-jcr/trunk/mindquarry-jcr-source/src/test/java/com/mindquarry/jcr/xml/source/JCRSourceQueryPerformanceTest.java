/**
 * Copyright (C) 2006 MindQuarry GmbH, All Rights Reserved
 */
package com.mindquarry.jcr.xml.source;

import java.io.OutputStream;
import java.util.Collection;

import org.apache.excalibur.source.ModifiableSource;

/**
 * Test cases for the JCRNodeWrapperSource implementation.
 * 
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 */
public class JCRSourceQueryPerformanceTest extends JCRSourceTestBase {

    public void testQueryInLargeRepository() throws Exception {

        for (int i = 0; i < 100; i++) {
            JCRNodeWrapperSource dummySource = (JCRNodeWrapperSource) resolveSource(BASE_URL
                    + "users/" + i);
            assertNotNull(dummySource);

            OutputStream os = ((ModifiableSource) dummySource)
                    .getOutputStream();
            assertNotNull(os);

            String dummyContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><user><id>"
                    + i + "</id></user>";
            os.write(dummyContent.getBytes());
            os.flush();
            os.close();
        }
        long startTime = System.currentTimeMillis();
        QueryResultSource qResult = (QueryResultSource) resolveSource(BASE_URL
                + "users?/*[.//user/id='67']");

        long testTime = System.currentTimeMillis() - startTime;
        System.out.println("Test time: " + testTime + " ms");
        assertNotNull(qResult);

        Collection results = qResult.getChildren();
        assertEquals(1, results.size());
    }
}
