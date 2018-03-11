package com.example.PROZ_283665;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

	private static Map<String, List<Pair<String, String>>> mapSrodoPairUserPass = new HashMap<>();
	private Dialog<Pair<String, String>> dialog = new Dialog<>();
	
	/**
	 * Glowna metoda tej klasy, uruchamia okienko
	 * 
	 * @param args - przekazywane bezposrednio do javafx.application.Application.launch(String[] args)
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	
	/**
	 * Inicjalizuje srodowiska i uzytkowników z haslami
	 */
	@Override
	public void init() {
		List<Pair<String, String>> prodList = new ArrayList<Pair<String, String>>();
		prodList.add(new Pair<>("adam.nowak", "nowaczek"));
		prodList.add(new Pair<>("ewa.cudna", "cudeńko"));
		prodList.add(new Pair<>("jan.kowalski", "kowal123"));
		
		List<Pair<String, String>> testList = new ArrayList<Pair<String, String>>();
		testList.add(new Pair<>("adrianna.zielonka", "zieleninka"));
		testList.add(new Pair<>("adrian.wesołek", "pass123"));
		
		List<Pair<String, String>> deweList = new ArrayList<Pair<String, String>>();
		deweList.add(new Pair<>("karolina.mirna", "pass123"));
		deweList.add(new Pair<>("jacek.narcyz", "password1"));
		deweList.add(new Pair<>("alicja.piernik", "toruński"));
		
		mapSrodoPairUserPass.put("Produkcyjne", prodList);
		mapSrodoPairUserPass.put("Testowe", testList);
		mapSrodoPairUserPass.put("Deweloperskie", deweList);
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
		
		final Function<ChoiceBox<String>, List<String>> getUzytk = cBox ->
		{
			List<String> uzyt = new ArrayList<>();
			for (Pair<String, String> pair: mapSrodoPairUserPass.get(cBox.getValue())) {
				uzyt.add(pair.getKey());
			}
			return uzyt;
		};

		ComboBox<String> comboUzytkownik = new ComboBox<>(
				FXCollections.observableArrayList(getUzytk.apply(choiceSrodowisko)));
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

		final ChangeListener<? super String> fieldUpdate = (obs, oldVal, newVal) -> logonButton.setDisable(
						choiceSrodowisko.getValue() == null
						|| choiceSrodowisko.getValue().toString().trim().isEmpty()
						|| comboUzytkownik.getValue() == null
						|| comboUzytkownik.getValue().toString().trim().isEmpty()
						|| passField.getText().trim().isEmpty());
		;
		
		choiceSrodowisko.valueProperty().addListener(fieldUpdate);
		choiceSrodowisko.valueProperty().addListener((obs, oldVal, newVal) -> {
			if(oldVal != newVal) {
				comboUzytkownik.getItems().clear();
				comboUzytkownik.getItems().addAll(getUzytk.apply(choiceSrodowisko));
			}
			
		});
		comboUzytkownik.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
			comboUzytkownik.setValue(comboUzytkownik.getEditor().getText());
			fieldUpdate.changed(obs, oldVal, newVal);
		});
		passField.textProperty().addListener(fieldUpdate);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			return dialogButton == logonButtonType
					? new Pair<>(choiceSrodowisko.getSelectionModel().getSelectedItem(),
							comboUzytkownik.getSelectionModel().getSelectedItem())
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