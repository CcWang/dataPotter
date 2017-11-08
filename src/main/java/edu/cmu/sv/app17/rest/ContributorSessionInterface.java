package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.models.ContributorSession;
import edu.cmu.sv.app17.models.Session;
import edu.cmu.sv.app17.models.Contributor;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("contributorSession")

public class ContributorSessionInterface {


    private MongoCollection<Document> contributorsCollection;
    private ObjectWriter ow;


    public ContributorSessionInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        this.contributorsCollection = database.getCollection("contributors");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }


    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            if (!json.has("email"))
                throw new APPBadRequestException(55, "missing email");
            if (!json.has("password"))
                throw new APPBadRequestException(55, "missing password");
            BasicDBObject query = new BasicDBObject();

            query.put("email", json.getString("email"));
//            query.put("password", APPCrypt.encrypt(json.getString("password")));
            query.put("password", json.getString("password"));

            Document item = contributorsCollection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No contributor found matching credentials");
            }
            Contributor contributor = new Contributor(
                    item.getString("name"),
                    item.getString("email"),
                    item.getString("password"),
                    item.getString("nativeLanguage"),
                    item.getString("phone"),
                    item.getString("gender")

            );
            contributor.setId(item.getObjectId("_id").toString());
            return new APPResponse(new ContributorSession(contributor));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        catch (APPBadRequestException e) {
            throw e;
        }
        catch (APPNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new APPInternalServerException(0, e.getMessage());
        }
    }
}



