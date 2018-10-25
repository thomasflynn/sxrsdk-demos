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

import com.samsungxr.SXRAndroidResource;
import com.samsungxr.SXRContext;
import com.samsungxr.SXRSceneObject;
import com.samsungxr.animation.SXRAnimation;
import com.samsungxr.animation.SXROpacityAnimation;
import com.samsungxr.animation.SXRRelativeMotionAnimation;
import com.samsungxr.controls.R;
import com.samsungxr.controls.util.MathUtils;

public class Star extends SXRSceneObject {

    private static final float ANIMATION_DURATION = 5;
    private static final float OPACITY_ANIMATION_DURATION = 4;
    private static final float Y_ANIMATION_DELTA = 10;
    private static final float STAR_SCALE = 0.75f;

    public Star(SXRContext gvrContext) {
        super(gvrContext, gvrContext.getAssetLoader().loadMesh(new SXRAndroidResource(gvrContext,
                        R.raw.star)),
                gvrContext
                        .getAssetLoader().loadTexture(new SXRAndroidResource(gvrContext, R.drawable.star_diffuse)));
        this.getRenderData().getMaterial().setOpacity(0);
        this.getTransform().setScale(STAR_SCALE, STAR_SCALE, STAR_SCALE);

    }

    public void playMoveAnimation(SXRContext gvrContext, SXRSceneObject returnTarget) {
        getTransform().setPosition(returnTarget.getTransform().getPositionX(),
                returnTarget.getTransform().getPositionY(),
                returnTarget.getTransform().getPositionZ());
        getTransform().setRotationByAxis(
                0, 1, 1, 1);
        getTransform().rotateByAxis(
                MathUtils.getYRotationAngle(this, gvrContext.getMainScene().getMainCameraRig()), 0,
                1, 0);
        SXRAnimation anim = new SXRRelativeMotionAnimation(this, ANIMATION_DURATION, 0,
                Y_ANIMATION_DELTA, 0);
        anim.start(gvrContext.getAnimationEngine());
        playOpacityAnimation(gvrContext);

    }

    public void playOpacityAnimation(SXRContext gvrContext) {

        this.getRenderData().getMaterial().setOpacity(1);
        SXRAnimation anim = new SXROpacityAnimation(this, OPACITY_ANIMATION_DURATION, 0);

        anim.start(gvrContext.getAnimationEngine());

    }

}