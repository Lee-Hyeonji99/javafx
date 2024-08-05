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

public class AddMovieController implements Initializable {

    @FXML private TextField txtName; // 영화 제목
    @FXML private ComboBox<String> cmbNation; // 국가
    @FXML private TextField txtComments; // 한줄평
    @FXML private DatePicker datePicker; // 관람일

    /* ~ 평점 관련 변수 정의 시작 ~ */

    @FXML private HBox hBoxRating;

    @FXML private ImageView imgStar1;
    @FXML private ImageView imgStar2;
    @FXML private ImageView imgStar3;
    @FXML private ImageView imgStar4;
    @FXML private ImageView imgStar5;

    private double widthPerRating;
    private int oldRating; // 클릭되거나 기본값일 때 저장되는 평점

    private ImageView[] starImages = new ImageView[5];
    private Rating rating;

    private Image starFill;
    private Image starHalf;
    private Image starEmpty;

    /* ~ 평점 관련 변수 정의 끝 ~ */

    private MovieDao dao = new MovieDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initRating();

        // 관람일 오늘 날짜로 기본값 설정
        datePicker.setValue(LocalDate.now());

        // 콤보박스 옵션 등록
        cmbNation.getItems().setAll(Utility.getNationList());
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

    // 등록 버튼
    public void onSubmitButtonClicked(ActionEvent event) {
        // 유효성 검사 (평점, 영화제목, 한줄평, 국가 )
        if(isNotValidData()) {
            showAlert(Alert.AlertType.WARNING, "유효하지 않은 데이터",
                    "유효한 값을 입력해 주세요", "영화제목 : 1자 이상 20자 이하\n평점 : 1점 이상\n한줄평 : 1자 이상 30자 이하\n국가 : 필수 선택\n관람일 : 미래 선택 불가");
            return;
        }

        if(!insertData()) {
            showAlert(Alert.AlertType.WARNING, "등록 실패", "등록에 실패하였습니다.", "");
            return;
        }

        // 현재 창 닫기
        closeWindow(event);
        showAlert(Alert.AlertType.INFORMATION, "등록 성공", "등록되었습니다.", "");
    }

    // 취소 버튼
    public void onCancelButtonClicked(ActionEvent event) {
        // 현재 창 닫기
        Utility.closeWindow(event);
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

    private boolean insertData() {
        Movie movie = new Movie();
        movie.setName(txtName.getText());
        movie.setNation(cmbNation.getValue());
        movie.setRating(oldRating);
        movie.setComments(txtComments.getText());
        movie.setViewingDate(getDatePickerValueToString(datePicker.getValue()));

        return dao.insertData(movie);
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
