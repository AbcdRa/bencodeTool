package com.github.abcdra.drivers;

import com.github.abcdra.sender.BencodeSender;
import com.github.abcdra.sender.ResultSetToBencodeAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;

class ConnectionProxy implements InvocationHandler {

    private final Connection original;
    private final ResultSetToBencodeAdapter adapter;
    private final BencodeSender sender;


    public ConnectionProxy(Connection original, ResultSetToBencodeAdapter adapter, BencodeSender sender) {
        this.adapter = adapter;
        this.sender=  sender;
        this.original = original;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals("createStatement")) {
            Object generalResult = method.invoke(original, args);
            return modifiedStatement((Statement) generalResult);
        }
        return method.invoke(original, args);
    }


    private Statement modifiedStatement(Statement statement) {
        //Получаем данные о классе стейтмента
        //Все эти данные необходимы для создания прокси
        ClassLoader classLoader = statement.getClass().getClassLoader();
        Class<?>[] interfaces = statement.getClass().getInterfaces();
        StatementProxy invocationHandler = new StatementProxy(statement, adapter, sender);

        return (Statement) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }


}
