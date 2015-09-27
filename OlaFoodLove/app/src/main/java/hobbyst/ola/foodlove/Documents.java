package hobbyst.ola.foodlove;

/**
 * Created by Arshad Parwez on 25/08/15.
 */
public class Documents {

    String name;
    String id;
    String category_id;
    String default_price;

    public String getDefault_price() {
        return default_price;
    }

    public void setDefault_price(String default_price) {
        this.default_price = default_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}