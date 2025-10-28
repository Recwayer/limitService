package ru.limit.scheduler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.limit.service.LimitService;


@Component
@ConditionalOnProperty(
        name = "app.scheduler.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class LimitScheduler {
    private static final Logger log = LoggerFactory.getLogger(LimitScheduler.class);
    private final LimitService limitService;

    public LimitScheduler(LimitService limitService) {
        this.limitService = limitService;
    }

    @Scheduled(cron = "${app.scheduler.reset-cron:0 0 0 * * ?}")
    public void resetAllLimitsAtMidnight() {
        try {
            log.info("Starting scheduled reset of all limits");
            limitService.resetAllLimits();
            log.info("Successfully reset all limits to default values");
        } catch (Exception e) {
            log.error("Failed to reset limits during scheduled task", e);
        }
    }

    @Scheduled(cron = "${app.scheduler.cleanup-cron:0 */15 * * * *}")
    public void cleanupExpiredReservations() {
        try {
            log.info("Starting cleanup of expired reservations");
            limitService.cleanupExpiredReservations();
            log.info("Successfully cleaned up expired reservations");
        } catch (Exception e) {
            log.error("Failed to cleanup expired reservations", e);
        }
    }
}
