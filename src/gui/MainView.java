package gui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainView extends BorderPane {

    public MainView(Stage ownerStage) {
        TabPane tabPane = new TabPane();

        Tab generateTab = new Tab("Generate");
        generateTab.setClosable(false);
        generateTab.setContent(new GenerateView(ownerStage));

        Tab auditTab = new Tab("Audit");
        auditTab.setClosable(false);
        auditTab.setContent(new AuditView(ownerStage));

        tabPane.getTabs().addAll(generateTab, auditTab);

        setCenter(tabPane);
    }
}