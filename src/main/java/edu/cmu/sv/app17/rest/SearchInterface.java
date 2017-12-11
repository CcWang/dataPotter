package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.models.Book;
import edu.cmu.sv.app17.models.Movie;
import edu.cmu.sv.app17.models.Tvshow;
import jdk.nashorn.internal.runtime.regexp.RegExp;
import org.bson.Document;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Path("search")
public class SearchInterface {
    private MongoCollection<Document> mCollection = null;
    private MongoCollection<Document> tCollection = null;
    private MongoCollection<Document> bCollection = null;
    private ObjectWriter ow;

    public SearchInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");
        mCollection = database.getCollection("movie");
        tCollection = database.getCollection("tvshow");
        bCollection = database.getCollection("books");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getScore(@QueryParam("type") int type, @QueryParam("name") String name,@QueryParam("level") int level) {
        try {
            if (type == 1) {
                BasicDBObject query = new BasicDBObject();
                if (name != null) query = new BasicDBObject("name", new BasicDBObject("$regex", ".*" + name + ".*"));
                if (level != 0) query.put("level", level);
                FindIterable<Document> results = mCollection.find(query).limit(30);
                ArrayList<Movie> movieList = new ArrayList<>();
                for (Document item : results) {
                    Movie movie = new Movie(
                            item.getString("name"),
                            item.getString("genre"),
                            item.getInteger("level"),
                            item.getString("contributorId"),
                            item.getInteger("movieid")
                    );
                    movie.setId(item.getObjectId("_id").toString());
                    movieList.add(movie);
                }
                return new APPResponse(movieList);
            } else if (type == 2) {
                BasicDBObject query = new BasicDBObject();
                if (name != null) query = new BasicDBObject("name", new BasicDBObject("$regex", ".*" + name + ".*"));
                if (level != 0) query.put("level", level);
                FindIterable<Document> results = tCollection.find(query).limit(30);
                ArrayList<Tvshow> tvList = new ArrayList<>();
                for (Document item : results) {
                    Tvshow tv = new Tvshow(
                            item.getString("name"),
                            item.getString("genre"),
                            item.getInteger("level"),
                            item.getString("contributorId"),
                            item.getInteger("tvid")
                    );
                    tv.setId(item.getObjectId("_id").toString());
                    tvList.add(tv);
                }
                return new APPResponse(tvList);
            } else {
                BasicDBObject query = new BasicDBObject();
                if (name != null) query = new BasicDBObject("name", new BasicDBObject("$regex", ".*" + name + ".*"));
                if (level != 0) query.put("level", level);
                FindIterable<Document> results = bCollection.find(query).limit(30);
                ArrayList<Book> bkList = new ArrayList<>();
                for (Document item : results) {
                    Book bk = new Book(
                            item.getString("name"),
                            item.getString("genre"),
                            item.getInteger("level"),
                            item.getString("contributorId")
                    );
                    bk.setId(item.getObjectId("_id").toString());
                    bkList.add(bk);
                }
                return new APPResponse(bkList);
            }
        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Item");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }
}
