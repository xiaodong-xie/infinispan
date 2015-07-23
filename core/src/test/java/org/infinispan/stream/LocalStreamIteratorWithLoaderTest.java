package org.infinispan.stream;

import org.infinispan.configuration.cache.CacheMode;
import org.testng.annotations.Test;

/**
 * Test to verify local stream behavior when a loader is present
 *
 * @author afield
 * @author wburns
 * @since 8.0
 */
@Test(groups = "functional", testName = "iteration.LocalStreamIteratorWithLoaderTest")
public class LocalStreamIteratorWithLoaderTest extends BaseStreamIteratorWithLoaderTest {

   public LocalStreamIteratorWithLoaderTest() {
      super(false, CacheMode.LOCAL, "LocalStreamIteratorWithLoaderTest");
   }
}
