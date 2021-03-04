package com.github.abcdra;

import com.github.abcdra.drivers.BencodeJDBCDriver;
import com.github.abcdra.sender.BencodeSender;
import com.github.abcdra.sender.BencodeSenderImp;
import com.github.abcdra.sender.ResultSetToBencodeAdapter;
import com.github.abcdra.sender.ResultSetToBencodeAdapterImp;
import com.github.abcdra.serialization.BencodeDeserializer;
import com.github.abcdra.serialization.BencodeSerializer;

import java.sql.*;
import java.text.ParseException;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        try {
            serilizationCheck();
        } catch (ParseException e) {
            System.err.println("Parse Error " + e.getMessage());
        }

        db();
    }

    private static void db() {
        try{
            String url = "bencode:mysql://localhost/general";
            String username = "root";
            String password = "toor";
            String source = "127.0.0.1:8080";
            ResultSetToBencodeAdapter adapter = new ResultSetToBencodeAdapterImp();
            BencodeSender sender = new BencodeSenderImp(source);

            DriverManager.registerDriver(new BencodeJDBCDriver(adapter,sender));
            try (Connection conn = DriverManager.getConnection(url, username, password)){
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM bencode1 where id=1");
                System.out.println("Connection to Store DB successfully!");
            }
        }
        catch(Exception ex){
            System.out.println("Connection failed...");

            System.err.println(ex.getMessage());
        }
    }

    public static void serilizationCheck() throws ParseException {
        int num = 5;
        System.out.println("Проверка сериализации числа: "+num);
        String serialize = BencodeSerializer.serialize(num);
        System.out.println("out: "+ serialize);
        System.out.println("Проверка десериализации числа: "+ BencodeDeserializer.deserialize(serialize).value());

        String str = "test";
        System.out.println("Проверка сериализации строки: "+str);
        serialize = BencodeSerializer.serialize(str);
        System.out.println("out: "+ serialize);
        System.out.println("Проверка десериализации строки: "+ BencodeDeserializer.deserialize(serialize).value());

        List<String> list = new ArrayList<>();
        list.add(str);
        list.add("spam");
        System.out.println("Проверка сериализации списка: "+ Arrays.toString(list.toArray()));
        System.out.println("out: "+ BencodeSerializer.serialize(list));

        Map<String, Number> dict = new HashMap<>();
        dict.put("firstKey",1);
        dict.put("secondKey",2);
        System.out.println("Проверка сериализации словаря: "+ dict.toString());
        System.out.println("out: "+ BencodeSerializer.serialize(dict));

        List<String> list2 = new ArrayList<>();
        list2.add("Hello");
        list2.add("Another Hello");
        List<List<String>> listInList = new ArrayList<>();
        listInList.add(list);
        listInList.add(list2);
        System.out.println("Проверка сериализации вложенного списка: "+ Arrays.deepToString(listInList.toArray()));
        System.out.println("out: "+ BencodeSerializer.serialize(listInList));

    }
}
