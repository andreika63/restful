package kay.restful;

import kay.restful.domain.AppGF;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class DataServiceImpl implements DataService {
    private final String DRIVER = "oracle.jdbc.OracleDriver";
    @Value("${oracle.server}")
    private String server;
    @Value("${oracle.user}")
    private String user;
    @Value("${oracle.password}")
    private String password;
    @Value("${oracle.port}")
    private String port;
    @Value("${oracle.sid}")
    private String sid;
    private String con_string;
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public DataServiceImpl() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @PostConstruct
    private void init() {
        con_string = "jdbc:oracle:thin:" + user + "/" + password + "@" + server + ":" + port + ":" + sid;
    }

    @Override
    public List<AppGF> getAppGF() {
        List<AppGF> result = null;
        try {
            con = DriverManager.getConnection(con_string);
            ps = con.prepareStatement("select ZONE,HOST,RELEASE,PRODUCTGROUP,PRODUCTNAME,PRODUCTVERSION,APPLICATIONNAME,APPLICATIONVERSION from CMDB.V_APPGF");
            rs = ps.executeQuery();
            result = new ArrayList<>();
            while (rs.next()) {
                AppGF row = new AppGF();
                row.setZone(rs.getString(1));
                row.setHost(rs.getString(2));
                row.setRelease(rs.getString(3));
                row.setProductgroup(rs.getString(4));
                row.setProductname(rs.getString(5));
                row.setProductversion(rs.getString(6));
                row.setApplicationname(rs.getString(7));
                row.setApplicationversion(rs.getString(8));
                result.add(row);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            releaseResourses(rs, ps, con);
            return result;
        }

    }

    @Override
    public List<Object> getTable(String table) {
        String sql = "select * from " + table.replaceAll("[ ;]", "");
        List<Object> result = null;
        try {
            con = DriverManager.getConnection(con_string);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            result = new ArrayList<>();
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                result.add(row);
            }
            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return Arrays.asList(e.getMessage());
        } finally {
            releaseResourses(rs, ps, con);
        }
    }

    @Override
    public String deleteAllAppGF() {
        String sql = "delete from CMDB.APPGF";
        try {
            con = DriverManager.getConnection(con_string);
            con.setAutoCommit(true);
            ps = con.prepareStatement(sql);
            int rows = ps.executeUpdate();
            return "Deleted "+String.valueOf(rows)+" rows";
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        } finally {
            releaseResourses(rs, ps, con);
        }
    }

    @Override
    public String insertAppGF(List<AppGF> appGFList) {
        String sql = "insert into CMDB.APPGF(ZONE,HOST,RELEASE,PRODUCTGROUP,PRODUCTNAME,PRODUCTVERSION,APPLICATIONNAME,APPLICATIONVERSION) values(?,?,?,?,?,?,?,?)";
        try {
            con = DriverManager.getConnection(con_string);
            con.setAutoCommit(false);
            ps = con.prepareStatement(sql);
            for(AppGF item: appGFList) {
                ps.setString(1, item.getZone());
                ps.setString(2, item.getHost());
                ps.setString(3, item.getRelease());
                ps.setString(4, item.getProductgroup());
                ps.setString(5, item.getProductname());
                ps.setString(6, item.getProductversion());
                ps.setString(7, item.getApplicationname());
                ps.setString(8, item.getApplicationversion());
                ps.addBatch();
            }
            int[] arr = ps.executeBatch();
            int rows = IntStream.of(arr).sum();
            con.commit();
            return "Interted "+String.valueOf(rows)+" rows";
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        } finally {
            releaseResourses(rs, ps, con);
        }
    }

    private void releaseResourses(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        closeResultSet(resultSet);
        closePreparedStatement(preparedStatement);
        closeConnection(connection);
    }

    private void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
        } catch (SQLException e) {
        }
    }

    private void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
        }
    }
}
