package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        while (true) {

            System.out.println("Enter your choice: \n" +
                    " 1 - Register, 2 - Get Tasks, 3 - Add Task, 4 - Set Task As Done");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> register(idInput());
                case 2 -> getTasks(idInput());
                case 3 -> addTask(idInput(), textInput());
                case 4 -> setTaskDone(idInput(), textInput());
                default -> System.out.println("Nothing selected, try again");
            }
        }
    }

    public static String idInput(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter id");
        return scanner.nextLine();
    }
    public static String textInput(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter task");
        return scanner.nextLine();
    }

    public static void register(String id){


        try {
            CloseableHttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://app.seker.live/fm1/register")
                    .setParameter("id", id)
                    .build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse response = client.execute(request);
            String output = EntityUtils.toString(response.getEntity());
            Response myResponse = new ObjectMapper().readValue(output, Response.class);
            Integer code = myResponse.getErrorCode();
            if (myResponse.isSuccess()){
                System.out.println("You have registered successfully");
            }else {
                System.out.println(getTextualError(code));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void getTasks(String id) {

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://app.seker.live/fm1/get-tasks")
                    .setParameter("id", id)
                    .build();
            HttpGet request = new HttpGet(uri);
            CloseableHttpResponse response = client.execute(request);
            String output = EntityUtils.toString(response.getEntity());
            Response myResponse = new ObjectMapper().readValue(output, Response.class);
            Integer code = myResponse.getErrorCode();
            if (myResponse.isSuccess()) {
                int cnt = 0;
                for (TaskModel taskModel : myResponse.getTasks()) {
                    if (!taskModel.isDone()) {
                        cnt++;
                        System.out.println(taskModel.getTitle());
                    }
                }
                System.out.println("you have " + cnt + " uncompleted tasks. ");

            } else {
                System.out.println(getTextualError(code));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void addTask(String id, String text){
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://app.seker.live/fm1/add-task")
                    .setParameter("id", id)
                    .addParameter("text", text)
                    .build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse response = client.execute(request);
            String output = EntityUtils.toString(response.getEntity());
            if (output.equals("")){
                System.out.println("The task has been successfully added");
            }else {
                Response myResponse = new ObjectMapper().readValue(output, Response.class);
                Integer code = myResponse.getErrorCode();
                System.out.println(getTextualError(code));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void setTaskDone(String id, String text){

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            URI uri = new URIBuilder("https://app.seker.live/fm1/set-task-done")
                    .setParameter("id", id)
                    .addParameter("text", text)
                    .build();
            HttpPost request = new HttpPost(uri);
            CloseableHttpResponse response = client.execute(request);
            String output = EntityUtils.toString(response.getEntity());
            Response myResponse = new ObjectMapper().readValue(output, Response.class);
            Integer code = myResponse.getErrorCode();
            if (myResponse.isSuccess()) {
                System.out.println("The task was marked as done");
            } else {
                System.out.println(getTextualError(code));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String  getTextualError (Integer code){
        return switch (code){
            case 1000, 1002-> "No ID was entered!";
            case 1003-> "This user is already registered!";
            case 1001 -> "The requested ID has not been registered yet";
            case 1004 -> "No task entered";
            case 1005 -> "This task has already been entered";
            case 1006 -> "The task does not exist for the user";
            case 1007 -> "The task is already marked as completed";
            default -> "Unknown error!";
        };
    }




}