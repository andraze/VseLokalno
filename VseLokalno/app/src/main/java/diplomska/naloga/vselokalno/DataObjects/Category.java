package diplomska.naloga.vselokalno.DataObjects;

import java.util.ArrayList;

public class Category {
    String category_id;
    String category_name;
    String farm_id;

    public Category() {
        this.category_id = "";
        this.category_name = "";
        this.farm_id = "";
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getFarm_id() {
        return farm_id;
    }

    public void setFarm_id(String farm_id) {
        this.farm_id = farm_id;
    }
}
