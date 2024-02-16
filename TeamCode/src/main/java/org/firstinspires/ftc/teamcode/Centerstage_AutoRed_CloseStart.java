/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

/*
 * This OpMode illustrates the basics of TensorFlow Object Detection,
 * including Java Builder structures for specifying Vision parameters.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */
@Autonomous(name = "Red Close Start", group = "Concept")
//@Disabled
public class Centerstage_AutoRed_CloseStart extends LinearOpMode {
    Gobbler gobbler = null;
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    private static final String TFOD_MODEL_ASSET = "redmayhem_v2.tflite";
    // TFOD_MODEL_FILE points to a model file stored onboard the Robot Controller's storage,
    // this is used when uploading models directly to the RC using the model upload interface.
    //private static final String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/myCustomModel.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
            "Red Mayhem",
    };

    // Open and close booleans (just to make code easier to read)
    boolean open = true;
    boolean close = false;

    ElapsedTime trapdoorToggle = new ElapsedTime();

    // Variable that will later be used for placing the second pixel.
    int desiredTag = 0;
    int borderLine = 450;

    /**
     * //The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;
    boolean seen = false;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        gobbler = new Gobbler(hardwareMap);
        initTfod();

        while (WaitingToStart()) {
            IdentifyTeamPropLocation();
        }

        if (opModeIsActive()) {
            PlaceFirstPixel();
            //setupRobotToPlaceSecondPixel();
        }

        // Save more CPU resources when camera is no longer needed.
        visionPortal.close();

    }   // end runOpMode()

    private void setupRobotToPlaceSecondPixel() {
        eatYellowPixel();
        faceBackdrop();
    }

    private void faceBackdrop() {
        if (desiredTag == 2) { // This turns the robot to the backboard if it is in the center position
            gobbler.driveTrain.turnClockwise(-180, 0.5);
        } else if (desiredTag == 1) { // This turns the robot to the backboard if it is in the right position
            gobbler.driveTrain.turnCounterClockwise(-90, 0.5);
        } else { // This turns the robot to the backboard if it is in the left positions
            gobbler.driveTrain.moveForward(3, 0.5);
            gobbler.driveTrain.Wait(3.0);
            gobbler.driveTrain.turnCounterClockwise(-180, 0.5);
        }
    }
    private void eatYellowPixel() {
        lowerMailbox();
        gobbler.driveTrain.Wait(1.0);
        movePixelIntoMailbox();
    }

    private void movePixelIntoMailbox() {
        gobbler.intake.turnOnConveyorBelt();
        gobbler.driveTrain.Wait(5.0);
        gobbler.intake.turnOffConveyorBelt();
    }

    private void lowerMailbox() {
        gobbler.outtake.driveLift(0.5);
        gobbler.driveTrain.Wait(1.0);
        gobbler.outtake.driveLift(0.0);
        gobbler.driveTrain.Wait(2);
    }

    private void IdentifyTeamPropLocation() {
        seen = false; // setting it to false again so that the robot will correctly detect Mayhem on the left piece of tape
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        for (Recognition recognition : currentRecognitions) {
            double xValue = (recognition.getLeft() + recognition.getRight()) / 2;
            // To figure out this part, you will have to use the ConceptTensorFlowObjectDetection file
            // The first two x values represent the minimum and maximum value x has to be for the team prop to be considered center.
            // The second two y values represent the minimum and maximum value x has to be for the team prop to be considered center.
            if (xValue < borderLine) {
                // center
                telemetry.addData("position", "Center");
                desiredTag = 2;
                seen = true;
            }

            // The first two x values represent the minimum and maximum value x has to be for the team prop to be considered right.
            // The second two y values represent the minimum and maximum value x has to be for the team prop to be considered right.
            else if (xValue > borderLine) {  //
                // right
                telemetry.addData("position", "Right");
                desiredTag = 1;
                seen = true;

            }
        }
        // If the team prop is not seen on the center or right, it will assume it is on the left.
        if (!seen) {
            telemetry.addData("position", "Left");
            desiredTag = 3;
        }

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
    }

    /**
     * Initialize the TensorFlow Object Detection processor.
     */
    private void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                // choose one of the following:
                //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                .setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                .setModelLabels(LABELS)
                //.setIsModelTensorFlow2(true)
                //.setIsModelQuantized(true)
                //.setModelInputSize(300)
                //.setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Cam1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        //builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        //tfod.setMinResultConfidence(0.75f);

        // Disable or re-enable the TFOD processor at any time.
        //visionPortal.setProcessorEnabled(tfod, true);

    }   // end method initTfod()

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    private void telemetryTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method telemetryTfod()

    private void PlaceFirstPixel() {
        if (desiredTag == 2) { // drives robot to the center position.
            gobbler.driveTrain.centerBlueFarRedClose();
        }

        else if (desiredTag == 1) { // drives robot to the right position.
            gobbler.driveTrain.rightBlueFarRedClose();
        }

        else { // drives robot to the left position.
            gobbler.driveTrain.leftBlueFarRedClose();
        }
        PlacePixelOnTape();
    }

    private void PlacePixelOnTape() {
        gobbler.outtake.trapdoor(true, trapdoorToggle);
        gobbler.driveTrain.Wait(1.0);
        gobbler.outtake.driveLift(-0.5);
        gobbler.driveTrain.Wait(1.0);
        gobbler.outtake.driveLift(0.0);
        gobbler.driveTrain.Wait(2);
        gobbler.outtake.trapdoor(true, trapdoorToggle);
    }

    private boolean WaitingToStart() {
        return (!isStarted() && !isStopRequested());
    }

}   // end class