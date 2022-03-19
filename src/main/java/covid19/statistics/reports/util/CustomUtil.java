package covid19.statistics.reports.util;

import covid19.statistics.reports.dto.UserDetail;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

public class CustomUtil {

    public static Logger LOG = LoggerFactory.getLogger(CustomUtil.class);

    // Stores all COVID19 Cases Records
    public static final List<List<String>> covid19ConfirmedGlobalCasesList = new ArrayList<List<String>>();

    // Stores all users of APIs
    public static final Map<String, String> usersList = new HashMap<String, String>();

    // Stores users after token generation
    public static final Map<String, UserDetail> activeUsersList = new HashMap<String,UserDetail>();


    public static String getTagValue(String text, String tagName) {

        String res = "";
        String[] array = null;

        try {

            if(hasText(text) && text.contains(tagName))
                array = text.split("<"+tagName+">");

            if(!array[1].trim().startsWith("</"+tagName+">"))
                res = array[1].split("</"+tagName+">")[Constants.INT_ZERO];
        } catch(Exception e){
            LOG.error("Exception occurred while getting NULL tag value: " + e);
        }

        return res;
    }

    public static String getTagValue(String text, int rowNum) {

        String res = "";
        String strRegex = null;

        try {

            strRegex = "<td id=\"LC" + rowNum + "\" class=\"blob-code blob-code-inner js-file-line\">";
            res = text.split(strRegex)[Constants.INT_ONE].split("</" + Constants.TAG_TD + ">")[Constants.INT_ZERO];

        } catch(Exception e){
            LOG.error("Exception occurred while getting NULL tag value: " + e);
        }

        return res;
    }

    public static String getFormattedDate(LocalDateTime localDateTime) {

        LOG.info("#CustomUtil getFormattedDate() called");
        DateTimeFormatter dateTimeFormatter = null;
        String formattedDate = null;

        try {
            dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
            String date = dateTimeFormatter.format(localDateTime);
            formattedDate = Integer.valueOf(date.substring(Constants.INT_ZERO,Constants.INT_TWO))+"/"+Integer.valueOf(date.substring(Constants.INT_THREE,Constants.INT_FIVE))+date.substring(Constants.INT_FIVE);
        } catch (Exception e) {
            LOG.error("Error while date formatting " + e);
        }

        return formattedDate;
    }

    public static void addUserInActiveList(String jwt, String username) {

        LOG.info("#CustomUtil addUserInActiveList() called");
        UserDetail user = Optional.ofNullable(activeUsersList.get(username)).map(au -> {
            au.setDateUpdate(LocalDateTime.now());
            return au;
        }).orElseGet(()-> {
            UserDetail newUser = new UserDetail();
            newUser.setUserName(username);
            return newUser;
        });

        user.setJwtCreationTime(LocalDateTime.now());
        user.setJwtToken(jwt);

        activeUsersList.put(username, user);
    }

    public static List<String> getActiveUserList() {

        LOG.info("#CustomUtil getActiveUserList() called");
        List<String> list = activeUsersList.entrySet().stream().filter( e ->
                isjwtTokenValid(e.getValue().getJwtCreationTime())
        ).map(Map.Entry::getKey).collect(Collectors.toList());

        return list;
    }

    public static boolean isjwtTokenValid(LocalDateTime localDateTime) {

        localDateTime = localDateTime.plusSeconds(Constants.JWT_TOKEN_VALIDITY_SECONDS);

        if(localDateTime.isAfter(LocalDateTime.now()))
            return true;

        return false;
    }

    public static String getTodayFormattedDate() {
        return getFormattedDate(LocalDateTime.now().minusDays(Constants.INT_ONE));
    }

    public static String getProvinceName(int rowIndex) {
        return getResultedStringWithColon(Constants.PROVINCE, covid19ConfirmedGlobalCasesList.get(rowIndex).get(Constants.INT_ZERO));
    }

    public static String getCountryName(int rowIndex) {
        return getResultedStringWithColon(Constants.COUNTRY, covid19ConfirmedGlobalCasesList.get(rowIndex).get(Constants.INT_ONE));
    }

    public static String getProvinceNameFromList(List<List<String>> list, int rowIndex) {
        return getResultedStringWithColon(Constants.PROVINCE, list.get(rowIndex).get(Constants.INT_ZERO));
    }

    public static String getCountryNameFromList(List<List<String>> list, int rowIndex) {
        return getResultedStringWithColon(Constants.COUNTRY, list.get(rowIndex).get(Constants.INT_ONE));
    }

    public static String getResultedStringWithColon(String str1, String str2) {
        return str1+" : "+str2;
    }

    public static int getHeaderColumnSize() {
        return covid19ConfirmedGlobalCasesList.get(Constants.INT_ZERO).size();
    }

    public static int getHeaderIndexByDate(String date) {

        LOG.info("#CustomUtil getHeaderIndexByDate() called");
        int columnIndex = 0;
        List<String> header = CustomUtil.covid19ConfirmedGlobalCasesList.get(Constants.INT_ZERO);

        for(int index=Constants.INT_ZERO; index<header.size(); index++) {

            if(header.get(index).contains(date)) {
                columnIndex = index;
                break;
            }
        }

        return columnIndex;
    }

    public static int getLatestRecordsSinceDate(int rowIndex, int columnIndex) {

        int sinceDateCount = Integer.valueOf(CustomUtil.covid19ConfirmedGlobalCasesList.get(rowIndex).get(columnIndex));
        int lastDateCount = Integer.valueOf(CustomUtil.covid19ConfirmedGlobalCasesList.get(rowIndex).get(getHeaderColumnSize()-1));

        return lastDateCount -sinceDateCount;
    }

    public static int getLatestRecordsFromList(List<String> list) {
        int size = list.size();
        return Integer.valueOf(list.get(size-Constants.INT_ONE)) - Integer.valueOf(list.get(size-Constants.INT_TWO));
    }

    public static List<List<String>> getOrderedListByMostCases() {

        List<List<String>> list = covid19ConfirmedGlobalCasesList;

        Collections.sort(list, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> list1, List<String> list2) {

                // Ignoring Header column for sorting
                if(list1.get(Constants.INT_ZERO).equals(Constants.PROVINCE) || list2.get(Constants.INT_ZERO).equals(Constants.PROVINCE))
                    return Constants.INT_ZERO;

                int latestCases1 = getLatestRecordsFromList(list1);
                int latestCases2 = getLatestRecordsFromList(list2);

                if(latestCases2 > latestCases1)
                    return Constants.INT_ONE;
                if(latestCases2 < latestCases1)
                    return Constants.INT_MINUS_ONE;

                return Constants.INT_ZERO;
            }
        });

        return list;
    }

    public static List<Integer> getRowIndicesByCountry(String countryName) {

        LOG.info("#CustomUtil getRowIndicesByCountry() called");
        List<Integer> rowIndicesList = new ArrayList<Integer>();
        int rowSize = covid19ConfirmedGlobalCasesList.size();
        String country = null;

        for(int index=Constants.INT_ONE; index<rowSize; index++) {
            country = CustomUtil.covid19ConfirmedGlobalCasesList.get(index).get(Constants.INT_ONE);
            if(country.equalsIgnoreCase(countryName)) {
                rowIndicesList.add(index);
            }
        }

        return rowIndicesList;
    }

    // Loading data of Covid-19 cases
    public static void loadData() {

        LOG.info("APIsEndpoint loadData() called");
        covid19ConfirmedGlobalCasesList.clear();
        Document doc = null;
        String splitString = null;
        int rowNum = Constants.INT_ZERO;
        String rowData = null;
        String dataValue = null;
        String[] rowDataInArray = null;
        String tag = null;

        try {

            doc = Jsoup.connect("https://github.com/CSSEGISandData/COVID-19/blob/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv").get();
            LOG.info("Page Title: " + doc.title());

            // Getting Table
            Elements resultTable = doc.getElementsByTag(Constants.TAG_TABLE);
            String strTable = resultTable.toString();

            // Splitting table on the basis of table row - tr
            String[] totalRows = strTable.split(Constants.REGEX_TAG_TR);
            LOG.info("Total Records: " + totalRows.length);

            for(int index=Constants.INT_ZERO; index<totalRows.length-Constants.INT_ONE; index++) {

                rowNum = Constants.INT_ONE+index;
                splitString = "data-line-number=\"" + rowNum + "\"></" + Constants.TAG_TD + ">";

                // Splitting table on the basis of table data - td [removing extra data from the original]
                rowData = totalRows[index].split(splitString)[Constants.INT_ONE];
                rowDataInArray = rowData.split(Constants.ESCAPE_CHARACTER);

                dataValue = CustomUtil.getTagValue(rowDataInArray[Constants.INT_ONE], rowNum);

                covid19ConfirmedGlobalCasesList.add(Arrays.asList(dataValue.split(Constants.REGEX_COMMA)));
            }
        } catch (IOException e){
            LOG.error("Exception while loading data: " + e);
        }

        LOG.info("COVID19 Confirmed Global Cases Country List: " + covid19ConfirmedGlobalCasesList.size());

        /*for (int i=0; i<covid19ConfirmedGlobalCasesList.size(); i++) {
            List list = covid19ConfirmedGlobalCasesList.get(i);
            LOG.info("covid19ConfirmedGlobalCasesList " + i +" : " + Arrays.toString(list.toArray()));
            //System.out.println(Arrays.toString(list.toArray()));
        }*/
    }

    public static void loadUsersList() {

        LOG.info("#CustomUtil loadUsersList() called");
        File usersFile = null;

        try {

            usersFile = new File(new CustomUtil().getClass().getClassLoader().getResource("UserList.txt").getFile());
            BufferedReader reader = new BufferedReader(new FileReader(usersFile));

            reader.lines().filter(line -> !line.startsWith(Constants.FORWARD_SLASH))
                    .forEach(line -> {
                        String[] userPass = getResultBySplit(line, Constants.SPLIT_BY_COLON);
                        usersList.put(userPass[Constants.INT_ZERO], userPass[Constants.INT_ONE]);
                        });
        } catch (IOException e) {
            LOG.error("Exception while loading users: " + e);
        }
    }

    public static String[] getResultBySplit(String value, String splitRegex) {
        return value.split(splitRegex);
    }

    public static String getUserFromJwtToken(String jwtToken){

        try {

            String jwtBody = null;

            try {
                jwtBody = new String(new Base64().decode(jwtToken.split("\\.")[Constants.INT_ONE]));
            } catch(IllegalArgumentException e) {
                LOG.error("Illegal Jwt token provided");
            }

            jwtBody = jwtBody.replace("{", "").replace("}", "").replace("\"", "");
            return  convert(jwtBody).get(Constants.JWT_CLAIM_SUB);

        } catch(Exception e) {
            LOG.error("error while getting user from Jwt token");
        }

        return null;
    }

    private static Map<String, String> convert(String str) {

        Map<String, String> map = new HashMap<>();
        String[] tokens = str.split(Constants.REGEX_COLON_WITH_COMMA);

        for (int i=0; i<tokens.length-1; )
            map.put(tokens[i++], tokens[i++]);

        return map;
    }
}
