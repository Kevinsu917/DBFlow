package com.raizlabs.android.dbflow.sql.language;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.Query;
import com.raizlabs.android.dbflow.sql.builder.ValueQueryBuilder;
import com.raizlabs.android.dbflow.structure.Model;

/**
 * Description: The SQLite INSERT command
 */
public class Insert<ModelClass extends Model> implements Query {

    private Class<ModelClass> mTable;

    private String[] mColumns;

    private Object[] mValues;

    private ConflictAction mConflictAction;

    /**
     * Constructs a new INSERT command
     *
     * @param table The table to insert into
     */
    public Insert(Class<ModelClass> table) {
        mTable = table;
    }

    /**
     * The optional columns to specify. If specified, the values length must correspond to these columns, and
     * each column has a 1-1 relationship to the values.
     *
     * @param columns The columns to use
     * @return This INSERT statement
     */
    public Insert<ModelClass> columns(String... columns) {
        mColumns = columns;
        return this;
    }

    /**
     * The required values to specify. It must be non-empty and match the length of the columns when
     * a set of columns are specified.
     *
     * @param values The non type-converted values
     * @return
     */
    public Insert<ModelClass> values(Object... values) {
        mValues = values;
        return this;
    }

    /**
     * Specifies the optional OR method to use for this insert query
     *
     * @param action The conflict action to use
     * @return
     */
    public Insert<ModelClass> or(ConflictAction action) {
        mConflictAction = action;
        return this;
    }

    /**
     * Specifies OR REPLACE, which will either insert if row does not exist, or replace the value if it does.
     *
     * @return
     */
    public Insert<ModelClass> orReplace() {
        return or(ConflictAction.REPLACE);
    }

    /**
     * Specifies OR ROLLBACK, which will cancel the current transaction or ABORT the current statement.
     *
     * @return
     */
    public Insert<ModelClass> orRollback() {
        return or(ConflictAction.ROLLBACK);
    }

    /**
     * Specifies OR ABORT, which will cancel the current INSERT, but all other operations will be preserved in
     * the current transaction.
     *
     * @return
     */
    public Insert<ModelClass> orAbort() {
        return or(ConflictAction.ABORT);
    }

    /**
     * Specifies OR FAIL, which does not back out of the previous statements. Anything else in the current
     * transaction will fail.
     *
     * @return
     */
    public Insert<ModelClass> orFail() {
        return or(ConflictAction.FAIL);
    }

    /**
     * Specifies OR IGNORE, which ignores any kind of error and proceeds as normal.
     *
     * @return
     */
    public Insert<ModelClass> orIgnore() {
        return or(ConflictAction.IGNORE);
    }

    @Override
    public String getQuery() {
        ValueQueryBuilder queryBuilder = new ValueQueryBuilder("INSERT ");
        if (mConflictAction != null) {
            queryBuilder.append("OR ").append(mConflictAction);
        }
        queryBuilder.appendSpaceSeparated("INTO")
                .appendTableName(mTable);

        if (mColumns != null) {
            queryBuilder.append("(")
                    .appendArray(mColumns)
                    .append(")");
        }

        if (mColumns != null && mValues != null && mColumns.length != mValues.length) {
            throw new IllegalStateException("The Insert of " + FlowManager.getTableName(mTable) + " when specifying" +
                    "columns needs to have the same amount of values and columns");
        } else if (mValues == null) {
            throw new IllegalStateException("The insert of " + FlowManager.getTableName(mTable) + " should have" +
                    "at least one value specified for the insert");
        }

        queryBuilder.append(" VALUES(").appendModelArray(mValues).append(")");

        return queryBuilder.getQuery();
    }
}
