package org.example.service;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UAlbertaService {

    public static final String N_A = "N/A";
    public static final String URL_PROGRAM = "https://www.ualberta.ca/en/graduate-programs";
    public static final String UALBERTA_GRADUATE_PROGRAMS_CSV = "ualberta_graduate_programs.csv";
    public static final int MAX_PAGES = 5;
    public static final String[] CSV_HEADERS = {"Degree", "Program Name", "Program Type", "Delivery Mode", "Faculty / Department", "Program Link", "Excerpt"};

    private WebDriver driver;
    private WebDriverWait driverWait;

    /**
     * Task 1
     * 1. Open the website in a web browser using Selenium.
     * 2. Find and interact with various elements on the page (e.g., links, buttons, text boxes) using Selenium commands.
     * 3. Extract data from the page using Selenium commands, such as finding and storing text,images, or other content.
     * 4. Save the scraped data in a CSV file or other format of your choice.
     */
    public void task1() {
        var result = scrap();
        createCsv(result);
    }

    /**
     * Task 3
     * 1. Use advanced Selenium commands, such as waiting for elements to load or handling pop-up windows.
     */
    public void task3() {
        try {
            // Navigate to target website and maximize window.
            getDriver().get(URL_PROGRAM);
            getDriver().manage().window().maximize();

            handlePopupsAndModals();

            // Interacting with autocomplete input field.
            WebElement searchInputElement = getDriverWait().until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder*='Search for your program']"))
            );
            // Scroll to search input and send keys to trigger autocomplete dropdown.
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({block: 'center'});", searchInputElement);
            searchInputElement.clear();
            searchInputElement.sendKeys("comp");
            // Find the suggestion selector.
            By suggestionOptionSelector = By.cssSelector(".magic-box-suggestion.coveo-omnibox-selectable");
            // Explicit wait for at least one suggestion item to be visible.
            getDriverWait().until(ExpectedConditions.presenceOfElementLocated(suggestionOptionSelector));
            // Find suggestions.
            List<WebElement> webElements = getDriver().findElements(suggestionOptionSelector);
            Thread.sleep(3000);

            // Click the first available autocomplete option.
            if (!webElements.isEmpty()) {
                WebElement firstElement = getDriverWait().until(ExpectedConditions.elementToBeClickable(webElements.get(0)));
                System.out.println("Selecting suggestion: " + firstElement.getAttribute("aria-label"));
                // Click option via JavaScript to prevent potential click interceptions.
                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", firstElement);
            } else {
                // Fallback to submit using ENTER key.
                searchInputElement.sendKeys(Keys.ENTER);
            }
            // Explicit wait for updated search results.
            getDriverWait().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".coveo-list-layout.CoveoResult")));
            System.out.println("Search results loaded successfully.");
            Thread.sleep(3000);
            // Store primary window handle.
            String mainWindowHandle = getDriver().getWindowHandle();
            // Locate a program link to interact with tab management.
            WebElement firstResultLinkElement = getDriver().findElement(By.cssSelector("a.CoveoResultLink"));
            String destinationUrl = firstResultLinkElement.getAttribute("href");
            Thread.sleep(3000);
            // Executing JavaScript to open a new tab.
            ((JavascriptExecutor) getDriver()).executeScript("window.open(arguments[0], '_blank');", destinationUrl);
            // Explicit wait for active window count to increase.
            getDriverWait().until(ExpectedConditions.numberOfWindowsToBe(2));
            Thread.sleep(3000);
            // Switching execution context across Window handles.
            Set<String> allWindowHandles = getDriver().getWindowHandles();
            for (String handle : allWindowHandles) {
                if (!handle.equals(mainWindowHandle)) {
                    getDriver().switchTo().window(handle); // Context switched to secondary tab.
                    break;
                }
            }

            // Explicit wait on the secondary tab.
            getDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
            System.out.println("Switched to tab: " + getDriver().getTitle());

            // Closing current tab and switching context back.
            getDriver().close();
            getDriver().switchTo().window(mainWindowHandle);
            System.out.println("Returned focus to main window: " + getDriver().getTitle());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            getDriver().quit();
            this.driver = null;
            this.driverWait = null;
        }
    }

    private WebDriver getDriver() {
        if (this.driver == null) {
            this.driver = new ChromeDriver();
            this.driverWait = new WebDriverWait(this.driver, Duration.ofSeconds(15));
        }
        return this.driver;
    }

    private WebDriverWait getDriverWait() {
        return this.driverWait;
    }

    /**
     * Scrap the required data in the website.
     *
     * @return a list of scraped data.
     */
    private List<String[]> scrap() {
        List<String[]> result = new ArrayList<>();

        try {
            // Open target URL using Selenium.
            getDriver().get(URL_PROGRAM);
            getDriver().manage().window().maximize();
            System.out.println("Successfully opened page: " + getDriver().getTitle());
            System.out.println("Current URL: " + getDriver().getCurrentUrl());

            // Interacting with autocomplete input field.
            WebElement searchInputElement = getDriverWait().until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder*='Search for your program']"))
            );
            // Scroll to search input and send keys to trigger autocomplete dropdown.
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({block: 'center'});", searchInputElement);
            searchInputElement.clear();
            searchInputElement.sendKeys("science");
            Thread.sleep(1500);
            searchInputElement.clear();

            // Crawl multiple pages from the same website and combine the results.
            for (int i = 0; i < MAX_PAGES; i++) {
                extract(result);
                if (!handlePagination()) break;
            }
        } catch (Exception e) {
            System.err.println("An error occurred while scraping: " + e.getMessage());
        } finally {
            getDriver().quit();
            this.driver = null;
            this.driverWait = null;
        }
        return result;
    }

    /**
     * Extracts the required data from the {@link WebElement}s.
     *
     * @param result a list of scraped data.
     * @throws InterruptedException a {@link InterruptedException}
     */
    private void extract(List<String[]> result) throws InterruptedException {
        // Wait for css selector result items to populate in the DOM.
        getDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".coveo-list-layout.CoveoResult")));
        Thread.sleep(1500);
        // Extract data using accurate css selector.
        List<WebElement> webElements = getDriver().findElements(By.cssSelector(".coveo-list-layout.CoveoResult"));

        for (WebElement webElement : webElements) {
            String degree = N_A;
            String programName = N_A;
            String programType = N_A;
            String mode = N_A;
            String faculty = N_A;
            String link = N_A;
            String excerpt = N_A;

            try {
                // Extract Degree and Program Type (e.g., "Master of Accounting – Course Based").
                try {
                    String text = webElement.findElement(By.cssSelector("p")).getText().trim();
                    String[] arrayText = text.split("–");
                    if (arrayText.length == 2) {
                        degree = arrayText[0];
                        programType = arrayText[1];
                    }
                } catch (Exception ignored) {
                }
                // Extract Program Name & Link.
                try {
                    WebElement linkElement = webElement.findElement(By.cssSelector("a.CoveoResultLink"));
                    programName = linkElement.getText().trim();
                    link = linkElement.getAttribute("href");
                } catch (Exception ignored) {
                }
                // Extract Delivery Mode (e.g., "On Campus", "Online").
                try {
                    mode = webElement.findElement(By.cssSelector(".campus-label")).getText().trim();
                } catch (Exception ignored) {
                }
                // Extract Program Excerpt.
                try {
                    excerpt = webElement.findElement(By.cssSelector(".CoveoExcerpt")).getText().trim();
                } catch (Exception ignored) {
                }
                // Extract Faculty (aligned on the right side).
                try {
                    faculty = webElement.findElement(By.cssSelector("p[style*='text-align: right']")).getText().trim();
                } catch (Exception ignored) {
                }
                result.add(new String[]{degree, programName, programType, mode, faculty, link, excerpt});
            } catch (Exception e) {
                System.err.println("Error parsing result web element: " + e.getMessage());
            }
        }
    }

    /**
     * Part of task 2 to handle pagination.
     * Find and click the "Next" button.
     *
     * @return true / false.
     */
    private boolean handlePagination() {
        try {
            // Find the 'Next' button span.
            List<WebElement> webElements = getDriver().findElements(By.cssSelector("li.coveo-pager-next span[role='button']"));

            if (!webElements.isEmpty() && webElements.get(0).isDisplayed()) {
                WebElement nextBtnElement = webElements.get(0);
                // Scroll button into view and execute click via JavaScript.
                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({block: 'center'});", nextBtnElement);
                Thread.sleep(500);
                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", nextBtnElement);
            } else {
                System.out.println("Next button not available. End of pagination reached.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Could not click Next button or reached last page: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Creates and writes the scraped data in a csv file.
     *
     * @param data a list of scraped data.
     */
    private void createCsv(List<String[]> data) {
        String csvFilePath = UALBERTA_GRADUATE_PROGRAMS_CSV;
        List<String[]> scrapedData = new ArrayList<>();

        // Add CSV Headers.
        scrapedData.add(CSV_HEADERS);
        scrapedData.addAll(data);

        // Write data to CSV file.
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            writer.writeAll(scrapedData);
            System.out.println("Data successfully scraped! Written " + (scrapedData.size() - 1) + " entries to " + csvFilePath);
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    /**
     * Detects and dismisses modal pop-ups.
     */
    private void handlePopupsAndModals() {
        try {
            // Looking for potential modal close buttons.
            List<WebElement> webElements = getDriver().findElements(
                    By.cssSelector("button.cc-accept, .modal-close, button[aria-label='Close']")
            );

            if (!webElements.isEmpty() && webElements.get(0).isDisplayed()) {
                // Wait until element is clickable before clicking.
                getDriverWait().until(ExpectedConditions.elementToBeClickable(webElements.get(0))).click();
                System.out.println("Dismissed modal pop-up successfully.");
            }
        } catch (Exception e) {
            System.out.println("No modal pop-ups present.");
        }
    }
}
