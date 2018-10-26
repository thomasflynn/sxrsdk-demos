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

package com.samsungxr.keyboard.spinner;

import android.graphics.Color;

import com.samsungxr.SXRContext;
import com.samsungxr.SXRNode;
import com.samsungxr.animation.SXRAnimation;
import com.samsungxr.animation.SXRRotationByAxisAnimation;
import com.samsungxr.keyboard.fling.Fling;
import com.samsungxr.keyboard.model.AudioClip;
import com.samsungxr.keyboard.model.CharItem;
import com.samsungxr.keyboard.model.CharList;
import com.samsungxr.keyboard.textField.Text;
import com.samsungxr.keyboard.util.CircularList;
import com.samsungxr.keyboard.util.RenderingOrder;
import com.samsungxr.keyboard.util.NodeNames;
import com.samsungxr.keyboard.util.Util;
import com.samsungxr.keyboard.util.VRSamplesTouchPadGesturesDetector.SwipeDirection;

import java.util.ArrayList;

public class SpinnerRoulette extends SXRNode {

    private static final int ITEM_DEGREE = 45;
    private static final float ITEM_DEGREE_TEST = 45;
    private SXRContext sxrContext;
    private int initialCharacterPosition;
    private static final int SPINNER_ITEM_SIZE = 8;
    private SXRAnimation swipeAnimation;
    private SpinnerAdapter spinnerAdapter;
    String lastSwipeDirection;
    private float amountRotation;
    private float actualRotation;
    private float oldRotation;
    private static final float SPINNER_ITEM_POSITION = 0.35f;
    private float distanceDegree;
    private int position;
    boolean flingOn = true;
    private float lastDegreeAnimation;
    private boolean soudOn;

    public boolean isSoudOn() {
        return soudOn;
    }

    public void setSoudOn(boolean soudOn) {
        this.soudOn = soudOn;
    }

    protected SpinnerRoulette(SXRContext sxrContext, int initialCharacterPosition, int mode) {
        super(sxrContext);
        setName(NodeNames.SPINNER);

        this.initialCharacterPosition = initialCharacterPosition;
        this.sxrContext = sxrContext;

        CircularList<CharItem> characterList;
        CircularList<SpinnerItem> spinnerItems;

        spinnerItems = new CircularList<SpinnerItem>(new ArrayList<SpinnerItem>());

        characterList = CharList.getInstance(sxrContext).getListCircular(mode);

        spinnerAdapter = new SpinnerAdapter(spinnerItems, characterList);

        createSpinner(createSpinnerItems());
        setDefaultCharactersInSpinner();

    }

    private void createSpinner(CircularList<SpinnerItem> spinnerItems) {

        for (int i = 0; i < spinnerItems.size(); i++) {

            float degreeNormal = 360.0f * i / (spinnerItems.size());

            // TODO Small test try to align center in different way

            float hafItem = ((360.0f / spinnerItems.size()) / 2);

            hafItem = 0;

            float degree = degreeNormal + hafItem;

            // float degree = degreeNormal;

            spinnerItems.get(i).getTransform().setPosition(0.0f, 0.0f, SPINNER_ITEM_POSITION);
            spinnerItems.get(i).getTransform()
                    .rotateByAxisWithPivot(degree, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            spinnerItems.get(i).getRenderData().setRenderingOrder(RenderingOrder.SPINNER);

            this.addChildObject(spinnerItems.get(i));
        }
    }

    public void animate(SwipeDirection swipeDirection, float velocityY) {

        int topVelocityY = (int) (velocityY * Math.signum(velocityY));

        float degree = 0;

        float remainValue = getRemainValue();

        lastSwipeDirection = swipeDirection.name();

        // amountRotation=0;

        if (flingOn) {

            if (swipeDirection.name() == SwipeDirection.Up.name()) {

                degree = -Fling.getDegreeRotation(topVelocityY);

            } else if (swipeDirection.name() == SwipeDirection.Down.name()) {

                degree = Fling.getDegreeRotation(topVelocityY);
            }
            degree = updateDegree(degree, remainValue);
            lastDegreeAnimation = degree;
            stop(swipeAnimation);
            swipeAnimation = new SXRRotationByAxisAnimation(this,
                    Fling.getDelayToAnimate(topVelocityY), degree, 1.0f, 0, 0);
            swipeAnimation.setInterpolator(Fling.getInterpolator(topVelocityY));

        } else {

            if (swipeDirection.name() == SwipeDirection.Up.name()) {

                degree = -ITEM_DEGREE;

            } else if (swipeDirection.name() == SwipeDirection.Down.name()) {

                degree = ITEM_DEGREE;
            }

            lastDegreeAnimation = degree;
            stop(swipeAnimation);
            swipeAnimation = new SXRRotationByAxisAnimation(this, 1, degree,
                    1.0f, 0, 0);

        }

        swipeAnimation.start(this.getSXRContext().getAnimationEngine());
        // spinnerAdapter.updateSpinnerIndex(degree, getSXRContext());
    }

    private float updateDegree(float degree, float remainValue) {

        if (lastDegreeAnimation > 0) {

            if (degree > 0) {

                float change = ITEM_DEGREE - remainValue;

                degree = degree + change;

            } else {

                degree = degree - remainValue;

            }

        } else {

            if (degree < 0) {

                float change = ITEM_DEGREE - remainValue;

                degree = degree - change;

            } else {

                degree = degree + remainValue;

            }

        }
        return degree;
    }

    private float getRemainValue() {

        float result;

        float xRotation = this.getTransform().getRotationPitch();

        xRotation = Math.abs(xRotation);

        result = xRotation % ITEM_DEGREE;

        return result;
    }

    protected void cleanRotation() {

        getTransform().setRotationByAxis(0, 1.0f, 0, 0);
        spinnerAdapter.clean();

    }

    public CharItem getCurrentValue() {

        return spinnerAdapter.getCurrentCharItem();
    }

    private CircularList<SpinnerItem> createSpinnerItems() {

        for (int i = 0; i < SPINNER_ITEM_SIZE; i++) {

            SpinnerItem item = createSpinnerItem(sxrContext, "", i);

            spinnerAdapter.getSpinnerItems().add(item);
        }

        return spinnerAdapter.getSpinnerItems();
    }

    protected void setDefaultCharactersInSpinner() {

        spinnerAdapter.setInitialPosition(initialCharacterPosition);

        spinnerAdapter
                .getSpinnerItems()
                .get(5)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().getPrevious(initialCharacterPosition - 2));

        spinnerAdapter
                .getSpinnerItems()
                .get(6)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().getPrevious(initialCharacterPosition - 1));

        spinnerAdapter
                .getSpinnerItems()
                .get(7)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().getPrevious(initialCharacterPosition));

        spinnerAdapter
                .getSpinnerItems()
                .get(0)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().get(initialCharacterPosition));

        spinnerAdapter
                .getSpinnerItems()
                .get(1)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().getNext(initialCharacterPosition));

        spinnerAdapter
                .getSpinnerItems()
                .get(2)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().getNext(initialCharacterPosition + 1));

        spinnerAdapter
                .getSpinnerItems()
                .get(3)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().getNext(initialCharacterPosition + 2));

        spinnerAdapter
                .getSpinnerItems()
                .get(4)
                .setText(sxrContext,
                        spinnerAdapter.getCharacterList().getNext(initialCharacterPosition + 3));

    }

    private SpinnerItem createSpinnerItem(SXRContext sxrContext, String spinnerText, int position) {

        float sceneObjectWidth = 0.19f;
        float sceneObjectHeigth = 0.29f;
        int bitmapWidth = 45;
        int bitmapHeigth = 72;

        Text text = new Text();
        text.textSize = 75;
        text.backgroundColor = Color.BLACK;

        SpinnerItem textSpinner = new SpinnerItem(getSXRContext(),
                sceneObjectWidth, sceneObjectHeigth,
                bitmapWidth, bitmapHeigth,
                position, text);

        return textSpinner;
    }

    public void onStep() {

        actualRotation = this.getTransform().getRotationPitch();
        distanceDegree = Util.getDistanceDegree(oldRotation, actualRotation, updateClockWise());
        amountRotation = amountRotation + distanceDegree;
        updateIndexIfNecessary();
        oldRotation = actualRotation;
    }

    private void updateIndexIfNecessary() {

        while (amountRotation >= ITEM_DEGREE_TEST) {
            playSpinnerSound();
            spinnerAdapter.updateSpinnerCentral(amountRotation, this.sxrContext);

            amountRotation = amountRotation - ITEM_DEGREE_TEST;

            // AudioClip.getInstance(getSXRContext().getContext()).playSound(AudioClip.getSpinnerSoundID(),
            // 0.5f, 0.5f);

            Util.Log("RotationControl", "amountRotation " + amountRotation);

        }

        while (amountRotation <= -ITEM_DEGREE_TEST) {

            // AudioClip.getInstance(getSXRContext().getContext()).playSound(AudioClip.getSpinnerSoundID(),
            // 0.5f, 0.5f);

            playSpinnerSound();

            spinnerAdapter.updateSpinnerCentral(amountRotation, this.sxrContext);

            amountRotation = amountRotation + ITEM_DEGREE_TEST;

        }

    }

    private void playSpinnerSound() {
        if (soudOn) {

            Runnable run = new Runnable() {

                @Override
                public void run() {
                    AudioClip.getInstance(getSXRContext().getContext()).playSound(
                            AudioClip.getSpinnerSoundID(), 0.5f, 0.5f);
                    // AudioClip.getInstance(getSXRContext().getContext()).playSound(AudioClip.getSpinnerSoundID());
                }
            };

            run.run();
        }

    }

    private boolean updateClockWise() {
        boolean clockWise = true;
        if (lastSwipeDirection == SwipeDirection.Up.name()) {
            clockWise = false;

        } else if (lastSwipeDirection == SwipeDirection.Down.name()) {

            clockWise = true;
        }

        return clockWise;
    }

    public int getInitialCharacterPosition() {
        return initialCharacterPosition;
    }

    public void setInitialCharacterPosition(int initialCharacterPosition) {
        actualRotation = 0;
        oldRotation = 0;

        amountRotation = 0;
        lastDegreeAnimation = 0;
        this.initialCharacterPosition = initialCharacterPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SpinnerAdapter getSpinnerAdapter() {
        return spinnerAdapter;
    }

    public void stopAnimations() {

        stop(swipeAnimation);

    }

    private void stop(SXRAnimation animation) {
        if (animation != null) {
            this.getSXRContext().getAnimationEngine().stop(animation);

        }

    }

}
