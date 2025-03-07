package com.masterprojekat.music_online_classes.helpers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.masterprojekat.music_online_classes.models.User;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    public void setUser(User user) {
        loggedInUser.setValue(user);
    }

    public LiveData<User> getUser() {
        return loggedInUser;
    }
}

