# covid19_stats_rest_apis



Project Structure
-----------------	
	

	-- Developed Spring boot application (as per the instructions)
		- Spring Security
		- JWT (Token Generation)
	-- No database configured in this application (as per the instructions)


	Packages Details
	
		-- controller 
			- All Rest APIs endpoint
		-- service	
			- All logic here for APIs
		-- dto	
			- Data beans for requests and responses
		-- security 
			- Overrided spring security configuration for customized user details and JWT token generation
		-- filter 
			- JWT token generation and verification handling
		-- Job 
			- Cron job for uploading latest data
		-- util 
			- All common utility functionalities
	
	
	Data Handling
		-- All data has been handled in Java Lists as follow:
			- covid19ConfirmedGlobalCasesList (used for saving all data of reported cases)
			- usersList (used for loading users list)
		-- Used JSoup library/plugin for data scraping/loading in the java list


	Application Running
		-- Loads all the data from the provided website (Github URL) in the List (maiantianing in the Java code)
			- covid19ConfirmedGlobalCasesList	(Java list name)
		-- Loads all the users in a List from the UserList.txt (in the Properties file)
			- usersList	(Java list name)
			- Users are defined in the text file at path "../resources/UserList.txt"
			- A user that is not available in the users list can't call any API
			- Can be edited for required number of users as per the pattern mentioned in that file
			
	JWT Token Generation
		-- Application generates a JWT token against a valid username and password
		-- All other APIs will require a JWT token for access
		-- JWT token expiry time is 10 hours
		-- JWT token expiry time can be reset in the following parameter at path: "covid19.statistics.reports.util.Constants.JWT_TOKEN_VALIDITY"

	Invalidate Token
		-- As, there is no proper mechaninsm to expire JWT token manually, it expires on default expiry time automatically
			-- Or, it can be expired by changing the secret key, but it will be an update
	
		Alternate
			-- To invalidate the JWT token for the user, I am saving the latest JWT token with expiry time for every user
				- Using this information to invalidate the previous tokens
				- Token is verified from the user list before Spring JWT Security verification
				- If it does not match the latest token against a user, then that user can't access any API
	
	Scheduler
		-- A job has been written that will update the list of new cases reported
		-- It will run every night at 01:00 AM
		
		

Assumptions
-----------

	-- I am using yesterday date as a current date (Today) for the APIs because on the provided website (Dataset) they upload the data of current day on the next date
	-- If I do choose Today's date for APIs, there will be no data in the result set
	-- API list is as follows where I'm using the date of last posted data on the website
			i) All new cases reported today
			ii) All new cases reported today country-wise
			iii) All new cases reported today in a country
			iv) Top N countries with most reported cases today
		


All APIs List
-------------

1. To get a JWT token

	-- Takes a body with two parameters (JSON Body)
		i) "username":{user name for the available user list}
		ii) "password":{password of that user}
	
	-- Any user name and password can be provided in the body to get a token available in the list (UserList.txt in the properties file)
	-- If user name and password are correct as per the available user list
		- A new JWT token will be returned in the response
		- Its record will also be updated in a list of users with following information
			i) User Name
			ii)	JWT token
			iii) JWT Token Creation Time
		- Above information will be used to invalidate the previous tokens


2. To get list of users currently accessing the API

	-- All users with active JWT token will be considered as active users
	-- This API will return all users in the active user list(maintaining in the java) with active token


3. All new cases reported today

	-- NOTE: Considering Yesterday's date as current, as there will be no record of today
		- Data is posted after a day passes
	-- This API will return total cases reported latest (e.g., if Today's date is 19-04-2020, It will return results of 18-04-2020)
	-- Calculated as 
		- (Latest date count(18-04-20) - Previous date count(17-04-20) = New Added (for a country)
		- Sum of all countries

4. All new cases reported today country wise (sorted by cases reported today descending)

	-- NOTE: Considering Yesterday's date as current as there will be no record of today
		- Data is posted after a day passes
	-- This API will return all the cases reported latest by country-wise (e.g., if Today's date is 19-04-2020, it will return result of 18-04-2020)
		- In the descending order (country would be on top with most cases )
	-- Calculated as 
		(Latest date count(18-04-20) - Previous date count(17-04-20) = New Added (for a country)

5. All new cases reported today in a country

	-- NOTE: Considering Yesterday's date as current as there will be no record of today
		- Data postated after day passed
	-- This API will return all the cases reported latest by a specific country (e.g., if Today's date is 19-04-2020, It will return result of 18-04-2020)
		- It will take path variable for country name (e.g., countryName=Pakistan )
	-- Calculated as 
		(Latest date count(18-04-20) - Previous date count(17-04-20) = New Added (for a country)	

6. All new cases reported since a date in a country (choose whatever format but explain that in readme file)

	-- It will return total cases reported since a date to latest posted date in a specific country
	-- It will take two request params
		i) Country Name (e.g., countryName=Pakistan)
		ii) Since Date	(e.g., sinceDate=4/15/20)
	-- NOTE: Date provided in the same format as mentioned (it is as per the dataset link - github)	


7. Top N countries with most reported cases today

	-- NOTE: Considering Yesterday's date as current date as there will be no record of today
		- Data is posted after a day passes
	-- It will return results of most cases reported by N countries	
	-- It will take a parameter for number of countries to show (example: countries-count=10)

