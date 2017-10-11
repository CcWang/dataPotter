package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.models.Car;
import edu.cmu.sv.app17.models.Driver;
import edu.cmu.sv.app17.models.WatchList;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("watchlist")
public class WatchListInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> movieCollection;
    private ObjectWriter ow;


    public WatchListInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("watchList");
        this.movieCollection = database.getCollection("movie");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<WatchList> getAll() {

        ArrayList<WatchList> watchListList = new ArrayList<WatchList>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return  watchListList;
        }
        for (Document item : results) {
            WatchList watchList = new WatchList(
                    item.getString("userID"),
                    item.getString("movieID"),
                    item.getString("tvShowID"),
                    item.getString("bookID"),
                    item.getString("audiobookID")
            );
            watchList.setId(item.getObjectId("_id").toString());
            watchListList.add(watchList);
        }
        return watchListList;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public WatchList getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No WatchList found, my friend");
            }
            WatchList watchList = new WatchList(
                    item.getString("userID"),
                    item.getString("movieID"),
                    item.getString("tvShowID"),
                    item.getString("bookID"),
                    item.getString("audiobookID")
            );
            watchList.setId(item.getObjectId("_id").toString());
            return watchList;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such media");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }


    @POST
    @Path("{id}/watchList")
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
        if (!json.has("audiobookID"))
            throw new APPBadRequestException(55,"missing audiobookID");

        Document doc = new Document("userID", json.getString("userID"))
                .append("movieID", json.getString("movieID"))
                .append("tvShowID", json.getString("tvShowID"))
                .append("bookID", json.getString("bookID"))
                .append("audiobookID", json.getString("audiobookID"));

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
