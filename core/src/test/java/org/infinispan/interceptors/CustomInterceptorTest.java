package org.infinispan.interceptors;

import static org.infinispan.test.TestingUtil.withCacheManager;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.List;

import org.infinispan.Cache;
import org.infinispan.commons.CacheConfigurationException;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.InterceptorConfiguration.Position;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.test.AbstractInfinispanTest;
import org.infinispan.test.CacheManagerCallable;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.Test;

@Test(groups = "functional", testName = "interceptors.CustomInterceptorTest")
public class CustomInterceptorTest extends AbstractInfinispanTest {

   public void testCustomInterceptorProperties() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.customInterceptors().addInterceptor().interceptor(new FooInterceptor()).position(Position.FIRST).addProperty("foo", "bar");
      withCacheManager(new CacheManagerCallable(
            TestCacheManagerFactory.createCacheManager(builder)) {
         @Override
         public void call() {
            final Cache<Object,Object> cache = cm.getCache();
            AsyncInterceptor i =
                  cache.getAdvancedCache().getAsyncInterceptorChain().getInterceptors().get(0);
            assertTrue("Expecting FooInterceptor in the interceptor chain", i instanceof FooInterceptor);
            assertEquals("bar", ((FooInterceptor)i).getFoo());
         }
      });
   }

   @Test(expectedExceptions=CacheConfigurationException.class)
   public void testMissingPosition() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.customInterceptors().addInterceptor().interceptor(new FooInterceptor());
      TestCacheManagerFactory.createCacheManager(builder);
   }

   public void testLastInterceptor() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.customInterceptors().addInterceptor().position(Position.LAST).interceptor(new FooInterceptor());
      final EmbeddedCacheManager cacheManager = TestCacheManagerFactory.createCacheManager();
      cacheManager.defineConfiguration("interceptors", builder.build());
      withCacheManager(new CacheManagerCallable(cacheManager) {
         @Override
         public void call() {
            List<AsyncInterceptor> interceptors =
                  cacheManager.getCache("interceptors").getAdvancedCache().getAsyncInterceptorChain()
                              .getInterceptors();
            assertEquals(FooInterceptor.class, interceptors.get(interceptors.size() - 2).getClass());
         }
      });
   }

   public void testOtherThanFirstOrLastInterceptor() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.customInterceptors().addInterceptor().position(Position.OTHER_THAN_FIRST_OR_LAST).interceptor(new FooInterceptor());
      final EmbeddedCacheManager cacheManager = TestCacheManagerFactory.createCacheManager();
      cacheManager.defineConfiguration("interceptors", builder.build());
      withCacheManager(new CacheManagerCallable(cacheManager) {
         @Override
         public void call() {
            AsyncInterceptorChain
                  interceptorChain = cacheManager.getCache("interceptors").getAdvancedCache().getAsyncInterceptorChain();
            assertEquals(interceptorChain.getInterceptors().get(1).getClass(), FooInterceptor.class);
         }
      });
   }

   public void testLastInterceptorDefaultCache() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      final FooInterceptor interceptor = new FooInterceptor();
      builder.customInterceptors().addInterceptor().position(Position.LAST).interceptor(interceptor);
      final EmbeddedCacheManager cacheManager = TestCacheManagerFactory.createCacheManager(builder);
      withCacheManager(new CacheManagerCallable(cacheManager) {
         @Override
         public void call() {
            List<AsyncInterceptor> interceptors =
                  cacheManager.getCache().getAdvancedCache().getAsyncInterceptorChain()
                              .getInterceptors();
            Object o = interceptors.get(interceptors.size() - 2);
            assertEquals(FooInterceptor.class, o.getClass());
            assertFalse(interceptor.putInvoked);
            cacheManager.getCache().put("k", "v");
            assertEquals("v", cacheManager.getCache().get("k"));
            assertTrue(interceptor.putInvoked);
         }
      });
   }

}
