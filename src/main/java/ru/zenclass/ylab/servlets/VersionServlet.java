package ru.zenclass.ylab.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zenclass.ylab.aop.annotations.Loggable;

@WebServlet(name = "VersionServlet", urlPatterns = {"/version"})
@Loggable
public class VersionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("req = " + req + ", resp = " + resp);
    }
}
