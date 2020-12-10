package net.redstonecraft.redstoneapi.sql.obj;

import net.redstonecraft.redstoneapi.sql.MySQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLManager extends MySQL {

    public MySQLManager(String host, int port, String database, String username, String password) throws SQLException {
        super(host, port, database, username, password);
    }

    public void addTable(Base table) {
        try {
            PreparedStatement statement = prepareStatement("CREATE TABLE IF NOT EXISTS ? (id BIGINT(8) unsigned not null," + repeatString("?,", table.columns.length) + "PRIMARY KEY (id))");
            statement.setString(1, table.tablename);
            int c = 2;
            for (Column i : table.columns) {
                statement.setString(c, i.name);
                c++;
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String repeatString(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

}
