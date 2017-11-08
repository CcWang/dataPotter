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
import static java.lang.Math.toIntExact;

import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.models.Movie;
import edu.cmu.sv.app17.models.Contributor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import com.mongodb.util.JSON;
import sun.rmi.runtime.Log;

@Path("movies")
public class MovieInterface {

    private MongoCollection<Document> collection = null;
    private MongoCollection<Document> contributorCollection;
    private ObjectWriter ow;

    public MovieInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("movie");
        contributorCollection = database.getCollection("contributor");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<Movie> movieList = new ArrayList<>();

        try {
            FindIterable<Document> results = collection.find();
            for (Document item : results) {
                Movie movie = new Movie(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                movie.setId(item.getObjectId("_id").toString());
//                System.out.print(movie);
                movieList.add(movie);
            }
            return new APPResponse(movieList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Movies");
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
public APPResponse getOne(@PathParam("id") String id) { ;
        BasicDBObject query = new BasicDBObject();
    try {
        query.put("_id", new ObjectId(id));
        Document item = collection.find(query).first();
        if (item == null) {
            throw new APPNotFoundException(0, "No such movie, my friend");
        }

        Movie movie = new Movie(
                item.getString("name"),
                item.getString("genre"),
                item.getInteger("level"),
                item.getString("contributorId")
        );
        movie.setId(item.getObjectId("_id").toString());
        return new APPResponse(movie);

    } catch (APPNotFoundException e) {
        throw new APPNotFoundException(0, "No such book");
    } catch (IllegalArgumentException e) {
        throw new APPBadRequestException(45, "Doesn't look like MongoDB ID");
    } catch (Exception e) {
        throw new APPInternalServerException(99, "Something happened, pinch me!");
    }
}
//


// get all level for same movie

public Integer avgLevel( String id) {


    BasicDBObject query = new BasicDBObject();
    ArrayList<Movie> movieRet = new ArrayList<>();
    Integer totalLevel = 0;

    try {
      query.put("_id", new ObjectId(id));
        FindIterable<Document> results = collection.find(query);
        long size = collection.count(query);
        for (Document item : results) {
           totalLevel = totalLevel+ item.getInteger("level");
        }

        return totalLevel/toIntExact(size);

    } catch(APPNotFoundException e) {
        throw new APPNotFoundException(0, "No Movies");
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


    BasicDBObject query = new BasicDBObject();
    ArrayList<Movie> movieRet = new ArrayList<>();

    try {
//        query = {"name":{search}};
        FindIterable<Document> results = collection.find(regex("name",".*"+search+".*"));
        for (Document item : results) {
            Movie movie = new Movie(
                    item.getString("name"),
                    item.getString("genre"),
                    item.getInteger("level"),
                    item.getString("contributorId")
            );
            movie.setId(item.getObjectId("_id").toString());
            System.out.print(movie);
            movieRet.add(movie);
        }
        return new APPResponse(movieRet);

    } catch(APPNotFoundException e) {
        throw new APPNotFoundException(0, "No Movies contain key word like: " + search);
    } catch(Exception e) {
        System.out.println("EXCEPTION!!!!");
        e.printStackTrace();
        throw new APPInternalServerException(99,e.getMessage());
    }
}

    @GET
    @Path("genre/{genre}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByGenre(@PathParam("genre") String genre) {


        ArrayList<Movie> movieRet = new ArrayList<>();

        try {
            FindIterable<Document> results = collection.find(regex("genre",".*"+genre+".*"));
            for (Document item : results) {
                Movie movie = new Movie(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                movie.setId(item.getObjectId("_id").toString());
                System.out.print(movie);
                movieRet.add(movie);
            }
            return new APPResponse(movieRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Movies contain genre like: " + genre);
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }
    @GET
    @Path("level/{level}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByLevel(@PathParam("level") Integer level) {


        ArrayList<Movie> movieRet = new ArrayList<>();

        try {
            FindIterable<Document> results = collection.find(eq("level",level));
            for (Document item : results) {
                Movie movie = new Movie(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                movie.setId(item.getObjectId("_id").toString());
                System.out.print(movie);
                movieRet.add(movie);
            }
            return new APPResponse(movieRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Movies contain genre like: " + level.toString());
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }
//    need to write a post function

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(JSONObject obj) {
        try {
            Document doc = new Document("contributorId",obj.getString("contributorId"))
                    .append("name", obj.getString("name"))
                    .append("genre", obj.getString("genre"))
                    .append("level", obj.getInt("level"));

            collection.insertOne(doc);


        } catch(JSONException e) {
            System.out.println("Failed to create a document");
        }
//
        return new APPResponse(obj);
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



}
