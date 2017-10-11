package edu.cmu.sv.app17.rest;

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
import edu.cmu.sv.app17.models.FavoriteList;
import edu.cmu.sv.app17.models.Book;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("books")
public class BooksInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> favoriteListCollection;
    private ObjectWriter ow;


    public BooksInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("books");
        this.favoriteListCollection = database.getCollection("favoriteLists");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public ArrayList<Book> getAll() {

        ArrayList<Book> bookList = new ArrayList<Book>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return bookList;
        }
        for (Document item : results) {
            Book book = new Book(
                    item.getString("name"),
                    item.getString("genre"),
                    item.getString("level")
            );
            book.setId(item.getObjectId("_id").toString());
            bookList.add(book);
        }
        return bookList;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Book getOne(@PathParam("id") String id) {
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
                    item.getString("level")
            );
            book.setId(item.getObjectId("_id").toString());
            return book;

        } catch (APPNotFoundException e) {
            throw new APPNotFoundException(0, "No such book");
        } catch (IllegalArgumentException e) {
            throw new APPBadRequestException(45, "Doesn't look like MongoDB ID");
        } catch (Exception e) {
            throw new APPInternalServerException(99, "Something happened, pinch me!");
        }


    }
}