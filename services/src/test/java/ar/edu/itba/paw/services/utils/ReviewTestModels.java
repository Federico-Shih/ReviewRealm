package ar.edu.itba.paw.services.utils;

import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;

public class ReviewTestModels {

    private ReviewTestModels(){

    }
    private static final int REVIEW_SUPERGAMEA_ID = 1;
    private static final String REVIEW_SUPERGAMEA_TITLE = "title1";
    private static final String REVIEW_SUPERGAMEA_CONTENT = "content1";
    private static final String REVIEW_SUPERGAMEA_CREATED_DATE = "2090-07-15";
    private static final int REVIEW_SUPERGAMEA_RATING = 7;
    private static final Difficulty REVIEW_SUPERGAMEA_DIFF = Difficulty.HARD;
    private static final Platform REVIEW_SUPERGAMEA_PLAT = Platform.PC;
    private static final double REVIEW_SUPERGAMEA_HS = 10.0;
    private static final boolean REVIEW_SUPERGAMEA_REP = true;
    private static final boolean REVIEW_SUPERGAMEA_COMP = true;
    private static final int REVIEW_SUPERGAMEA_LIKES = 10;
    private static final int REVIEW_SUPERGAMEA_DISLIKES = 1;
    private static final Game SUPERGAMEA = GameTestModels.getSuperGameA();
    private static final User USER1 = UserTestModels.getUser1();

    private static final int REVIEW_SUPERGAMEB_ID = 2;
    private static final String REVIEW_SUPERGAMEB_TITLE = "title2";
    private static final String REVIEW_SUPERGAMEB_CONTENT = "content2";
    private static final String REVIEW_SUPERGAMEB_CREATED_DATE = "2090-07-18";
    private static final int REVIEW_SUPERGAMEB_RATING = 10;
    private static final Difficulty REVIEW_SUPERGAMEB_DIFF = Difficulty.EASY;
    private static final Platform REVIEW_SUPERGAMEB_PLAT = Platform.PS;
    private static final double REVIEW_SUPERGAMEB_HS = 5.0;
    private static final boolean REVIEW_SUPERGAMEB_REP = true;
    private static final boolean REVIEW_SUPERGAMEB_COMP = false;
    private static final int REVIEW_SUPERGAMEB_LIKES = 5;
    private static final int REVIEW_SUPERGAMEB_DISLIKES = 15;
    private static final Game SUPERGAMEB= GameTestModels.getSuperGameB();
    private static final User USER2 = UserTestModels.getUser2();

    private static final int REVIEW_SUPERGAMEB_ID2 = 2;
    private static final String REVIEW_SUPERGAMEB_TITLE2 = "title3";
    private static final String REVIEW_SUPERGAMEB_CONTENT2 = "content3";
    private static final String REVIEW_SUPERGAMEB_CREATED_DATE2 = "2090-07-31";
    private static final int REVIEW_SUPERGAMEB_RATING2 = 5;
    private static final Difficulty REVIEW_SUPERGAMEB_DIFF2 = Difficulty.MEDIUM;
    private static final Platform REVIEW_SUPERGAMEB_PLAT2 = Platform.PS;
    private static final double REVIEW_SUPERGAMEB_HS2 = 30.0;
    private static final boolean REVIEW_SUPERGAMEB_REP2 = false;
    private static final boolean REVIEW_SUPERGAMEB_COMP2 = true;
    private static final int REVIEW_SUPERGAMEB_LIKES2 = 0;
    private static final int REVIEW_SUPERGAMEB_DISLIKES2 = 1;
    private static final User USER3 = UserTestModels.getUser3();

    private static final int REVIEW_CREATE_ID = 4;
    private static final String REVIEW_CREATE_TITLE = "title4";
    private static final String REVIEW_CREATE_CONTENT = "content4";
    private static final String REVIEW_CREATE_CREATED_DATE = "2090-08-01";
    private static final int REVIEW_CREATE_RATING = 10;
    private static final Difficulty REVIEW_CREATE_DIFF = Difficulty.EASY;
    private static final Platform REVIEW_CREATE_PLAT = Platform.PS;
    private static final double REVIEW_CREATE_HS = 5.0;
    private static final boolean REVIEW_CREATE_REP = true;
    private static final boolean REVIEW_CREATE_COMP = false;
    private static final int REVIEW_CREATE_LIKES = 5;
    private static final int REVIEW_CREATE_DISLIKES = 15;
    private static final Game SUBNAUTICA2= GameTestModels.getSubnautica2();

    private static final String REVIEW_UPDATE_TITLE="Updated Title";
    private static final String REVIEW_UPDATE_CONTENT = "Updated Content";
    private static final int REVIEW_UPDATE_RATING = 4;
    private static final Difficulty REVIEW_UPDATE_DIFF = Difficulty.EASY;
    private static final double REVIEW_UPDATE_HS= 60.6;
    private static final Platform REVIEW_UPDATE_PLAT = Platform.PC;
    private static final boolean REVIEW_UPDATE_COMP = true;
    private static final boolean REVIEW_UPDATE_REP = false;


    public static Review getReview1(){
        return new Review(REVIEW_SUPERGAMEA_ID,USER1,REVIEW_SUPERGAMEA_TITLE,REVIEW_SUPERGAMEA_CONTENT, LocalDateTime.parse(REVIEW_SUPERGAMEA_CREATED_DATE),REVIEW_SUPERGAMEA_RATING,SUPERGAMEA,REVIEW_SUPERGAMEA_DIFF,REVIEW_SUPERGAMEA_HS,REVIEW_SUPERGAMEA_PLAT,REVIEW_SUPERGAMEA_COMP,REVIEW_SUPERGAMEA_REP,REVIEW_SUPERGAMEA_LIKES,REVIEW_SUPERGAMEA_DISLIKES);
    }
    public static Review getReview2(){
        return new Review(REVIEW_SUPERGAMEB_ID,USER2,REVIEW_SUPERGAMEB_TITLE,REVIEW_SUPERGAMEB_CONTENT, LocalDateTime.parse(REVIEW_SUPERGAMEB_CREATED_DATE),REVIEW_SUPERGAMEB_RATING,SUPERGAMEB,REVIEW_SUPERGAMEB_DIFF,REVIEW_SUPERGAMEB_HS,REVIEW_SUPERGAMEB_PLAT,REVIEW_SUPERGAMEB_COMP,REVIEW_SUPERGAMEB_REP,REVIEW_SUPERGAMEB_LIKES,REVIEW_SUPERGAMEB_DISLIKES);
    }
    public static Review getReview3(){
        return new Review(REVIEW_SUPERGAMEB_ID2,USER3,REVIEW_SUPERGAMEB_TITLE2,REVIEW_SUPERGAMEB_CONTENT2, LocalDateTime.parse(REVIEW_SUPERGAMEB_CREATED_DATE2),REVIEW_SUPERGAMEB_RATING2,SUPERGAMEB,REVIEW_SUPERGAMEB_DIFF2,REVIEW_SUPERGAMEB_HS2,REVIEW_SUPERGAMEB_PLAT2,REVIEW_SUPERGAMEB_COMP2,REVIEW_SUPERGAMEB_REP2,REVIEW_SUPERGAMEB_LIKES2,REVIEW_SUPERGAMEB_DISLIKES2);
    }
    public static Review getCreateReview(){
        return new Review(REVIEW_CREATE_ID,USER1,REVIEW_CREATE_TITLE,REVIEW_CREATE_CONTENT, LocalDateTime.parse(REVIEW_CREATE_CREATED_DATE),REVIEW_CREATE_RATING,SUBNAUTICA2,REVIEW_CREATE_DIFF,REVIEW_CREATE_HS,REVIEW_CREATE_PLAT,REVIEW_CREATE_COMP,REVIEW_CREATE_REP,REVIEW_CREATE_LIKES,REVIEW_CREATE_DISLIKES);
    }
    public static SaveReviewDTO getUpdateReview(){
        return new SaveReviewDTO(REVIEW_UPDATE_TITLE,REVIEW_UPDATE_CONTENT,REVIEW_UPDATE_RATING,REVIEW_UPDATE_DIFF,REVIEW_UPDATE_HS,REVIEW_UPDATE_PLAT,REVIEW_UPDATE_COMP,REVIEW_UPDATE_REP);
    }


}
