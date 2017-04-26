package org.gearvrf.planeterium;

import org.gearvrf.GVRActivity;

import android.os.Bundle;

public class PlaneteriumActivity extends GVRActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMain(new PlaneteriumMain(this));
    }
}
