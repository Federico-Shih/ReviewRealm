package db.migration;

import org.apache.commons.lang3.RandomStringUtils;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import sun.misc.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class V20230427221610__SaveImages extends BaseJavaMigration {
    public void migrate(Context context) throws Exception {
        Connection databaseConn = context.getConnection();
        try(Statement select = databaseConn.createStatement()) {
            try (ResultSet rows = select.executeQuery("SELECT * FROM games")) {
                while (rows.next()) {
                    String imageUrl = rows.getString("imageUrl");
                    if (imageUrl.contains("https") || imageUrl.contains("http")) {
                        URL url = new URL(imageUrl);
                        InputStream is = url.openStream();
                        byte[] resultArray = IOUtils.readAllBytes(is);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("HEAD");
                        conn.connect();
                        String mediaType = conn.getContentType();
                        try (PreparedStatement createImage = databaseConn.prepareStatement("INSERT INTO images(id, data, mediatype) VALUES (?, ?, ?)")) {
                            String id = generateId(16);
                            createImage.setString(1, id);
                            createImage.setBytes(2, resultArray);
                            createImage.setString(3, mediaType);
                            createImage.execute();
                            try (PreparedStatement updateGame = databaseConn.prepareStatement("UPDATE games SET imageurl = ? WHERE id = ?")) {
                                updateGame.setString(1, "/images/" + id);
                                updateGame.setInt(2, rows.getInt("id"));
                                updateGame.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateId(Integer length){
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
