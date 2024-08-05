package com.itgroup.controller;

import com.itgroup.bean.Movie;
import com.itgroup.dao.MovieDao;
import com.itgroup.utility.Paging;
import com.itgroup.utility.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.itgroup.utility.Utility.*;

public class MovieController implements Initializable {

    @FXML private ComboBox<String> cmbNation;

    @FXML private Label lblPagingStatus;
    @FXML private Pagination pagination;

    @FXML private Button btnDelete;

    @FXML private TableView<Movie> tableView;

    private double lastClickedTime = 0.0;
    private int lastSelectedIndex;
    private final double DOUBLE_CLICKED_THRESHOLD_TIME = 300; // 더블클릭 허용 시간차(ms)

    private double TABLE_HEADER_HEIGHT = 25;

    private MovieDao dao = new MovieDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 콤보박스 옵션 등록
        cmbNation.getItems().add("모든 국가"); // 인덱스 0
        cmbNation.getItems().addAll(Utility.getNationList());

        // 페이지네이션 이벤트 핸들러 등록
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> updatePage());

        initTableColumnNames();

        // 현재 페이지 데이터 불러오기
        updatePage();
    }

    // 콤보박스 값 또는 현재 페이지 인덱스 변경시 호출되는 메서드
    private void updatePage() {
        String mode = cmbNation.getSelectionModel().getSelectedItem();
        mode = (cmbNation.getSelectionModel().getSelectedIndex() < 1) ? null : mode;

        int totalCount = dao.getTotalCount(mode);
        Paging paging = new Paging(String.valueOf(pagination.getCurrentPageIndex() + 1), "10", totalCount, null, mode, null);

        // 데이터 업데이트
        updatePageData(paging, totalCount);

        // "삭제" 버튼 비활성화
        btnDelete.setVisible(false);
    }

    // 테이블 뷰의 column 설정, 객체 연결
    private void initTableColumnNames() {
        TableColumn tableColumn = null;

        String[] tableViewColumns = {"등록번호", "영화 제목", "국가", "한줄평", "관람일", "평점"};
        String[] columns = {"id", "name", "nation", "comments", "viewingDate", "rating"};
        for (int i = 0; i < columns.length; i++) {
            tableColumn = tableView.getColumns().get(i);
            tableColumn.setText(tableViewColumns[i]);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(columns[i]));
        }

        // 마지막 tableColumn('평점') 출력 형식 지정
        tableColumn.setCellFactory(new Callback<TableColumn<Movie, Integer>, javafx.scene.control.TableCell<Movie, Integer>>() {
            @Override
            public javafx.scene.control.TableCell<Movie, Integer> call(TableColumn<Movie, Integer> param) {
                return new javafx.scene.control.TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item + " / " + Utility.MAX_RATING);
                        }
                    }
                };
            }
        });
    }

    private void updatePageData(Paging paging, int totalCount) {
        // 테이블 데이터 업데이트
        List<Movie> movies = dao.getPaginationData(paging);
        ObservableList<Movie> data = FXCollections.observableArrayList(movies);
        tableView.setItems(data);

        // 라벨 텍스트, 총 페이지수 업데이트
        String pageStatus = String.format("총 %d건[%d/%d]", totalCount, paging.getPageNumber(), paging.getTotalPage());
        lblPagingStatus.setText(pageStatus);

        // pagination.setPageCount(2); // pageCount 가 이전 값과 같으면 그대로 두고, 다르면 0으로 초기화한다
        pagination.setPageCount(paging.getTotalPage());
    }

    // 테이블뷰 클릭 이벤트
    public void onTableRowClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getY() < TABLE_HEADER_HEIGHT) { return; }

        Movie selectedItem = tableView.getSelectionModel().getSelectedItem();
        if(selectedItem == null) { return; }

        // "삭제" 버튼 활성화
        btnDelete.setVisible(true);

        boolean doubleClicked = tableView.getSelectionModel().getSelectedIndex() == lastSelectedIndex
                && System.currentTimeMillis() - lastClickedTime <= DOUBLE_CLICKED_THRESHOLD_TIME;

        // 수정창 열기
        if(doubleClicked) {
            onTableRowDoubleClicked(selectedItem);
        }

        lastClickedTime = System.currentTimeMillis();
        lastSelectedIndex = tableView.getSelectionModel().getSelectedIndex();
    }

    // 테이블뷰 더블클릭 이벤트
    private void onTableRowDoubleClicked(Movie selectedItem) {

        // reviseMovie.fxml 열기
        FXMLLoader loader = getFXMLLoader("reviseMovie.fxml");
        Parent root = loadFXML(loader);

        // 컨트롤러에 선택된 movie 전달
        ReviseMovieController controller = loader.getController();
        controller.initMovieData(selectedItem);

        Stage stage = craeteStage(root, "관람평 수정 화면", false);

        // 수정 화면 창이 닫힐 때, 데이터를 새로고침함
        stage.setOnHiding(event -> updatePage());

        stage.initModality(Modality.APPLICATION_MODAL);  // 창을 모달로 표시하여 다른 창과 상호작용하거나 코드 실행되지 않음
        stage.showAndWait();
    }

    // 추가 버튼 클릭
    public void onAddButtonClicked(ActionEvent event) {
        // addMovie.fxml 열기
        Stage stage = craeteStage("addMovie.fxml", "관람평 등록 화면", false);

        // 등록 화면 창이 닫힐 때, 데이터를 새로고침함
        stage.setOnHiding(e -> setPageIndex(0));

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    // 삭제 버튼 클릭
    public void onDeleteButtonClicked(ActionEvent event) {
        Movie selectedItem = tableView.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            return;
        }

        // 삭제 확인 alert
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, "삭제 확인", "삭제하시겠습니까?", "");
        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.get() == ButtonType.OK) {
            if(!dao.deleteData(selectedItem.getId())) {
                return;
            }

            showAlert(Alert.AlertType.INFORMATION, "삭제 성공", "삭제되었습니다.", "");
            updatePage();
        }
    }

    public void onCmbNationChanged(ActionEvent event) {
        setPageIndex(0);
    }

    public void setPageIndex(int pageIndex) {
        if(pagination.getCurrentPageIndex() == pageIndex) {
            updatePage();
            return;
        }
        pagination.setCurrentPageIndex(pageIndex);
    }
}
