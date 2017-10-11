package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.PATCH;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.models.FavoriteList;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

@Path("favoriteLists")
public class FavoriteListInterface {

    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public FavoriteListInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("favoriteLists");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<FavoriteList> getAll() {

        ArrayList<FavoriteList> favoriteListList = new ArrayList<FavoriteList>();

        try {
            FindIterable<Document> results = collection.find();
            for (Document item : results) {
                String users_id = item.getString("userID");
                FavoriteList favoriteList = new FavoriteList(
                        users_id,
                        item.getString("movieID"),
                        item.getString("tvShowID"),
                        item.getString("bookID"),
                        item.getString("audioBookID")
                );
                favoriteList.setId(item.getObjectId("_id").toString());
                favoriteListList.add(favoriteList);
            }
            return favoriteListList;

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public FavoriteList getOne(@PathParam("id") String id) {


        BasicDBObject query = new BasicDBObject();

        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such book, my friend");
            }
            FavoriteList favoriteList = new FavoriteList(
                    item.getString("userID"),
                    item.getString("movieID"),
                    item.getString("tvShowID"),
                    item.getString("bookID"),
                    item.getString("audioBookID")
            );
            favoriteList.setId(item.getObjectId("_id").toString());
            return favoriteList;

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }


    @POST
    @Path("{id}/favoriteList")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object create(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("userID"))
            throw new APPBadRequestException(55,"missing userID");
        if (!json.has("movieID"))
            throw new APPBadRequestException(55,"missing movieID");
        if (!json.has("tvShowID"))
            throw new APPBadRequestException(55,"missing tvShowID");
        if (!json.has("bookID"))
            throw new APPBadRequestException(55,"missing bookID");
        if (!json.has("audioBookID"))
            throw new APPBadRequestException(55,"missing audioBookID");

        Document doc = new Document("userID", json.getString("userID"))
                .append("movieID", json.getString("movieID"))
                .append("tvShowID", json.getString("tvShowID"))
                .append("bookID", json.getString("bookID"))
                .append("audioBookID", json.getInt("audioBookID"));
        collection.insertOne(doc);
        return request;
    }


    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        DeleteResult deleteResult = collection.deleteOne(query);
        if (deleteResult.getDeletedCount() < 1)
            throw new APPNotFoundException(66,"Could not delete");

        return new JSONObject();
    }
}
