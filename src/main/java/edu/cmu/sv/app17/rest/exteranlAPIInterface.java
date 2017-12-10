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
import edu.cmu.sv.app17.models.FavoriteList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;


import java.io.IOException;




import static com.mongodb.client.model.Filters.eq;



@Path("themoviedb")
public class exteranlAPIInterface {

    private MongoCollection<Document> collection = null;
    private MongoCollection<Document> movieCollection = null;
    private MongoCollection<Document> bookCollection = null;
    private MongoCollection<Document> tvshowCollection = null;
    private ObjectWriter ow;
    private String apikey = "664f8054c78de425d08aba35e84e6a11";

    public exteranlAPIInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        collection = database.getCollection("favoriteLists");
        movieCollection = database.getCollection("movie");
        bookCollection = database.getCollection("books");
        tvshowCollection = database.getCollection("tvshow");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

//get movie information from themoviedb
//    movies
    @GET
    @Path("{type}/{name}")
    @Produces({ MediaType.APPLICATION_JSON})
    @Consumes({ MediaType.APPLICATION_JSON})
    public  APPResponse getMovie(@PathParam("type") String type, @PathParam("name") String name){
        BasicDBObject query = new BasicDBObject();
        query.put("name", name);
        System.out.print(name);

        try{
            Document result;
            Integer id;
            String mediaType;
            if (type.equals("movies")){
                result = movieCollection.find(query).first();
                mediaType = "movie";
                id = result.getInteger("movieid");
            }else{
                result = tvshowCollection.find(query).first();
                mediaType = "tv";
                id = result.getInteger("tvid");
            }


            String urlAddress = "https://api.themoviedb.org/3/"+mediaType+"/"+id+"?language=en-US&api_key="+apikey;
            System.out.print(urlAddress);
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urlAddress)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                // converts response into an array of books
                String resStr = response.body().string().toString();
                return new APPResponse(resStr);
            }catch (IOException e) {
                e.printStackTrace();
            }

            return new APPResponse("wrong");
        }catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "You have no favorite list");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }




}
