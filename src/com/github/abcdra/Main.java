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
            //Указываем перед базой данной префикс bencode для того чтобы DriverManager
            //использовал самописный зарегистрированный драйвер
            String url = "bencode:mysql://localhost/general";
            String username = "root";
            String password = "toor";
            //Указываем хост и порт в который сокет попытается отправить перехваченные данные
            String source = "127.0.0.1:8080";

            //Используем конкретные реализации адаптера(преобразоваеие resultSet в строку bencode) и
            //sender(отправитель данных по источнику)
            ResultSetToBencodeAdapter adapter = new ResultSetToBencodeAdapterImp();
            BencodeSender sender = new BencodeSenderImp(source);

            //Регистрируем кастомный драйвер
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
        System.out.println("out: \n"+ serialize);
        System.out.println("Проверка десериализации числа: \n"+ BencodeDeserializer.deserialize(serialize).value());

        String str = "test";
        System.out.println("\nПроверка сериализации строки: "+str);
        serialize = BencodeSerializer.serialize(str);
        System.out.println("out: \n"+ serialize);
        System.out.println("Проверка десериализации строки: \n"+ BencodeDeserializer.deserialize(serialize).value());

        List<String> list = new ArrayList<>();
        list.add(str);
        list.add("spam");
        System.out.println("\nПроверка сериализации списка: "+ Arrays.toString(list.toArray()));
        serialize = BencodeSerializer.serialize(list);
        System.out.println("out: \n"+ serialize);
        System.out.println("Проверка десериализации списка: \n"+ BencodeDeserializer.deserialize(serialize).value());

        Map<String, Number> dict = new HashMap<>();
        dict.put("firstKey",1);
        dict.put("secondKey",2);
        System.out.println("\nПроверка сериализации словаря: "+ dict.toString());
        serialize = BencodeSerializer.serialize(dict);
        System.out.println("out:\n "+ serialize);
        System.out.println("Проверка десериализации словаря: \n"+ BencodeDeserializer.deserialize(serialize).value());

        List<String> list2 = new ArrayList<>();
        list2.add("Hello");
        list2.add("Another Hello");
        List<List<String>> listInList = new ArrayList<>();
        listInList.add(list);
        listInList.add(list2);
        System.out.println("\nПроверка сериализации вложенного списка: "+ Arrays.deepToString(listInList.toArray()));
        serialize = BencodeSerializer.serialize(listInList);
        System.out.println("out:\n "+ serialize);
        System.out.println("Проверка десериализации вложенного списка: \n"+ BencodeDeserializer.deserialize(serialize).value());

        Map<String, List<String>> dict2 = new HashMap<>();
        dict2.put("key1", list);
        dict2.put("abc", list2);
        System.out.println("\nПроверка сериализации вложенного словаря: "+ dict2.toString());
        serialize = BencodeSerializer.serialize(dict2);
        System.out.println("out:\n "+ serialize);
        System.out.println("Проверка десериализации вложенного словаря: \n"+ BencodeDeserializer.deserialize(serialize).value());
    }
}
