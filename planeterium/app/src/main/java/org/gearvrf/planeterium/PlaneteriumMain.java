package org.gearvrf.planeterium;

import android.util.Log;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.IPickEvents;
import org.gearvrf.scene_objects.GVRSphereSceneObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class PlaneteriumMain extends GVRMain {

    private GVRActivity mActivity;
    private static final String TAG = "Planetarium";

    public PlaneteriumMain(GVRActivity activity) {
        mActivity = activity;
    }
    public GVRScene mainScene = null;
    public List<Planet> planets = new ArrayList<>();
    private boolean planeNameDisplayed = false;
    private boolean skyBoxDisplayed = false;

    private IPickEvents mPickHandler = new PickHandler();
    private GVRPicker mPicker;

    // For SkyBox
    private Planet currentPlanet = null;
    public GVRSceneObject PickedObject = null;
    private int currentSkyBoxIndex = 0;

    public class PickHandler implements IPickEvents
    {

        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo) {
            showPlanetName(sceneObj);
            PickedObject = sceneObj;
        }
        public void onExit(GVRSceneObject sceneObj) {
            removePlanetName(sceneObj);
        }
        public void onInside(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo) { }
        public void onNoPick(GVRPicker picker)
        {
            PickedObject = null;
        }
        public void onPick(GVRPicker picker)
        {
        }
    }

    void showPlanetName(GVRSceneObject sceneObject){
        if(!planeNameDisplayed){
            planeNameDisplayed = true;
            Planet planetObject = null;

            for (Planet p:
                 planets) {
                if(sceneObject.getName().equals(p.getPlanetName())){
                    planetObject = p;
                    break;
                }
            }

            planetObject.showName();
        }
    }

    void removePlanetName(GVRSceneObject sceneObject){
        if(planeNameDisplayed){
            planeNameDisplayed = false;

            Planet planetObject = null;

            for (Planet p:
                    planets) {
                if(sceneObject.getName().equals(p.getPlanetName())){
                    planetObject = p;
                    break;
                }
            }

            planetObject.removeName();
        }
    }

    @Override
    public void onInit(GVRContext gvrContext) {
        mainScene = gvrContext.getMainScene();
        mainScene.getEventReceiver().addListener(mPickHandler);
        mPicker = new GVRPicker(gvrContext, mainScene);

        addSpace();
        addHeadTracker(mainScene);
        createPlanets();
        startRotation();
        showPlanets(mainScene);
    }

    void addSpace(){
        GVRSphereSceneObject sphereObject = null;
        // load texture
        Future<GVRTexture> texture = null;
        try {
            texture = getGVRContext().loadFutureTexture(new GVRAndroidResource(getGVRContext(), "gvrf_space.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sphereObject = new GVRSphereSceneObject(getGVRContext(), false, texture);
        sphereObject.getTransform().setScale(150, 150, 150);
        mainScene.addSceneObject(sphereObject);
    }
    void handleTapActivity(){
        if(skyBoxDisplayed){
            mainScene.removeAllSceneObjects();
            currentPlanet = null;
            addSpace();
            addHeadTracker(mainScene);
            showPlanets(mainScene);
            skyBoxDisplayed = false;
        }else if(PickedObject != null){
            for (Planet p:
                    planets) {
                if(PickedObject.getName().equals(p.getPlanetName()) && p.getCountOfSkyboxes() != 0){
                    currentPlanet = p;
                    currentSkyBoxIndex = -1;
                    showSkyBox();
                    skyBoxDisplayed = true;
                    break;
                }
            }


        }
    }

    void handleSwipeActivity(){
        if(currentPlanet != null)
            showSkyBox();
    }

    void showSkyBox(){

        mainScene.removeAllSceneObjects();

        currentSkyBoxIndex++;
        currentSkyBoxIndex %= currentPlanet.getCountOfSkyboxes();

        GVRSphereSceneObject sphereObject = null;
        // load texture
        Future<GVRTexture> texture = null;
        try {
            texture = getGVRContext().loadFutureTexture(new GVRAndroidResource(getGVRContext(), currentPlanet.getSkyBoxPath() + "/" + currentPlanet.getSkyBoxWithIndex(currentSkyBoxIndex)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // create a sphere scene object with the specified texture and triangles facing inward (the 'false' argument)
        sphereObject = new GVRSphereSceneObject(getGVRContext(), false, texture);
        sphereObject.getTransform().setScale(100, 100, 100);

        mainScene.addSceneObject(sphereObject);
    }

    void createPlanets(){
        // Earth;
        Planet earth = new Planet("Earth", "sphere_earthmap.obj", 560f, getGVRContext(), R.drawable.earthmap1k);
        earth.setPosition(0, 0, -10);
        earth.setSkyBoxPath("Earth");
        earth.loadSkyBoxNames(mActivity);
        planets.add(earth);

        // Mercury;
        Planet mercury = new Planet("Mercury", "sphere_mercury.obj", 160f, getGVRContext(), R.drawable.mercurymap);
        mercury.setPosition(5, 0, -10);
        mercury.setSkyBoxPath("Mercury");
        mercury.loadSkyBoxNames(mActivity);
        planets.add(mercury);

        // Venus;
        Planet venus = new Planet("Venus", "sphere_venus.obj", 260f, getGVRContext(), R.drawable.venusmap);
        venus.setPosition(10, 0, -10);
        venus.setSkyBoxPath("Venus");
        venus.loadSkyBoxNames(mActivity);
        planets.add(venus);

        // Mars;
        Planet mars = new Planet("Mars", "sphere_mars.obj", 360f, getGVRContext(), R.drawable.mars_1k_color);
        mars.setPosition(15, 0, -10);
        mars.setSkyBoxPath("Mars");
        mars.loadSkyBoxNames(mActivity);
        planets.add(mars);
    }

    void showPlanets(GVRScene mainScene){
        for (Planet p:
                planets) {
            mainScene.addSceneObject(p.getPlanetScene());
        }
    }

    void startRotation(){
        for (Planet p:
                planets) {
            p.startRotation(getGVRContext().getAnimationEngine());
        }
    }

    void addHeadTracker(GVRScene mainScene){
        GVRSceneObject headTracker = new GVRSceneObject(getGVRContext(),
                getGVRContext().createQuad(0.1f, 0.1f),
                getGVRContext().getAssetLoader().loadTexture(new GVRAndroidResource(getGVRContext(), R.drawable.headtrackingpointer)));
        headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);
        headTracker.getRenderData().setDepthTest(false);
        headTracker.getRenderData().setRenderingOrder(100000);
        mainScene.getMainCameraRig().addChildObject(headTracker);
    }
}
