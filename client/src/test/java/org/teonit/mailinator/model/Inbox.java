package org.teonit.mailinator.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 *
 * @author Andrii Iakovenko
 */
public class Inbox {

    @SerializedName("messages")
    private List<InboxItem> items;

    public List<InboxItem> getItems() {
        return items;
    }

    public void setItems(List<InboxItem> items) {
        this.items = items;
    }

}
