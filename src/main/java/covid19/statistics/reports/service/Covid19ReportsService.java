package covid19.statistics.reports.service;

import covid19.statistics.reports.dto.AuthenticationRequest;
import covid19.statistics.reports.util.Constants;
import covid19.statistics.reports.util.CustomUtil;
import covid19.statistics.reports.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Covid19ReportsService {

    public Logger LOG = LoggerFactory.getLogger(Covid19ReportsService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;


    /**
     *  Generate Token method.
     *  @param authenticationRequest for getting user name and password
     *  @return the generated token
     */

    public String generateToken(AuthenticationRequest authenticationRequest) throws Exception {

        LOG.info("#Covid19ReportsService generateToken() called");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final String jwt = jwtTokenUtil.generateToken(userDetailsService.loadUserByUsername(authenticationRequest.getUsername()));

        // Adding in the active Users list
        CustomUtil.addUserInActiveList(jwt, authenticationRequest.getUsername());

        return jwt;
    }

    /**
     *  getActiveUserList
     *  @return the active users list
     */

    public List<String> getActiveUsers() {

        LOG.info("#Covid19ReportsService getActiveUsers() called");
        return CustomUtil.getActiveUserList();
    }

    /**
     *  totalCasesReportedToday
     *  @return the total Cases Reported Today
     */

    public String[] totalCasesReportedToday() {

        LOG.info("#Covid19ReportsService totalCasesReportedToday() called");
        String[] resultArray = {};
        int dateIndex = Constants.INT_ZERO;
        String date = null;
        int totalCases = Constants.INT_ZERO;


        try {
            // Getting formatted date according to the data set
            date = CustomUtil.getTodayFormattedDate();
            LOG.info("Today's Date: " + date);

            // Getting date value index [Header column]
            dateIndex = CustomUtil.getHeaderIndexByDate(date);
            LOG.info("Searched Column Index: " + dateIndex);

            if(dateIndex > Constants.INT_ZERO) {

                // Adding all cases
                for(int index=Constants.INT_ONE; index<CustomUtil.covid19ConfirmedGlobalCasesList.size(); index++)
                    totalCases = totalCases + CustomUtil.getLatestRecordsSinceDate(index, dateIndex-Constants.INT_ONE);

                resultArray = new String[Constants.INT_TWO];
                resultArray[Constants.INT_ZERO] = CustomUtil.getResultedStringWithColon(Constants.RESULT_DATE, date);
                resultArray[Constants.INT_ONE] = CustomUtil.getResultedStringWithColon(Constants.TOTAL_CASES_REPORTED, String.valueOf(totalCases));
            }
        } catch (Exception e) {
            LOG.error("Error while getting today's records " + e);
        }

        return resultArray;
    }

    /**
     *  casesReportedTodayByAllCountries
     *  @return the total Cases Reported Today all countries
     */

    public List<String[]> casesReportedTodayByAllCountries() {

        LOG.info("#Covid19ReportsService casesReportedTodayByAllCountries() called");
        List<String[]> casesReportedToday = new ArrayList<String[]>();
        String[] resultArray = null;

        try {

            // Getting sorted list by most number of cases
            List<List<String>> sortedList = CustomUtil.getOrderedListByMostCases();
            int listSize = sortedList.size();

            // Putting/ fetching required data in the result list
            for(int index=Constants.INT_ONE; index<listSize; index++) {
                resultArray = new String[Constants.INT_THREE];
                resultArray[Constants.INT_ZERO] = CustomUtil.getProvinceNameFromList(sortedList, index);
                resultArray[Constants.INT_ONE] = CustomUtil.getCountryNameFromList(sortedList, index);
                resultArray[Constants.INT_TWO] = CustomUtil.getResultedStringWithColon(Constants.CASES_REPORTED, String.valueOf(CustomUtil.getLatestRecordsFromList(sortedList.get(index))));

                casesReportedToday.add(resultArray);
            }
        } catch (Exception e) {
            LOG.error("Error while getting today's records of all countries " + e);
        }

        return casesReportedToday;
    }

    /**
     *  casesReportedTodayByCountry
     *  @param countryName for getting country name
     *  @return the cases reported detail
     */

    public List<String[]> casesReportedTodayByCountry(String countryName){

        LOG.info("#Covid19ReportsService casesReportedTodayByCountry() called");
        List<String[]> casesReportedInCountryToday = new ArrayList<String[]>();
        int dateIndex = Constants.INT_ZERO;
        String date = null;
        List<Integer> countryList = null;

        try {
            // Getting formatted date according to the data set
            date = CustomUtil.getTodayFormattedDate();
            LOG.info("Today's Date: " + date);
            LOG.info("Country Name: " + countryName);

            // Getting date value index
            dateIndex = CustomUtil.getHeaderIndexByDate(date);
            LOG.info("Searched Column Index: " + dateIndex);

            // Getting country indices list by country name
            countryList = CustomUtil.getRowIndicesByCountry(countryName);
            LOG.info("Country Indices: " + countryList.size());

            if(dateIndex>0 && countryList.size() > Constants.INT_ZERO) {

                String[] resultArray = null;

                // Putting/ fetching required data in the result list
                for(int index=Constants.INT_ZERO; index<countryList.size(); index++) {
                    resultArray = new String[Constants.INT_THREE];
                    resultArray[Constants.INT_ZERO] = CustomUtil.getProvinceName(countryList.get(index));
                    resultArray[Constants.INT_ONE] = CustomUtil.getCountryName(countryList.get(index));
                    resultArray[Constants.INT_TWO] = CustomUtil.getResultedStringWithColon(Constants.CASES_REPORTED, String.valueOf(CustomUtil.getLatestRecordsSinceDate(countryList.get(index), dateIndex-Constants.INT_ONE)));

                    casesReportedInCountryToday.add(resultArray);
                }
            }
        } catch (Exception e) {
            LOG.error("Error while getting today's records " + e);
        }

        return casesReportedInCountryToday;
    }

    /**
     *  casesReportedByCountrySinceDate
     *  @param countryName for getting country name
     *  @param sinceDate for getting since date
     *  @return the cases reported detail since date
     */

    public List<String[]> casesReportedByCountrySinceDate(String countryName, String sinceDate){

        LOG.info("#Covid19ReportsService casesReportedByCountrySinceDate() called");
        List<String[]> casesReportedSinceDate = new ArrayList<String[]>();
        int dateIndex = Constants.INT_ZERO;
        List<Integer> countryList = null;

        try {
            // Expecting formatted date according to the data set
            LOG.info("Since Date: " + sinceDate);
            LOG.info("Country Name: " + countryName);

            // Getting date value index
            dateIndex = CustomUtil.getHeaderIndexByDate(sinceDate);
            LOG.info("Searched Column Index: " + dateIndex);

            // Getting country indices list by country name
            countryList = CustomUtil.getRowIndicesByCountry(countryName);
            LOG.info("Country Indices: " + countryList.size());

            if(dateIndex > Constants.INT_ZERO && dateIndex > Constants.INT_ZERO) {

                String[] resultArray = null;

                // Putting/ fetching required data in the result list
                for(int index=Constants.INT_ZERO; index<countryList.size(); index++) {
                    resultArray = new String[Constants.INT_FOUR];
                    resultArray[Constants.INT_ZERO] = CustomUtil.getProvinceName(countryList.get(index));
                    resultArray[Constants.INT_ONE] = CustomUtil.getCountryName(countryList.get(index));
                    resultArray[Constants.INT_TWO] = CustomUtil.getResultedStringWithColon(Constants.SINCE_DATE, sinceDate);
                    resultArray[Constants.INT_THREE] = CustomUtil.getResultedStringWithColon(Constants.TOTAL_CASES_REPORTED, String.valueOf(CustomUtil.getLatestRecordsSinceDate(countryList.get(index), dateIndex)));

                    casesReportedSinceDate.add(resultArray);
                }
            }
        } catch (Exception e) {
            LOG.error("Error while getting today's records " + e);
        }

        return casesReportedSinceDate;
    }

    /**
     *  casesReportedOfSelectedCountries method
     *  @param countriesCountToShow for number of countries to show
     *  @return the cases reported detail of selected countries in desc order
     */

    public List<String[]> casesReportedOfSelectedCountries(int countriesCountToShow){

        LOG.info("#Covid19ReportsService casesReportedOfSelectedCountries() called");
        List<String[]> mostCasesReportedToday = new ArrayList<String[]>();
        String[] resultArray = null;

        try {

            // Getting sorted list by most number of cases
            List<List<String>> sortedList = CustomUtil.getOrderedListByMostCases();
            int listSize = sortedList.size();

            // Putting/ fetching required data in the result list
            for(int index=Constants.INT_ONE; index<listSize; index++) {
                resultArray = new String[Constants.INT_THREE];
                resultArray[Constants.INT_ZERO] = CustomUtil.getProvinceNameFromList(sortedList, index);
                resultArray[Constants.INT_ONE] = CustomUtil.getCountryNameFromList(sortedList, index);
                resultArray[Constants.INT_TWO] = CustomUtil.getResultedStringWithColon(Constants.CASES_REPORTED, String.valueOf(CustomUtil.getLatestRecordsFromList(sortedList.get(index))));

                mostCasesReportedToday.add(resultArray);

                if(countriesCountToShow == index)
                    break;
            }
        } catch (Exception e) {
            LOG.error("Error while getting selected countries records " + e);
        }

        return mostCasesReportedToday;
    }
}
