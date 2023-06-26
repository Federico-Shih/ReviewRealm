package ar.edu.itba.paw.persistence.tests.utils;

import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReportTestModels {

    private ReportTestModels(){

    }

    private final static LocalTime TIME = LocalTime.now();
    private final static long REPORT1_ID = 1L;
    private final static User REPORT1_REPORTED_USER = UserTestModels.getUser1();
    private final static User REPORT1_REPORTER = UserTestModels.getUser2();
    private final static ReportReason REPORT1_REASON = ReportReason.SPAM;
    private final static Review REPORT1_REPORTED_REVIEW = ReviewTestModels.getReview1();
    private final static LocalDateTime REPORT1_SUBMITTED_DATE = LocalDateTime.of(LocalDate.parse("2090-11-30"),TIME);
    private final static long REPORT2_ID = 2L;
    private final static User REPORT2_REPORTED_USER = UserTestModels.getUser2();
    private final static User REPORT2_REPORTER = UserTestModels.getUser3();
    private final static ReportReason REPORT2_REASON = ReportReason.PIRACY;
    private final static Review REPORT2_REPORTED_REVIEW = ReviewTestModels.getReview2();
    private final static LocalDateTime REPORT2_SUBMITTED_DATE = LocalDateTime.of(LocalDate.parse("2090-12-15"),TIME);
    private final static long REPORT3_ID = 3;
    private final static User REPORT3_REPORTED_USER = UserTestModels.getUser2();
    private final static User REPORT3_REPORTER = UserTestModels.getUser3();
    private final static ReportReason REPORT3_REASON = ReportReason.SPAM;
    private final static Review REPORT3_REPORTED_REVIEW = ReviewTestModels.getReview2();
    private final static LocalDateTime REPORT3_SUBMITTED_DATE = LocalDateTime.of(LocalDate.parse("2090-07-31"),TIME);
    private final static User REPORT_CREATE_REPORTER = UserTestModels.getUser4();
    private final static ReportReason REPORT_CREATE_REASON = ReportReason.PRIVACY;
    private final static Review REPORT_CREATE_REPORTED_REVIEW = ReviewTestModels.getReview3();
    private final static LocalDateTime REPORT_CREATE_SUBMITTED_DATE = LocalDateTime.of(LocalDate.parse("2090-12-15"),TIME);

    private final static User COMMON_REPORTER = UserTestModels.getUser3();
    private final static Review COMMON_REPORTED_REVIEW = ReviewTestModels.getReview2();
    public static Report getReport1(){
        return new Report(REPORT1_ID,REPORT1_REPORTER,REPORT1_REPORTED_REVIEW,REPORT1_REASON,REPORT1_SUBMITTED_DATE,false);
    }
    public static Report getReport2(){
        return new Report(REPORT2_ID,REPORT2_REPORTER,REPORT2_REPORTED_REVIEW,REPORT2_REASON,REPORT2_SUBMITTED_DATE,false);
    }
    public static Report getCreateReport(){
        return new Report(REPORT_CREATE_REPORTER,REPORT_CREATE_REPORTED_REVIEW,REPORT_CREATE_REASON,REPORT_CREATE_SUBMITTED_DATE);
    }
    public static Report getReport3(){
        return new Report(REPORT3_ID,REPORT3_REPORTER,REPORT3_REPORTED_REVIEW,REPORT3_REASON,REPORT3_SUBMITTED_DATE,false);
    }
    public static User getCommonReporter(){
        return COMMON_REPORTER;
    }
    public static Review getCommonReportedReview(){
        return COMMON_REPORTED_REVIEW;
    }

}
