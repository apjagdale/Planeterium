package org.gearvrf.planeterium;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRComponent;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMaterialShaderManager;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderTemplate;
import org.gearvrf.GVRSphereCollider;
import org.gearvrf.GVRTexture;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRRotationByAxisAnimation;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.jagdale on 4/24/2017.
 */



public class Planet {
    GVRSceneObject planetScene;
    GVRTextViewSceneObject textObject;
    private float rotationSpeed;
    String planetName;
    String skyBoxPath;
    String[] skyboxes = null;
    private final int IDLE_STATE = 0;
    private final int HOVER_STATE = 1;

    public int getCountOfSkyboxes(){
        return skyboxes.length;
    }

    public String getSkyBoxWithIndex(int i){
        return skyboxes[i];
    }

    public Planet(String planetName, String planetObj, float rotationSpeed, GVRContext gvrContext, int idleImageRes){

        planetScene = new GVRSceneObject(gvrContext);

        GVRTexture idle = gvrContext.getAssetLoader().loadTexture(new GVRAndroidResource(gvrContext, idleImageRes));

        planetScene.attachRenderData(new GVRRenderData(gvrContext));
        planetScene.getRenderData().setMaterial(new GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.BeingGenerated.ID));
        try {
            planetScene.getRenderData().setMesh(gvrContext.loadMesh(new GVRAndroidResource(gvrContext, planetObj)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        planetScene.getRenderData().getMaterial().setTexture(AlphaShader.STATE1_TEXTURE, idle);
        planetScene.getRenderData().getMaterial().setFloat(AlphaShader.TEXTURE_SWITCH, IDLE_STATE);
        GVRMaterialShaderManager shaderManager = gvrContext.getMaterialShaderManager();
        GVRShaderTemplate shaderTemplate = shaderManager.retrieveShaderTemplate(AlphaShader.class);
        shaderTemplate.bindShader(gvrContext, planetScene.getRenderData(), gvrContext.getMainScene());

        planetScene.setName(planetName);
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
        planetScene.getRenderData().getMaterial().setFloat(AlphaShader.TEXTURE_SWITCH, HOVER_STATE);
        planetScene.addChildObject(textObject);
        textObject.getTransform().setPosition(0, 2, 0);
    }

    public void removeName(){
        planetScene.getRenderData().getMaterial().setFloat(AlphaShader.TEXTURE_SWITCH, IDLE_STATE);
        planetScene.removeChildObject(textObject);
    }

    public GVRSceneObject getPlanetScene(){
        return planetScene;
    }

    public String getPlanetName(){
        return planetName;
    }

    public void startRotation(GVRAnimationEngine animationEngine){
        GVRRotationByAxisAnimation animation = new GVRRotationByAxisAnimation( //
                                planetScene, rotationSpeed, -30.0f, //
                                0.0f, 1.0f, 0.0f);

        animation.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1);
        animation.start(animationEngine);
    }

    public float getRotationSpeed()
    {
        return this.rotationSpeed;
    }


    public void setPosition(float x, float y, float z){
        planetScene.getTransform().setPosition(x, y, z);
    }

    private void attachSphereCollider(GVRContext context) {
        planetScene.attachComponent(new GVRSphereCollider(context));
    }

    public void setSkyBoxPath(String path){
        skyBoxPath = path;
    }

    public String getSkyBoxPath(){
        return skyBoxPath;
    }

    public void loadSkyBoxNames(Activity activity){
        try {
            Resources resources = activity.getResources();
            AssetManager assetManager = resources.getAssets();
            skyboxes = assetManager.list(skyBoxPath);
            for (int i = 0; i < skyboxes.length; i++) {
                Log.d("Planeterium", skyboxes[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Planeterium", "Directory " + skyBoxPath + " not found");
        }

    }
}
