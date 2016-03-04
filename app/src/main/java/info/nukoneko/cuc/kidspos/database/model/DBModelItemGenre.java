package info.nukoneko.cuc.kidspos.database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by atsumi on 2016/03/05.
 */
public class DBModelItemGenre extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String name;
    private String store;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStore() {
        return store;
    }
}
