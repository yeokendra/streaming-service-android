package com.nontivi.nonton.realm;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MyMigration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        // Migrate from version 0 to version 1
//        if (oldVersion == 0) {
//            RealmObjectSchema userSchema = schema.get("User");
//
//            userSchema.addField("testRealm", String.class);
//            oldVersion++;
//        }
//
//        if (oldVersion == 1) {
//        }
    }

    @Override
    public int hashCode() { return MyMigration.class.hashCode(); }

    @Override
    public boolean equals(Object object) { return object != null && object instanceof MyMigration; }
}
