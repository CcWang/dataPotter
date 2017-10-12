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
import edu.cmu.sv.app17.helpers.PATCH;
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
    private ObjectWriter ow;


    public UsersInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("users");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<User> getAll() {

        ArrayList<User> userList = new ArrayList<User>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return  userList;
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
        return userList;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public User getOne(@PathParam("id") String id) {
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
            return user;

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
    public Object create(JSONObject obj){
        try {
            Document doc = new Document("username", obj.getString("username"))
                    .append("email", obj.getString("email"))
                    .append("password", obj.getString("password"))
                    .append("nativeLanguage", obj.getString("nativeLanguage"))
                    .append("englishLevel", obj.getInt("englishLevel"))
                    .append("phone", obj.getString("phone"))
                    .append("gender", obj.getString("gender"))
                    .append("birthday", obj.getString("birthday"));

            collection.insertOne(doc);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such user");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
        return obj;
    }

    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object update(@PathParam("id") String id, JSONObject obj) {
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
            if (obj.has("addressLineOne"))
                doc.append("phone",obj.getString("phone"));
            if (obj.has("phone"))
                doc.append("gender",obj.getString("gender"));
            if (obj.has("gender"))
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
        return obj;
    }





}
