# Team Project

Please keep this up-to-date with information about your project throughout the term.

The readme should include information such as:
- a summary of what your application is all about
- a list of the user stories, along with who is responsible for each one
- information about the API(s) that your project uses 
- screenshots or animations demonstrating current functionality




# **Project Blueprint**

**Team Name:** FinWise
---

**Domain:** Personal Finance Tracker and Simulated investment \- Tracks finances, visualizes trends in expenses, provides investment recommendations.
---

#### **User Stories**

**User story 1:** As a user, I want to track my expenses over time, so that I can manage my expenses and savings.  
**User story 2:** As a user, I want to visualize trends by time range in my historical transaction expenses, so that I can better understand the rate of my expenses and savings over time.  
**User story 3:** As a user, I want to search for stock and its price that I am interested in, and also view charts of stock performance by time range, so that I can have up-to-date information and make reliable investment decisions.  
**User story 4:** As a user, I want to simulate buying or selling stock options, so that I can test my strategies without using real money.  
**User story 5:** As a user, I want to track my overall portfolio value so I can monitor how my investments are performing.  
**User story 6:** As a user, I want to read the latest news related to the market, so that I can stay informed and make decisions.

---

#### **Use Cases**

**Use case 1:** Track expenses

* **app.Main flow:**
    * User enters the amount for the type of expense
    * The system stores transactions under the user’s profile
* **Alternative flow:**
    * Duplicate entry → undo transaction
    * Invalid or empty input → prompt user to enter amount in correct format
  [![Watch the video](https://raw.githubusercontent.com/Wayne328x/Finwise/tree/main/videodemo/usecase1%20demo.mp4)](https://raw.githubusercontent.com/Wayne328x/Finwise/tree/main/videodemo/usecase1%20demo.mp4)

**Use case 2**: Financial trends

* **app.Main flow:**
    * Choose the type of time range (view by day, by week, or by month).
    * System displays user’s financial history using line chart
* **Alternative flow:**
    * Not enough entries → prompt “not enough information” to show

**Use case 3:** Fetch Selected Live Stock prices

* **app.Main flow:**
    * User enters the name of the stock in the search bar.
    * Fetch and display the list of related stock symbols.
    * User selects the stock symbol that they are interested in.
    * System shows the current line chart of the selected stock.
    * User can view the chart by selecting different time ranges.
    * User can hover their cursor on the line chart and it would show the price at the timestamp
* **Alternative flow:**
    * Network stability issues/API call failure → System shows warning and displays last-loaded prices; label the chart “as of \<timestamp\>.”
    * Invalid symbol in the search bar → display “No results for “what user just entered”

**Use Case 4:** Investing in Simulated Market

* **app.Main flow:**
    * User enters or selects a symbol (e.g., Apple’s symbol)
    * User chooses either buy or sell, and then enters the amount and clicks submit.
    * Show the latest price
    * Update the holdings, lots and simulated money amount
    * Recalculated the profit or loss
* **Alternative flow:**
    * No recent price available (e.g., off the market or internet stability issues) after clicking the submit button → ask user to refresh the market
    * Not enough simulated money → notify the user “Not enough money” and show user a button to add more simulated money if they want

**Use Case 5:** Portfolio performance diagnostics

* **app.Main flow:**
    * User clicks “stock holdings”
    * System fetches portfolio gain/loss over time
    * System updates portfolio gain/loss over time with up-to-date prices of invested stocks
    * System displays a line chart of the gain/loss of investment over time, and also overall percent increase/decrease in returns
    * System displays all invested stock options stacked vertically with current price of stock, gain/loss, holding amount of portfolio
* **Alternative flow:**
    * Network stability issue with fetching historical data → prompt to refresh
    * New users have not invested at all and opened it → display “No data available”

**Use Case 6:** Real-time News

* **app.Main flow:**
    * User can directly see the latest news headlines at the top of the home page for their holdings and watchlist
    * The news automatically rotates the page
    * User can pause the news by hovering the mouse if they are interested in
    * User can click the news panel and navigate to the corresponding external website on their browser
* **Alternative flow:**
    * Failure to load \- Display “No Results”.

#### ---

#### **MVP Table**

| Use Case | User Story | Lead Developer |
| ----- | ----- | ----- |
| Use Case \#1 | User story 1 | Saladin  |
| Use Case \#2 | User story 2 | Simon  |
| Use Case \#3 | User story 3 | Wayne  |
| Use Case \#4 | User story 4 | Jim  |
| Use Case \#5 | User story 5 | Hongcheng  |
| Use Case \#6 | User story 6 | Aaron  |

---

#### **Proposed Entities**

**User**

- id: int
- username: String
- password: String
- balance: double
- invBalance: double
- relArticles: ArrayList
+ getId(): int
+ getUsername(): String
+ getPassword(): String
+ getBalance(): double
+ getInvBalance(): double
+ addRelArticle(Article: a): Null
+ removeRelArticle(Article: a): Null

**Transaction \<\<interface\>\>**

+ getAmount(): double

**Expense implements Transaction**

- type: String
- amount: double
+ getAmount(): double

**Article**

- headline: String
- description: String
- hyperlink: String
+ getHeadline(): String
+ getDescription(): String
+ getLink(): String

#### **Proposed API**

Alpha Vantage  
[https://www.alphavantage.co/documentation/](https://www.alphavantage.co/documentation/)

* For our use case 3: “Fetch Selected Live Stock Prices”, we would use the Ticker Search (SYMBOL\_SEARCH) from this api to help us display correct symbol results.  When the user types a company name or symbol letter, the list will return the best-match symbols.
* We would use TIME\_SERIES\_DAILY, TIME\_SERIES\_WEEKLY, TIME\_SERIES\_MONTHLY to help us view the line chart of Stock performance
* We also use NEWS\_SENTIMENT from this API to fetch real-time news
* We would also use GLOBAL\_QUOTE from this API to return the latest price and volume information for selected symbol.





By keeping this README up-to-date,
your team will find it easier to prepare for the final presentation
at the end of the term.
