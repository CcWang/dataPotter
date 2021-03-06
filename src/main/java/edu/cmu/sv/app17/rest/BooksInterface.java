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
import edu.cmu.sv.app17.helpers.APPListResponse;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.helpers.APPResponse;
import edu.cmu.sv.app17.models.FavoriteList;
import edu.cmu.sv.app17.models.Book;
import edu.cmu.sv.app17.models.Book;
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
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

@Path("books")
public class BooksInterface {

    private MongoCollection<Document> collection;
    private ObjectWriter ow;


    public BooksInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("books");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getAll(@DefaultValue("_id") @QueryParam("sort") String sortArg ,@DefaultValue("100") @QueryParam("count") int count,
    @DefaultValue("0") @QueryParam("offset") int offset) {

        ArrayList<Book> bookList = new ArrayList<Book>();

        BasicDBObject sortParams = new BasicDBObject();
        List<String> sortList = Arrays.asList(sortArg.split(","));
        sortList.forEach(sortItem -> {
            sortParams.put(sortItem,1);
        });

        try {
            FindIterable<Document> results = collection.find().skip(offset).limit(count).sort(sortParams);
            for (Document item : results) {
                Book book = new Book(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
            book.setId(item.getObjectId("_id").toString());
            bookList.add(book);
            }
            return new APPResponse(bookList);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No TV Shows");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }


    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such book, my friend");
            }
            Book book = new Book(
                    item.getString("name"),
                    item.getString("genre"),
                    item.getInteger("level"),
                    item.getString("contributorId")
            );
            book.setId(item.getObjectId("_id").toString());
            return new APPResponse(book);

        } catch (APPNotFoundException e) {
            throw new APPNotFoundException(0, "No such book");
        } catch (IllegalArgumentException e) {
            throw new APPBadRequestException(45, "Doesn't look like MongoDB ID");
        } catch (Exception e) {
            throw new APPInternalServerException(99, "Something happened, pinch me!");
        }
    }
    @GET
    @Path("bookOne/{name}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOneByName(@PathParam("name") String name) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("name", name);
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such book, my friend");
            }
            Book book = new Book(
                    item.getString("name"),
                    item.getString("genre"),
                    item.getInteger("level"),
                    item.getString("contributorId")
            );
            book.setId(item.getObjectId("_id").toString());
            return new APPResponse(book);

        } catch (APPNotFoundException e) {
            throw new APPNotFoundException(0, "No such book");
        } catch (IllegalArgumentException e) {
            throw new APPBadRequestException(45, "Doesn't look like MongoDB ID");
        } catch (Exception e) {
            throw new APPInternalServerException(99, "Something happened, pinch me!");
        }
    }

    //return that book's individual level and avg level
    @GET
    @Path("levels/{conId}/{booksName}")
    @Produces({MediaType.APPLICATION_JSON})
    public APPResponse getOne(@PathParam("conId") String id, @PathParam("booksName") String name) { ;
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("contributorId", id);
            query.put("name",name);
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such movie, my friend");
            }
            Integer indLev = item.getInteger("level");
            Integer size = avgLevel(name).get("size");
            Integer avgLev = avgLevel(name).get("avgLev");
            HashMap<String,Integer> levels = new HashMap<String,Integer>();
            levels.put("indLev",indLev);
            levels.put("avgLev",avgLev);
            levels.put("size",size);

            return new APPResponse(levels);

        } catch (APPNotFoundException e) {
            throw new APPNotFoundException(0, "No such book");
        } catch (IllegalArgumentException e) {
            throw new APPBadRequestException(45, "Doesn't look like MongoDB ID");
        } catch (Exception e) {
            throw new APPInternalServerException(99, "Something happened, pinch me!");
        }
    }
// get all level for same book

    public HashMap<String, Integer> avgLevel( String name) {

        Integer totalLevel = 0;

        try {

            FindIterable<Document> results = collection.find(eq("name",name));
            int size = 0;
            for (Document item : results) {
                totalLevel = totalLevel+ item.getInteger("level");
                size = size +1;
            }
            HashMap<String,Integer> totals = new HashMap<String,Integer>();
            totals.put("size",size);
            totals.put("avgLev",totalLevel/size);
            return totals;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No Movies");
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }


//  search
    @GET
    @Path("name/{search}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByName(@PathParam("search") String search) {
        BasicDBObject query = new BasicDBObject();
        ArrayList<Book> bookRet = new ArrayList<>();

        try {
//  query = {"name":{search}};
            FindIterable<Document> results = collection.find(regex("name",".*"+search+".*"));
            for (Document item : results) {
                Book book = new Book(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                book.setId(item.getObjectId("_id").toString());
                System.out.print(book);
                bookRet.add(book);
            }
            return new APPResponse(bookRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No books contain key word like: " + search);
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }

    @GET
    @Path("genre/{genre}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByGenre(@PathParam("genre") String genre) {

        ArrayList<Book> bookRet = new ArrayList<>();

        try {
            FindIterable<Document> results = collection.find(regex("genre",".*"+genre+".*"));
            for (Document item : results) {
                Book book = new Book(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                book.setId(item.getObjectId("_id").toString());
                System.out.print(book);
                bookRet.add(book);
            }
            return new APPResponse(bookRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No books contain genre like: " + genre);
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }

    @GET
    @Path("level/{level}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse searchByLevel(@PathParam("level") Integer level) {


        ArrayList<Book> bookRet = new ArrayList<>();

        try {
            FindIterable<Document> results = collection.find(eq("level",level));
            for (Document item : results) {
                Book book = new Book(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("contributorId")
                );
                book.setId(item.getObjectId("_id").toString());
                System.out.print(book);
                bookRet.add(book);
            }
            return new APPResponse(bookRet);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "No books contain genre like: " + level.toString());
        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }
    }


    @POST
    @Path("create/{id}")
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
            if (json.has("name"))
                doc.append("name",json.getString("name"));
            if (json.has("level"))
                doc.append("level",json.getInt("level"));
            if (json.has("genre"))
                doc.append("genre",json.getString("genre"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return new APPResponse(request);
    }

    @DELETE
    @Path("{croId}/{bookId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("croId") String croId, @PathParam("bookId") String bookId) {
        BasicDBObject query = new BasicDBObject();

        query.put("_id", new ObjectId(bookId));
        query.put("contributorId", croId);

        try{
            DeleteResult deleteResult = collection.deleteOne(query);
            if (deleteResult.getDeletedCount() < 1)
                throw new APPNotFoundException(66,"Could not delete");
        }
        catch(APPNotFoundException e) {
            throw new APPNotFoundException(0, "That Book was not found");
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