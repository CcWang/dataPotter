package edu.cmu.sv.app17.rest;

/*
* username  string
* email     string
* password  string
* nativeLanguage    string
* englishLevel  string
* phone     string
* gender    string
* birthday  string
* */

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
import edu.cmu.sv.app17.exceptions.APPUnauthorizedException;
import edu.cmu.sv.app17.helpers.APPCrypt;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.*;

import edu.cmu.sv.app17.models.LanguageLevel;
import edu.cmu.sv.app17.models.FavoriteList;
import edu.cmu.sv.app17.models.User;


import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import java.util.Date;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("users")
public class UsersInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> langLevelCollection;
    private MongoCollection<Document> bookCollection;
    private MongoCollection<Document> favoriteListsCollection;

    private ObjectWriter ow;


    public UsersInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("users");
        this.langLevelCollection = database.getCollection("langs");
        this.bookCollection = database.getCollection("books");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {
        try {
            ArrayList<User> userList = new ArrayList<User>();

            FindIterable<Document> results = collection.find();
            if (results == null) {
                return new APPResponse(userList);
            }
            for (Document item : results) {
                User user = new User(
                        item.getString("username"),
                        item.getString("email"),
                        item.getString("password"),
                        item.getString("nativeLanguage"),
                        item.getInteger("englishLevel"),
                        item.getString("phone"),
                        item.getString("gender"),
                        item.getString("birthday")
                );
                user.setId(item.getObjectId("_id").toString());
                userList.add(user);
            }
            return new APPResponse(userList);
        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No Users");
        } catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@Context HttpHeaders headers, @PathParam("id") String id) {

        try {
            checkAuthentication(headers,id);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "Sorry, we cannot find you. Sign up? ");
            }
            User user = new User(
                    item.getString("username"),
                    item.getString("email"),
                    item.getString("password"),
                    item.getString("nativeLanguage"),
                    item.getInteger("englishLevel"),
                    item.getString("phone"),
                    item.getString("gender"),
                    item.getString("birthday")
            );
            user.setId(item.getObjectId("_id").toString());
            return new APPResponse(user);
//            return user;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such User");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse create(Object request){

        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("username"))
            throw new APPBadRequestException(55,"username");
        if (!json.has("email"))
            throw new APPBadRequestException(55,"email");
        if (!json.has("password"))
            throw new APPBadRequestException(55,"password");
        if (!json.has("nativeLanguage"))
            throw new APPBadRequestException(55,"nativeLanguage");
        if (!json.has("englishLevel"))
            throw new APPBadRequestException(55,"englishLevel");
        if (!json.has("phone"))
            throw new APPBadRequestException(55,"phone");
        if(!json.has("gender"))
            throw new APPBadRequestException (55,"missing gender");
        if(!json.has("birthday"))
            throw new APPBadRequestException (55,"birthday");
//        if (json.getInt("odometer") < 0) {
//            throw new APPBadRequestException(56, "Invalid odometer - cannot be less than 0");
//        }
        Document doc = new Document("username", json.getString("username"))
                .append("email", json.getString("email"))
                .append("password", json.getString("password"))
                .append("nativeLanguage", json.getString("nativeLanguage"))
                .append("englishLevel", json.getInt("englishLevel"))
                .append("phone", json.getString("phone"))
                .append("gender", json.getString("gender"))
                .append("birthday", json.getString("birthday"));

        collection.insertOne(doc);
        return new APPResponse(request);

    }

    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse update(@Context HttpHeaders headers, @PathParam("id") String id, Object obj) {
        try {
            JSONObject json = null;

            json = new JSONObject(ow.writeValueAsString(obj));
            checkAuthentication(headers,id);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("username"))
                doc.append("username",json.getString("username"));
            if (json.has("email"))
                json.append("email",json.getString("email"));
            if (json.has("password"))
                doc.append("password",json.getString("password"));
            if (json.has("nativeLanguage"))
                doc.append("nativeLanguage",json.getString("nativeLanguage"));
            if (json.has("englishLevel"))
                doc.append("englishLevel",json.getInt("englishLevel"));
            if (json.has("phone"))
                doc.append("phone",json.getString("phone"));
            if (json.has("gender"))
                doc.append("gender",json.getString("gender"));
            if (json.has("birthday"))
                doc.append("birthday",json.getString("birthday"));


            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such user");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
        return new APPResponse(obj);
    }

    @GET
    @Path("{id}/langs")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getLangLevelForUser(@Context HttpHeaders headers, @PathParam("id") String id) {

        ArrayList<LanguageLevel> lanList = new ArrayList<LanguageLevel>();

        try {
            checkAuthentication(headers,id);
            BasicDBObject query = new BasicDBObject();
            query.put("usersId", id);

            FindIterable<Document> results = langLevelCollection.find(query);
            for (Document item : results) {
                LanguageLevel lang = new LanguageLevel(
                        item.getString("usersId"),
                        item.getInteger("movies_level"),
                        item.getInteger("tvshows_level"),
                        item.getInteger("books_level"),
                        item.getInteger("audioBooks_level")

                );
                lang.setId(item.getObjectId("_id").toString());
                lanList.add(lang);
            }
            return new APPResponse(lanList);

        } catch(APPNotFoundException e) {
                throw new APPNotFoundException(0,"No such User");
            } catch(IllegalArgumentException e) {
                throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
            }  catch(Exception e) {
                throw new APPInternalServerException(99,"Something happened, pinch me!");
            }

    }

    @POST
    @Path("{id}/langs")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(@Context HttpHeaders headers, @PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            checkAuthentication(headers,id);

            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        Document item = collection.find(query).first();
        if (item == null) {
            throw new APPNotFoundException(0, "Sorry, we cannot find you. Sign up? ");
        }

        if (!json.has("movies_level"))
            throw new APPBadRequestException(55,"movies_level");
        if (!json.has("tvshows_level"))
            throw new APPBadRequestException(55,"tvshows_level");
        if (!json.has("books_level"))
            throw new APPBadRequestException(55,"books_level");
        if (!json.has("audioBooks_level"))
            throw new APPBadRequestException(55,"audioBooks_level");

        Document doc = new Document("usersId", id)
                .append("movies_level", json.getInt("movies_level"))
                .append("tvshows_level", json.getInt("tvshows_level"))
                .append("books_level", json.getInt("books_level"))
                .append("audioBooks_level",json.getInt("audioBooks_level"));

        langLevelCollection.insertOne(doc);
        return new APPResponse(request);
    }

    void checkAuthentication(HttpHeaders headers,String id) throws Exception{
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null)
            throw new APPUnauthorizedException(70,"No Authorization Headers");
        String token = authHeaders.get(0);
        String clearToken = APPCrypt.decrypt(token);
        if (id.compareTo(clearToken) != 0) {
            throw new APPUnauthorizedException(71,"Invalid token. Please try getting a new token");
        }
    }




}

