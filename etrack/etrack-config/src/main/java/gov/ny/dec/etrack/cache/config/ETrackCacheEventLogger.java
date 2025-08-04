package gov.ny.dec.etrack.cache.config;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETrackCacheEventLogger implements CacheEventListener<Object, Object> {

  private static final Logger logger =
      LoggerFactory.getLogger(ETrackCacheEventLogger.class.getName());

  @Override
  public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
    logger.info("Cache Event {} " , cacheEvent.getKey());
  }
}
