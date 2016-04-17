package io.somet.somet;

import android.app.Application;

import im.delight.android.ddp.MeteorSingleton;
import io.somet.somet.data.User;

public class Somet extends Application {
    private User targetedUser;
    private User currentUser;

    public User getTargetedUser() {
        return targetedUser;
    }

    public void setTargetedUser(User targetedUser) {
        this.targetedUser = targetedUser;
    }

    public void setTargetedUser(String usr) {
        this.targetedUser = new User(MeteorSingleton.getInstance().getDatabase().getCollection("users").whereEqual("username", usr).findOne());
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}