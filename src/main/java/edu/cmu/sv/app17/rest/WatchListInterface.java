package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
                    item.getInteger("userID"),
                    item.getInteger("movieID"),
                    item.getInteger("tvShowID"),
                    item.getInteger("bookID"),
                    item.getInteger("audiobookID")
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
                    item.getInteger("userID"),
                    item.getInteger("movieID"),
                    item.getInteger("tvShowID"),
                    item.getInteger("bookID"),
                    item.getInteger("audiobookID")
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

    @GET
    @Path("{id}/cars")
    @Produces({MediaType.APPLICATION_JSON})
    public ArrayList<Car> getCarsForDriver(@PathParam("id") String id) {

        ArrayList<Car> carList = new ArrayList<Car>();

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("driverId", id);

            FindIterable<Document> results = carCollection.find(query);
            for (Document item : results) {
                String make = item.getString("make");
                Car car = new Car(
                        make,
                        item.getString("model"),
                        item.getInteger("year", -1),
                        item.getString("size"),
                        item.getString("color"),
                        item.getInteger("odometer"),
                        item.getString("driverId")
                );
                car.setId(item.getObjectId("_id").toString());
                carList.add(car);
            }
            return carList;

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }


    @POST
    @Path("{id}/cars")
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
        if (!json.has("make"))
            throw new APPBadRequestException(55,"missing make");
        if (!json.has("model"))
            throw new APPBadRequestException(55,"missing model");
        if (!json.has("color"))
            throw new APPBadRequestException(55,"missing color");
        if (!json.has("year"))
            throw new APPBadRequestException(55,"missing year");
        if (!json.has("size"))
            throw new APPBadRequestException(55,"missing size");
        if (!json.has("odometer"))
            throw new APPBadRequestException(55,"missing odometer");
        if (json.getInt("odometer") < 0) {
            throw new APPBadRequestException(56, "Invalid odometer - cannot be less than 0");
        }
        Document doc = new Document("make", json.getString("make"))
                .append("model", json.getString("model"))
                .append("size", json.getString("size"))
                .append("color", json.getString("color"))
                .append("year", json.getInt("year"))
                .append("odometer", json.getInt("odometer"))
                .append("driverId", id);
        carCollection.insertOne(doc);
        return request;
    }


}
