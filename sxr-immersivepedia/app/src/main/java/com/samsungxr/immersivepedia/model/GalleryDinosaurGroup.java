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

package com.samsungxr.immersivepedia.model;

import com.samsungxr.SXRAndroidResource;
import com.samsungxr.SXRContext;
import com.samsungxr.SXRMeshCollider;
import com.samsungxr.SXRScene;
import com.samsungxr.SXRSceneObject;
import com.samsungxr.animation.SXRRotationByAxisAnimation;
import com.samsungxr.immersivepedia.R;
import com.samsungxr.immersivepedia.dinosaur.Dinosaur;
import com.samsungxr.immersivepedia.dinosaur.DinosaurFactory;
import com.samsungxr.immersivepedia.focus.FocusableSceneObject;
import com.samsungxr.immersivepedia.focus.OnGestureListener;
import com.samsungxr.immersivepedia.gallery.Gallery;
import com.samsungxr.immersivepedia.props.Totem;
import com.samsungxr.immersivepedia.util.AudioClip;

import java.io.IOException;

public class GalleryDinosaurGroup extends SXRSceneObject {
    private int photos[] = new int[] {
            R.drawable.photo01, R.drawable.photo02, R.drawable.photo03,
            R.drawable.photo04,
            R.drawable.photo05, R.drawable.photo06, R.drawable.photo07,
            R.drawable.photo08,
            R.drawable.photo09, R.drawable.photo10, R.drawable.photo03,
            R.drawable.photo02
    };

    private SXRSceneObject galleryGroup;
    private Gallery gallery;
    private SXRScene scene;
    private SXRContext gvrContext;
    private FocusableSceneObject focus;
    private Dinosaur dinosaur;

    public GalleryDinosaurGroup(SXRContext gvrContext, SXRScene scene) throws IOException {
        super(gvrContext);
        this.gvrContext = gvrContext;
        this.scene = scene;

        createGallery();
        createGalleryGroup();
        createTotem();
        createDinosaur();
        createFocus();
    }

    private void createFocus() {

        focus = new FocusableSceneObject(gvrContext, gvrContext.createQuad(15f, 9f),
                gvrContext.getAssetLoader().loadTexture(new SXRAndroidResource(gvrContext, R.drawable.empty)));
        focus.getTransform().setPosition(0f, 3.5f, DinosaurFactory.APATOSAURUS_DISTANCE - 0.01f);
        focus.getTransform().rotateByAxis(-180.0f, 0f, 1f, 0f);
        focus.attachCollider(new SXRMeshCollider(getSXRContext(), false));

        focus.setName("apatosaurus");
        focus.setOnGestureListener(new OnGestureListener() {

            @Override
            public void onSwipeUp() {
            }

            @Override
            public void onSwipeIgnore() {
            }

            @Override
            public void onSwipeForward() {
                AudioClip.getInstance(getSXRContext().getContext()).playSound(AudioClip.getUIRotateSoundID(), 1.0f, 1.0f);
                new SXRRotationByAxisAnimation(dinosaur, 4f, 45, 0, 1, 0).start(gvrContext.getAnimationEngine());
            }

            @Override
            public void onSwipeDown() {
            }

            @Override
            public void onSwipeBack() {
                AudioClip.getInstance(getSXRContext().getContext()).playSound(AudioClip.getUIRotateSoundID(), 1.0f, 1.0f);
                new SXRRotationByAxisAnimation(dinosaur, 4f, -45, 0, 1, 0).start(gvrContext.getAnimationEngine());
            }
        });
        scene.addSceneObject(focus);
    }

    private void createGallery() {
        gallery = new Gallery(getSXRContext(), photos);
        gallery.getTransform().setPosition(Gallery.GALLERY_POSITION_X, Gallery.GALLERY_POSITION_Y, Gallery.GALLERY_POSITION_Z);
        gallery.getTransform().setRotationByAxis(180.0f, 0f, 1f, 0f);
        scene.addSceneObject(gallery);
    }

    private void createGalleryGroup() {
        galleryGroup = new SXRSceneObject(getSXRContext());
        galleryGroup.getTransform().setPosition(0f, 0f, -8.0f);
        galleryGroup.getTransform().rotateByAxis(180.0f, 0f, 1f, 0f);
        galleryGroup.getTransform().rotateByAxisWithPivot(
                DinosaurFactory.APATOSAURUS_ANGLE_AROUND_CAMERA - 35.0f, 0f, 1f, 0f, 0f, 0f, 0f);
    }

    private void createDinosaur() throws IOException {
        dinosaur = DinosaurFactory.getInstance(getSXRContext()).getApatosaurus();
        dinosaur.getTransform().rotateByAxisWithPivot(-90.0f, 1f, 0f, 0f, 0f, 0f, 0f);
        dinosaur.getTransform().rotateByAxisWithPivot(180.0f, 0f, 1f, 0f, 0f, 0f, 0f);
        dinosaur.getTransform().setPosition(0f, 0f, DinosaurFactory.APATOSAURUS_DISTANCE);
        dinosaur.getTransform().rotateByAxis(-70.0f, 0f, 1f, 0f);
        scene.addSceneObject(dinosaur);
    }

    private void createTotem() {
        Totem totem = new Totem(getSXRContext(),
                getSXRContext().getAssetLoader().loadTexture(new SXRAndroidResource(getSXRContext(),
                        R.drawable.totem_tex_diffuse)));

        totem.getTransform().setPosition(-1f, 0f, -3f);
        totem.setTotemEventListener(gallery);
        totem.setName("totem_apatosaurus");
        totem.setText(gvrContext.getActivity().getResources().getString(R.string.gallery_totem));
        galleryGroup.addChildObject(totem);
        scene.addSceneObject(galleryGroup);
    }

    public boolean isOpen() {
        return gallery.isOpen();
    }

    public void closeThis() {
        gallery.closeThis();
    }

}