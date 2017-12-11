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
import edu.cmu.sv.app17.exceptions.APPUnauthorizedException;
import edu.cmu.sv.app17.helpers.APPCrypt;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Book;
import edu.cmu.sv.app17.models.Comment;
import jdk.nashorn.internal.lookup.MethodHandleFactory;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Filters.type;

@Path("comment")
public class CommentInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> usersCollection;
    private MongoCollection<Document> booksCollection;
    private MongoCollection<Document> movieCollection;
    private MongoCollection<Document> tvshowCollection;
    private ObjectWriter ow;


    public CommentInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        this.collection = database.getCollection("comment");
        usersCollection = database.getCollection("users");
        booksCollection = database.getCollection("books");
        movieCollection = database.getCollection("movie");
        tvshowCollection = database.getCollection("tvshow");
       ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }



//GET comments

    @GET
    @Path("{mediaType}/{name}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getCommentsForMovie(@PathParam("mediaType") String mediaType,
                                           @PathParam("name") String name ) {
        ArrayList<Comment> comments = new ArrayList<Comment>();

        try {
//            FindIterable<Document> results;
//            if (new String("mediaType").equals("movie") ) {
//                results = collection.find(regex("mediaName", ".*" + name + ".*"));
//
//
//
//            }else if (new String("mediaType").equals("tvshow") ) {
//                results = tvshowCollection.find(regex("name", ".*" + name + ".*"));
//            }else if (new String("mediaType").equals("book") ) {
//                results = booksCollection.find(regex("name", ".*" + name + ".*"));
//            }else {
//                results = movieCollection.find(regex("name", ".*" + name + ".*"));;
//                System.out.println("confirm your mediatype");
//            }

            FindIterable<Document> results = collection.find();
            for (Document item : results) {
                String mtype = item.getString("mediaType");
                Comment comment = new Comment(
                        item.getString("mediaType"),
                        item.getString("mediaName"),
                        item.getString("content"),
                        item.getString("userId")
            );

                comment.setId(item.getObjectId("_id").toString());

                if (mtype.equals(mediaType)){
                    System.out.print(comment);
                    comments.add(comment);
                }
            }
            return new APPResponse(comments);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No comments yet");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }


    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getCommentsForMovie(Object request) {

        JSONObject json = null;
        try {

            json = new JSONObject(ow.writeValueAsString(request));
            if (!json.has("content"))
                throw new APPBadRequestException(55,"name");
            System.out.print(json);
            Document doc = new Document("content", json.getString("content"))
                        .append("userId", json.getString("userId"))
                        .append("mediaType", json.getString("mediaType"))
                        .append("mediaName", json.getString("mediaName"));

            collection.insertOne(doc);
            return new APPResponse(request);

        } catch(APPUnauthorizedException e) {
            throw e;
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
            if (json.has("content"))
                doc.append("content",json.getString("content"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to edit a comment");

        }
        return new APPResponse(request);
    }

    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("id") String id) {

        BasicDBObject query = new BasicDBObject();

        query.put("_id", new ObjectId(id));

        try{
            DeleteResult deleteResult = collection.deleteOne(query);
            if (deleteResult.getDeletedCount() < 1)
                throw new APPNotFoundException(66,"Could not delete");
        }
        catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "That Comment was not found");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
        return new JSONObject();

    }

    private void checkAuthentication(HttpHeaders headers, String id) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70, "No Auhthorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71, "Invalid token. Please try getting a new token");
        }
    }


}