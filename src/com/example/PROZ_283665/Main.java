package com.example.PROZ_283665;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Moja klasa implementujaca proste Okienko Logowania
 * 
 * @author Szymon Bienkowski 283665
 * @version 1.1
 *
 */
public class Main extends Application {

	private static Map<String, Map<String, String>> mapSrodoPairUserPass = new HashMap<>();
	private Dialog<Pair<String, String>> dialog = new Dialog<>();

	/**
	 * Glowna metoda tej klasy, uruchamia okienko
	 * 
	 * @param args
	 *            - przekazywane bezposrednio do
	 *            javafx.application.Application.launch(String[] args)
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Inicjalizuje srodowiska i uzytkowników z haslami
	 */
	@Override
	public void init() {
		Map<String, String> prodMap = new HashMap<String, String>();
		prodMap.put("adam.nowak", "nowaczek");
		prodMap.put("ewa.cudna", "cudeńko");
		prodMap.put("jan.kowalski", "kowal123");

		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("adrianna.zielonka", "zieleninka");
		testMap.put("adrian.wesołek", "pass123");

		Map<String, String> dewelMap = new HashMap<String, String>();
		dewelMap.put("karolina.mirna", "pass123");
		dewelMap.put("jacek.narcyz", "password1");
		dewelMap.put("alicja.piernik", "toruński");

		mapSrodoPairUserPass.put("Produkcyjne", prodMap);
		mapSrodoPairUserPass.put("Testowe", testMap);
		mapSrodoPairUserPass.put("Deweloperskie", dewelMap);
	}

	/**
	 * Konstruuje okienko
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		dialog.setTitle("Logowanie");
		dialog.setHeaderText("Logowanie do systemu STYLEman");

		ButtonType logonButtonType = new ButtonType("Logon", ButtonData.OK_DONE);
		ButtonType anulujButtonType = new ButtonType("Anuluj", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(logonButtonType, anulujButtonType);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ChoiceBox<String> choiceSrodowisko = new ChoiceBox<>(
				FXCollections.observableArrayList(mapSrodoPairUserPass.keySet()));
		choiceSrodowisko.setValue(mapSrodoPairUserPass.keySet().stream().findFirst().get());

		ComboBox<String> comboUzytkownik = new ComboBox<>(
				FXCollections.observableArrayList(mapSrodoPairUserPass.get(choiceSrodowisko.getValue()).keySet()));
		comboUzytkownik.setEditable(true);

		PasswordField passField = new PasswordField();

		grid.add(new Label("Środowisko:"), 0, 0);
		grid.add(new Label("Użytkownik:"), 0, 1);
		grid.add(new Label("Hasło:"), 0, 2);

		grid.add(choiceSrodowisko, 1, 0);
		grid.add(comboUzytkownik, 1, 1);
		grid.add(passField, 1, 2);

		Node logonButton = dialog.getDialogPane().lookupButton(logonButtonType);
		logonButton.setDisable(true);

		final ChangeListener<? super String> fieldUpdate = (obs, oldVal,
				newVal) -> logonButton.setDisable(choiceSrodowisko.getValue() == null
						|| choiceSrodowisko.getValue().toString().trim().isEmpty() || comboUzytkownik.getValue() == null
						|| comboUzytkownik.getValue().toString().trim().isEmpty()
						|| passField.getText().trim().isEmpty());
		;

		choiceSrodowisko.valueProperty().addListener(fieldUpdate);
		choiceSrodowisko.valueProperty().addListener((obs, oldVal, newVal) -> {
			if (oldVal != newVal) {
				comboUzytkownik.getItems().clear();
				comboUzytkownik.getItems().addAll(mapSrodoPairUserPass.get(choiceSrodowisko.getValue()).keySet());
			}

		});
		comboUzytkownik.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
			comboUzytkownik.setValue(comboUzytkownik.getEditor().getText());
			fieldUpdate.changed(obs, oldVal, newVal);
		});
		passField.textProperty().addListener(fieldUpdate);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			return dialogButton == logonButtonType && mapSrodoPairUserPass.get(choiceSrodowisko.getValue())
					.get(comboUzytkownik.getValue()).equals(passField.getText())
							? new Pair<>(choiceSrodowisko.getValue(), comboUzytkownik.getValue())
							: null;
		});

		Optional<Pair<String, String>> result = showAndWait();

		result.ifPresent(srodoUzyt -> System.out
				.println("Środowisko=" + srodoUzyt.getKey() + ", Użytkownik=" + srodoUzyt.getValue()));
	}

	public Optional<Pair<String, String>> showAndWait() {
		return dialog.showAndWait();
	}

}