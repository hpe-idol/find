package com.autonomy.abc.selenium.connections;

import com.autonomy.abc.selenium.config.Application;
import com.autonomy.abc.selenium.element.GritterNotice;
import com.autonomy.abc.selenium.indexes.Index;
import com.autonomy.abc.selenium.menu.NavBarTabId;
import com.autonomy.abc.selenium.page.AppBody;
import com.autonomy.abc.selenium.page.HSOElementFactory;
import com.autonomy.abc.selenium.page.connections.ConnectionsDetailPage;
import com.autonomy.abc.selenium.page.connections.ConnectionsPage;
import com.autonomy.abc.selenium.page.connections.NewConnectionPage;
import com.autonomy.abc.selenium.page.connections.wizard.ConnectorIndexStepTab;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ConnectionService {
    private Application application;
    private HSOElementFactory elementFactory;
    private ConnectionsPage connectionsPage;
    private NewConnectionPage newConnectionPage;
    private ConnectionsDetailPage connectionsDetailPage;

    public ConnectionService(Application application, HSOElementFactory elementFactory) {
        this.application = application;
        this.elementFactory = elementFactory;
    }

    protected WebDriver getDriver() {
        return getElementFactory().getDriver();
    }

    protected HSOElementFactory getElementFactory() {
        return elementFactory;
    }

    protected AppBody getBody() {
        return application.createAppBody(getDriver());
    }

    public ConnectionsPage goToConnections() {
        getBody().getSideNavBar().switchPage(NavBarTabId.CONNECTIONS);
        connectionsPage = getElementFactory().getConnectionsPage();
        return connectionsPage;
    }

    public ConnectionsDetailPage goToDetails(final Connector connector) {
        return goToDetails(connector.getName());
    }

    public ConnectionsDetailPage goToDetails(final String name) {
        goToConnections();
        connectionsPage.displayedConnectionWithTitleContaining(name).click();
        connectionsDetailPage = getElementFactory().getConnectionsDetailPage();
        return connectionsDetailPage;
    }

    public ConnectionsPage setUpConnection(final Connector connector) {
        goToConnections();
        connectionsPage.newConnectionButton().click();
        newConnectionPage = elementFactory.getNewConnectionPage();
        connector.makeWizard(newConnectionPage).apply();
        new WebDriverWait(getDriver(), 300).withMessage("connection " + connector + " timed out").until(GritterNotice.notificationContaining(connector.getFinishedNotification()));
        return connectionsPage;
    }

    public ConnectionsPage deleteConnection(final Connector connector, boolean deleteIndex) {
        beginDelete(connector);

        if(deleteIndex) {
            deleteIndex();
        }

        confirmDelete(connector);

        return connectionsPage;
    }

    private void beginDelete(Connector connector){
        ConnectionsDetailPage connectionsDetailPage = goToDetails(connector);
        connectionsDetailPage.deleteButton().click();
    }

    private void deleteIndex(){
        connectionsDetailPage.alsoDeleteIndexCheckbox().click();
    }

    private void confirmDelete(Connector connector){
        connectionsDetailPage.deleteConfirmButton().click();
        connectionsPage = elementFactory.getConnectionsPage();
        new WebDriverWait(getDriver(), 100).until(GritterNotice.notificationContaining(connector.getDeleteNotification()));
    }

    public ConnectionsPage deleteAllConnections(boolean deleteIndex) {
        goToConnections();
        for(WebElement connector : getDriver().findElements(By.className("listItemTitle"))){
            WebConnector webConnector = new WebConnector(null, connector.getText().split("\\(")[0].trim());

            beginDelete(webConnector);

            if (deleteIndex) {
                try {
                    deleteIndex();
                } catch (Exception e) {/* May have other connections associated */}
            }

            confirmDelete(webConnector);
        }
        return connectionsPage;
    }

    public ConnectionsDetailPage updateLastRun(WebConnector webConnector) {
        goToDetails(webConnector);
        webConnector.setStatistics(new ConnectionStatistics(connectionsDetailPage.lastRun()));
        return connectionsDetailPage;
    }

    public Connector changeIndex(Connector connector, Index index) {
        goToDetails(connector);
        connectionsDetailPage.editButton().click();

        NewConnectionPage newConnectionPage = NewConnectionPage.make(getDriver());
        newConnectionPage.nextButton().click();
        newConnectionPage.loadOrFadeWait();
        newConnectionPage.nextButton().click();
        newConnectionPage.loadOrFadeWait();
        ConnectorIndexStepTab connectorIndexStep = newConnectionPage.getIndexStep();

        connectorIndexStep.selectIndexButton().click();
        connectorIndexStep.selectIndex(index);

        newConnectionPage.finishButton().click();

        connector.setIndex(index);

        new WebDriverWait(getDriver(), 300).withMessage("connection " + connector + " timed out").until(GritterNotice.notificationContaining(connector.getFinishedNotification()));

        return connector;
    }

    public ConnectionsDetailPage cancelConnectionScheduling(Connector connector) {
        goToDetails(connector);

        connectionsDetailPage.editButton().click();

        NewConnectionPage newConnectionPage = NewConnectionPage.make(getDriver());

        newConnectionPage.nextButton().click();
        newConnectionPage.loadOrFadeWait();

        newConnectionPage.getConnectorConfigStep().skipSchedulingCheckbox().click();

        newConnectionPage.nextButton().click();
        newConnectionPage.loadOrFadeWait();
        newConnectionPage.finishButton().click();

        new WebDriverWait(getDriver(), 10).until(GritterNotice.notificationContaining("Connector " + connector.getName() + " schedule has been cancelled successfully"));

        return connectionsDetailPage;
    }
}
