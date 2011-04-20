package org.nds.dbdroid.entity;

import org.nds.dbdroid.annotation.Entity;
import org.nds.dbdroid.annotation.Id;

@Entity
public class Entity2 {

    @Id
    private Integer _id;

    private Long time;

    private Entity3 entity3;

    // Default Constructor
    public Entity2() {
    }

    public Entity2(Long time) {
        this.time = time;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer get_id() {
        return _id;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setEntity3(Entity3 entity3) {
        this.entity3 = entity3;
    }

    public Entity3 getEntity3() {
        return entity3;
    }
}
