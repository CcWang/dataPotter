package edu.cmu.sv.app17.rest;

/*
* teacherName  string
* email     string
* password  string
* nativeLanguage    string
* phone     string
* gender    string
* year of teaching experience  Number
* open to accept new student Boolean
* */

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
import edu.cmu.sv.app17.helpers.APPListResponse;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Book;
import edu.cmu.sv.app17.models.Contributor;
import edu.cmu.sv.app17.models.Movie;
import edu.cmu.sv.app17.models.Tvshow;

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


@Path("contributors")
public class ContributorInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> booksCollection;
    private MongoCollection<Document> movieCollection;
    private MongoCollection<Document> tvCollection;

    private ObjectWriter ow;


    public ContributorInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("contributors");
        this.booksCollection = database.getCollection("books");
        movieCollection = database.getCollection("movie");
        tvCollection = database.getCollection("tvshow");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<Contributor> contributorList = new ArrayList<Contributor>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            throw new APPBadRequestException(33, "No contributers were found");
        }
        try {
            for (Document item : results) {
                Contributor c = new Contributor(
                        item.getString("name"),
                        item.getString("email"),
                        item.getString("password"),
                        item.getString("nativeLanguage"),
                        item.getString("phone"),
                        item.getString("gender")
                );
                c.setId(item.getObjectId("_id").toString());
                contributorList.add(c);
            }
            return new APPResponse(contributorList);
        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No Contributers");
        } catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@Context HttpHeaders headers,
                              @PathParam("id") String id) {

        try {
            checkAuthentication(headers,id);

        BasicDBObject query = new BasicDBObject();

            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "Sorry, we cannot find you. Sign up? ");
            }
            Contributor c = new Contributor(
                    item.getString("name"),
                    item.getString("email"),
                    item.getString("password"),
                    item.getString("nativeLanguage"),
                    item.getString("phone"),
                    item.getString("gender")

            );
            c.setId(item.getObjectId("_id").toString());
            return new APPResponse(c);
//            return teacher;
        } catch(APPUnauthorizedException e) {
            throw e;
        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such contributor");
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
        if (!json.has("name"))
            throw new APPBadRequestException(55,"name");
        if (!json.has("email"))
            throw new APPBadRequestException(55,"email");
        if (!json.has("password"))
            throw new APPBadRequestException(55,"password");
        if (!json.has("nativeLanguage"))
            throw new APPBadRequestException(55,"nativeLanguage");
        if (!json.has("phone"))
            throw new APPBadRequestException(55,"phone");
        if(!json.has("gender"))
            throw new APPBadRequestException (55,"missing gender");
//        if (json.getInt("odometer") < 0) {
//            throw new APPBadRequestException(56, "Invalid odometer - cannot be less than 0");
//        }
        try {
            Document doc = new Document("name", json.getString("name"))
                    .append("email", json.getString("email"))
                    .append("password", json.getString("password"))
                    .append("nativeLanguage", json.getString("nativeLanguage"))
                    .append("phone", json.getString("phone"))
                    .append("gender", json.getString("gender"));

            collection.insertOne(doc);
            return new APPResponse(request);
        } catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }

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
            if (obj.has("name"))
                doc.append("name",obj.getString("name"));
            if (obj.has("email"))
                doc.append("email",obj.getString("email"));
            if (obj.has("password"))
                doc.append("password",obj.getString("password"));
            if (obj.has("nativeLanguage"))
                doc.append("nativeLanguage",obj.getString("nativeLanguage"));
            if (obj.has("phone"))
                doc.append("phone",obj.getString("phone"));

            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such contributor");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
        return new APPResponse(obj);
    }


    @GET
    @Path("{id}/books")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getBooksForContributor(@Context HttpHeaders headers, @PathParam("id") String id, @DefaultValue("20") @QueryParam("count") int count,
                                                  @DefaultValue("0") @QueryParam("offset") int offset, @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        try {
            checkAuthentication(headers, id);

        ArrayList<Book> bookList = new ArrayList<>();
        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });



            BasicDBObject query = new BasicDBObject();
            query.put("contributorId", id);

            long resultCount = booksCollection.count(query);

            FindIterable<Document> results = booksCollection.find(query).skip(offset).limit(count).sort(sortParams);
            for (Document item : results) {
                Book b = new Book(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                b.setId(item.getObjectId("_id").toString());
                bookList.add(b);
            }
            return new APPListResponse(bookList,resultCount,offset, bookList.size());

        } catch(APPUnauthorizedException e) {
            throw e;

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }


    @GET
    @Path("{id}/movies")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getMoviesForContributor(@Context HttpHeaders headers, @PathParam("id") String id, @DefaultValue("20") @QueryParam("count") int count,
    @DefaultValue("0") @QueryParam("offset") int offset, @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Movie> movielist = new ArrayList<>();
        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("contributorId", id);

            long resultCount = movieCollection.count(query);

            FindIterable<Document> results = movieCollection.find(query).skip(offset).limit(count).sort(sortParams);
            for (Document item : results) {
                Movie m = new Movie(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                m.setId(item.getObjectId("_id").toString());
                movielist.add(m);
            }
            return new APPListResponse(movielist,resultCount,offset, movielist.size());

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }
    @GET
    @Path("{id}/tvshows")
    @Produces({MediaType.APPLICATION_JSON})
    public APPListResponse getTvshowsForContributor(@Context HttpHeaders headers, @PathParam("id") String id, @DefaultValue("20") @QueryParam("count") int count,
                                                    @DefaultValue("0") @QueryParam("offset") int offset, @DefaultValue("_id") @QueryParam("sort") String sortArg) {

        ArrayList<Tvshow> tvList = new ArrayList<>();
        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("contributorId", id);
            long resultCount = tvCollection.count(query);
            FindIterable<Document> results = tvCollection.find(query).skip(offset).limit(count).sort(sortParams);;
            for (Document item : results) {
                Tvshow tv = new Tvshow(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                tv.setId(item.getObjectId("_id").toString());
                tvList.add(tv);
            }
            return new APPListResponse(tvList,resultCount,offset, tvList.size());

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }


    @POST
    @Path("{id}/books")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse create(@Context HttpHeaders headers,
                              @PathParam("id") String id, Object request) {

        JSONObject json = null;
        try {
            checkAuthentication(headers,id);

            json = new JSONObject(ow.writeValueAsString(request));


        if (!json.has("name"))
            throw new APPBadRequestException(55,"name");
        if (!json.has("genre"))
            throw new APPBadRequestException(55,"genre");
        if (!json.has("level"))
            throw new APPBadRequestException(55,"level");
//        if (!json.has("contributorId"))
//            throw new APPBadRequestException(55,"contributorId");

            Document doc = new Document("name", json.getString("name"))
                    .append("genre", json.getString("genre"))
                    .append("level", json.getInt("level"))
                    .append("contributorId", id);
            booksCollection.insertOne(doc);
            return new APPResponse(request);

        } catch(APPUnauthorizedException e) {
            throw e;
        } catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }
    }


    @PATCH
    @Path("{id}/books/{bookid}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse update(@PathParam("id") String id,
                              @PathParam("bookid") String bookid, Object request)
    {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(bookid));
            query.put("contributorId", id);

            Document doc = new Document();
            if (json.has("name"))
                doc.append("name",json.getString("name"));
            if (json.has("genre"))
                doc.append("genre",json.getString("genre"));
            if (json.has("level"))
                doc.append("level",json.getInt("level"));

            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse();
    }



        @DELETE
        @Path("{croId}/{movieId}")
        @Produces({ MediaType.APPLICATION_JSON})
        public Object delete(@PathParam("croId") String croId, @PathParam("movieId") String movieId) {
            BasicDBObject query = new BasicDBObject();

            query.put("_id", new ObjectId(movieId));
            query.put("contributorId", croId);

            try{
                DeleteResult deleteResult = collection.deleteOne(query);
                if (deleteResult.getDeletedCount() < 1)
                    throw new APPNotFoundException(66,"Could not delete");
            }
            catch(APPNotFoundException e) {
                throw new APPNotFoundException(0, "That Movie was not found");
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