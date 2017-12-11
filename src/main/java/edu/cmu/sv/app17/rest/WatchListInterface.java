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

import edu.cmu.sv.app17.models.WatchList;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

@Path("watchlists")
public class WatchListInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> movieCollection = null;
    private MongoCollection<Document> bookCollection = null;
    private MongoCollection<Document> tvshowCollection = null;
    private ObjectWriter ow;


    public WatchListInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        collection = database.getCollection("watchList");
        movieCollection = database.getCollection("movie");
        bookCollection = database.getCollection("books");
        tvshowCollection = database.getCollection("tvshow");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<WatchList> watchListList = new ArrayList<WatchList>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return  new APPResponse(watchListList);
        }
        for (Document item : results) {
            WatchList watchList = new WatchList(
                    item.getString("userID"),
                    item.getString("movie"),
                    item.getString("tvShow"),
                    item.getString("book")
            );
            watchList.setId(item.getObjectId("_id").toString());
            watchListList.add(watchList);
        }
        return new APPResponse(watchListList);
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No WatchList found, my friend");
            }
            WatchList watchList = new WatchList(
                    item.getString("userID"),
                    item.getString("movie"),
                    item.getString("tvShow"),
                    item.getString("book")

            );
            watchList.setId(item.getObjectId("_id").toString());
            return new APPResponse(watchList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such media");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

    @GET
    @Path("check/{type}/{userId}/{name}")
    @Produces({ MediaType.APPLICATION_JSON})
    @Consumes({ MediaType.APPLICATION_JSON})
    public APPResponse checkWatch(@PathParam("type") String type, @PathParam("userId") String id, @PathParam("name") String name){
        BasicDBObject query = new BasicDBObject();
        query.put("userID", id);
        if(type.equals("movies")){
            query.put("movie",name);
        }else if(type.equals("tv")){
            query.put("tvShow",name);
        }else{
            query.put("book",name);
        }
        try {
            Document item = collection.find(query).first();
            if (item !=null){
                item.put("watchID",item.getObjectId("_id").toString());

            }else{
                item = null;
            }

            return new APPResponse(item);
        }catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "You have no watch list");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }


    }

    /*
       *
       * the getall/id will return the all the watchlist under that userid
       * {id} is the user id
       * return value
       * example
       * {
       "content": {
           "movies": {
               "Sleight": "5a2744c0772e1773deb33ba0", / favlistId
           },
           "tvshows": {},
           "book": {},
           "userID": {
               "userid": "5a028d3257ba5f33d2bff3c3"
           }
       },
       "success": true
   }
       *
       *
       * */
    @GET
    @Path("getall/{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    @Consumes({ MediaType.APPLICATION_JSON})
    public  APPResponse getAllUser(@PathParam("id") String id){
        BasicDBObject query = new BasicDBObject();
        HashMap<String, String> movieList = new HashMap<String, String>();
        HashMap <String, String>  tvshowsList = new HashMap<String, String>();
        HashMap <String, String>  bookList = new HashMap<String, String>();
        HashMap <String, String>  user = new HashMap<String, String>();
        user.put("userid", id);
        HashMap<String, HashMap <String, String>> watchlists = new HashMap<String, HashMap <String, String>>();


        try{
            FindIterable<Document> results = collection.find(eq("userID",id));
            for (Document item: results) {
                String nameMovie =item.getString("movie");
                String nameTV = item.getString("tvShow");
                String nameBook = item.getString("book");
                if (nameMovie != null){
//                    movieList.put("name",nameMovie);
//                    the value is favlist id, easy for delete function
                    movieList.put(nameMovie,item.getObjectId("_id").toString());
                }
                if (nameTV !=null){
//                    tvshowsList.put("name",nameTV);
                    tvshowsList.put(nameTV, item.getObjectId("_id").toString());
                }
                if (nameBook !=null){
//                    bookList.put("name",nameBook);
                    bookList.put(nameBook, item.getObjectId("_id").toString());
                }
            }
            watchlists.put("movies",movieList);
            watchlists.put("tvshows",tvshowsList);
            watchlists.put("book",bookList);
            watchlists.put("userID",user);


            return new APPResponse(watchlists);
        }catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "You have no watch list");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }


    @POST
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
        System.out.print(json);
        if (!json.has("type")){

            throw new APPBadRequestException(55,"missing type");
        }
        if(!json.has("media")){
            throw new APPBadRequestException(55,"missing media name");
        }

        if (!json.has("userId")){
            throw new APPBadRequestException(55,"missing user Id");
        }
        String type = json.getString("type");
        Document doc = new Document("userID", json.getString("userId"))
                .append("movie", null)
                .append("tvShow", null)
                .append("book", null);
        if (type.equals("movies")){
            doc.put("movie", json.getString("media"));
        }

        if (type.equals("tv")){

            doc.put("tvShow",  json.getString("media"));
        }

        if (type.equals("books")){

            doc.put("book",  json.getString("media"));

        }
        System.out.print(doc);
        collection.insertOne(doc);
        return new APPResponse(request);
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
