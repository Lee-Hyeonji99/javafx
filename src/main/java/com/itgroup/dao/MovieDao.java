package com.itgroup.dao;

import com.itgroup.bean.Movie;
import com.itgroup.utility.Paging;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDao {

    private String url = "jdbc:oracle:thin:@localhost:1521:xe"; // 데이터베이스 url
    private String username = "hyeonji"; // 사용자 아이디
    private String password = "oracle"; // 사용자 비밀번호

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    protected Connection getConnection() {
        Connection conn = null;

        try {
            // 데이터베이스 연결(Connect) 객체 생성
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    private Movie makeBean(ResultSet rs) throws SQLException {
        Movie movie = new Movie();

        movie.setId(rs.getInt("id"));
        movie.setName(rs.getString("name"));
        movie.setNation(rs.getString("nation"));
        movie.setViewingDate(String.valueOf(rs.getDate("viewing_date")));
        movie.setComments(rs.getString("comments"));
        movie.setRating(rs.getInt("rating"));

        return movie;
    }

    // selectByPK
    public Movie selectById(int inputId) {
        Movie movie = null;

        String sql = "SELECT * FROM movie WHERE id = ? ";

        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, inputId);

            rs = pstmt.executeQuery();
            if(rs.next()) {
                movie = makeBean(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return movie;
    }

    public boolean insertData(Movie movie) {
        boolean success = false;

        String sql = "INSERT INTO movie(id, name, nation, " +
                "viewing_date, comments, rating)";

        sql += "VALUES(seq_movie.NEXTVAL, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, movie.getName());
            pstmt.setString(2, movie.getNation());
            pstmt.setString(3, movie.getViewingDate());
            pstmt.setString(4, movie.getComments());
            pstmt.setInt(5, movie.getRating());

            success = pstmt.executeUpdate() > 0;

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();

            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        return success;
    }

    public boolean updateData(Movie movie) {
        boolean success = false;

        String sql = "UPDATE movie " +

                "SET name = ?, nation = ?, " +
                "viewing_date = ?, comments = ?, rating = ? " +

                "WHERE id = ?";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, movie.getName());
            pstmt.setString(2, movie.getNation());
            pstmt.setString(3, movie.getViewingDate());
            pstmt.setString(4, movie.getComments());
            pstmt.setInt(5, movie.getRating());
            pstmt.setInt(6, movie.getId());

            success = pstmt.executeUpdate() > 0;

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();

            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        return success;
    }

    public boolean deleteData(int id) {
        boolean success = false;

        String sql = "DELETE FROM movie ";
        sql += "WHERE id = ?";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            success = pstmt.executeUpdate() > 0;

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();

            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        return success;
    }

    public int getTotalCount(String nation) {
        int count = 0;

        boolean selectAll = nation == null || nation.isEmpty() || nation.equals("all");
        String sql = "SELECT COUNT(*) AS count FROM movie";

        if(!selectAll) {
            sql += " WHERE nation = ?";
        }

        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(sql);

            if(!selectAll) {
                pstmt.setString(1, nation);
            }

            rs = pstmt.executeQuery();
            if(rs.next()) {
                count = rs.getInt("count");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return count;
    }

    public List<Movie> getPaginationData(Paging pageInfo) {
        List<Movie> movieList = new ArrayList<>();

        boolean selectAll = pageInfo.getMode() == null || pageInfo.getMode().isEmpty() || pageInfo.getMode().equals("all") ||
                pageInfo.getMode().isBlank();

        String sql = "SELECT id, name, nation, viewing_date, comments, rating " +
                "FROM (" +
                "    SELECT id, name, nation, viewing_date, comments, rating, " +
                "    RANK() OVER(ORDER BY id DESC) AS ranking " +
                "    FROM movie ";

        if(!selectAll) {
            sql += " WHERE nation = ?";
        }

        sql +=  ")" +
                "WHERE ranking BETWEEN ? AND ?";

        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(sql);

            if(!selectAll) {
                pstmt.setString(1, pageInfo.getMode());
                pstmt.setInt(2, pageInfo.getBeginRow());
                pstmt.setInt(3, pageInfo.getEndRow());
            } else {
                pstmt.setInt(1, pageInfo.getBeginRow());
                pstmt.setInt(2, pageInfo.getEndRow());
            }

            rs = pstmt.executeQuery();
            while(rs.next()) {
                movieList.add(makeBean(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return movieList;
    }

    /*public List<String> selectDistinctNation() {
        List<String> categoryList = new ArrayList<>();

        String sql = "SELECT DISTINCT category FROM products";

        try {
            connect();

            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();
            while(rs.next()) {
                categoryList.add(rs.getString("category"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    disconnect();
                }
                if (pstmt != null) {
                    disconnect();
                }
                if (conn != null) {
                    disconnect();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return categoryList;
    }
    public List<Movie> selectAll() {
        List<Movie> allData = new ArrayList<>();
        String sql = "SELECT * FROM movie ORDER BY id DESC";

        try {
            conn = getConnection();

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = makeBean(rs);
                allData.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    disconnect();
                }
                if (pstmt != null) {
                    disconnect();
                }
                if (conn != null) {
                    disconnect();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return allData;
    }
    public List<Movie> selectByNation(String nation) {
        List<Product> allData = new ArrayList<>();

        if(inputCategory.equals("all") || inputCategory.isEmpty()) {
            return selectAll();
        }

        // String sql = "SELECT * FROM products WHERE category = '" + inputCategory + "' ORDER BY pno DESC";
        String sql = "SELECT * FROM products WHERE category = ? ORDER BY pno DESC";

        try {
            connect();

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, inputCategory);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = makeBean(rs);
                allData.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    disconnect();
                }
                if (pstmt != null) {
                    disconnect();
                }
                if (conn != null) {
                    disconnect();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return allData;
    }*/
}
