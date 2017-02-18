package info.nukoneko.cuc.android.kidspos.util;

import java.sql.SQLException;

import info.nukoneko.kidspos4j.model.QueryCallback;
import info.nukoneko.kidspos4j.model.TableKind;
import info.nukoneko.kidspos4j.util.sqlite.ISql;

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
