package covid19.statistics.reports.controller;

import covid19.statistics.reports.dto.AuthenticationRequest;
import covid19.statistics.reports.dto.AuthenticationResponse;
import covid19.statistics.reports.service.Covid19ReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class Covid19ReportsResource {

    public Logger LOG = LoggerFactory.getLogger(Covid19ReportsResource.class);

    private final Covid19ReportsService covid19ReportsService;

    public Covid19ReportsResource(Covid19ReportsService covid19ReportsService) {
        this.covid19ReportsService = covid19ReportsService;
    }


    @PostMapping(value = "/jwt")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        return ResponseEntity.ok(new AuthenticationResponse(covid19ReportsService.generateToken(authenticationRequest)));
    }

    @GetMapping("/active-users")
    public ResponseEntity<List<String>> listOfActiveUsers(){
        return ResponseEntity.ok(covid19ReportsService.getActiveUsers());
    }

    @GetMapping({"/covid19/today/cases"})
    public ResponseEntity<String[]> totalCasesReportedToday() {
        return ResponseEntity.ok(covid19ReportsService.totalCasesReportedToday());
    }

    @GetMapping({"/covid19/countries/cases"})
    public ResponseEntity<List<String[]>> casesReportedTodayByAllCountries() {
        return ResponseEntity.ok(covid19ReportsService.casesReportedTodayByAllCountries());
    }

    @GetMapping({"/covid19/country/{countryName}/cases"})
    public ResponseEntity<List<String[]>> casesReportedByCountry(@PathVariable (value = "countryName") String countryName,
                                                                      @RequestParam (value = "sinceDate", required = false) String sinceDate){
        if(StringUtils.hasText(sinceDate))
            return ResponseEntity.ok(covid19ReportsService.casesReportedByCountrySinceDate(countryName, sinceDate));
        return ResponseEntity.ok(covid19ReportsService.casesReportedTodayByCountry(countryName));
    }

    @GetMapping(value = "/covid19/{countries-count}/countries/cases")
    public ResponseEntity<List<String[]>> casesReportedOfSelectedCountries(@PathVariable (value = "countries-count") int countriesCountToShow){
        return ResponseEntity.ok(covid19ReportsService.casesReportedOfSelectedCountries(countriesCountToShow));
    }
}
