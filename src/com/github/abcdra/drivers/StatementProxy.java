package com.github.abcdra.drivers;

import com.github.abcdra.sender.BencodeSender;
import com.github.abcdra.sender.ResultSetToBencodeAdapter;
import com.github.abcdra.types.BencodeType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;


public class StatementProxy implements InvocationHandler {

    private final Statement original;
    private final ResultSetToBencodeAdapter adapter;
    private final BencodeSender sender;

    public StatementProxy(Statement original, ResultSetToBencodeAdapter adapter, BencodeSender sender) {
        this.adapter = adapter;
        this.sender=  sender;
        this.original = original;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("executeQuery")) {
            Object generalResult = method.invoke(original, args);
            ResultSet resultSet = (ResultSet)  generalResult;
            BencodeType content = adapter.convert(resultSet);
            sender.send(content);
            return generalResult;
        }
        return method.invoke(original,args);
    }
}
