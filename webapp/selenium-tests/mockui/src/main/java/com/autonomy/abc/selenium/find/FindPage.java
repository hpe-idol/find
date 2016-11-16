package com.autonomy.abc.selenium.find;

import com.autonomy.abc.selenium.find.filters.FilterPanel;
import com.autonomy.abc.selenium.find.results.ListView;
import com.autonomy.abc.selenium.indexes.tree.IndexesTree;
import com.autonomy.abc.selenium.query.*;
import com.hp.autonomy.frontend.selenium.element.DatePicker;
import com.hp.autonomy.frontend.selenium.element.FormInput;
import com.hp.autonomy.frontend.selenium.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class FindPage extends AppElement implements AppPage,
        IndexFilter.Filterable,
        DatePickerFilter.Filterable,
        StringDateFilter.Filterable,
        ParametricFilter.Filterable {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    FindPage(final WebDriver driver) {
        super(new WebDriverWait(driver, 30)
                .withMessage("loading Find page")
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("find-pages-container"))), driver);
    }

    //WAITS
    @Override
    public void waitForLoad() {
        new WebDriverWait(getDriver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.className("find-pages-container")));
    }

    private void waitForResultsToLoad() {
        Container.MIDDLE.waitForLoad(getDriver());
    }

    public void waitUntilParametricModalGone() {
        new WebDriverWait(getDriver(),10)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.className(".parametric-modal")));
    }

    public void waitUntilDatabasesLoaded() {
        new WebDriverWait(getDriver(),20)
                .withMessage("databases not loaded message to disappear")
                .until(ExpectedConditions.invisibilityOfElementWithText(By.cssSelector(".main-results-list .results")
                        ,"The list of databases has not yet been retrieved"));
    }

    //RESULTS
    private WebElement currentView() {
        return Container.currentTabContents(getDriver()).findElement(By.cssSelector(".tab-pane.active"));
    }

    //TODO: are there any messages (e.g. error or empty that are present in all the views?)
    public boolean resultsMessagePresent() {
        return !currentView().findElements(By.className("result-message")).isEmpty();
    }

    //FILTER PANEL
    protected FilterPanel filters() {
        return new FilterPanel(new IndexesTree.Factory(), getDriver());
    }

    @Override
    public IndexesTree indexesTree() {
        return filters().indexesTree();
    }

    @Override
    public void filterBy(final QueryFilter filter) {
        filter.apply(this);
        waitForResultsToLoad();
    }

    @Override
    public DatePicker fromDatePicker() {
        return filters().datePickerFilterable().fromDatePicker();
    }

    @Override
    public DatePicker untilDatePicker() {
        return filters().datePickerFilterable().untilDatePicker();
    }

    @Override
    public FormInput fromDateInput() {
        return filters().stringDateFilterable().fromDateInput();
    }

    @Override
    public FormInput untilDateInput() {
        return filters().stringDateFilterable().untilDateInput();
    }

    @Override
    public String formatInputDate(final Date date) {
        return filters().stringDateFilterable().formatInputDate(date);
    }

    @Override
    public WebElement parametricContainer() {
        final WebElement firstParametric = findElement(By.cssSelector("[data-field]"));
        return ElementUtil.ancestor(firstParametric, 2);
    }

    @Override
    public void waitForParametricValuesToLoad() {
        filters().waitForParametricFields();
    }

    @Override
    public void openContainer(WebElement container) {
        WebElement list = container.findElement(By.className("collapse"));

        if(list.getAttribute("aria-expanded").equals("false")) {
            container.click();
            Waits.loadOrFadeWait();
        }
    }

    //FILTER LABELS
    public List<String> filterLabelsText() {
        return ElementUtil.getTexts(filterLabels());
    }

    public List<WebElement> filterLabels() {
        return findElements(By.className("filter-label"));
    }

    public void removeFilterLabel(WebElement filter) {
        filter.findElement(By.cssSelector(".filters-remove-icon")).click();
    }

    //MISCELLANEOUS
    public WebElement footerLogo() {
        return findElement(By.className("hp-logo-footer"));
    }

    public WebElement originalQuery() { return findElement(By.className("original-query")); }

    public WebElement correctedQuery() { return findElement(By.className("corrected-query")); }

    public String getCorrectedQuery() { return correctedQuery().getText(); }

    public boolean hasAutoCorrected() {
        final List<WebElement> originalQuery = findElements(By.className("original-query"));
        return !originalQuery.isEmpty() && originalQuery.get(0).isDisplayed(); }

    public void ensureTermNotAutoCorrected() {
        if(hasAutoCorrected()) {
            originalQuery().click();
        }
    }

    public void scrollToBottom() {
        findElement(By.className("results-number")).click();
        DriverUtil.scrollToBottom(getDriver());
        waitForResultsToLoad();
    }

    public ListView goToListView() {
        LOGGER.info("Non-BI User: already on list view.");
        return new ListView(findElement(By.className("service-view-container")), getDriver());
    }

    protected WebElement currentTab() {
        return Container.currentTabContents(getDriver());
    }

    public boolean verticalScrollBarPresent() {
        String javaScript = "return document.documentElement.scrollHeight>document.documentElement.clientHeight;";
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        return (boolean) executor.executeScript(javaScript);
    }

    public static class Factory implements ParametrizedFactory<WebDriver, FindPage> {
        @Override
        public FindPage create(final WebDriver context) {
            return new FindPage(context);
        }
    }
}
