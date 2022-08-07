package com.example.study.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(urlPatterns = { "/api/v1/todos", "/api/v1/todos/*" })
public class ToDoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ObjectMapper mapper = new ObjectMapper();

    private ToDoItem readToDoItemFromBody(HttpServletRequest request) throws ServletException {
        try {
            return mapper.readValue(request.getInputStream(), ToDoItem.class);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    private void writeJsonResponse(HttpServletResponse response, Object body) throws ServletException {
        response.setHeader("Content-Type", "application/json");
        try {
            mapper.writeValue(response.getWriter(), body);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    private void errorResponse(HttpServletResponse response, int statusCode) throws ServletException {
        response.setStatus(statusCode);
        Map<String, Integer> body = new HashMap<>();
        body.put("code", statusCode);
        writeJsonResponse(response, body);
    }

    @Override
    public void init() throws ServletException {
        try {
            Database.initialize();
        } catch (IOException | SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        boolean findAll = false;
        int id = -1;
        String sql = "SELECT id, title, done FROM todo";

        String requestPath = request.getPathInfo();
        if (requestPath == null) {
            findAll = true;
        } else {
            try {
                id = Integer.parseInt(requestPath.substring(1));
            } catch (NumberFormatException e) {
                errorResponse(response, 404);
                return;
            }
            sql += " WHERE id = ?";
        }

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!findAll) {
                pstmt.setInt(1, id);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (findAll) {
                    List<ToDoItem> items = new ArrayList<>();
                    while (rs.next()) {
                        ToDoItem item = new ToDoItem(rs.getInt(1), rs.getString(2), rs.getBoolean(3));
                        items.add(item);
                    }
                    writeJsonResponse(response, items);
                } else {
                    if (!rs.next()) {
                        errorResponse(response, 404);
                        return;
                    }
                    ToDoItem item = new ToDoItem(rs.getInt(1), rs.getString(2), rs.getBoolean(3));
                    writeJsonResponse(response, item);
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (request.getPathInfo() != null) {
            errorResponse(response, 405);
            return;
        }

        ToDoItem toDoItem = readToDoItemFromBody(request);

        String sql = "insert into todo (title, done) values (?, ?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, toDoItem.getTitle());
            pstmt.setBoolean(2, toDoItem.isDone());
            int num = pstmt.executeUpdate();
            if (num == 0) {
                errorResponse(response, 404);
                return;
            }
            ResultSet rs = pstmt.getGeneratedKeys();
            int generatedkey = 0;
            if (rs.next()) {
                generatedkey = rs.getInt(1);
            }
            toDoItem.setId(generatedkey);
            writeJsonResponse(response, toDoItem);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String requestPath = request.getPathInfo();
        if (requestPath == null) {
            errorResponse(response, 405);
            return;
        }
        int id = -1;
        try {
            id = Integer.parseInt(requestPath.substring(1));
        } catch (NumberFormatException e) {
            errorResponse(response, 404);
            return;
        }
        String sql = "update todo set id = ?, title = ?, done = ? where id = ?";
        ToDoItem toDoItem = readToDoItemFromBody(request);
        if (toDoItem.getId() != id) {
            errorResponse(response, 400);
            return;
        }
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, toDoItem.getId());
            pstmt.setString(2, toDoItem.getTitle());
            pstmt.setBoolean(3, toDoItem.isDone());
            pstmt.setInt(4, id);
            int num = pstmt.executeUpdate();
            if (num == 0) {
                errorResponse(response, 404);
                return;
            }
            writeJsonResponse(response, toDoItem);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String requestPath = request.getPathInfo();
        if (requestPath == null) {
            errorResponse(response, 405);
            return;
        }

        // FIXME not implemented yet.
    }

    private boolean checkExistence(Connection conn, int id) throws SQLException {
        String sql = "SELECT id FROM todo WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }
}
