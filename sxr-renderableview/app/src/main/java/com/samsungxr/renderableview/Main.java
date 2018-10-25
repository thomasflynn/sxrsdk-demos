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

package com.samsungxr.renderableview;

import android.graphics.Color;
import android.widget.TextView;

import com.samsungxr.SXRContext;
import com.samsungxr.SXRMain;
import com.samsungxr.scene_objects.SXRCubeSceneObject;
import com.samsungxr.scene_objects.SXRViewSceneObject;

public class Main extends SXRMain {
    private final MainActivity mActivity;

    private SXRViewSceneObject mLayoutLeftSceneObject;
    private SXRViewSceneObject mWebSceneObject;
    private SXRViewSceneObject mTextSceneObject;

    public Main(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onInit(final SXRContext gvrContext) throws Throwable {
        // SXRCubeSceneObject - Just to take cube mesh.
        SXRCubeSceneObject cube = new SXRCubeSceneObject(gvrContext);

        mLayoutLeftSceneObject = new SXRViewSceneObject(gvrContext,
                R.layout.activity_main, cube.getRenderData().getMesh());

        gvrContext.getMainScene().addSceneObject(mLayoutLeftSceneObject);

        mLayoutLeftSceneObject.getTransform().setPosition(-1.0f, 0.0f, -2.5f);
        mLayoutLeftSceneObject.setTextureBufferSize(512);

        mWebSceneObject = new SXRViewSceneObject(gvrContext,
                mActivity.getWebView(), cube.getRenderData().getMesh());

        gvrContext.getMainScene().addSceneObject(mWebSceneObject);

        mWebSceneObject.getTransform().setPosition(1.0f, 0.0f, -2.5f);
        mWebSceneObject.setTextureBufferSize(512);

        TextView  textView = new TextView(gvrContext.getActivity());
        textView.setText("Android's Renderable Views");
        textView.setTextColor(Color.WHITE);

        mTextSceneObject = new SXRViewSceneObject(gvrContext, textView, 2.0f, 1.0f);
        gvrContext.getMainScene().addSceneObject(mTextSceneObject);
        mTextSceneObject.getTransform().setPosition(0.0f, -2.0f, -2.5f);
        mTextSceneObject.setTextureBufferSize(512);
    }

    @Override
    public void onStep() {
        mLayoutLeftSceneObject.getTransform().rotateByAxis(0.5f, 1, 1, 0);

        mWebSceneObject.getTransform().rotateByAxis(-0.5f, 1, 1, 0);
    }

}