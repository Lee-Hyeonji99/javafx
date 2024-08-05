package com.itgroup.controller;

import com.itgroup.bean.Movie;
import com.itgroup.dao.MovieDao;
import com.itgroup.rating.Rating;
import com.itgroup.utility.Utility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.itgroup.utility.Utility.*;

public class ReviseMovieController implements Initializable {

    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbNation;
    @FXML private TextField txtComments;
    @FXML private DatePicker datePicker;

    /* ~ 평점 관련 변수 정의 시작 ~ */
    @FXML private HBox hBoxRating;

    @FXML private ImageView imgStar1;
    @FXML private ImageView imgStar2;
    @FXML private ImageView imgStar3;
    @FXML private ImageView imgStar4;
    @FXML private ImageView imgStar5;

    private double widthPerRating;
    private int oldRating;

    private ImageView[] starImages = new ImageView[5];
    private Rating rating;

    private Image starFill;
    private Image starHalf;
    private Image starEmpty;
    /* ~ 평점 관련 변수 정의 끝 ~ */

    private MovieDao dao = new MovieDao();
    private Movie selectedMovie;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initRating();

        // 콤보박스 옵션 등록
        cmbNation.getItems().setAll(Utility.getNationList());
    }

    // 수정 폼이 열릴 때 MovieController 에서 Movie 값 넘겨주는 메서드(초기화)
    public void initMovieData(Movie movie) {
        // fxml 데이터에 DB 값 불러와서 세팅하기
        selectedMovie = movie;

        txtName.setText(selectedMovie.getName());
        cmbNation.setValue(selectedMovie.getNation());
        oldRating = selectedMovie.getRating();
        rating.setStarImage(oldRating);
        txtComments.setText(selectedMovie.getComments());
        datePicker.setValue(getDatePickerValue(selectedMovie.getViewingDate()));
    }

    private void initRating() {
        // 별 반개를 점수 1점으로 함
        widthPerRating = imgStar1.getFitWidth() * 0.5;

        starImages[0] = imgStar1;
        starImages[1] = imgStar2;
        starImages[2] = imgStar3;
        starImages[3] = imgStar4;
        starImages[4] = imgStar5;

        // 별 이미지 로드
        starFill = new Image(getClass().getResource(Utility.IMAGE_PATH + "star_fill.png").toString());
        starHalf = new Image(getClass().getResource(Utility.IMAGE_PATH + "star_half.png").toString());
        starEmpty = new Image(getClass().getResource(Utility.IMAGE_PATH + "star.png").toString());

        rating = new Rating(starImages, starFill, starHalf, starEmpty);
    }

    // 수정 버튼
    public void onSubmitButtonClicked(ActionEvent event) {
        // 유효성 검사 (평점, 영화제목, 한줄평, 국가 )
        if(isNotValidData()) {
            showAlert(Alert.AlertType.WARNING, "유효하지 않은 데이터",
                    "유효한 값을 입력해 주세요", "영화제목 : 1자 이상 20자 이하\n평점 : 1점 이상\n한줄평 : 1자 이상 30자 이하\n국가 : 필수 선택\n관람일 : 미래 선택 불가");
            return;
        }

        // 데이터베이스 값 수정 실패
        if(!updateData()) {
            Utility.showAlert(Alert.AlertType.WARNING, "수정 실패", "수정에 실패하였습니다.", "");
            return;
        }

        // 현재 창 닫기
        Utility.closeWindow(event);
        showAlert(Alert.AlertType.INFORMATION, "수정 성공", "수정되었습니다.", "");
    }

    /**
     *  유효한 데이터
     *  평점 : 1점 이상
     *  영화제목 : 공백 아닐 경우
     *  한줄평 : 공백 아닐 경우
     *  국가 : 인덱스 >= 0
     *  관람일 : 오늘까지의 날짜만 가능
     */
    private boolean isNotValidData() {
        return oldRating < 1 ||
                isNotValidLength(txtName.getText(), 1, 40) || isNotValidLength(txtComments.getText(), 1, 60) ||
                cmbNation.getSelectionModel().getSelectedIndex() < 0 || datePicker.getValue().isAfter(LocalDate.now());
    }

    private boolean updateData() {
        Movie movie = new Movie();

        movie.setId(selectedMovie.getId());
        movie.setName(txtName.getText());
        movie.setNation(cmbNation.getValue());
        movie.setViewingDate(getDatePickerValueToString(datePicker.getValue()));
        movie.setComments(txtComments.getText());
        movie.setRating(oldRating);

        return dao.updateData(movie);
    }

    // 취소 버튼
    public void onCancelButtonClicked(ActionEvent event) {
        // 현재 창 닫기
        Utility.closeWindow(event);
    }

    /* ~ 마우스 x좌표에 따른 평점 처리 시작 ~ */

    // On Mouse Clicked
    public void onRatingImageClicked(MouseEvent mouseEvent) {
        // 클릭 시점에 점수 백업
        oldRating = rating.getRating(mouseEvent.getX(), widthPerRating, Utility.MAX_RATING);
    }

    // On Mouse Exited
    public void onRatingImageEndEntered(MouseEvent mouseEvent) {
        // 별 이미지 롤백
        rating.setStarImage(oldRating);
    }

    // On Mouse Moved
    public void onRatingImageMouseMoved(MouseEvent mouseEvent) {
        rating.setStarImage(rating.getRating(mouseEvent.getX(), widthPerRating, Utility.MAX_RATING));
    }

    /* ~ 마우스 x좌표에 따른 평점 처리 끝 ~ */
}
