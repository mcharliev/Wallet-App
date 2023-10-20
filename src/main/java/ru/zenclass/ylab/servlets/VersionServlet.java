package ru.zenclass.ylab.servlets;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



/*
Этот сервлет сконфигурен через webapp/WEB-INF/web.xml
 */
public class VersionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("req = " + req + ", resp = " + resp);
    }
}
