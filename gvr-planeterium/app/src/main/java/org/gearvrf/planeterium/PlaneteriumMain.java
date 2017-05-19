package org.gearvrf.planeterium;

import android.util.Log;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.IPickEvents;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.animation.GVRRotationByAxisAnimation;
import org.gearvrf.animation.GVRRotationByAxisWithPivotAnimation;
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
    private GVRSceneObject headTracker = null;

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
            if(mainScene.getMainCameraRig().getChildrenCount() > 1){
                mainScene.getMainCameraRig().removeAllChildren();
                addHeadTracker(mainScene);
            }
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

            planetObject.showName(mainScene);
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

            planetObject.removeName(mainScene);
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
        GVRSceneObject sphereObject;
        GVRMesh spaceMesh = getGVRContext().loadMesh(new GVRAndroidResource(
                getGVRContext(), R.raw.gvrf_space_mesh));

        GVRTexture spaceTexture = getGVRContext().getAssetLoader()
                .loadTexture(new GVRAndroidResource(getGVRContext(),
                        R.drawable.gvrf_space));
        sphereObject = new GVRSceneObject(getGVRContext(), spaceMesh,
                spaceTexture);
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

        // Mercury;
        Planet mercury = new Planet("Mercury", "sphere_mercury.obj", 140.3f, getGVRContext(), R.drawable.mercurymap);
        mercury.setPosition(-15, 0, -10);
        mercury.setDepth(-7.2f);
        mercury.setSkyBoxPath("Mercury");
        mercury.loadSkyBoxNames(mActivity);
        mercury.setInfo("Radius : 1516 mi \nGravity : 3.7 m/s\n Length Of Day : 58 ed\n Orbital Period : 88 ed");
        mercury.createTextObject(getGVRContext());
        planets.add(mercury);
        startRevolution(mercury, 88);

        // Venus;
        Planet venus = new Planet("Venus", "sphere_venus.obj", 280.0f, getGVRContext(), R.drawable.venusmap);
        venus.setPosition(-10, 0, -10);
        venus.setDepth(-13.5f);
        venus.setSkyBoxPath("Venus");
        venus.loadSkyBoxNames(mActivity);
        venus.setInfo("Radius : 3760 mi \nGravity : 8.87 m/s\n Length Of Day : 116 ed\n Orbital Period : 225 ed");
        venus.createTextObject(getGVRContext());
        planets.add(venus);
        startRevolution(venus, 225);

        // Earth;
        Planet earth = new Planet("Earth", "sphere_earthmap.obj", 24.0f, getGVRContext(), R.drawable.earthmap1k);
        earth.setPosition(-5, 0, -10);
        earth.setDepth(-18.5f);
        earth.setSkyBoxPath("Earth");
        earth.loadSkyBoxNames(mActivity);
        earth.setInfo("Radius : 3959 mi \nGravity : 9.80 m/s\n Length Of Day : 24 hours\n Orbital Period : 365 ed");
        earth.createTextObject(getGVRContext());

        planets.add(earth);
        startRevolution(earth, 365);

        // Mars;
        Planet mars = new Planet("Mars", "sphere_mars.obj", 24.0f, getGVRContext(), R.drawable.mars_1k_color);
        mars.setPosition(0, 0, -10);
        mars.setDepth(-25.3f);
        mars.setSkyBoxPath("Mars");
        mars.loadSkyBoxNames(mActivity);
        mars.setInfo("Radius : 2106 mi \nGravity : 3.71 m/s\n Length Of Day : 1 earth day\n Orbital Period : 686 ed");
        mars.createTextObject(getGVRContext());
        planets.add(mars);
        startRevolution(mars, 687);

        // Jupiter;
        Planet jupiter = new Planet("Jupiter", "sphere_mars.obj", 9.5f, getGVRContext(), R.drawable.jupiter);
        jupiter.setPosition(5, 0, -10);
        jupiter.setDepth(-30.3f);
        jupiter.setSkyBoxPath("Jupiter");
        jupiter.loadSkyBoxNames(mActivity);
        jupiter.setInfo("Radius : 43441 mi \nGravity : 24.79 m/s\n Length Of Day : 9 h 56 m\n Orbital Period : 4380 ed");
        jupiter.createTextObject(getGVRContext());
        planets.add(jupiter);
        startRevolution(jupiter, 1200);

        // Saturn;
        Planet saturn = new Planet("Saturn", "sphere_mars.obj", 10.4f, getGVRContext(), R.drawable.saturn_text);
        saturn.setPosition(10, 0, -10);
        saturn.setDepth(-35.4f);
        saturn.setSkyBoxPath("Saturn");
        saturn.loadSkyBoxNames(mActivity);
        saturn.setInfo("Radius : 36184 mi \nGravity : 10.42 m/s\n Length Of Day : 10 h 42 m\n Orbital Period : 29 years");
        saturn.createTextObject(getGVRContext());
        planets.add(saturn);
        startRevolution(saturn, 2000);

        // Uranus;
        Planet Uranus = new Planet("Uranus", "sphere_mars.obj", 17.1f, getGVRContext(), R.drawable.uranus);
        Uranus.setPosition(15, 0, -10);
        Uranus.setDepth(-42.5f);
        Uranus.setSkyBoxPath("Uranus");
        Uranus.loadSkyBoxNames(mActivity);
        Uranus.setInfo("Radius : 15759 mi \nGravity : 8.69 m/s\n Length Of Day : 17 h 14 m\n Orbital Period : 84 years");
        Uranus.createTextObject(getGVRContext());
        planets.add(Uranus);
        startRevolution(Uranus, 2700);

        // Neptune;
        Planet Neptune = new Planet("Neptune", "sphere_mars.obj", 16.6f, getGVRContext(), R.drawable.neptune);
        Neptune.setPosition(20, 0, -10);
        Neptune.setDepth(-46.3f);
        Neptune.setSkyBoxPath("Neptune");
        Neptune.loadSkyBoxNames(mActivity);
        Neptune.setInfo("Radius : 15299 mi \nGravity : 11.15 m/s\n Length Of Day : 16 h 6 m\n Orbital Period : 165 years");
        Neptune.createTextObject(getGVRContext());
        planets.add(Neptune);
        startRevolution(Neptune, 3000);


        // Pluto;
        Planet Pluto = new Planet("Pluto", "sphere_mars.obj", 153.3f, getGVRContext(), R.drawable.pluto);
        Pluto.setPosition(25, 0, -10);
        Pluto.setDepth(-48.0f);
        Pluto.setSkyBoxPath("Pluto");
        Pluto.loadSkyBoxNames(mActivity);
        Pluto.setInfo("Radius : 737.6 mi \nGravity : 0.62 m/s\n Length Of Day : 153 h 30 m\n Orbital Period : 248 ey");
        Pluto.createTextObject(getGVRContext());
        planets.add(Pluto);
        startRevolution(Pluto, 3300);
    }

    void showPlanets(GVRScene mainScene){
        float yCoordinate = 0;
        float degrees = 40.0f;
        float currentDegree = 1;
        for(int i = 0; i < planets.size(); i++){
            mainScene.addSceneObject(planets.get(i).getPlanetScene());
            planets.get(i).getPlanetScene().getTransform().setPosition(0, yCoordinate, planets.get(i).getDepth());
            planets.get(i).getPlanetScene().getTransform().rotateByAxisWithPivot(currentDegree, 0, 1, 0, 0, 0, 0);
            currentDegree += degrees;
        }
    }

    void startRotation(){
        for (Planet p:
                planets) {
            GVRRotationByAxisAnimation rotationObj = new GVRRotationByAxisAnimation(p.getPlanetScene(),p.getRotationSpeed(),360.0F,0.0F,1.0F,0.0F);
            rotationObj.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1);
            getGVRContext().getAnimationEngine().start(rotationObj);
        }
    }

    void startRevolution(Planet planet,float duration)
    {
        GVRRotationByAxisWithPivotAnimation revolutionOject = new GVRRotationByAxisWithPivotAnimation(planet.getPlanetScene(),duration,360.0f,0.0f,1.0f,0.0f, 0.0f,0.0f, 0.0f);
        revolutionOject.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1);
        getGVRContext().getAnimationEngine().start(revolutionOject);
    }

    void addHeadTracker(GVRScene mainScene){
        headTracker = new GVRSceneObject(getGVRContext(),
                getGVRContext().createQuad(0.1f, 0.1f),
                getGVRContext().getAssetLoader().loadTexture(new GVRAndroidResource(getGVRContext(), R.drawable.headtrackingpointer)));
        headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);
        headTracker.getRenderData().setDepthTest(false);
        headTracker.getRenderData().setRenderingOrder(100000);
        mainScene.getMainCameraRig().addChildObject(headTracker);
    }
}
