/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samsungxr.controls.model;

import android.content.res.TypedArray;

import com.samsungxr.SXRAndroidResource;
import com.samsungxr.SXRContext;
import com.samsungxr.SXRMaterial;
import com.samsungxr.SXRMesh;
import com.samsungxr.SXRRenderData;
import com.samsungxr.SXRNode;
import com.samsungxr.SXRTexture;
import com.samsungxr.controls.util.RenderingOrder;

public class Cloud extends SXRNode {

    private final int CLOUD_ANGLE = 30;
    private final float CLOUD_OFFSET = 0.5f;

    private float originalScaleX;
    private float originalScaleY;
    private float originalScaleZ;
    private int originalTexture;

    public Cloud(SXRContext sxrContext, SXRMesh mesh, float cloudDistance, TypedArray array, float angle) {
        super(sxrContext);

        populateArray(array);

        this.getTransform().setScale(originalScaleX,
                originalScaleY,
                originalScaleZ);

        setPosition(angle, cloudDistance);

        createRendereData(sxrContext, mesh);

    }

    private void setPosition(float angle, float cloudDistance) {

        this.getTransform().setPositionZ(cloudDistance);

        this.getTransform().rotateByAxisWithPivot((float)
                (Math.random() + CLOUD_OFFSET) * CLOUD_ANGLE, 1, 0, 0, 0, 0, 0);

        this.getTransform().rotateByAxisWithPivot(angle, 0, 1, 0, 0, 0, 0);
    }

    private void createRendereData(SXRContext sxrContext, SXRMesh mesh) {

        SXRTexture cloudTexture = sxrContext.getAssetLoader().loadTexture(new SXRAndroidResource(sxrContext,
                originalTexture));

        SXRMaterial material = new SXRMaterial(sxrContext);
        material.setMainTexture(cloudTexture);

        SXRRenderData renderData = new SXRRenderData(sxrContext);
        renderData.setMesh(mesh);
        renderData.setMaterial(material);
        renderData.setRenderingOrder(RenderingOrder.CLOUDS);
        this.attachRenderData(renderData);

    }

    public void populateArray(TypedArray array) {
        originalScaleX = array.getFloat(0, 0);
        originalScaleY = array.getFloat(1, 0);
        originalScaleZ = array.getFloat(2, 0);
        originalTexture = array.getResourceId(3, 0);

    }

}