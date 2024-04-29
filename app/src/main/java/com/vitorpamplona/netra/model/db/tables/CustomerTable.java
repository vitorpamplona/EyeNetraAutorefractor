/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact me at vitor@vitorpamplona.com.
 * For AGPL licensing, see below.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This application has not been clinically tested, approved by or registered in any health agency.
 * Even though this repository grants licenses to use to any person that follow it's license,
 * any clinical or commercial use must additionally follow the laws and regulations of the
 * pertinent jurisdictions. Having a license to use the source code does not imply on having
 * regulatory approvals to use or market any part of this code.
 */
package com.vitorpamplona.netra.model.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vitorpamplona.netra.model.db.Column;
import com.vitorpamplona.netra.model.db.DataUtil;
import com.vitorpamplona.netra.model.db.SQLiteHelper;
import com.vitorpamplona.netra.model.db.objects.Customer;

import java.util.Date;

public class CustomerTable extends Table {

    protected String firstName;
    protected String lastName;
    protected Date dob;

    public static final String TABLE_NAME = "customers";

    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String INSIGHT_USER_NAME = "insight_user_name";
    public static final String DOB = "dob";

    public CustomerTable(SQLiteHelper helper) {
        super(helper, new Column[]{

                new Column(ID, Column.ColumnType.INTEGER, true, true, true, null),
                new Column(SYNC_ID, Column.ColumnType.TEXT),

                new Column(CREATED, Column.ColumnType.TIMESTAMP),
                new Column(UPDATED, Column.ColumnType.TIMESTAMP),
                new Column(SYNCED, Column.ColumnType.TIMESTAMP),
                new Column(INSIGHT_USER_NAME, Column.ColumnType.TEXT),

                new Column(TO_SYNC_DEBUG, Column.ColumnType.BOOLEAN),
                new Column(TO_SYNC_INSIGHT, Column.ColumnType.BOOLEAN),
                new Column(CAN_DELETE, Column.ColumnType.BOOLEAN),

                new Column(FIRST_NAME, Column.ColumnType.TEXT),
                new Column(LAST_NAME, Column.ColumnType.TEXT),
                new Column(DOB, Column.ColumnType.DATE)
        });
    }

    public Cursor findAll(String username) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = INSIGHT_USER_NAME + "=?";
        String[] selectionArgs = new String[]{username};

        Cursor c = db.query(getName(), new String[]{getIdName(), UPDATED}, selection, selectionArgs, null, null, getIdName() + " DESC");

        return c;
    }

    @Override
    public String getName() {
        return CustomerTable.TABLE_NAME;
    }

    public void save(Customer m) {
        ContentValues cv = new ContentValues();

        //cv.put(ID, m.getId());

        cv.put(SYNC_ID, DataUtil.uuidToString(m.getSyncId()));

        //cv.put(CREATED, DataUtil.dateToTimestampString(m.getCreated()));
        //cv.put(UPDATED, DataUtil.dateToTimestampString(m.getUpdated()));
        //cv.put(SYNCED, DataUtil.dateToTimestampString(m.getSynced()));

        //cv.put(TO_SYNC_DEBUG, m.getToSync());
        //cv.put(TO_SYNC_INSIGHT, m.getToSync());
        cv.put(INSIGHT_USER_NAME, m.getUsername());
        cv.put(FIRST_NAME, m.getFirstName());
        cv.put(LAST_NAME, m.getLastName());
        cv.put(DOB, DataUtil.dateToTimestampString(m.getDob()));

        save(m, cv);
    }
}
