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
import java.util.Date;

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
import edu.cmu.sv.app17.models.Book;
import edu.cmu.sv.app17.models.Teacher;
import edu.cmu.sv.app17.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("teachers")
public class TeacherInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> booksCollection;
    private ObjectWriter ow;


    public TeacherInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("dataPotter");

        this.collection = database.getCollection("teachers");
        this.booksCollection = database.getCollection("books");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getAll() {

        ArrayList<Teacher> teacherList = new ArrayList<Teacher>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return  new APPResponse(teacherList);
        }
        for (Document item : results) {
            Teacher teacher = new Teacher(
                    item.getString("teachername"),
                    item.getString("email"),
                    item.getString("password"),
                    item.getString("nativeLanguage"),
                    item.getString("phone"),
                    item.getString("gender")
//                    item.getInteger("exp"),
//                    item.getBoolean("newStudent")
            );
            teacher.setId(item.getObjectId("_id").toString());
            teacherList.add(teacher);
        }
        return new APPResponse(teacherList);

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
            Teacher teacher = new Teacher(
                    item.getString("teachername"),
                    item.getString("email"),
                    item.getString("password"),
                    item.getString("nativeLanguage"),
                    item.getString("phone"),
                    item.getString("gender")
//                    item.getInteger("exp"),
//                    item.getBoolean("newStudent")
            );
            teacher.setId(item.getObjectId("_id").toString());
            return new APPResponse(teacher);
//            return teacher;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such teacher");
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
        if (!json.has("exp"))
            throw new APPBadRequestException(55,"exp");
        if (!json.has("newStudent"))
            throw new APPBadRequestException(55,"newStudent");
//        if (json.getInt("odometer") < 0) {
//            throw new APPBadRequestException(56, "Invalid odometer - cannot be less than 0");
//        }
        Document doc = new Document("teachername", json.getString("teachername"))
                .append("email", json.getString("email"))
                .append("password", json.getString("password"))
                .append("nativeLanguage", json.getString("nativeLanguage"))
                .append("phone", json.getString("phone"))
                .append("gender", json.getString("gender"))
                .append("exp", json.getInt("exp"))
                .append("newStudent", json.getBoolean("newStudent"));

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
            if (obj.has("phone"))
                doc.append("phone",obj.getString("phone"));
            if (obj.has("gender"))
                doc.append("gender",obj.getString("gender"));
            if (obj.has("exp"))
                doc.append("exp",obj.getInt("exp"));
            if (obj.has("newStudent"))
                doc.append("newStudent",obj.getBoolean("newStudent"));


            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such teacher");
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
    public APPResponse getBooksForTeacer(@PathParam("id") String id) {

        ArrayList<Book> bookList = new ArrayList<Book>();

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("teacherId", id);

            FindIterable<Document> results = booksCollection.find(query);
            for (Document item : results) {
                Book book = new Book(
                        item.getString("name"),
                        item.getString("genre"),
                        item.getInteger("level"),
                        item.getString("teacherId")
                );
                book.setId(item.getObjectId("_id").toString());
                bookList.add(book);
            }
            return new APPResponse(bookList);

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
    public APPResponse create(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        if (!json.has("name"))
            throw new APPBadRequestException(55,"name");
        if (!json.has("genre"))
            throw new APPBadRequestException(55,"genre");
        if (!json.has("level"))
            throw new APPBadRequestException(55,"level");
        if (!json.has("teacherId"))
            throw new APPBadRequestException(55,"teacherId");

        Document doc = new Document("usersId", id)
                .append("name", json.getString("name"))
                .append("genre", json.getString("genre"))
                .append("level", json.getString("level"))
                .append("teacherId",json.getInt("teacherId"));

        booksCollection.insertOne(doc);
        return new APPResponse(request);
    }

}