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
import edu.cmu.sv.app17.models.Tvshow;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;

@Path("share")
public class ShareInterface {

    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public ShareInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("share");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

//    get and patch

    @GET
    @Path("{link}")
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("link") String link) {


        BasicDBObject query = new BasicDBObject();

        try {
            query.put("shoren_link", link);
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such link, my friend");
            }
            Integer count = item.getInteger("count");
            count = count+1;
            try {
                Document doc = new Document("userId", item.getString("userId"))
                        .append("shoren_link", item.getString("shoren_link"))
                        .append("count", count)
                        .append("media", item.getString("media"))
                        .append("type",item.getString("type"));
                collection.insertOne(doc);
                return new APPResponse(doc);
            } catch(Exception e) {
                throw new APPInternalServerException(99,"Something happened, pinch me!");
            }


        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "That link show was not found");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }
//    create


    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create( Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        if (!json.has("userId"))
            throw new APPBadRequestException(55,"id");
        if (!json.has("shoren_link"))
            throw new APPBadRequestException(55,"shoren_link");
        if (!json.has("media"))
            throw new APPBadRequestException(55,"media");
        if (!json.has("type"))
            throw new APPBadRequestException(55,"type");

        try {
            Document doc = new Document("userId", json.getString("userId"))
                    .append("shoren_link", json.getString("shoren_link"))
                    .append("count", 0)
                    .append("media", json.getString("media"))
                    .append("type",json.getString("type"));
            collection.insertOne(doc);
            return new APPResponse(request);
        } catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
    }







}
