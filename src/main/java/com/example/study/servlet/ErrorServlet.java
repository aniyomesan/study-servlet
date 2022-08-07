package com.example.study.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/error")
public class ErrorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        response.setStatus(500);
        response.setHeader("Content-Type", "application/json");
        try {
            response.getWriter().write("{\"code\":500}");
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
}
