package com.models;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FriendRelationshipId implements Serializable {
    @Column(name = "Unique_ID")
    private String uniqueId;

    @Column(name = "Friend_Unique_ID")
    private String friendUniqueId;

    // Constructors, equals, and hashCode methods
    public FriendRelationshipId() {}

    public FriendRelationshipId(String uniqueId, String friendUniqueId) {
        this.uniqueId = uniqueId;
        this.friendUniqueId = friendUniqueId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getFriendUniqueId() {
        return friendUniqueId;
    }

    public void setFriendUniqueId(String friendUniqueId) {
        this.friendUniqueId = friendUniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRelationshipId that = (FriendRelationshipId) o;
        return uniqueId.equals(that.uniqueId) && friendUniqueId.equals(that.friendUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, friendUniqueId);
    }
}
