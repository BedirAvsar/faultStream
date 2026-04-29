package com.faultstream.domain.dashboard;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
@Service
public class DashboardCacheService {
    @CacheEvict(cacheNames = "dashboardTerminal", allEntries = true)
    public void evictTerminalSnapshot() {
    }
}
