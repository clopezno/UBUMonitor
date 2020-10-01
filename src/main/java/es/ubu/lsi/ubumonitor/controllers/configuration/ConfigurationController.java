package es.ubu.lsi.ubumonitor.controllers.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.PropertySheet.Mode;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ConfigurationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

	private MainController mainController;

	@FXML
	private TabPane tabPane;

	private MainConfiguration mainConfiguration;

	private static final Callback<Item, PropertyEditor<?>> DEFAUL_PROPERTY_EDITOR_FACTORY = new DefaultPropertyEditorFactory();

	public void init(MainController mainController, MainConfiguration mainConfiguration) {
		this.mainController = mainController;
		this.mainConfiguration = mainConfiguration;
		createPropertySheets();
	}

	public void createPropertySheets() {
		Tab tab = new Tab(I18n.get(MainConfiguration.GENERAL));
		tab.setClosable(false);
		PropertySheet propertySheet = createPropertySheet(null);

		tab.setContent(propertySheet);
		tabPane.getTabs()
				.add(tab);

		for (Tabs chartTab : Tabs.values()) {
			tab = new Tab(I18n.get("tabs." + chartTab));
			tab.setClosable(false);
			propertySheet = createPropertySheet(chartTab);
			tab.setContent(propertySheet);
			tabPane.getTabs()
					.add(tab);
		}
	}

	public PropertySheet createPropertySheet(Tabs chartTab) {

		PropertySheet propertySheet = new PropertySheet(
				FXCollections.observableArrayList(mainConfiguration.getProperties(chartTab)));
		propertySheet.setMode(Mode.CATEGORY);
		propertySheet.setModeSwitcherVisible(false);

		propertySheet.setPropertyEditorFactory(item -> {

			if (item.getValue() instanceof ObservableList<?>) {
				Class<?> type = item.getType();
				if (type == Role.class) {
					return new CheckComboBoxPropertyEditor<>(item, Controller.getInstance()
							.getActualCourse()
							.getRoles());
				}
				if (type == Group.class) {
					return new CheckComboBoxPropertyEditor<>(item, Controller.getInstance()
							.getActualCourse()
							.getGroups());
				}
				if (type == LastActivity.class) {
					return new CheckComboBoxPropertyEditor<>(item, LastActivityFactory.DEFAULT.getAllLastActivity());
				}

				if (type == ChartType.class) {
					return new CheckComboBoxPropertyEditor<>(item, ChartType.getNonDefaultValues(),
							new StringConverter<ChartType>() {

								@Override
								public String toString(ChartType object) {
									return I18n.get(object) + " (" + I18n.get("tabs." + object.getTab()) + ")";
								}

								@Override
								public ChartType fromString(String string) {
									// not used
									return null;
								}
							});
				}
			}

			return DEFAUL_PROPERTY_EDITOR_FACTORY.call(item);
		});

		return propertySheet;
	}

	public void setOnClose() {
		tabPane.getScene()
				.getWindow()
				.setOnHidden(e -> onClose());
	}

	public void onClose() {
		apply();

	}

	public static void saveConfiguration(MainConfiguration mainConfiguration, Path path) {
		try {
			path.toFile()
					.getParentFile()
					.mkdirs();
			Files.write(path, mainConfiguration.toJson()
					.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			LOGGER.error("Error al guardar el fichero de configuración", e);
			UtilMethods.errorWindow(I18n.get("error.saveconfiguration"), e);
		}
	}

	public void applyConfiguration() {
		mainController.getActions()
				.applyConfiguration();
	}

	public void restoreConfiguration() {
		ButtonType option = UtilMethods.confirmationWindow(I18n.get("text.restoredefault"));
		if (option == ButtonType.OK) {
			mainConfiguration.setDefaultValues();
			tabPane.getTabs()
					.clear();
			createPropertySheets();
		}

	}

	public void restoreSavedConfiguration() {
		Controller controller = Controller.getInstance();
		loadConfiguration(mainConfiguration, controller.getConfiguration(controller.getActualCourse()));

	}

	public static void loadConfiguration(MainConfiguration mainConfiguration, Path path) {
		if (path.toFile()
				.exists()) {
			try {
				mainConfiguration.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));

			} catch (IOException e) {
				UtilMethods.errorWindow(I18n.get("error.chargeconfiguration"), e);
			}
		}
	}

	public void apply() {
		Controller controller = Controller.getInstance();
		saveConfiguration(mainConfiguration, controller.getConfiguration(controller.getActualCourse()));
		applyConfiguration();
	}
}
