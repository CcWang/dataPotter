package edu.cmu.sv.app17.rest;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.cmu.sv.app17.helpers.APPResponse;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Path("score")
public class ScoreInterface {

    private MongoCollection<Document> col = null;
    private final String curDir = System.getProperty("user.dir");
    private final String zipFile = System.getProperty("user.dir")+ "//temp.zip";
    private int low_frequent_count = 0;
    private int high_frequent_count = 0;
    private int low_count = 0;
    private int total_count = 0;
    HashMap<String, Integer> lowList = new HashMap<String, Integer>();
    HashMap<String, Integer> highList = new HashMap<String, Integer>();
    private int[] data = new int[6];

    public ScoreInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("wordfre");
        col = database.getCollection("wordfre");
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public APPResponse getScore(@QueryParam("url") String url) {

        /* This is to download the zip file from subscene.com */
        int flag = 0;
        try {
            URL src = new URL(url);
            // set to avoid 403 error
            URLConnection conn = src.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            conn.setRequestProperty("Referer", "https://www.nseindia.com/products/content/equities/equities/archieve_eq.htm");
            conn.connect();

            ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(zipFile));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
            flag = 1;
        } catch (MalformedURLException e) {
            data[5] = 0;
            e.printStackTrace();
        } catch (IOException e) {
            data[5] = 0;
            e.printStackTrace();
        }

        /* This is to uncompress the zip file */
        if (flag == 1) {
            final int BUFFER_SIZE = 2048;
            File destDir = new File(curDir);
            try {
                ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
                ZipEntry entry = zipIn.getNextEntry();
                if (entry == null) System.out.println("Unsupported Files");
                else flag = 2;

                while (entry != null) {
                    String filePath = curDir + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        // if the entry is a file, extracts it
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                        byte[] bytesIn = new byte[BUFFER_SIZE];
                        int read = 0;
                        while ((read = zipIn.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                        bos.close();
                    } else {
                        // if the entry is a directory, make the directory
                        File dir = new File(filePath);
                        dir.mkdir();
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
                zipIn.close();
            } catch (FileNotFoundException e) {
                data[5] = 0;
                e.printStackTrace();
            } catch (IOException e) {
                data[5] = 0;
                e.printStackTrace();
            }
        }

        /* Use mongodb to construct a word frequency table
           if windows: https://docs.mongodb.com/v2.6/tutorial/install-mongodb-on-windows/
           mongoimport:https://docs.mongodb.com/manual/reference/program/mongoimport/
           command: mongoimport --db wordfre --collection wordfre --type csv --headerline --file /path/to/myfile.csv
           In my example: "C:/Program Files/MongoDB/Server/3.4/bin>mongoimport --db wordfre --collection wordfre --type csv --headerline --file C:/Users/wangf/IdeaProjects/untitled/heatmap.csv"
         */

        /* This is to read the srt file */
        if (flag == 2) {
            File srtFile = null;
            File[] fileList = new File(curDir).listFiles(new FilenameFilter() {
                public boolean accept(File srtDest, String filename)
                { return filename.endsWith(".srt"); }
            } );

            for(int i=0; i<fileList.length; i++) {
                srtFile = fileList[i];
                System.out.println("Proceesing File Name:" + srtFile.getName());

                try {
                    FileReader fileReader = new FileReader(srtFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        /* Valid subtitle text: start with alphabet letter; remove punctuations; remove HTML tags */
                        if (line.matches("[a-zA-Z].*")) {
                            line = line.replaceAll("[.:,!\"/?]","");
                            line = line.replaceAll("\\<[^>]*>","");
                            String[] splited = line.split("\\s+");
                            total_count = total_count + splited.length;

                            for (int j=0; j<splited.length; j++) {
                                int spaceIndex = splited[j].indexOf("'");
                                if (spaceIndex != -1)
                                {
                                    splited[j] = splited[j].substring(0, spaceIndex);
                                }
                                Document item = col.find(new BasicDBObject("Word", splited[j].toLowerCase())).first();
                                /* Low frequency: cannot find in database heatmap collection; First occurrence: cannot find in in-memory hashmap */
                                if ((item == null && !splited[j].contains("-") && !splited[j].contains(")"))){
                                    low_count++;
                                    if (lowList.get(splited[j]) ==null){
                                        lowList.put(splited[j], 1);
                                        low_frequent_count++;
                                        //System.out.println(splited[j]);
                                    }
                                } else if (highList.get(splited[j]) ==null) {
                                    highList.put(splited[j],1);
                                    high_frequent_count++;
                                }
                            }
                        }
                    }
                    fileReader.close();
                    /* Delete the srt file on distk */
                    srtFile.delete();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fileList.length == 0 ) {
                    System.out.println("no srt file");
                }
            }
        }

        data[0] = total_count;
        data[1] = low_count;
        data[2] = total_count-low_count;
        data[3] = low_frequent_count;
        data[4] = high_frequent_count;

        if (total_count == 0) data[5] = 0; //Unsupported Files
        if (low_frequent_count < total_count*0.05 && low_frequent_count < high_frequent_count*0.3) data[5] = 1; //Easy
        if (low_frequent_count > total_count*0.06 && low_frequent_count > high_frequent_count*0.35) data[5] = 3; //Difficult
        else data[5] = 2; //Normal

        return new APPResponse(data);

        /* Examples - War deployed as context path /app - Note subscene.com changes its link every day.
        Easy:
        Inside out: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=kdxfT0L_-RdjSwRN_WHV3-GHGoC-LWHULx4tM2qrIG1mVxa6gxbtcAsxKs9jJpoimamHRFQicTB2Z1308AdyRYqkWZxLePDm437DpaLsuWh98tiknaDeWpeXY7oKmqff0
        Forrest Gump: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=0idGtMfTgWruJ6UGFgZhFYGQ2mMnYga5QOmpFaB7ujx1qv1yOKvpydFl7i69N5LvOvomuGVWRtPVVO3NRep7YauKlfvwqTAunKAk86fcPZQZ3-K9GaI_gkVTSoJFDiL60
        La La Land: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=Xpv_0b4-bfHnZ09MLdNnY1T7Ej7D3_j7riqjNmNFyuknJ0CFRb1KLKRcimeGe5x-gf6ODFUqDYlc1PoA9sI-YXoiFUZGYh_FHI_0fMAECs2zoOQwgamDNSYQe3-TCr_P0

        Difficult:
        Inception: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=WUJXGRrr7GHyzCqNh8-uCfl12vfKDziCSJx96uGnN732tTiRZhoNehfsOpvyKzVhFlP9tSO1ycJBx0fBY_yq_N6sOF9la4WwHxhIMR63FOEjM5wBYaw6B_9-8M6hGTAS0
        Cloud Atlas: https://subscene.com/subtitle/download?mac=dWTBGKRRDy4jqSjG-lTNdQkrmCr2jqbqex6GfzR2ZP3Jlw5ju2cUQ80ORp5irxLcQ8aImscu7mLb67jNzy64_OtepSgiBKmj7wU4831rWQEMbfnv84GXEiCPAM2wfiWa0

        Just Pass the line of Difficult:
        Kingsman: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=_8mqeJoc3_yda5u8sLG3mcrkAMbJhpE8Z74Ueq3tK6NmVd2pH3c0zcGHN6X-IigwtXK0IZ-c1g8J_LZXhTJ3qXBq4j4543dIOkthyn_vfY33tFu8WEZ3NWOf1IZ9cDWD0
        Fight Club: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=izheEWLjeoRnCFx-YjMQsoPAhIHEj8fUwaY-S78ofQUGvzy2L9QbmBmk2DRxo1aEvM8rCN1IEpvj1o7OrGagR240VttC8M7fUo_YhcLFuL7BnU7fs1grnHuutxQGXVAS0

        Normal:
        Before Sunrise: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=agAzhMYZxe9kz8dJqyT4MaDJtt0PM2ggRYeKceyH90aiOG__i9MQYr3_tzlVi0kocWQs_88zuqt47Iuac27Y529WUMmwoDzCYe0nsQrmTAk4tSTU4QkwlsdKBxhBagc40
        The Shawshank Redemption: http://localhost:8080/app/score/?url=https://subscene.com/subtitle/download?mac=P5tVkmajoHxm1iOa4TuHY9ERIwVksgHeDYAJEMLEH1zyRbgIpiaOlW2Q9KcDVW9AMYERbK-jipqB2je8wtDu4eDEbW3Joo6DzTngz4BDUyjJyhGRUpUcRk1-E_CMPt3_0
         */
    }
}