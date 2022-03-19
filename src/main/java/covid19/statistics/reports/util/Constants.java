package covid19.statistics.reports.util;

public class Constants {

    public static String SECRET_KEY = "secret";
    public static final String AUTHORIZATION = "Authorization";
    public static final String UNAUTHORIZED_ERROR_MSG = "Token has expired, get a fresh token";
    public static final String BEARER = "Bearer ";
    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 10;
    public static final long JWT_TOKEN_VALIDITY_SECONDS = JWT_TOKEN_VALIDITY/1000;
    public static final String ROLES = "ROLES";

    public static final String DATE_PATTERN = "MM/dd/yy";
    public static final String DATE_TIME_FORMAT = "MM/dd/yy/hh/mm/ss";
    public static final int INT_ZERO = 0;
    public static final int INT_ONE = 1;
    public static final int INT_TWO = 2;
    public static final int INT_THREE = 3;
    public static final int INT_FOUR = 4;
    public static final int INT_FIVE = 5;
    public static final int INT_SEVEN = 7;
    public static final int INT_MINUS_ONE = -1;

    public static String TAG_TABLE = "table";
    public static String REGEX_TAG_TR = "</tr>";
    public static String REGEX_COLON_WITH_COMMA = ":|,";
    public static String TAG_TD = "td";
    public static String JWT_CLAIM_SUB = "sub";
    public static String TAG_TH = "th";
    public static String ESCAPE_CHARACTER = "\\r?\\n";
    public static String REGEX_COMMA = ",";

    public static String PROVINCE = "Province/State";
    public static String COUNTRY = "Country/Region";
    public static String CASES_REPORTED = "Cases Reported";
    public static String TOTAL_CASES_REPORTED = "Total Cases Reported";
    public static String RESULT_DATE = "Result Date";
    public static String SINCE_DATE = "Since Date";
    public static String SPLIT_BY_COLON = "\\:";
    public static String FORWARD_SLASH = "//";
}
