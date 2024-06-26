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
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
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
// Merged Auto File
public class Centerstage_AutoRed_CloseStart extends LinearOpMode {
    Gobbler gobbler = null;
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera
    private static final boolean hasSecondCamera = false;
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

    final double SPEED_GAIN  =  0.1; //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN =  0.1; //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
    final double TURN_GAIN   =  0.1; //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)
    final double MAX_AUTO_SPEED = 0.5; //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE= 0.5; //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.3; //  Clip the turn speed to this max value (adjust for your robot)

    boolean targetFound = false;

    private static int DESIRED_TAG_ID = 6;

    private AprilTagProcessor aprilTag;

    private AprilTagDetection desiredTag = null;

    ElapsedTime trapdoorToggle = new ElapsedTime();

    // Variable that will later be used for placing the second pixel.
    int borderLine = 300;

    /**
     * //The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;
    boolean seen = false;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal myVisionPortal;
    ElapsedTime runtimeTimer = null;

    @Override
    public void runOpMode() {

        runtimeTimer = new ElapsedTime();
        runtimeTimer.startTime();

        gobbler = new Gobbler(hardwareMap);
        initDoubleVision();
        gobbler.planeHang.initServo();

        while (WaitingToStart()) {
            IdentifyTeamPropLocation();
        }

        if (opModeIsActive()) {gobbler.planeHang.initServo();
           PlaceFirstPixel();
//            setupRobotToPlaceSecondPixel();
           // placeSecondPixel();
//            parkRobot();
        }

        // Save more CPU resources when camera is no longer needed.
        myVisionPortal.close();

    }   // end runOpMode()

    private void parkRobot() {



    }

    private void placeSecondPixel() {
        // Want to avoid a scenario wherein the camera doesn't recognize the april tag on the first
        // few frames, but is capable of identifying the april tag positions in subsequent frames.
        runtimeTimer.reset();
        while (runtimeTimer.time() < 0.5) {
            LocateTargetAprilTag();
        }
        // How do we want to handle potentially needing to wait for our alliance partner to
        // place their pixel on the backboard before we can?

        // What do we want        to do if we don't identify the target?
        if (targetFound) {

            driveToTarget();
            //placePixelOnBackboard();

        }
    }
    private void driveToTarget() {
        // First, we want to move the robot to a known location with the april tag still visible
        // to the camera.  Once there, we need to move the robot assuming the camera will no longer
        // see the april tag.  As a consequence, we want the second leg of the trip to be as simple
        // as possible.  To that extent, we'll position the robot in such a way that it will only
        // need to drive forward.

        driveToIntermediatePosition();
        driveToFinalPosition();

        if (hasSecondCamera) {
            fineTunePositioning();
        }
    }

    // Once we implement RoadRunner, this function will likely not be needed as RoadRunner should
    // be able to accurately and smoothly take us from whatever initial position we have to the
    // desired final position.
    private void driveToIntermediatePosition() {
        // Need to travel from current location to a predetermined intermediate location so that
        // we can accurately drive the robot up to the backboard without keeping the april tag
        // in sight the entire time.

        // What is our desired intermediate position? This can and should be independent of which
        // april tag we're moving toward.  Probably needs to be determined through testing, but a
        // reasonable approximation could be calculated from the geometry of the field/robot.
        double desiredDistance = 30.0;
        // double desiredHeading = 20.0;
        // double desiredYaw = 20.0;

        while (opModeIsActive()) {
            LocateTargetAprilTag();
            // if the camera didn't detect the desired tag in the previous cycle, give it a chance
            // to try again.
            // Might want to build in some mechanism to prevent getting stuck here, such as only
            // spending a certain amount of time here.
            if (desiredTag == null) {
                gobbler.driveTrain.stop();
                continue;
            }

            double rangeError = desiredTag.ftcPose.range - desiredDistance;
            double headingError = desiredTag.ftcPose.bearing;    // - desiredHeading;
            double yawError = desiredTag.ftcPose.yaw;   // - desiredYaw;

            double drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
            double strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
            double turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);

            if (!errorIsAcceptable(rangeError, headingError, yawError)) {
                gobbler.driveTrain.driveAutonomously(-drive, strafe, turn);

                //telemetry.addData("strafe", "%5.1f", strafe);
                telemetry.addData("Range", "%5.2f inches", desiredTag.ftcPose.range);
                telemetry.addData("Bearing", "%3.2f degrees", desiredTag.ftcPose.bearing);
                telemetry.addData("Yaw", "%3.2f degrees", desiredTag.ftcPose.yaw);
                telemetry.update();
            }
            else {
                gobbler.driveTrain.stop();
                telemetry.addData("Made it to the intermediate position", "");
                telemetry.update();
                break;
            }
        }
    }

    private boolean errorIsAcceptable(double rangeError, double headingError, double yawError) {
        double epsilon = 1.0;
        return ((Math.abs(rangeError) <= epsilon) && (Math.abs(headingError) < epsilon) && (Math.abs(yawError) < epsilon));
    }

    private void driveToFinalPosition() {
        // If using an intermediate position, we'll need to put in the (simple) controls to drive
        // the robot the last leg to in front of the backboard, in position to score a pixel
        if (DESIRED_TAG_ID == 4) {
            gobbler.driveTrain.moveBackward(12, 0.5);
            gobbler.driveTrain.Wait(0.5);
            gobbler.driveTrain.strafeLeft(2, 0.5);
            gobbler.driveTrain.Wait(0.5);
        }

        else if (DESIRED_TAG_ID == 5) {
            gobbler.driveTrain.moveBackward(12, 0.5);
            gobbler.driveTrain.Wait(0.5);
            gobbler.driveTrain.strafeLeft(3, 0.5);
            gobbler.driveTrain.Wait(0.5);
        }

        else if (DESIRED_TAG_ID == 6) {
            gobbler.driveTrain.moveBackward(12, 0.5);
            gobbler.driveTrain.Wait(0.5);
            gobbler.driveTrain.strafeLeft(6, 0.5);
            gobbler.driveTrain.Wait(0.5);
        }

        gobbler.driveTrain.moveBackward(10, 0.5);

        // If using RoadRunner, can just directly put in a RR path to go from current position to
        // the desired final position in front of the backboard
    }

    private void fineTunePositioning() {
        // If we have the second camera ready for viewing the april tag when we're up against the
        // backboard, we can put a similar loop in here to make finer adjustments to the robot
        // position before attempting to score a pixel on the backboard.
    }

    private void LocateTargetAprilTag() {
        targetFound = false;
        desiredTag = null;

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                //  Check to see if we want to track towards this tag.
                // **DESIRED TAG IS DETERMINED BY FIRST PIXEL CODE**

                if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID)) {
                    // Yes, we want to use this tag.
                    targetFound = true;
                    desiredTag = detection;

                    //gobbler.driveTrain.moveForward(1,0.5);

                    break;  // don't look any further.
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
            }
        }
    }

    private void setupRobotToPlaceSecondPixel() {
        eatYellowPixel();
        faceBackdrop();
    }

    private void faceBackdrop() {
        if (DESIRED_TAG_ID == 2) { // This turns the robot to the backboard if it is in the center position
            gobbler.driveTrain.turnClockwise(-180, 0.5);
        } else if (DESIRED_TAG_ID == 1) { // This turns the robot to the backboard if it is in the right position
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
        gobbler.outtake.driveLift(0.0, 0.5);
        gobbler.driveTrain.Wait(1.0);
        gobbler.outtake.driveLift(0.0, 0.0);
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
                telemetry.addData("position", "Left");
                DESIRED_TAG_ID = 1;
                seen = true;
            }

            // The first two x values represent the minimum and maximum value x has to be for the team prop to be considered right.
            // The second two y values represent the minimum and maximum value x has to be for the team prop to be considered right.
            else if (xValue > borderLine) {  //
                // right
                telemetry.addData("position", "Center");
                DESIRED_TAG_ID = 2;
                seen = true;

            }
        }
        // If the team prop is not seen on the center or right, it will assume it is on the left.
        if (!seen) {
            telemetry.addData("position", "Right");
            DESIRED_TAG_ID = 3;
        }

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
    }

    /**
     * Initialize the TensorFlow Object Detection processor.
     */
    private void initDoubleVision() {
        // -----------------------------------------------------------------------------------------
        // AprilTag Configuration
        // -----------------------------------------------------------------------------------------

        aprilTag = new AprilTagProcessor.Builder()
                .setDrawCubeProjection(true)
                .setDrawAxes(true)
                .build();

        // -----------------------------------------------------------------------------------------
        // TFOD Configuration
        // -----------------------------------------------------------------------------------------

        tfod = new TfodProcessor.Builder()
                .setModelAssetName(TFOD_MODEL_ASSET)
                .setModelLabels(LABELS)
                .build();

        // -----------------------------------------------------------------------------------------
        // Camera Configuration
        // -----------------------------------------------------------------------------------------

        if (USE_WEBCAM) {
            myVisionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Cam1"))
                    .addProcessors(tfod, aprilTag)
                    .build();
        } else {
            myVisionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessors(tfod, aprilTag)
                    .build();
        }
    }   // end initDoubleVision()

    private void PlaceFirstPixel() {
        if (DESIRED_TAG_ID == 2) { // drives robot to the center position.
            gobbler.driveTrain.centerBlueFarRedClose();
        }

        else if (DESIRED_TAG_ID == 3) { // drives robot to the right position.
            gobbler.driveTrain.rightBlueFarRedClose();
        }

        else { // drives robot to the left position.
            gobbler.driveTrain.leftBlueFarRedClose();
        }
        PlacePixelOnTape();
    }

    private void PlacePixelOnTape() {
        gobbler.outtake.trapdoor(true, trapdoorToggle);
        gobbler.driveTrain.Wait(0.6);
        gobbler.outtake.driveLift(0.5, 0.0);
        gobbler.driveTrain.Wait(0.6);
        gobbler.outtake.driveLift(0.0, 0.0);
        gobbler.driveTrain.Wait(0.8);
        gobbler.outtake.trapdoor(true, trapdoorToggle);
    }

    private boolean WaitingToStart() {
        return (!isStarted() && !isStopRequested());
    }

}   // end class