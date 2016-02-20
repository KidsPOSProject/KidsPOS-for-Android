package info.nukoneko.cuc.kidspos.util;

import java.sql.SQLException;

import info.nukoneko.kidspos4j.model.QueryCallback;
import info.nukoneko.kidspos4j.model.TableKind;
import info.nukoneko.kidspos4j.util.sqlite.ISql;

/**
 * Created by atsumi on 2016/02/20.
 */
public class SQLiteAdapter implements ISql {
    @Override
    public boolean ExecuteQuery(TableKind tableKind, String s, QueryCallback queryCallback) throws SQLException {
        return false;
    }

    @Override
    public boolean Execute(TableKind tableKind, String s) throws SQLException {
        return false;
    }

    @Override
    public boolean truncate(TableKind tableKind) {
        return false;
    }
}
