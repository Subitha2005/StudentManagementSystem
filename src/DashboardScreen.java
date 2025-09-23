import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Map;

public class DashboardScreen extends Application {

    private final StudentDAO dao = new StudentDAO();
    private TableView<Student> table = new TableView<>();
    private Label totalLbl = new Label("0");
    private PieChart genderChart = new PieChart();
    private BarChart<String, Number> yearChart;

    @Override
    public void start(Stage stage) {
        // --- Table columns ---
        TableColumn<Student, String> c1 = new TableColumn<>("Name");
        c1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> c2 = new TableColumn<>("Roll");
        c2.setCellValueFactory(new PropertyValueFactory<>("roll"));

        TableColumn<Student, String> c3 = new TableColumn<>("Grade");
        c3.setCellValueFactory(new PropertyValueFactory<>("grade"));

        TableColumn<Student, String> c4 = new TableColumn<>("Gender");
        c4.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Student, Integer> c5 = new TableColumn<>("Year");
        c5.setCellValueFactory(new PropertyValueFactory<>("year"));

        table.getColumns().addAll(c1, c2, c3, c4, c5);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Charts ---
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Year");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Students");

        yearChart = new BarChart<>(xAxis, yAxis);
        yearChart.setTitle("Students per Year");
        yearChart.setLegendVisible(false);
        yearChart.setPrefHeight(250);

        genderChart.setTitle("Gender Distribution");
        genderChart.setLegendVisible(true);
        genderChart.setLabelsVisible(true);
        genderChart.setPrefHeight(250);

        // --- Buttons ---
        Button addBtn = new Button("➕ Add");
        addBtn.getStyleClass().add("primary-btn");
        addBtn.setOnAction(e -> showStudentForm(null));

        Button editBtn = new Button("✏️ Edit");
        editBtn.getStyleClass().add("secondary-btn");
        editBtn.setOnAction(e -> {
            Student s = table.getSelectionModel().getSelectedItem();
            if (s != null) showStudentForm(s);
            else showAlert("No Selection", "Please select a student to edit.");
        });

        Button delBtn = new Button("🗑 Delete");
        delBtn.getStyleClass().add("danger-btn");
        delBtn.setOnAction(e -> {
            Student s = table.getSelectionModel().getSelectedItem();
            if (s != null) {
                try {
                    dao.deleteStudent(s.getId());
                    refresh();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Failed to delete student.");
                }
            } else {
                showAlert("No Selection", "Please select a student to delete.");
            }
        });

        HBox btns = new HBox(15, addBtn, editBtn, delBtn);
        btns.setPadding(new Insets(10));

        // --- Sidebar ---
        Label dashboardTitle = new Label("📊 Dashboard");
        dashboardTitle.getStyleClass().add("heading");

        VBox sidebar = new VBox(20,
                dashboardTitle,
                new Label("Total Students:"),
                totalLbl,
                genderChart
        );
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(280);
        sidebar.getStyleClass().add("sidebar");

        // --- Right content ---
        VBox content = new VBox(20, yearChart, table, btns);
        content.setPadding(new Insets(20));

        // --- Root layout ---
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(content);
        root.getStyleClass().add("dashboard-root");

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Student Management Dashboard");
        stage.show();

        refresh();
    }

    // --- Refresh data ---
    private void refresh() {
        try {
            ObservableList<Student> students = FXCollections.observableArrayList(dao.getAllStudents());
            table.setItems(students);

            totalLbl.setText(String.valueOf(students.size()));

            // Gender chart
            genderChart.getData().clear();
            Map<String, Integer> genders = dao.getGenderCounts();
            for (Map.Entry<String, Integer> entry : genders.entrySet()) {
                genderChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            // Year chart
            yearChart.getData().clear();
            Map<Integer, Integer> years = dao.getStudentsByYear();

            // Create one series per year (for different colors)
            int colorIndex = 0;
            for (Map.Entry<Integer, Integer> entry : years.entrySet()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Year " + entry.getKey()); // legend label
                series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
                yearChart.getData().add(series);
                colorIndex++;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load student data.");
        }
    }

    // --- Form for add/edit student ---
   private void showStudentForm(Student s) {
    Stage stage = new Stage();
    stage.setTitle(s == null ? "Add Student" : "Edit Student");

    // --- Fields ---
    TextField nameField = new TextField();
    nameField.setPromptText("Name");
    nameField.getStyleClass().add("input-field");

    TextField rollField = new TextField();
    rollField.setPromptText("Roll Number");
    rollField.getStyleClass().add("input-field");

    TextField gradeField = new TextField();
    gradeField.setPromptText("Grade (A-F)");
    gradeField.getStyleClass().add("input-field");

    ComboBox<String> genderBox = new ComboBox<>();
    genderBox.getItems().addAll("Male", "Female");
    genderBox.setPromptText("Gender");
    genderBox.getStyleClass().add("input-field");

    TextField yearField = new TextField();
    yearField.setPromptText("Year");
    yearField.getStyleClass().add("input-field");

    // --- Pre-fill if editing ---
    if (s != null) {
        nameField.setText(s.getName());
        rollField.setText(s.getRoll());
        gradeField.setText(s.getGrade());
        genderBox.setValue(s.getGender());
        yearField.setText(String.valueOf(s.getYear()));
    }

    // --- Buttons ---
    Button saveBtn = new Button("Save");
    saveBtn.getStyleClass().add("primary-btn");

    Button cancelBtn = new Button("Cancel");
    cancelBtn.getStyleClass().add("secondary-btn");

    saveBtn.setOnAction(e -> {
        String name = nameField.getText(),
               roll = rollField.getText(),
               grade = gradeField.getText();
        String gender = genderBox.getValue();
        int year;

        try {
            year = Integer.parseInt(yearField.getText());
        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Year must be a number.");
            return;
        }

        if (name.isEmpty() || roll.isEmpty() || grade.isEmpty() || gender == null) {
            showAlert("Input Error", "All fields are required.");
            return;
        }

        try {
            if (s == null) {
                dao.addStudent(new Student(name, roll, grade, gender, year));
            } else {
                s.setName(name);
                s.setRoll(roll);
                s.setGrade(grade);
                s.setGender(gender);
                s.setYear(year);
                dao.updateStudent(s);
            }
            stage.close();
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to save student.");
        }
    });

    cancelBtn.setOnAction(e -> stage.close());

    // --- Layout ---
    // --- Layout ---
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(20));
    grid.setHgap(15);
    grid.setVgap(15);
    grid.setAlignment(Pos.CENTER);

    // --- Column constraints ---
    ColumnConstraints col1 = new ColumnConstraints();
    col1.setHalignment(HPos.RIGHT);  // align labels to the right
    col1.setPercentWidth(30);

    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(70);

    grid.getColumnConstraints().addAll(col1, col2);

    // --- Make text fields stretch properly ---
    nameField.setMaxWidth(Double.MAX_VALUE);
    rollField.setMaxWidth(Double.MAX_VALUE);
    gradeField.setMaxWidth(Double.MAX_VALUE);
    genderBox.setMaxWidth(Double.MAX_VALUE);
    yearField.setMaxWidth(Double.MAX_VALUE);

    // --- Add controls ---
    grid.add(new Label("Name:"), 0, 0);    grid.add(nameField, 1, 0);
    grid.add(new Label("Roll:"), 0, 1);    grid.add(rollField, 1, 1);
    grid.add(new Label("Grade:"), 0, 2);   grid.add(gradeField, 1, 2);
    grid.add(new Label("Gender:"), 0, 3);  grid.add(genderBox, 1, 3);
    grid.add(new Label("Year:"), 0, 4);    grid.add(yearField, 1, 4);

    HBox btnBox = new HBox(15, saveBtn, cancelBtn);
    btnBox.setAlignment(Pos.CENTER);
    grid.add(btnBox, 0, 5, 2, 1);

    VBox card = new VBox(grid);
    card.setPadding(new Insets(25));
    card.getStyleClass().add("form-card");

    BorderPane root = new BorderPane();
    root.setCenter(card);

    Scene scene = new Scene(root, 400, 350);
    scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
}

    // --- Alert helper ---
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
