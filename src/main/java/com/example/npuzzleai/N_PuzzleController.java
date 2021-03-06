package com.example.npuzzleai;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class N_PuzzleController implements Initializable, Runnable {
    @FXML
    private ToggleGroup difficultyToggle;
    @FXML
    private ToggleGroup algorithmToggle;
    @FXML
    private ToggleGroup goalToggle;
    @FXML
    private Canvas imgCanvas;
    @FXML
    private ImageView imgView;
    @FXML
    private ImageView goal1Image;
    @FXML
    private ImageView goal2Image;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button solveBtn;
    @FXML
    private Button playBtn;
    @FXML
    private Button jumbleBtn;
    @FXML
    private Button addImage;
    @FXML
    private Button addNumber;
    @FXML
    private Button compareBtn;
    @FXML
    private SplitMenuButton sizeMenu;
    @FXML
    private SplitMenuButton algorithmMenu;
    @FXML
    private RadioButton goal1;
    @FXML
    private RadioButton goal2;
    @FXML
    private TextField stepField;
    @FXML
    private AnchorPane displayPane;

    public AStar aStar;
    public BFS bFS;
    public Image image;
    public HandleImage handledImage;
    private int size;
    private State state;
    private State goalState;
    private int[] value;
    private Vector<int[]> result;
    private String algorithm;
    private int countStep = 0;
    private boolean isSolve = false;
    private boolean isPlay = false;
    private int approvedNodes;
    private int totalNodes;
    private long solveTime;
    private long startTime;
    private String error;
    private final Vector<Result> compareResults = new Vector<>();

    @Override
    // Tr???ng th??i kh???i t???o ban ?????u
    public void initialize(URL url, ResourceBundle resourceBundle) {
        State.heuristic = 1;
        State.goal = 1;
        size = 3;
        algorithm = "A*";
        state = new State(size);
        value = state.createGoalArray();
        goalState = new State(size);
        goalState.createGoalArray();
        displayImage(null);
        progressBar.setVisible(false);
        goal1Image.setImage(new Image(Objects.requireNonNull(N_PuzzleApplication.class.getResourceAsStream("img/goal-1.png"))));
        goal2Image.setImage(new Image(Objects.requireNonNull(N_PuzzleApplication.class.getResourceAsStream("img/goal-2.png"))));
    }
    // Lu???ng ch???y l???i gi???i
    public void run() {
        int totalStep = result.size() - 1;
        for (int i = 0; i <= totalStep; i++) {
            value = result.get(i);
            state.value = value;
            displayImage(image);
            String step = i + "/" + totalStep;
            Platform.runLater(() -> stepField.setText(step));
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(this::notSolve);
    }

    @FXML
    // Ch???n size b???ng
    public void onChangeImageSize() {
        RadioMenuItem selectedDiff = (RadioMenuItem) difficultyToggle.getSelectedToggle();
        switch (selectedDiff.getId()) {
            case "medium" -> size = 4;
            case "hard" -> size = 5;
            default -> size = 3;
        }
        sizeMenu.setText(selectedDiff.getText());
        state = new State(size);
        value = state.createGoalArray();
        goalState = new State(size);
        goalState.createGoalArray();
        displayImage(image);
    }
    // Ch???n thu???t to??n
    public void onChangeAlgorithm() {
        RadioMenuItem selectedAlgorithm = (RadioMenuItem) algorithmToggle.getSelectedToggle();
        switch (selectedAlgorithm.getId()) {
            case "heuristic1" -> {
                State.heuristic = 1;
                algorithm = "A*";
            }
            case "heuristic2" -> {
                State.heuristic = 2;
                algorithm = "A*";
            }
            case "heuristic3" -> {
                State.heuristic = 3;
                algorithm = "A*";
            }
            case "heuristic4" -> {
                State.heuristic = 4;
                algorithm = "A*";
            }
            case "heuristic5" -> {
                State.heuristic = 5;
                algorithm = "A*";
            }
            case "heuristic6" -> {
                State.heuristic = 6;
                algorithm = "A*";
            }
            default -> algorithm = "BFS";
        }
        algorithmMenu.setText(selectedAlgorithm.getText());
    }
    // Thay ?????i tr???ng th??i ????ch
    public void onChangeGoal() {
        RadioButton selectedGoal = (RadioButton) goalToggle.getSelectedToggle();
        if (Objects.equals(selectedGoal.getId(), "goal1")) {
            State.goal = 1;
        } else {
            State.goal = 2;
        }
        value = state.createGoalArray();
        goalState.createGoalArray();
        displayImage(image);
    }
    // Button th??m ???nh
    public void onAddImgBtnClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            image = new Image(file.toURI().toString());
            // Th??m ???nh nh???
            if (image.getHeight() > image.getWidth()) {
                double width = image.getWidth() * 180 / image.getHeight();
                imgView.setX((180 - width) / 2);
            } else {
                imgView.setX(0);
            }
            countStep = 0;
            stepField.setText("0");
            imgView.setImage(image);
            value = state.createGoalArray();
            displayImage(image);
        }
    }
    // Button th??m b???ng s???
    public void onAddNumberBtnClick() {
        countStep = 0;
        image = null;
        stepField.setText("0");
        imgView.setImage(null);
        value = state.createGoalArray();
        displayImage(image);
    }
    // Button tr???n ???nh
    public void onJumbleBtnClick() {
        stepField.setText("0");
        countStep = 0;
        value = state.createRandomArray();
        displayImage(image);
    }
    // Button t??m k???t qu???
    public void onSolveBtnClick() {
        countStep = 0;
        if (!isSolve) {
            if(Objects.equals(algorithm, "BFS")) {
                BFS.stop = false;
            } else {
                AStar.stop = false;
            }
            solveThread().start();
            solving();
        } else {
            if(Objects.equals(algorithm, "BFS")) {
                BFS.stop = true;
            } else {
                AStar.stop = true;
            }
            notSolve();
        }
    }
    // Button so s??nh Heuristic
    public void onCompareBtnClick() {
        algorithm = "A*";
        AStar.stop = false;
        compareThread().start();
    }
    // Button ch??i
    public void onPlayBtnClick() {
        if (!isPlay) {
            playing();
            startTime = System.currentTimeMillis();
        } else {
            countStep = 0;
            notPlay();
        }
    }
    // S??? ki???n t??? b??n ph??m
    public void onKeyPressed(KeyEvent ke) {
        if (isPlay) {
            countStep++;
            int[] tmpValue = Arrays.copyOf(value, size * size);
            switch (ke.getCode()) {
                case W -> state.UP();
                case A -> state.LEFT();
                case S -> state.DOWN();
                case D -> state.RIGHT();
                default -> value = tmpValue;            }
            if (Arrays.equals(tmpValue, value)) {
                countStep--;
            }
            if (Arrays.equals(value, goalState.value)) {
                if (countStep != 0) {
                    showResult();
                    countStep = 0;
                }
            }
            stepField.setText(String.valueOf(countStep));
            displayImage(image);
        }
    }
    // S??? ki???n click chu???t
    public void onMouseClicked(MouseEvent me) {
        if (isPlay) {
            int blank = state.posBlank(state.value);
            int x = blank % size;
            int y = blank / size;
            int mx = (int) (me.getX() / imgCanvas.getWidth() * size);
            int my = (int) (me.getY() / imgCanvas.getHeight() * size);
            countStep++;
            if (mx == x && my == y - 1) {
                state.UP();
            } else if (mx == x && my == y + 1) {
                state.DOWN();
            } else if (mx == x - 1 && my == y) {
                state.LEFT();
            } else if (mx == x + 1 && my == y) {
                state.RIGHT();
            } else {
                countStep--;
            }
            if (Arrays.equals(value, goalState.value)) {
                if (countStep != 0) {
                    showResult();
                    countStep = 0;
                }
            }
            stepField.setText(String.valueOf(countStep));
            displayImage(image);
        }
    }
    // Gi???i quy???t b??i to??n b???ng thu???t to??n A*
    public void solveAStar() {
        aStar = new AStar();
        aStar.startNode = new Node(state, 0);
        aStar.goalNode = new Node(goalState, 1);
        aStar.solve();
        result = aStar.RESULT;
        approvedNodes = aStar.approvedNodes;
        totalNodes = aStar.totalNodes;
        solveTime = aStar.time;
        error = aStar.error;
    }
    // Gi???i quy???t b??i to??n b???ng thu???t to??n BFS
    public void solveBFS() {
        bFS = new BFS();
        bFS.startNode = new Node(state, 0);
        bFS.goalNode = new Node(goalState, 0);
        bFS.solve();
        result = bFS.RESULT;
        approvedNodes = bFS.approvedNodes;
        totalNodes = bFS.totalNodes;
        solveTime = bFS.time;
        error = bFS.error;
    }
    // Lu???ng t??m ki???m l???i gi???i
    public Thread solveThread() {
        return new Thread(() -> {
            if (Objects.equals(algorithm, "BFS")) {
                solveBFS();
            } else {
                solveAStar();
            }
            // N???u t??m ???????c l???i gi???i
            if (result.size() > 1) {
                Platform.runLater(this::showAlert);
            }
            // N???u kh??ng t??m ???????c l???i gi???i
            else if(result.size() == 0 && error != null) {
                Platform.runLater(this::showWarning);
            }
            // Ng?????i ch??i ch???n d???ng t??m ki???m ho???c tr???ng th??i ban ?????u l?? tr???ng th??i ????ch
            else {
                Platform.runLater(this::notSolve);
            }
        });
    }
    // Lu???ng so s??nh Heuristic
    public Thread compareThread() {
        return new Thread(() -> {
            algorithm = "A*";
            int tmp = State.heuristic;
            goalState.createGoalArray();
            if (!Arrays.equals(value, goalState.value)) {
                Platform.runLater(this::solving);
                // Gi???i b??i to??n b???ng l???n l?????t c??c Heuristic
                for (int i = 1; i <= 6; i++) {
                    State.heuristic = i;
                    solveAStar();
                    Result result1 = new Result("H" + i, approvedNodes, totalNodes, result.size() - 1,solveTime, error);
                    compareResults.add(result1); // L??u k???t qu???
                }
                // N???u d???ng so s??nh
                if (AStar.stop) {
                    compareResults.clear();
                } else {
                    Platform.runLater(this::showCompare); // Show b???ng so s??nh
                }
            }
            State.heuristic = tmp;
        });
    }
    // Tr???ng th??i ??ang t??m ki???m
    public void solving() {
        isSolve = true;
        solveBtn.setText("D???ng");
        playBtn.setDisable(true);
        setDisable();
    }
    // Tr???ng th??i kh??ng t??m ki???m
    public void notSolve() {
        isSolve = false;
        solveBtn.setText("AI Gi???i");
        playBtn.setDisable(false);
        setEnable();
    }
    // Tr???ng th??i ng?????i ch??i
    public void playing() {
        isPlay = true;
        playBtn.setText("D???ng");
        solveBtn.setDisable(true);
        setDisable();
    }
    // Tr???ng th??i kh??ng ch??i
    public void notPlay() {
        isPlay = false;
        playBtn.setText("Ch??i");
        solveBtn.setDisable(false);
        setEnable();
    }
    // Enable c??c n??t
    private void setEnable() {
        solveBtn.setDisable(false);
        jumbleBtn.setDisable(false);
        addImage.setDisable(false);
        addNumber.setDisable(false);
        compareBtn.setDisable(false);
        sizeMenu.setDisable(false);
        algorithmMenu.setDisable(false);
        progressBar.setVisible(false);
        goal1.setDisable(false);
        goal2.setDisable(false);
    }
    // Disable c??c n??t
    private void setDisable() {
        jumbleBtn.setDisable(true);
        addImage.setDisable(true);
        addNumber.setDisable(true);
        compareBtn.setDisable(true);
        sizeMenu.setDisable(true);
        algorithmMenu.setDisable(true);
        progressBar.setVisible(true);
        goal1.setDisable(true);
        goal2.setDisable(true);
    }
    // B???ng th??ng b??o kh??ng t??m ???????c l???i gi???i
    public void showWarning() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        ButtonType closeTypeBtn = new ButtonType("????ng", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(closeTypeBtn);
        alert.setTitle("Th??ng b??o");
        alert.setHeaderText("Kh??ng t??m ???????c l???i gi???i!");
        alert.setContentText("Nguy??n nh??n: \n" + error);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(N_PuzzleApplication.class.getResourceAsStream("img/logo.png"))));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        alert.showAndWait().ifPresent(res -> notSolve());
    }
    // B???ng th??ng b??o k???t qu??? t??m ki???m
    public void showAlert() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        ButtonType runTypeBtn = new ButtonType("Ch???y", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeTypeBtn = new ButtonType("????ng", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.setTitle("Th??ng b??o");
        alert.getButtonTypes().setAll(runTypeBtn, closeTypeBtn);
        alert.setHeaderText("L???i gi???i: ");
        // K???t qu??? t??m ki???m ???????c
        alert.setContentText("Thu???t to??n s??? d???ng: " + (Objects.equals(algorithm, "BFS") ? "BFS" : "A* v???i Heuristic " + State.heuristic)  + "\n"
            + "S??? node ???? duy???t: " + approvedNodes + "\n"
            + "T???ng s??? node tr??n c??y: " + totalNodes + "\n"
            + "T???ng s??? b?????c: " + (result.size() - 1) + "\n"
            + "Th???i gian t??m ki???m: " + solveTime + "ms" + "\n"
            + "B???n c?? mu???n ch???y l???i gi???i?"
        );
        alertStyle(alert, closeTypeBtn);
        // Hi???n th??? k???t qu??? v?? ?????i ph???i h???i
        alert.showAndWait().ifPresent(res -> {
            if (res == runTypeBtn) {
                solveBtn.setDisable(true);
                Thread runResult = new Thread(this);
                runResult.start();
            } else {
                notSolve();
            }
        });
    }
    // Hi???n th??? b???ng so s??nh Heuristic
    public void showCompare() {
        Alert compare = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType runTypeBtn = new ButtonType("Ch???y", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeTypeBtn = new ButtonType("????ng", ButtonBar.ButtonData.CANCEL_CLOSE);
        compare.setTitle("Th??ng b??o");
        compare.setHeaderText("So S??nh: ");
        // Hi???n th??? k???t qu???
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(15);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        // Hi???n th??? k???t qu??? c???a t???ng Heuristic
        for (int i = 0; i < compareResults.size(); i++) {
            Result rs = compareResults.get(i);
            Label rsLabel = new Label(rs.showResult());
            GridPane.setConstraints(rsLabel, i % 3, i / 3);
            gridPane.getChildren().add(rsLabel);
        }
        // S???p x???p v?? hi???n th??? k???t qu??? so s??nh
        compareResults.sort(Comparator.comparingInt(o -> o.approved));
        Collections.reverse(compareResults);
        Label cpLabel = new Label("K???t lu???n : ");
        boolean flag = false;
        for (Result rs : compareResults) {
            if (rs.error == null) {
                cpLabel.setText(cpLabel.getText() + rs.heuristic + (rs == compareResults.lastElement() ? " " : " < "));
                flag = true;
            }
        }
        // Flag ki???m tra xem c?? t??m ???????c k???t qu??? hay kh??ng
        if (flag) {
            cpLabel.setText(cpLabel.getText() + ". B???n c?? mu???n ch???y l???i gi???i?");
            compare.getButtonTypes().setAll(runTypeBtn, closeTypeBtn);
        } else {
            cpLabel.setText(cpLabel.getText() + "Kh??ng t??m ???????c l???i gi???i!");
            compare.getButtonTypes().setAll(closeTypeBtn);
        }
        alertStyle(compare, closeTypeBtn);
        vBox.getChildren().addAll(gridPane, cpLabel);
        compare.getDialogPane().setContent(vBox);
        // Ch??? ph???i h???i
        compare.showAndWait().ifPresent(res -> {
            if (res == runTypeBtn) {
                solveBtn.setDisable(true);
                Thread runResult = new Thread(this);
                runResult.start();
            } else {
                notSolve();
            }
        });
        // Clear vector k???t qu???
        compareResults.clear();
    }
    // Show k???t qu??? ng?????i ch??i
    public void showResult() {
        long time = (System.currentTimeMillis() - startTime) / 1000;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Th??ng b??o");
        alert.setHeaderText("B???n ???? ho??n th??nh tr?? ch??i!");
        alert.setContentText("S??? b?????c gi???i: " + countStep + "\n"
            + "Th???i gian gi???i: " + (time >= 60 ? time / 60 + ":" + time % 60 : time) + "s"
        );
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(N_PuzzleApplication.class.getResourceAsStream("img/logo.png"))));
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        alert.showAndWait().ifPresent(res -> notPlay());
    }
    // Th??m icon v?? style cho b???ng l???i gi???i v?? b???ng so s??nh
    public void alertStyle(Alert alert, ButtonType closeTypeBtn) {
        // Th??m icon
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(N_PuzzleApplication.class.getResourceAsStream("img/logo.png"))));
        DialogPane dialogPane = alert.getDialogPane();
        // Th??m css
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        javafx.scene.Node closeBtn = alert.getDialogPane().lookupButton(closeTypeBtn);
        closeBtn.setId("close-btn");
    }
    // Hi???n th??? ra m??n h??nh
    public void displayImage(Image img) {
        if (img == null) {
            displayPane.setStyle("-fx-background-radius: 20px; -fx-background-color: #703838");
        } else {
            displayPane.setStyle("");
        }
        handledImage = new HandleImage(img ,size, value);
        if (state.isGoal(goalState)) {
            handledImage.win = true;
        }
        GraphicsContext gc = imgCanvas.getGraphicsContext2D();
        handledImage.paint(gc);
    }
}
