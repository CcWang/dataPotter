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
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Movie;
import edu.cmu.sv.app17.models.Tvshow;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import edu.cmu.sv.app17.rest.MovieInterface;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

@Path("tvshows")
public class TvshowInterface {

    private MongoCollection<Document> collection = null;
    private MongoCollection<Document> contributorCollection;
    private ObjectWriter ow;

    public TvshowInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("tvshow");
        contributorCollection = database.getCollection("contributor");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<Tvshow> tvlist = new ArrayList<Tvshow>();

        try {
            FindIterable<Document> results = collection.find();
            for (Document item : results) {
                System.out.println(item);

//                List<String> genres = item.get("genre", List.class);
//                List<String> levels = item.get("level", List.class);
                Tvshow tv = new Tvshow(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                System.out.println(tv);
                tv.setId(item.getObjectId("_id").toString());
                tvlist.add(tv);
            }
            return new APPResponse(tvlist);
//            return new APPResponse("hello!");
        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No TV Shows");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    };

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {


        BasicDBObject query = new BasicDBObject();

        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such tv, my friend");
            }
//            List<String> genres = item.get("genre", List.class);
//            HashMap levels = item.get("level", HashMap.class);
//            List<String> levels = item.get("level", List.class);
            String name = item.getString("name");
            int avgLevel = getAvg(name);
            Tvshow tv = new Tvshow(
                    item.getString("name"),
                    item.getString("genre"),
//                    item.getInteger("level"),
                    avgLevel,
                    item.getString("contributorId")
            );
            tv.setId(item.getObjectId("_id").toString());
            return new APPResponse(tv);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "That TV show was not found");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

    public Integer getAvg( String name) {

        Integer totalLevel = 0;

        try {

            FindIterable<Document> results = collection.find(eq("name",name));
            int size = 0;
            for (Document item : results) {
                totalLevel = totalLevel+ item.getInteger("level");
                size = size +1;
            }

            return totalLevel/size;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No TVshows");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }


    //    search
    @GET
    @Path("name/{search}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByName(@PathParam("search") String search) {


        ArrayList<Tvshow> tvshowRet = new ArrayList<>();

        try {
//
            FindIterable<Document> results = collection.find(regex("name",".*"+search+".*"));
            for (Document item : results) {
                Tvshow tvshow = new Tvshow(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                tvshow.setId(item.getObjectId("_id").toString());
                System.out.print(tvshow);
                tvshowRet.add(tvshow);
            }
            return new APPResponse(tvshowRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No TV Shows contain key word like: " + search);
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }
//search by genre

    @GET
    @Path("genre/{genre}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByGenre(@PathParam("genre") String genre) {


        ArrayList<Tvshow> tvshowRet = new ArrayList<>();

        try {
            FindIterable<Document> results = collection.find(regex("genre",".*"+genre+".*"));
            for (Document item : results) {
                Tvshow tvshow = new Tvshow(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                tvshow.setId(item.getObjectId("_id").toString());
                System.out.print(tvshow);
                tvshowRet.add(tvshow);
            }
            return new APPResponse(tvshowRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Movies contain genre like: " + genre);
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }

    //search by level

    @GET
    @Path("level/{level}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByGenre(@PathParam("level") Integer level) {


        ArrayList<Tvshow> tvshowRet = new ArrayList<>();

        try {
            FindIterable<Document> results = collection.find(eq("level",level));
            for (Document item : results) {
                Tvshow tvshow = new Tvshow(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                tvshow.setId(item.getObjectId("_id").toString());
                System.out.print(tvshow);
                tvshowRet.add(tvshow);
            }
            return new APPResponse(tvshowRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Movies contain genre like: " + level.toString());
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }


    @POST
    @Path("create/{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        if (!json.has("name"))
            throw new APPBadRequestException(55,"name");
        if (!json.has("genre"))
            throw new APPBadRequestException(55,"genre");
        if (!json.has("level"))
            throw new APPBadRequestException(55,"level");
//        if (!json.has("contributorId"))
//            throw new APPBadRequestException(55,"contributorId");

        try {
            Document doc = new Document("name", json.getString("name"))
                    .append("genre", json.getString("genre"))
                    .append("level", json.getInt("level"))
                    .append("contributorId", id);
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
            if (json.has("name"))
                doc.append("name",json.getString("name"));
            if (json.has("genre"))
                doc.append("genre",json.getString("genre"));
            if (json.has("level"))
                doc.append("level",json.getInt("level"));
            if(json.has("contributorId"))
                doc.append("contributorId", json.getString("contributorId"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse(request);
    }
    @DELETE
    @Path("{croId}/{tvshowId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("croId") String croId, @PathParam("tvshowId") String tvshowId) {
        BasicDBObject query = new BasicDBObject();

        query.put("_id", new ObjectId(tvshowId));
        query.put("contributorId", croId);

        DeleteResult deleteResult = collection.deleteOne(query);
        if (deleteResult.getDeletedCount() < 1)
            throw new APPNotFoundException(66,"Could not delete");

        return new JSONObject();
    }



}
