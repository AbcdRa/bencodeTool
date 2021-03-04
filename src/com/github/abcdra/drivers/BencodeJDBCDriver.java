package com.github.abcdra.drivers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.github.abcdra.sender.BencodeSender;
import com.github.abcdra.sender.ResultSetToBencodeAdapter;
import com.mysql.cj.jdbc.Driver;

public class BencodeJDBCDriver extends Driver  {
    private final ResultSetToBencodeAdapter adapter;
    private final BencodeSender sender;

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        String modifiedURL = "jdbc" + url.substring(7);
        return getProxyConnection(super.connect(modifiedURL, info));
    }

    private Connection getProxyConnection(Connection connection) {
            //Получаем данные о классе Connection
            //Все эти данные необходимы для создания прокси
            ClassLoader classLoader = connection.getClass().getClassLoader();
            Class<?>[] interfaces = connection.getClass().getInterfaces();
            InvocationHandler invocationHandler = new ConnectionProxy(connection, adapter, sender);

        return (Connection) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);

    }

    public BencodeJDBCDriver(ResultSetToBencodeAdapter adapter, BencodeSender sender) throws SQLException {
        super();
        this.adapter = adapter;
        this.sender = sender;
    }
}



