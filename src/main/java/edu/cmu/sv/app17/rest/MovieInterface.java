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
    public APPResponse getAll(@DefaultValue("_id") @QueryParam("sort") String sortArg ,@DefaultValue("100") @QueryParam("count") int count,
                              @DefaultValue("0") @QueryParam("offset") int offset) {

        ArrayList<Movie> movieList = new ArrayList<>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            FindIterable<Document> results = collection.find().skip(offset).limit(count).sort(sortParams).sort( orderBy(ascending("_id")));
            for (Document item : results) {
                Movie movie = new Movie(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId"),
                        item.getInteger("movieid")

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
        String name = item.getString("name");
        int avg = avgLevel(name).get("avgLev");
        Movie movie = new Movie(
                item.getString("name"),
                item.getString("genre"),
//                item.getInteger("level"),
                avg,
                item.getString("contributorId"),
                item.getInteger("movie")
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

//return that movie's individual level and avg level
@GET
@Path("levels/{conId}/{movieName}")
@Produces({MediaType.APPLICATION_JSON})
public APPResponse getOne(@PathParam("conId") String id, @PathParam("movieName") String name) { ;
    BasicDBObject query = new BasicDBObject();
    try {
        query.put("contributorId", id);
        query.put("name",name);
        Document item = collection.find(query).first();
        if (item == null) {
            throw new APPNotFoundException(0, "No such movie, my friend");
        }
        Integer indLev = item.getInteger("level");
        Integer size = avgLevel(name).get("size");
        Integer avgLev = avgLevel(name).get("avgLev");
        HashMap<String,Integer> levels = new HashMap<String,Integer>();
        levels.put("indLev",indLev);
        levels.put("avgLev",avgLev);
        levels.put("size",size);

        return new APPResponse(levels);

    } catch (APPNotFoundException e) {
        throw new APPNotFoundException(0, "No such book");
    } catch (IllegalArgumentException e) {
        throw new APPBadRequestException(45, "Doesn't look like MongoDB ID");
    } catch (Exception e) {
        throw new APPInternalServerException(99, "Something happened, pinch me!");
    }
}
// get all level for same movie

public HashMap<String, Integer> avgLevel( String name) {

    Integer totalLevel = 0;

    try {

        FindIterable<Document> results = collection.find(eq("name",name));
        int size = 0;
        for (Document item : results) {
           totalLevel = totalLevel+ item.getInteger("level");
           size = size +1;
        }
        HashMap<String,Integer> totals = new HashMap<String,Integer>();
        totals.put("size",size);
        totals.put("avgLev",totalLevel/size);
        return totals;

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
                    item.getString("contributorId"),
                    item.getInteger("movieid")
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
                        item.getString("contributorId"),
                        item.getInteger("movieid")
                );
                movie.setId(item.getObjectId("_id").toString());
//                System.out.print(movie);
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
                        item.getString("contributorId"),
                        item.getInteger("movieid")
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

//    will use API java to create new movie
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
                    .append("contributorId", id)
                    .append("movieid",json.getInt("movieid"));
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
