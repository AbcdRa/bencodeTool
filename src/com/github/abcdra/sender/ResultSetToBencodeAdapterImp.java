package com.github.abcdra.sender;

import com.github.abcdra.serialization.JSONToBencode;
import com.github.abcdra.types.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Класс который преобразует ResultSet в Bencode
public class ResultSetToBencodeAdapterImp implements ResultSetToBencodeAdapter{
    @Override
    public BencodeType convert(ResultSet resultSet) {
        try {
            List<List<Object>> list = asList(resultSet);
            if(list.size()==0) {
                return new BencodeString("");
            }
            List<String> types = getTypes(resultSet.getMetaData());
            List<String> names = getColumnNames(resultSet.getMetaData());
            if(list.size()==1) {
                List<Object> row = list.get(0);
                return row2BencodeDict(row, types, names);
            }
            List<BencodeType> bencodeRows = new ArrayList<>();
            for(List<Object> row:list) {
                bencodeRows.add(row2BencodeDict(row,types,names));
            }
            return new BencodeList(bencodeRows);
        } catch (SQLException e) {
            System.err.println("Не удалось преобразовать сет в бенкод((( ");
            System.err.println(e.getMessage());
        }
        return new BencodeString("Error");
    }


    //Преобразует resultSet в лист, с которым удобней работать
    private List<List<Object>> asList(ResultSet resultSet) throws SQLException {
        List<List<Object>> list = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int column_count = metaData.getColumnCount();
        var columnType = metaData.getColumnTypeName(4);
        List<Object> row = new ArrayList<>();
        do {
            resultSet.next();
            for(int i=1; i<=column_count; i++) {
                row.add(resultSet.getObject(i));
            }
            list.add(row);
            row = new ArrayList<>();

        } while (!resultSet.isLast());
        return list;
    }

    //Возвращает список типов столбцов
    private List<String> getTypes(ResultSetMetaData metaData) throws SQLException {
        List<String> list = new ArrayList<>();
        for (var i=1; i<=metaData.getColumnCount(); i++) {
            list.add(metaData.getColumnTypeName(i));
        }
        return list;
    }

    //Возвращает список имен столбцов
    private List<String> getColumnNames(ResultSetMetaData metaData) throws SQLException {
        List<String> list = new ArrayList<>();
        for (var i=1; i<=metaData.getColumnCount(); i++) {
            list.add(metaData.getColumnName(i));
        }
        return list;
    }


    //Преобразует одну строчку в bencode словарь
    private BencodeDict row2BencodeDict(List<Object> list,List<String> types,List<String> names) {
        Map<BencodeString,BencodeType> dict = new HashMap<>();

        //Поддерживается только 3 типа, но это самые распространенные
        for (var i=0; i<list.size();i++) {
            if(types.get(i).equals("INT")) {
                dict.put(new BencodeString(names.get(i)),new BencodeNumber((Number) list.get(i)));
            }
            if(types.get(i).equals("VARCHAR")) {
                dict.put(new BencodeString(names.get(i)),new BencodeString((String) list.get(i)));
            }
            if(types.get(i).equals("JSON")) {
                String bencode = JSONToBencode.convert((String) list.get(i));
                dict.put(new BencodeString(names.get(i)), new BencodeRaw(bencode));
            }
        }
        return new BencodeDict(dict);
    }
}
