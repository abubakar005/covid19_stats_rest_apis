package covid19.statistics.reports;

import covid19.statistics.reports.util.CustomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportsApplication {

	public static Logger LOG = LoggerFactory.getLogger(ReportsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReportsApplication.class, args);

		LOG.info("Started Loading Application Data.....");
		CustomUtil.loadData();
		LOG.info("Application data loaded successfully");
		CustomUtil.loadUsersList();
		LOG.info("Application Users loaded successfully");
	}

}
