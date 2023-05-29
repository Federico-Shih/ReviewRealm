package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class V20230527111800__deleting_author_review_feedback extends BaseJavaMigration {
    public void migrate(Context context) throws Exception{
        Connection databaseConn = context.getConnection();
        try(Statement select = databaseConn.createStatement()) {
            try (ResultSet rows = select.executeQuery("SELECT userid,reviewid,rf.feedback FROM reviews r JOIN reviewfeedback rf on r.id = rf.reviewid " +
                    "WHERE rf.userid = r.authorid")) {
                while(rows.next()){
                    long userid = rows.getLong("userid");
                    long reviewid = rows.getLong("reviewid");
                    String feedback = rows.getString("feedback");

                    try( PreparedStatement deleteReviewFeedback = databaseConn.prepareStatement("DELETE FROM reviewfeedback WHERE userid = ? AND reviewid = ?");){
                        deleteReviewFeedback.setLong(1,userid);
                        deleteReviewFeedback.setLong(2,reviewid);
                        deleteReviewFeedback.execute();
                        try(PreparedStatement updateReview = databaseConn.prepareStatement("UPDATE reviews SET likes = likes - ? , dislikes = dislikes - ? WHERE id = ?")){
                            if(feedback.equals("LIKE")){
                                updateReview.setInt(1,1);
                                updateReview.setInt(2,0);
                            }else{
                                updateReview.setInt(1,0);
                                updateReview.setInt(2,1);
                            }
                            updateReview.setLong(3,reviewid);
                            updateReview.execute();
                            try(PreparedStatement updateReputation = databaseConn.prepareStatement("UPDATE users SET reputation = reputation + ? WHERE id = ?")) {
                                updateReputation.setInt(1,(feedback.equals("LIKE") ? -1 : 1));
                                updateReputation.setLong(2, userid);
                                updateReputation.execute();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                         }
                    }catch (Exception e) {
                      e.printStackTrace();
                     }
                }
            } catch (Exception e) {
            e.printStackTrace();
        }
    }catch (Exception e) {
        e.printStackTrace();
    }
    }

}
