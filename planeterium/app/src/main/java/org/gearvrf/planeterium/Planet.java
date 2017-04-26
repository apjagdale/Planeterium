package org.gearvrf.planeterium;

import android.graphics.Color;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRRotationByAxisAnimation;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;

import java.io.IOException;

/**
 * Created by a.jagdale on 4/24/2017.
 */

public class Planet {
    GVRSceneObject planetScene;
    GVRTextViewSceneObject textObject;
    float rotationSpeed;
    String planetName;

    public Planet(String planetName, String planetObj, float rotationSpeed, GVRContext gvrContext){
        try {
            planetScene = gvrContext.getAssetLoader().loadModel(planetObj, gvrContext.getMainScene());
            planetScene.setName(planetName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.planetName = planetName;
        this.rotationSpeed = rotationSpeed;

        createTextObject(gvrContext);

        attachSphereCollider(gvrContext);
    }

    private void createTextObject(GVRContext gvrContext){
        textObject = new GVRTextViewSceneObject(gvrContext);
        textObject.setText(planetName);
        textObject.setTextColor(Color.GREEN);
        textObject.setTextSize(15);
    }

    public void showName(){
        planetScene.addChildObject(textObject);
        textObject.getTransform().setPosition(0, 2, 0);
    }

    public void removeName(){
        planetScene.removeChildObject(textObject);
    }

    public GVRSceneObject getPlanetScene(){
        return planetScene;
    }

    public String getPlanetName(){
        return planetName;
    }

    public void startRotation(GVRAnimationEngine animationEngine){
        GVRAnimation animation = new GVRRotationByAxisAnimation( //
                                planetScene, rotationSpeed, -30.0f, //
                                0.0f, 1.0f, 0.0f);

        animation.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1);
        animation.start(animationEngine);
    }

    public void setPosition(float x, float y, float z){
        planetScene.getTransform().setPosition(x, y, z);
    }

    private void attachSphereCollider(GVRContext context) {
        planetScene.attachComponent(new GVRSphereCollider(context));
    }
}
