package ru.yandex.clickhouse.jdbcbridge;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.yandex.clickhouse.jdbcbridge.servlet.ColumnsInfoServlet;
import ru.yandex.clickhouse.jdbcbridge.servlet.PingHandlerServlet;
import ru.yandex.clickhouse.jdbcbridge.servlet.QueryHandlerServlet;
import ru.yandex.clickhouse.jdbcbridge.servlet.QuoteStyleServlet;
import ru.yandex.clickhouse.settings.ClickHouseProperties;
import ru.yandex.clickhouse.util.ClickHouseRowBinaryStream;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;

/**
 * Created by krash on 21.09.18.
 */
public class JdbcBridge {

    public static void main(String... argv) throws Exception {

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(new QueryHandlerServlet()), "/");
        handler.addServletWithMapping(new ServletHolder(new PingHandlerServlet()), "/ping");
        handler.addServletWithMapping(new ServletHolder(new ColumnsInfoServlet()), "/columns_info");
        handler.addServletWithMapping(new ServletHolder(new QuoteStyleServlet()), "/quote_style");

        Server jettyServer = new Server(9019);
        jettyServer.setHandler(handler);
        jettyServer.setErrorHandler(new ErrorHandler() {
            @Override
            protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException {
                writer.write(message);
            }
        });

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }

}
