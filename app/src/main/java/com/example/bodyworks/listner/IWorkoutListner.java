package com.example.bodyworks.listner;

import com.example.bodyworks.Modals.WorkOut;

import java.util.List;

public interface IWorkoutListner {

    void onLoadSuccess(List<WorkOut> cart,String Uid);
    void onLoadFail(String message);
}
