package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoSocketOpenException;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.helpers.APPResponse;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static java.lang.Math.toIntExact;

import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.models.Book;
import edu.cmu.sv.app17.models.Movie;
import edu.cmu.sv.app17.models.Savy;
import edu.cmu.sv.app17.models.Contributor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import com.mongodb.util.JSON;
import sun.rmi.runtime.Log;

@Path("savy")
public class SavyInterface {

    private MongoCollection<Document> collection = null;
    private MongoCollection<Document> contributorCollection;
    private ObjectWriter ow;

    public SavyInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("savy");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<Savy> savyList = new ArrayList<Savy>();

        /*
        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });
        */

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return new APPResponse(savyList);
        }

        try {
            //FindIterable<Document> results = collection.find().skip(offset).limit(count).sort(sortParams).sort( orderBy(ascending("_id")));
            for (Document item : results) {
                Savy savy = new Savy(
                        item.getString("question"),
                        item.getString("answer01"),
                        item.getString("answer02"),
                        item.getString("answer03"),
                        item.getString("answer04"),
                        item.getInteger("answer01count"),
                        item.getInteger("answer02count"),
                        item.getInteger("answer03count"),
                        item.getInteger("answer04count")
                );
                savy.setId(item.getObjectId("_id").toString());
//                System.out.print(movie);
                savyList.add(savy);
            }
            return new APPResponse(savyList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Questionare");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }
    //
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No questionare bro");
            }
            //String name = item.getString("name");
            //int avg = avgLevel(name);
            Savy savy = new Savy(
                    item.getString("question"),
                    item.getString("answer01"),
                    item.getString("answer02"),
                    item.getString("answer03"),
                    item.getString("answer04"),
                    item.getInteger("answer01count"),
                    item.getInteger("answer02count"),
                    item.getInteger("answer03count"),
                    item.getInteger("answer04count")
            );
            savy.setId(item.getObjectId("_id").toString());
            return new APPResponse(savy);

        } catch (APPNotFoundException e) {
            throw new APPNotFoundException(0, "No such questionare");
        } catch (IllegalArgumentException e) {
            throw new APPBadRequestException(45, "Doesn't look like MongoDB ID");
        } catch (Exception e) {
            throw new APPInternalServerException(99, "Something happened, pinch me!");
        }
    }
//


    //    will use API java to create new movie
    @POST
    //@Path("create/{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        if (!json.has("question"))
            throw new APPBadRequestException(55,"question");
        if (!json.has("answer01"))
            throw new APPBadRequestException(55,"answer01");
        if (!json.has("answer02"))
            throw new APPBadRequestException(55,"answer02");
        if (!json.has("answer03"))
            throw new APPBadRequestException(55,"answer03");
        if (!json.has("answer04"))
            throw new APPBadRequestException(55,"answer04");
        if (!json.has("answer01count"))
            throw new APPBadRequestException(55,"answer01count");
        if (!json.has("answer02count"))
            throw new APPBadRequestException(55,"answer02count");
        if (!json.has("answer03count"))
            throw new APPBadRequestException(55,"answer03count");
        if (!json.has("answer04count"))
            throw new APPBadRequestException(55,"answer04count");


        try {
            Document doc = new Document("question", json.getString("question"))
                    .append("answer01", json.getString("answer01"))
                    .append("answer02", json.getString("answer02"))
                    .append("answer03", json.getString("answer03"))
                    .append("answer04", json.getString("answer04"))
                    .append("answer01count", json.getInt("answer01count"))
                    .append("answer02count", json.getInt("answer02count"))
                    .append("answer03count", json.getInt("answer03count"))
                    .append("answer04count", json.getInt("answer04count"));
            collection.insertOne(doc);
            return new APPResponse(request);
        } catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
    }


    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {

            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("question"))
                doc.append("question",json.getString("question"));
            if (json.has("answer01"))
                doc.append("answer01",json.getString("answer01"));
            if (json.has("answer02"))
                doc.append("answer02",json.getString("answer02"));
            if (json.has("answer03"))
                doc.append("answer03",json.getString("answer03"));
            if (json.has("answer04"))
                doc.append("answer04",json.getString("answer04"));
            if (json.has("answer01count"))
                doc.append("answer01count",json.getInt("answer01count"));
            if (json.has("answer02count"))
                doc.append("answer02count",json.getInt("answer02count"));
            if (json.has("answer03count"))
                doc.append("answer03count",json.getInt("answer03count"));
            if (json.has("answer04count"))
                doc.append("answer04count",json.getInt("answer04count"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse(request);
    }


    @DELETE
    @Path("{croId}/{movieId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("croId") String croId, @PathParam("movieId") String movieId) {
        BasicDBObject query = new BasicDBObject();

        query.put("_id", new ObjectId(movieId));
        query.put("contributorId", croId);

        try{
            DeleteResult deleteResult = collection.deleteOne(query);
            if (deleteResult.getDeletedCount() < 1)
                throw new APPNotFoundException(66,"Could not delete");
        }
        catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "That Movie was not found");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
        return new JSONObject();

    }




}
