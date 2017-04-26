package org.gearvrf.planeterium;

import android.util.Log;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.IPickEvents;
import java.util.ArrayList;
import java.util.List;

public class PlaneteriumMain extends GVRMain {

    private GVRActivity mActivity;
    private static final String TAG = "Planetarium";

    public PlaneteriumMain(GVRActivity activity) {
        mActivity = activity;
    }
    public GVRScene mainScene = null;
    public List<Planet> planets = new ArrayList<>();
    private boolean planeNameDisplayed = false;

    private IPickEvents mPickHandler = new PickHandler();
    private GVRPicker mPicker;

    public class PickHandler implements IPickEvents
    {
        public GVRSceneObject   PickedObject = null;

        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo) {
            showPlanetName(sceneObj);
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

        addHeadTracker(mainScene);
        createPlanets();
        startRotation();
        showPlanets(mainScene);


    }

    void createPlanets(){
        // Earth;
        Planet earth = new Planet("Earth", "sphere_earthmap.obj", 560f, getGVRContext());
        earth.setPosition(0, 0, -10);
        planets.add(earth);

        // Mercury;
        Planet mercury = new Planet("Mercury", "sphere_mercury.obj", 160f, getGVRContext());
        mercury.setPosition(5, 0, -10);
        planets.add(mercury);

        // Venus;
        Planet venus = new Planet("Venus", "sphere_venus.obj", 260f, getGVRContext());
        venus.setPosition(10, 0, -10);
        planets.add(venus);

        // Mars;
        Planet mars = new Planet("Mars", "sphere_mars.obj", 360f, getGVRContext());
        mars.setPosition(15, 0, -10);
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
