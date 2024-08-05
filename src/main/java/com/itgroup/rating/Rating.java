package com.itgroup.rating;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Rating {

    private ImageView[] starImages = new ImageView[5];
    private Image starFill;
    private Image starHalf;
    private Image starEmpty;

    public Rating(ImageView[] starImages, Image starFill, Image starHalf, Image starEmpty) {
        this.starImages = starImages;
        this.starFill = starFill;
        this.starHalf = starHalf;
        this.starEmpty = starEmpty;
    }

    public int getRating(double posX,  double widthPerRating, int maxRating) {
        // posX : 현재 이벤트 발생시키는 노드의 상대 마우스 x좌표
        return (int)(Math.min(posX / widthPerRating + 1.0, maxRating));
    }

    public void setStarImage(double score) {
        int rating = (int) (score / 2);
        // 한개짜리 별 이미지 채우기
        for (int i = 0; i < rating; i++) {
            starImages[i].setImage(starFill);
        }

        int emptyStarBeginIndx = rating;
        // 반개짜리 별 이미지 채우기
        if((int)score % 2  == 1) {
            emptyStarBeginIndx++;
            starImages[rating].setImage(starHalf);
        }

        // 비어있는 별 이미지 채우기
        for (int i = emptyStarBeginIndx; i < starImages.length; i++) {
            starImages[i].setImage(starEmpty);
        }
    }
}
