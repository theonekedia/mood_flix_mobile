/**
 * 
 * @author Arshad Parwez
 * 
 * @Company Freelancing
 *
 */

package hobbyst.ola.foodlove.libs.collectionpicker;

public class CollectionItem {

    public String id;
    public String text;
    public boolean isSelected;

    public CollectionItem(String id, String text) {
        this(id, text, false);
        this.id = id;
        this.text = text;
    }

    public CollectionItem(String id, String text, boolean isSelected) {
        this.id = id;
        this.text = text;
        this.isSelected = isSelected;
    }
}