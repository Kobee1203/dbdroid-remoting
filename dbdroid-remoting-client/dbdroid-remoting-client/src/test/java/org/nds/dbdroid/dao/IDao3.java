package org.nds.dbdroid.dao;

import org.nds.dbdroid.entity.Entity3;

public interface IDao3 extends IAndroidDAO<Entity3, Integer> {

    Entity3 getByName(String name);
}
