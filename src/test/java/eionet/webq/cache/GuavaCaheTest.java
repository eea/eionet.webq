package eionet.webq.cache;

import com.google.common.cache.CacheBuilderSpec;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.Cache;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public class GuavaCaheTest {
    
    @Test
    public void testCacheItem() {
        final String itemKey = "test key";
        final String itemValue = "test value";
        
        GuavaCacheManager manager = new GuavaCacheManager();
        manager.setCacheBuilderSpec(CacheBuilderSpec.parse("expireAfterWrite=1m"));
        Cache testCache = manager.getCache("testCache");
        
        testCache.put(itemKey, itemValue);
        Assert.assertEquals(itemValue, testCache.get(itemKey).get());
    }
    
    @Test
    public void testCacheInvalidation() throws InterruptedException {
        final String itemKey = "test key";
        final String itemValue = "test value";
        final int cacheTtlSeconds = 4;
        
        GuavaCacheManager manager = new GuavaCacheManager();
        manager.setCacheBuilderSpec(CacheBuilderSpec.parse(String.format("expireAfterWrite=%ds", cacheTtlSeconds)));
        Cache testCache = manager.getCache("testCache");
        
        testCache.put(itemKey, itemValue);
        Assert.assertEquals(itemValue, testCache.get(itemKey).get());
        
        Thread.sleep((cacheTtlSeconds + 2) * 1000);
        Assert.assertNull(testCache.get(itemKey));
    }
    
}
