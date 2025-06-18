package com.phonekart.auth;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElasticSearchIndexer {
    private static final Logger logger = Logger.getLogger(ElasticSearchIndexer.class.getName());

    public static void indexAllProducts() {
        ElasticsearchClient client = null;
        try {
            client = ElasticSearchOperations.getClient();
            
            // Delete and recreate index
            ElasticSearchOperations.deleteIndex(ElasticSearchOperations.PRODUCTS_INDEX);
            ElasticSearchOperations.createProductIndex();
            
            // Wait for index to be ready
            TimeUnit.SECONDS.sleep(1);
            
            // Index products
            String query = "SELECT * FROM products";
            ResultSet rs = new Conn().s.executeQuery(query);
            int successCount = 0;
            int failCount = 0;
            
            while (rs.next()) {
                if (indexProduct(rs)) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            logger.info(String.format(
                "Indexing completed. Success: %d, Failed: %d", 
                successCount, failCount
            ));
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Critical error during product indexing", e);
            throw new RuntimeException("Product indexing failed", e);
        }
    }
    
    private static boolean indexProduct(ResultSet rs) {
        try {
            JSONObject productJson = createProductJson(rs);
            ElasticsearchClient client = ElasticSearchOperations.getClient();
            
            IndexResponse response = client.index(IndexRequest.of(i -> {
				try {
					return i
					    .index(ElasticSearchOperations.PRODUCTS_INDEX)
					    .id(String.valueOf(rs.getInt("productid")))
					    .document(productJson.toMap());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return i;
			})
            );
            
            logger.fine(String.format(
                "Indexed product ID: %s, version: %d", 
                response.id(), response.version()
            ));
            return true;
        } catch (Exception e) {
            try {
                logger.log(Level.WARNING, 
                    "Failed to index product ID: " + rs.getInt("productid"), e);
            } catch (SQLException ex) {
                logger.log(Level.WARNING, "Failed to get product ID", ex);
            }
            return false;
        }
    }
    
    private static JSONObject createProductJson(ResultSet rs) throws SQLException {
        JSONObject json = new JSONObject();
        json.put("productid", rs.getInt("productid"));
        json.put("title", rs.getString("title"));
        json.put("brand", rs.getString("brand"));
        json.put("model", rs.getString("model"));
        json.put("battery_capacity", rs.getString("battery_capacity"));
        json.put("screen_size", rs.getString("screen_size"));
        json.put("storagespace", rs.getString("storagespace"));
        json.put("ram", rs.getString("ram"));
        json.put("camera_mp", rs.getString("camera_mp"));
        json.put("prod_description", rs.getString("prod_description"));
        json.put("price", rs.getInt("price"));
        json.put("username", rs.getString("username"));
        return json;
    }
}