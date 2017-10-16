package edu.cmu.sv.app17.rest;

/*
* username  string
* email     string
* password  string
* nativeLanguage    string
* englishLevel  string
* phone     string
* gender    string
* birthday Date
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
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.LanguageLevel;
import edu.cmu.sv.app17.models.User;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import java.util.Date;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("users")
public class UsersInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> langLevelCollection;
    private ObjectWriter ow;


    public UsersInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("users");
        this.langLevelCollection = database.getCollection("langs");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<User> userList = new ArrayList<User>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return  new APPResponse(userList);
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
                    item.getDate("birthday")
            );
            user.setId(item.getObjectId("_id").toString());
            userList.add(user);
        }
        return new APPResponse(userList);

    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
//        need to change
//  need to check if name and pwd are match
        BasicDBObject query = new BasicDBObject();
        try {
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
                    item.getDate("birthday")
            );
            user.setId(item.getObjectId("_id").toString());
            return new APPResponse(user);
//            return user;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such car");
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
    public APPResponse update(@PathParam("id") String id, JSONObject obj) {
        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (obj.has("username"))
                doc.append("username",obj.getString("username"));
            if (obj.has("email"))
                doc.append("email",obj.getString("email"));
            if (obj.has("password"))
                doc.append("password",obj.getString("password"));
            if (obj.has("nativeLanguage"))
                doc.append("nativeLanguage",obj.getString("nativeLanguage"));
            if (obj.has("englishLevel"))
                doc.append("englishLevel",obj.getInt("englishLevel"));
            if (obj.has("phone"))
                doc.append("phone",obj.getString("phone"));
            if (obj.has("gender"))
                doc.append("gender",obj.getString("gender"));
            if (obj.has("birthday"))
                doc.append("birthday",obj.getString("birthday"));


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
    public APPResponse getLangLevelForUser(@PathParam("id") String id) {

        ArrayList<LanguageLevel> lanList = new ArrayList<LanguageLevel>();

        try {
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

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }

    @POST
    @Path("{id}/langs")
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




}
