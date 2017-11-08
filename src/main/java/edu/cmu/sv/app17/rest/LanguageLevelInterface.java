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
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.models.LanguageLevel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("langs")
public class LanguageLevelInterface {

    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public LanguageLevelInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("langs");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<LanguageLevel> lanLevelList = new ArrayList<LanguageLevel>();
        try {
            FindIterable<Document> results = collection.find();
            for (Document item : results) {

                LanguageLevel lanLevel = new LanguageLevel(
                        item.getString("usersId"),
                        item.getInteger("movies_level",0),
                        item.getInteger("tvshows_level",0),
                        item.getInteger("books_level", 0)
//                        item.getInteger("audioBooks_level",0)

                );
                lanLevel.setId(item.getObjectId("_id").toString());
                lanLevelList.add(lanLevel);
            }
            return new APPResponse(lanLevelList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Langs Found");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {

        BasicDBObject query = new BasicDBObject();

        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such language, my friend");
            }
            LanguageLevel lanlevel = new LanguageLevel(
                    item.getString("usersId"),
                    item.getInteger("movies_level",0),
                    item.getInteger("tvshows_level",0),
                    item.getInteger("books_level", 0)
//                    item.getInteger("audioBooks_level",0)
            );
            lanlevel.setId(item.getObjectId("_id").toString());
            return new APPResponse(lanlevel);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "That Language was not found");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(JSONObject obj) {
        try {
            Document doc = new Document("usersId",obj.getString("usersId"))
                    .append("movies_level", obj.getInt("movies_level"))
                    .append("tvshows_level", obj.getInt("tvshows_level"))
                    .append("books_level", obj.getInt("books_level"))
                    .append("audioBooks_level", obj.getInt("audioBooks_level"));
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
            if (json.has("movies_level"))
                doc.append("movies_level",json.getInt("movies_level"));
            if (json.has("tvshows_level"))
                doc.append("tvshows_level",json.getInt("tvshows_level"));
            if (json.has("books_level"))
                doc.append("books_level",json.getInt("books_level"));
//            if (json.has("audioBooks_level"))
//                doc.append("audioBooks_level",json.getString("audioBooks_level"));

            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse(request);
    }



}
