package covid19.statistics.reports.job;

import covid19.statistics.reports.util.CustomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class scheduler {

    public static Logger LOG = LoggerFactory.getLogger(scheduler.class);

    @Scheduled(cron = "0 0 1 * * *")    // It will run at 1:00 AM every day
    public void cronJobSch() {
        LOG.info("----------Job started------------------ ");
        CustomUtil.loadData();
        LOG.info("Application data loaded successfully at " + LocalDateTime.now());
        LOG.info("-------------Job End ------------------ ");
    }
}
