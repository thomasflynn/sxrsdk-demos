package com.samsungxr.modelviewer2;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.samsungxr.SXRActivity;
import com.samsungxr.SXRCollider;
import com.samsungxr.SXRContext;
import com.samsungxr.SXRMaterial;
import com.samsungxr.SXRRenderData;
import com.samsungxr.SXRRenderPass;
import com.samsungxr.SXRScene;
import com.samsungxr.SXRNode;
import com.samsungxr.SXRShaderId;
import com.samsungxr.SXRTransform;
import com.samsungxr.animation.SXRAnimation;
import com.samsungxr.animation.SXRRepeatMode;
import com.samsungxr.animation.SXRRotationByAxisAnimation;
import com.samsungxr.nodes.SXRSphereNode;
import com.samsungxr.util.AssetsReader;
import com.samsungxr.util.Banner;
import com.samsungxr.util.NoTextureShader;
import com.samsungxr.util.OutlineShader;
import com.samsungxr.widgetplugin.SXRWidgetNode;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.util.ArrayList;

public class Controller {
    private static final String TAG = "SXRModelViewer2";

    // Variables related to SkyBox
    private ArrayList<SkyBox> aODefaultSkyBox;
    private ArrayList<SkyBox> aOSDSkyBox;
    private final String sSDSkyBoxDirectory = "SceneEditor/environments/";
    private final String sDefaultSkyBoxDirectory = "skybox";
    private SXRSphereNode currentSkyBox;

    // Variables related to Camera
    private ArrayList<CameraPosition> oDefaultCameraPosition;
    private CameraPosition oCurrentPosition;
    private boolean lookInsideFlag = false;
    private Vector3f lookInsidePosition = new Vector3f(0, 1.75f, 0);

    // Variables related to Model
    private final String sEnvironmentPath = Environment.getExternalStorageDirectory().getPath();
    private ArrayList<Model> aModel;
    private Model currentDisplayedModel;
    public boolean currentModelFlag = false;
    private SXRAnimation currentAnimation;

    // Variables related to Banner
    private Banner oBannerCount;
    private Banner oBannerLoading;

    // Variables related to Custom Shader
    private ArrayList<String> aSCustomShaderList;

    // Lights
    private Lights oLight;
    private boolean oLightFlag = false;

    private Vector3f defaultCenterPosition;

    private SXRActivity activity;
    private SXRContext context;

    public void setDefaultCenterPosition(Vector3f defaultPosition) {
        defaultCenterPosition = new Vector3f(defaultPosition);
    }

    public Controller(SXRActivity activity, SXRContext context) {
        this.activity = activity;
        this.context = context;
    }

    void initializeController() {
        loadDefaultSkyBoxList();
        loadSDSkyBoxList();

        loadModelsList();
        loadCameraPositionList();
        loadCustomShaderList();
        loadLights();
    }

    // START Lights
    private void loadLights() {
        oLight = new Lights();
        oLight.createLight(context);

        //Add white light source
        oLight.addAmbient(0.5f, 0.5f, 0.5f, 0.5f);
        oLight.addDiffuse(0.5f, 0.5f, 0.5f, 0.5f);
        oLight.addSpecular(0.5f, 0.5f, 0.5f, 0.5f);
        
        // Add Ambient
        oLight.addAmbient(0.3f, 0.0f, 0.0f, 0.5f);

        // Add Diffuse
        oLight.addDiffuse(0.3f, 0.0f, 0.0f, 0.5f);

        // Add Specular
        oLight.addSpecular(0.3f, 0.0f, 0.0f, 0.5f);

        // Add Ambient
        oLight.addAmbient(0.0f, 0.0f, 0.5f, 0.5f);

        // Add Diffuse
        oLight.addDiffuse(0.0f, 0.0f, 0.5f, 0.5f);

        // Add Specular
        oLight.addSpecular(0.0f, 0.0f, 0.5f, 0.5f);

        // Add Ambient
        oLight.addAmbient(0.0f, 0.3f, 0.0f, 0.5f);

        // Add Diffuse
        oLight.addDiffuse(0.0f, 0.3f, 0.0f, 0.5f);

        // Add Specular
        oLight.addSpecular(0.0f, 0.3f, 0.0f, 0.5f);
    }

    public ArrayList<String> getAmbient() {
        ArrayList<String> list = new ArrayList<String>();
        for (Vector4f grbaValue : oLight.getAmbient()) {
            String sPosition = "R:" + Float.toString(grbaValue.x) + " G:" + Float.toString
                    (grbaValue.y) + " B:" + Float.toString(grbaValue.z) + " A:" + Float.toString
                    (grbaValue.w);
            list.add(sPosition);
        }
        return list;
    }

    public ArrayList<String> getDiffuse() {
        ArrayList<String> list = new ArrayList<String>();
        for (Vector4f grbaValue : oLight.getDiffuse()) {
            String sPosition = "R:" + Float.toString(grbaValue.x) + " G:" + Float.toString
                    (grbaValue.y) + " B:" + Float.toString(grbaValue.z) + " A:" + Float.toString
                    (grbaValue.w);
            list.add(sPosition);
        }
        return list;
    }

    public ArrayList<String> getSpecular() {
        ArrayList<String> list = new ArrayList<String>();
        for (Vector4f grbaValue : oLight.getSpecular()) {
            String sPosition = "R:" + Float.toString(grbaValue.x) + " G:" + Float.toString
                    (grbaValue.y) + " B:" + Float.toString(grbaValue.z) + " A:" + Float.toString
                    (grbaValue.w);
            list.add(sPosition);
        }
        return list;
    }

    public void setAmbient(int index) {
        oLight.setAmbient(index);
    }

    public void setDiffuse(int index) {
        oLight.setDiffuse(index);
    }

    public void setSpecular(int index) {
        oLight.setSpecular(index);
    }

    public void addLight(SXRScene scene) {
        oLight.getLightScene().getTransform().setPosition(0, 10, 0);
        oLight.getLightScene().getTransform().rotateByAxis(-90, 1, 0, 0);
        scene.addNode(oLight.getLightScene());
    }

    public void enableDisableLightOnModel(SXRNode model, boolean flag) {
        ArrayList<SXRRenderData> rdata = model.getAllComponents(SXRRenderData.getComponentType());
        for (SXRRenderData r : rdata) {

            if (r != null) {
                if (flag) {
                    r.enableLight();
                } else {
                    r.disableLight();
                }
            }
        }
    }

    public void turnOnOffLight(boolean flag) {
        if (flag) {
            oLight.setSelected(0);
            oLightFlag = true;

            if (currentDisplayedModel != null) {
                enableDisableLightOnModel(currentDisplayedModel.getModel(context), true);
            }
        } else {
            oLightFlag = false;

            if (currentDisplayedModel != null) {
                enableDisableLightOnModel(currentDisplayedModel.getModel(context), false);
            }
        }
    }
    // END Lights

    // START Custom Shader Feature
    private void loadCustomShaderList() {
        aSCustomShaderList = new ArrayList<String>();
        aSCustomShaderList.add("Original");
        aSCustomShaderList.add("No Texture");
        aSCustomShaderList.add("Outline");
        aSCustomShaderList.add("Lines");
        aSCustomShaderList.add("Lines_Loop");
        aSCustomShaderList.add("Points");
    }

    public ArrayList<String> getListOfCustomShaders() {
        return aSCustomShaderList;
    }

    public void applyCustomShader(int index, SXRScene scene) {
        if (currentDisplayedModel == null)
            return;
        ArrayList<SXRRenderData> renderDatas = currentDisplayedModel.getModel(context)
                .getAllComponents(SXRRenderData.getComponentType());
        SXRMaterial outlineMaterial = new SXRMaterial(context, new SXRShaderId(OutlineShader.class));

        switch (index) {
            case 0:
                for (int i = 0; i < renderDatas.size(); i++) {
                    renderDatas.get(i).setMaterial(currentDisplayedModel.originalMaterial.get(i));
                    renderDatas.get(i).setCullFace(SXRRenderPass.SXRCullFaceEnum.Back);
                    renderDatas.get(i).setDrawMode(4);
                    enableDisableLightOnModel(currentDisplayedModel.getModel(context), false);
                }
                break;
            case 1:
                for (SXRRenderData rdata : renderDatas) {
                    SXRMaterial noMaterial = new SXRMaterial(context, new SXRShaderId(NoTextureShader.class));
                    rdata.setMaterial(noMaterial);
                    rdata.setDrawMode(4);
                }
                break;

            case 2:
                outlineMaterial.setVec4(OutlineShader.COLOR_KEY, 0.4f, 0.1725f, 0.1725f, 1.0f);
                outlineMaterial.setFloat(OutlineShader.THICKNESS_KEY, 2.0f);
                for (SXRRenderData rdata : renderDatas) {
                    rdata.setMaterial(outlineMaterial);
                    rdata.setCullFace(SXRRenderPass.SXRCullFaceEnum.Front);
                    rdata.setDrawMode(4);
                }
                break;
            case 3:
                for (SXRRenderData rdata : renderDatas) {
                    rdata.setDrawMode(1);
                }

                break;
            case 4:
                for (SXRRenderData rdata : renderDatas) {
                    rdata.setDrawMode(3);
                }

                break;
            case 5:
                for (SXRRenderData rdata : renderDatas) {
                    rdata.setDrawMode(0);
                }

                break;
        }
    }
    // END Custom Shader Feature

    // START Banner Feature
    void displayCountInRoom(SXRScene room) {
        if (oBannerCount == null) {
            oBannerCount = new Banner(context, "Total Models " + String.valueOf(aModel.size()),
                    10, Color.BLUE, defaultCenterPosition.x - 2, defaultCenterPosition.y + 5,
                    defaultCenterPosition.z);
        }
        room.addNode(oBannerCount.getBanner());
    }

    void displayLoadingInRoom(SXRScene room) {
        if (oBannerLoading == null) {
            oBannerLoading = new Banner(context, "Loading", 10, Color.BLUE, defaultCenterPosition
                    .x, defaultCenterPosition.y, defaultCenterPosition.z);
        }
        room.addNode(oBannerLoading.getBanner());
    }

    void removeLoadingInRoom(SXRScene room) {
        if (oBannerLoading == null) {
            return;
        }
        room.removeNode(oBannerLoading.getBanner());
    }
    // END Banner Feature

    // START Camera Position Feature
    private void loadCameraPositionList() {
        oDefaultCameraPosition = new ArrayList<CameraPosition>();

        int offset = 15;
        // User Position Or Front
        oDefaultCameraPosition.add(new CameraPosition(defaultCenterPosition.x,
                defaultCenterPosition.y + 5, defaultCenterPosition.z + offset, 0, 0, 0, 0));

        // Top
        oDefaultCameraPosition.add(new CameraPosition(defaultCenterPosition.x,
                defaultCenterPosition.y + offset, defaultCenterPosition.z, -90, 1, 0, 0));

        // Bottom
        oDefaultCameraPosition.add(new CameraPosition(defaultCenterPosition.x,
                defaultCenterPosition.y - offset, defaultCenterPosition.z, 90, 1, 0, 0));

        // Back
        oDefaultCameraPosition.add(new CameraPosition(defaultCenterPosition.x,
                defaultCenterPosition.y + 5, defaultCenterPosition.z - offset, 180, 0, 1, 0));

        // Left
        oDefaultCameraPosition.add(new CameraPosition(defaultCenterPosition.x - offset,
                defaultCenterPosition.y + 5, defaultCenterPosition.z, -90, 0, 1, 0));

        // Right
        oDefaultCameraPosition.add(new CameraPosition(defaultCenterPosition.x + offset,
                defaultCenterPosition.y + 5, defaultCenterPosition.z, 90, 0, 1, 0));
    }

    public ArrayList<String> getCameraPositionList() {
        ArrayList<String> list = new ArrayList<String>();
        for (CameraPosition position : oDefaultCameraPosition) {
            Vector3f coordinate = position.getCameraPosition();
            String sPosition = "X:" + Float.toString(coordinate.x) + " Y:" + Float.toString
                    (coordinate.y) + " Z:" + Float.toString(coordinate.z);
            list.add(sPosition);
        }
        return list;
    }

    public void displayNavigators(SXRScene room) {
        oCurrentPosition = oDefaultCameraPosition.get(0);
        oDefaultCameraPosition.get(0).loadNavigator(context);

        for (int i = 1; i < oDefaultCameraPosition.size(); i++) {
            SXRNode temp = oDefaultCameraPosition.get(i).loadNavigator(context);
            room.addNode(temp);
            enableDisableLightOnModel(temp, false);
        }
    }

    protected void lookAt(SXRTransform modeltransform, SXRTransform camera, SXRNode
            mCharacter) {
        Vector3f cameraV = new Vector3f(camera.getPositionX(), camera.getPositionY(), camera
                .getPositionZ());

        Vector3f modeltransformV = new Vector3f(modeltransform.getPositionX(), modeltransform
                .getPositionY(), modeltransform.getPositionZ());

        Vector3f delta = cameraV.sub(modeltransformV);
        Vector3f direction = delta.normalize();

        Vector3f up;

        if (Math.abs(direction.x) < 0.00001
                && Math.abs(direction.z) < 0.00001) {
            if (direction.y > 0) {
                up = new Vector3f(0.0f, 0.0f, -1.0f); // if direction points in +y
            } else {
                up = new Vector3f(0.0f, 0.0f, 1.0f); // if direction points in -y
            }
        } else {
            up = new Vector3f(0.0f, 1.0f, 0.0f); // y-axis is the general up
        }

        up.normalize();
        Vector3f right = new Vector3f();
        up.cross(direction, right);
        right.normalize();
        direction.cross(right, up);
        up.normalize();

        float[] matrix = new float[]{right.x, right.y, right.z, 0.0f, up.x, up.y,
                up.z, 0.0f, direction.x, direction.y, direction.z, 0.0f,
                modeltransform.getPositionX(), modeltransform.getPositionY(), modeltransform
                .getPositionZ(), 0.0f};
        mCharacter.getTransform().setModelMatrix(matrix);
    }

    public void setCameraPositionByNavigator(SXRCollider picked, SXRScene scene, SXRScene
            room, SXRWidgetNode widget, float original[]) {
        CameraPosition campos = null;
        int camIndex = 0;
        if (picked != null) {
            for (camIndex = 0; camIndex < oDefaultCameraPosition.size(); camIndex++) {
                CameraPosition cp = oDefaultCameraPosition.get(camIndex);
                if (picked.equals(cp.cameraModel.getCollider()))
                {
                    campos = cp;
                    break;
                }
            }
            if (campos == null) {
                return;
            }
        }
        else {
            campos = oDefaultCameraPosition.get(0);
        }

        // START Code to Attach Menu According to Camera Position
        scene.removeNode(widget);
        widget.getTransform().setModelMatrix(original);
        campos.cameraModel.addChildObject(widget);
        Vector3f axis = campos.getRotationAxis();
        campos.cameraModel.getTransform().setRotationByAxis(campos.getCameraAngle(), axis.x, axis.y, axis.z);

        float temp[] = widget.getTransform().getModelMatrix();
        widget.getTransform().setModelMatrix(temp);
        campos.cameraModel.removeChildObject(widget);
        scene.addNode(widget);

        // END Code to Attach Menu According to Camera Position

        Vector3f coordinates = campos.getCameraPosition();
        scene.getMainCameraRig().getTransform().setPosition(coordinates.x, coordinates.y,
                coordinates.z);

        axis = campos.getRotationAxis();
        scene.getMainCameraRig().getTransform().setRotationByAxis(campos.getCameraAngle(), axis.x, axis.y, axis.z);

        if (oCurrentPosition != null) {
            room.addNode(oCurrentPosition.loadNavigator(context));
        }

        Log.i(TAG, "Removing navigator " + Integer.toString(camIndex));
        room.removeNode(campos.cameraModel);
        oCurrentPosition = campos;

        for (int j = 0; j < oDefaultCameraPosition.size(); j++) {
            if (j != camIndex)
                lookAt(oDefaultCameraPosition.get(j).cameraModel.getTransform(), scene
                        .getMainCameraRig().getTransform(), oDefaultCameraPosition.get(j)
                        .cameraModel);
        }
    }

    public void lookInside(SXRScene scene, boolean flag) {
        if (flag && (currentDisplayedModel != null)) {
            lookInsideFlag = true;
            scene.getMainCameraRig().getTransform().setPosition(lookInsidePosition.x,
                    lookInsidePosition.y, lookInsidePosition.z);
        }
    }

    public void checkLookInside(SXRScene scene) {
        Log.i("", "Check look inside");
        if (lookInsideFlag) {
            Vector3f coord = oCurrentPosition.getCameraPosition();
            Log.i("", "True Check look inside" + Float.toString(coord.x) + Float.toString(coord
                    .y) + Float.toString(coord.z));
            scene.getMainCameraRig().getTransform().setPosition(coord.x, coord.y, coord.z);
            lookInsideFlag = false;
        }
    }
    // END Camera Position Feature

    // START Look Inside

    // START Models Features

    public int getCountOfAnimations() {
        if (currentDisplayedModel != null)
            return currentDisplayedModel.getAnimation().getAnimationCount();
        return 0;
    }

    public void setSelectedAnimation(int index) {
        // No Animation
        if (index == 0) {
            if (currentAnimation != null) {
                context.getAnimationEngine().stop(currentAnimation);
                currentAnimation = null;
            }
        } else {
            index -= 1;
            if (currentDisplayedModel != null && currentDisplayedModel.getAnimation().getAnimationCount() > 0)
                currentAnimation = currentDisplayedModel.getAnimation().getAnimation(index);
            currentAnimation.setRepeatMode(SXRRepeatMode.REPEATED);
            currentAnimation.setRepeatCount(-1);
            currentAnimation.start(context.getAnimationEngine());
        }
    }

    private ArrayList<File> getListOfModels() {
        ArrayList<File> listOfAllModels = new ArrayList<File>();

        // Add All the Extensions you want to load
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add("fbx");
        extensions.add("FBX");
        extensions.add("3ds");
        extensions.add("dae");
        extensions.add("obj");
        extensions.add("ma");
        extensions.add("x3d");

        // Reads the List of Files from specified folder having extension specified in extensions.
        // Please place your models by creating SXRModelViewer2 folder in your internal phone memory
        CardReader cRObject = new CardReader(sEnvironmentPath + "/SceneEditor", extensions);
        File list[] = cRObject.getModels();

        if (list == null)
            return listOfAllModels;

        // Adds all the models
        for (File file : list) {
            Log.i("path", file.getPath());

            listOfAllModels.add(file);
        }

        return listOfAllModels;
    }

    void loadModelsList() {
        aModel = new ArrayList<Model>();
        ArrayList<File> listOfAllModels = getListOfModels();
        for (File file : listOfAllModels) {
            Model tempModel = new Model(file.getName(), file.getPath().replaceAll
                    ("/storage/emulated/0/", ""));
            aModel.add(tempModel);
        }
    }

    ArrayList<String> getModelsList() {
        ArrayList<String> listOfModels = new ArrayList<String>();

        for (Model m : aModel)
            listOfModels.add(m.getModelName());

        return listOfModels;
    }

    void setModelWithIndex(int index, SXRScene room) {
        if (currentDisplayedModel != null) {
            room.removeNode(currentDisplayedModel.getModel(context));
        }

        displayLoadingInRoom(room);
        SXRNode tempModelSO = aModel.get(index).getModel(context);

        Log.d(TAG, "Loading Done");
        if (tempModelSO != null) {
            tempModelSO.getTransform().setPosition(defaultCenterPosition.x, defaultCenterPosition
                    .y, defaultCenterPosition.z);
            room.addNode(tempModelSO);
            enableDisableLightOnModel(tempModelSO, oLightFlag);

            removeLoadingInRoom(room);
            Log.d(TAG, "Loading Done");
            currentDisplayedModel = aModel.get(index);
            currentModelFlag = true;
        } else {
            Log.d(TAG, "Loading Error");
        }
        removeLoadingInRoom(room);
    }

    void onScrollOverModel(SXRCollider holder, float scrollValue) {
        if (null != currentDisplayedModel && currentDisplayedModel.getModel(context).getCollider() == holder) {
                Log.d(TAG, "Angle mover applied");
                if (scrollValue > 0)
                    new SXRRotationByAxisAnimation(currentDisplayedModel.getModel
                            (context), 0.1f, 35, 0, 1, 0).start(context.getAnimationEngine());
                else
                    new SXRRotationByAxisAnimation(currentDisplayedModel.getModel
                            (context), 0.1f, 35, 0, -1, 0).start(context.getAnimationEngine());
        }
    }

    void onZoomOverModel(float zoomBy) {
        float zTransform = (int) ((zoomBy) / (10));
        Log.d(TAG, "Zoom by" + Float.toString(zTransform) + "  " + Float.toString(zoomBy));
        if (currentDisplayedModel != null) {
            SXRTransform trans = currentDisplayedModel.getModel(context).getTransform();
            float units = currentDisplayedModel.getCurrentZoom();
            if (units < zTransform) {
                float scaleFactor = zTransform - units;
                float sf = 1.1f;
                for (int i = 0; i < scaleFactor; i++) {
                    float x = trans.getScaleX();
                    float y = trans.getScaleY();
                    float z = trans.getScaleZ();
                    trans.setScale(sf * x, sf *
                            y, sf * z);
                }
            } else {
                float scaleFactor = units - zTransform;
                float sf = 0.9f;
                for (int i = 0; i < scaleFactor; i++) {
                    float x = trans.getScaleX();
                    float y = trans.getScaleY();
                    float z = trans.getScaleZ();
                    trans.setScale(sf * x, sf *
                            y, sf * z);
                }
            }
            currentDisplayedModel.setCurrentZoom(zTransform);
        }
    }
    // END Models Features

    // START SkyBox Features
    void loadSDSkyBoxList() {
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add("png");

        CardReader cRObject = new CardReader(sEnvironmentPath + "/" + sSDSkyBoxDirectory + "/",
                extensions);
        File list[] = cRObject.getModels();

        aOSDSkyBox = new ArrayList<SkyBox>();

        if (list != null)
            for (File sSkyBoxName : list) {
                aOSDSkyBox.add(new SkyBox(sSkyBoxName.getName()));
            }
    }

    void loadDefaultSkyBoxList() {
        String[] aSSkyBox = AssetsReader.getAssetsList(activity, sDefaultSkyBoxDirectory);
        aODefaultSkyBox = new ArrayList<SkyBox>();

        for (String sSkyBoxName : aSSkyBox) {
            aODefaultSkyBox.add(new SkyBox(sSkyBoxName));
        }
    }

    ArrayList<String> getSkyBoxList() {
        ArrayList<String> sAEntireList = new ArrayList<String>();

        // Default SkyBox List
        for (SkyBox oSkyBox : aODefaultSkyBox) {
            sAEntireList.add(oSkyBox.getSkyBoxName());
        }

        // SDCard SkyBox List
        if (aOSDSkyBox != null)
            for (SkyBox oSkyBox : aOSDSkyBox) {
                sAEntireList.add(oSkyBox.getSkyBoxName());
            }

        return sAEntireList;
    }

    void addSkyBox(int index, SXRScene scene) {
        Log.d(TAG, "Adding SkyBox");
        SXRSphereNode current = null;
        if (currentSkyBox != null)
            scene.removeNode(currentSkyBox);

        int count = aODefaultSkyBox.size();
        if (index < count) {
            current = aODefaultSkyBox.get(index).getSkyBox(context, sDefaultSkyBoxDirectory + "/");
        } else {
            current = aOSDSkyBox.get(index - count).getSkyBoxFromSD(context, sEnvironmentPath +
                    "/" + sSDSkyBoxDirectory + "/");
        }

        if (current != null) {
            scene.addNode(current);
            currentSkyBox = current;
            current.getTransform().setPosition(defaultCenterPosition.x, defaultCenterPosition.y,
                    defaultCenterPosition.z);
        } else {
            Log.d(TAG, "SkyBox is null");
        }

        enableDisableLightOnModel(current, false);
    }

    // END SkyBox Features
}
