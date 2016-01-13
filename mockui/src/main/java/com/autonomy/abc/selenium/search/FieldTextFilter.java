package com.autonomy.abc.selenium.search;

import com.autonomy.abc.selenium.page.search.SearchBase;
import com.autonomy.abc.selenium.util.Waits;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebElement;

public class FieldTextFilter implements SearchFilter {
    private String fieldText;

    public FieldTextFilter(String fieldText) {
        this.fieldText = fieldText;
    }

    @Override
    public void apply(SearchFilter.Filterable page) {
        if (page instanceof SearchBase) {
            SearchBase searchBase = (SearchBase) page;
            searchBase.expand(SearchBase.Facet.FIELD_TEXT);
            try {
                searchBase.fieldTextAddButton().click();
                Waits.loadOrFadeWait();
            } catch (ElementNotVisibleException e) {
			/* already clicked */
            }

            WebElement editButton = searchBase.fieldTextEditButton();

            if(editButton.isDisplayed()) {
                searchBase.fieldTextEditButton().click();
            }
            
            searchBase.fieldTextInput().setAndSubmit(fieldText);
        }
    }

    public void clear(SearchFilter.Filterable page) {
        if (page instanceof SearchBase) {
            ((SearchBase) page).clearFieldText();
        }
    }

    @Override
    public String toString() {
        return "FieldTextFilter:" + fieldText;
    }
}
