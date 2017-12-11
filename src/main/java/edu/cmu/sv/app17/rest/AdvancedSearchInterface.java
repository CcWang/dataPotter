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
import edu.cmu.sv.app17.models.AdvancedSearch;
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

@Path("advancedSearch")
public class AdvancedSearchInterface {

    private MongoCollection<Document> collection = null;
    private MongoCollection<Document> contributorCollection;
    private ObjectWriter ow;

    public AdvancedSearchInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("advancedSearch");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    //NEED TO GET SPECIFIC META CATEGORY
    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<AdvancedSearch> advancedSearchList = new ArrayList<AdvancedSearch>();

        /*
        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });
        */

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return new APPResponse(advancedSearchList);
        }

        try {
            //FindIterable<Document> results = collection.find().skip(offset).limit(count).sort(sortParams).sort( orderBy(ascending("_id")));
            for (Document item : results) {
                AdvancedSearch advancedSearch = new AdvancedSearch(
                        item.getString("metaCategory"),
                        item.getString("category")
                );
                advancedSearch.setId(item.getObjectId("_id").toString());
                advancedSearchList.add(advancedSearch);
            }
            return new APPResponse(advancedSearchList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Advanced Search Data");
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
                throw new APPNotFoundException(0, "No Advanced Search Material bro");
            }
            //String name = item.getString("name");
            //int avg = avgLevel(name);
            AdvancedSearch advancedSearch = new AdvancedSearch(
                    item.getString("metaCategory"),
                    item.getString("category")
            );
            advancedSearch.setId(item.getObjectId("_id").toString());
            return new APPResponse(advancedSearch);

        } catch (APPNotFoundException e) {
            throw new APPNotFoundException(0, "No such Advanced Search Material");
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

        if (!json.has("metaCategory"))
            throw new APPBadRequestException(55,"metaCategory");
        if (!json.has("category"))
            throw new APPBadRequestException(55,"category");



        try {
            Document doc = new Document("metaCategory", json.getString("metaCategory"))
                    .append("category", json.getInt("category"));
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
            if (json.has("metaCategory"))
                doc.append("metaCategory",json.getString("metaCategory"));
            if (json.has("category"))
                doc.append("category",json.getString("category"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse(request);
    }


    @DELETE
    @Path("{croId}/{advancedSearchId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("croId") String croId, @PathParam("advancedSearchId") String advancedSearchId) {
        BasicDBObject query = new BasicDBObject();

        query.put("_id", new ObjectId(advancedSearchId));
       // query.put("contributorId", croId);

        try{
            DeleteResult deleteResult = collection.deleteOne(query);
            if (deleteResult.getDeletedCount() < 1)
                throw new APPNotFoundException(66,"Could not delete");
        }
        catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "That Questionare was not found");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
        return new JSONObject();

    }




}
