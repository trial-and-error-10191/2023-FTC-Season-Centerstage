// This is the drive train subsystem file.
// All drive train stuff should be found here.

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.opencv.core.Core;

/** @noinspection ALL*/
public class DriveTrain {

    private double maxSpeed = 0.5;
    DcMotor leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive;

    private int lfPos, rfPos, lrPos, rrPos;

    static final double COUNTS_PER_MOTOR_REV = 537.7;
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // No External Gearing.
    static final double WHEEL_DIAMETER_INCHES = 3.78;     // For figuring circumference
    static final double clicksPerInch = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    private final double clicksPerDeg = clicksPerInch / 4.99; // empirically measured
    private ElapsedTime runtime = new ElapsedTime();


    boolean directionToggle = true;
//    private DistanceSensor BackWDRight;
//    private DistanceSensor BackWDLeft;

    private double RampDownStart = 16;
    private double RampDownEnd = 8;
    private double RampDownSpeed = 0.2;
    private double slope = (1 - RampDownSpeed) / (RampDownStart - RampDownEnd);
    private double intercept = 1 - slope * RampDownStart ;

    // All subsystems should have a hardware function that labels all of the hardware required of it.
    public DriveTrain(HardwareMap hwMap) {

        // Initializes motor names:
        leftFrontDrive = hwMap.get(DcMotor.class, "leftFront");
        leftBackDrive = hwMap.get(DcMotor.class, "leftBack");
        rightFrontDrive = hwMap.get(DcMotor.class, "rightFront");
        rightBackDrive = hwMap.get(DcMotor.class, "rightBack");

        // Initializes motor directions:
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);


//        distanceRight = hwMap.get(DistanceSensor.class, "distanceRight");
//        distanceLeft = hwMap.get(DistanceSensor.class, "distanceLeft");

    }


    // This function needs an axial, lateral, and yaw input. It uses this input to drive the drive train motors.
    // The last two variables are for direction switching.
    public void drive(double axial, double lateral, double yaw, boolean directionButton, ElapsedTime time) {

//        distanceRightValue = distanceRight.getDistance(DistanceUnit.INCH);
//        distanceLeftValue = distanceLeft.getDistance(DistanceUnit.INCH);


        // The next two lines calculate the needed variables for the distance sensor.
        // If BackWDValueLeft is less than BackWDValueRight, then return BackWDValueLeft.
        // If BackWDValueLeft is greater thank BackWDValueRight, then return BackWDValueRight.
//        double effectiveDistance = distanceLeftValue < distanceRightValue ? distanceLeftValue : distanceRightValue;
//        double DistanceEquationValue = slope * effectiveDistance + intercept;

        // Adjustable variable for sensitivity. The default is 0.5. (half power)
        double sensitivity = 0.5;

        double leftFrontPower = 0;
        double rightFrontPower = 0;
        double leftBackPower = 0;
        double rightBackPower = 0;

        double max;

//        // Ramps down speed as mailbox approaches backstage.
//        if (effectiveDistance <= RampDownStart && effectiveDistance >= RampDownEnd){
//            yaw *= DistanceEquationValue;
//            if (axial < 0) {
//                axial *= DistanceEquationValue;
//            }
//        }
//        if (effectiveDistance < RampDownEnd){
//            yaw *= RampDownSpeed;
//            if ( axial < 0) {
//                axial *= RampDownSpeed;
//            }
//        }

        // This code calculates the power to give to each motor.
        if (Math.abs(axial) > 0.05 || Math.abs(lateral) > 0.05 || Math.abs(yaw) > 0.05) {
            if (directionToggle) {
                leftFrontPower = axial + lateral + yaw;
                rightFrontPower = axial - lateral - yaw;
                leftBackPower = axial - lateral + yaw;
                rightBackPower = axial + lateral - yaw;
            }
            else {
                leftFrontPower = -axial - lateral + yaw;
                rightFrontPower = -axial + lateral - yaw;
                leftBackPower = -axial + lateral + yaw;
                rightBackPower = -axial - lateral - yaw;
            }

        }

        // All code below this comment normalizes the values so no wheel power exceeds 100%.
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max; // leftFrontPower = leftFrontPower / max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        sensitivity = maxSpeed;

        // Calculates power using sensitivity variable.
        leftFrontPower *= sensitivity;
        leftBackPower *= sensitivity;
        rightFrontPower *= sensitivity;
        rightBackPower *= sensitivity;

        // The next few lines make the direction boolean switch when the button is pressed.
        // It includes a timer to avoid mistakes.
        if (time.time() > .25 && !directionToggle && directionButton) {
            directionToggle = true;
            time.reset();
        }
        else if (time.time() > .25 && directionToggle && directionButton) {
            directionToggle = false;
            time.reset();
        }

        // The next four lines gives the calculated power to each motor.

            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);

    }

    public void adjustMaxSpeed (boolean faster, boolean slower) {
        if (faster == true && slower == true) {

        }
        else if (faster == true) {
            maxSpeed = maxSpeed + 0.2;
            if (maxSpeed > 1) {
                maxSpeed = 1;
            }
        }
        else if (slower == true) {
            maxSpeed = maxSpeed - 0.2;
            if (maxSpeed < 0) {
                maxSpeed = 0;
            }
        }
    }
    public void driveAutonomously(double axial, double lateral, double yaw) {

        double leftFrontPower = 0;
        double rightFrontPower = 0;
        double leftBackPower = 0;
        double rightBackPower = 0;

        double max;

        // This code calculates the power to give to each motor.
        leftFrontPower = axial + lateral + yaw;
        rightFrontPower = axial - lateral - yaw;
        leftBackPower = axial - lateral + yaw;
        rightBackPower = axial + lateral - yaw;

        // All code below this comment normalizes the values so no wheel power exceeds 100%.
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // The next four lines gives the calculated power to each motor.
        leftFrontDrive.setPower(leftFrontPower/2);
        rightFrontDrive.setPower(rightFrontPower/2);
        leftBackDrive.setPower(leftBackPower/2);
        rightBackDrive.setPower(rightBackPower/2);
    }

    public void stop() { // Makes the robot stop whenever this function is called
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void moveForward(int howMuch, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.
        if (howMuch < 0) {
            howMuch = Math.abs(howMuch);
        }

        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        lfPos += (int) (howMuch * clicksPerInch);
        rfPos += (int) (howMuch * clicksPerInch);
        lrPos += (int) (howMuch * clicksPerInch);
        rrPos += (int) (howMuch * clicksPerInch);


        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {

        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void moveBackward(int howMuch, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.
        if (howMuch > 0) {
            howMuch *= -1;
        }

        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        lfPos += (int) (howMuch * clicksPerInch);
        rfPos += (int) (howMuch * clicksPerInch);
        lrPos += (int) (howMuch * clicksPerInch);
        rrPos += (int) (howMuch * clicksPerInch);

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {

        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void strafe(int howMuch, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.

        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        lfPos += (int) (howMuch * clicksPerInch);
        rfPos -= (int) (howMuch * clicksPerInch);
        lrPos -= (int) (howMuch * clicksPerInch);
        rrPos += (int) (howMuch * clicksPerInch);

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void strafeLeft(int howMuch, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.
        if (howMuch > 0) {
            howMuch *= -1;
        }
        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        lfPos += (int) (howMuch * clicksPerInch);
        rfPos -= (int) (howMuch * clicksPerInch);
        lrPos -= (int) (howMuch * clicksPerInch);
        rrPos += (int) (howMuch * clicksPerInch);

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void strafeRight(int howMuch, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.
        if (howMuch < 0) {
            howMuch = Math.abs(howMuch);
        }
        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        lfPos += (int) (howMuch * clicksPerInch);
        rfPos -= (int) (howMuch * clicksPerInch);
        lrPos -= (int) (howMuch * clicksPerInch);
        rrPos += (int) (howMuch * clicksPerInch);

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void moveWholeBlock(String direction, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.

        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        if (direction == "forward") {
            lfPos += (int) (24 * clicksPerInch);
            rfPos += (int) (24 * clicksPerInch);
            lrPos += (int) (24 * clicksPerInch);
            rrPos += (int) (24 * clicksPerInch);
        } else if (direction == "backward") {
            lfPos += (int) (-24 * clicksPerInch);
            rfPos += (int) (-24 * clicksPerInch);
            lrPos += (int) (-24 * clicksPerInch);
            rrPos += (int) (-24 * clicksPerInch);
        }

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void moveHalfBlock(String direction, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.

        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        if (direction == "forward") {
            lfPos += (int) (12 * clicksPerInch);
            rfPos += (int) (12 * clicksPerInch);
            lrPos += (int) (12 * clicksPerInch);
            rrPos += (int) (12 * clicksPerInch);
        } else if (direction == "backward") {
            lfPos += (int) (-12 * clicksPerInch);
            rfPos += (int) (-12 * clicksPerInch);
            lrPos += (int) (-12 * clicksPerInch);
            rrPos += (int) (-12 * clicksPerInch);
        }

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void strafeWholeBlock(String direction, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.

        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        if (direction == "forward") {
            lfPos += (int) (24 * clicksPerInch);
            rfPos -= (int) (24 * clicksPerInch);
            lrPos -= (int) (24 * clicksPerInch);
            rrPos += (int) (24 * clicksPerInch);
        } else if (direction == "backward") {
            lfPos += (int) (-24 * clicksPerInch);
            rfPos -= (int) (-24 * clicksPerInch);
            lrPos -= (int) (-24 * clicksPerInch);
            rrPos += (int) (-24 * clicksPerInch);
        }

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void strafeHalfBlock(String direction, double speed) {
        // "howMuch" is in inches. A negative howMuch moves backward.

        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        if (direction == "forward") {
            lfPos += (int) (12 * clicksPerInch);
            rfPos -= (int) (12 * clicksPerInch);
            lrPos -= (int) (12 * clicksPerInch);
            rrPos += (int) (12 * clicksPerInch);
        } else if (direction == "backward") {
            lfPos += (int) (-12 * clicksPerInch);
            rfPos -= (int) (-12 * clicksPerInch);
            lrPos -= (int) (-12 * clicksPerInch);
            rrPos += (int) (-12 * clicksPerInch);
        }

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);

        // Set the drive Drive run modes to prepare for move to encoder:
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    public void turnClockwise(int whatAngle, double speed) {
        // "whatAngle" is in degrees. A negative whatAngle turns counterclockwise.
        if (whatAngle < 0) {
            whatAngle = Math.abs(whatAngle);
        }

        // Fetch motor positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        lfPos += whatAngle * clicksPerDeg;
        rfPos -= whatAngle * clicksPerDeg;
        lrPos += whatAngle * clicksPerDeg;
        rrPos -= whatAngle * clicksPerDeg;

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);
        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }
    }

    public void turnCounterClockwise(int whatAngle, double speed) {
        // "whatAngle" is in degrees. A negative whatAngle turns counterclockwise.
        if (whatAngle > 0) {
            whatAngle *= -1;
        }
        // Fetch motor positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Calculate new targets based on input:
        lfPos += whatAngle * clicksPerDeg;
        rfPos -= whatAngle * clicksPerDeg;
        lrPos += whatAngle * clicksPerDeg;
        rrPos -= whatAngle * clicksPerDeg;

        // Move robot to new position:
        leftFrontDrive.setTargetPosition(lfPos);
        rightFrontDrive.setTargetPosition(rfPos);
        leftBackDrive.setTargetPosition(lrPos);
        rightBackDrive.setTargetPosition(rrPos);
        leftFrontDrive.setPower(speed);
        rightFrontDrive.setPower(speed);
        leftBackDrive.setPower(speed);
        rightBackDrive.setPower(speed);

        // Wait for move to complete:
        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
        }
    }
//    public double getDistanceRightValue(){
//        return distanceRightValue;
//    }
//    public double getDistanceLeftValue(){
//        return distanceLeftValue;
//    }
    public boolean getDriveDirection() {
        return directionToggle;
    }
    public void rightPos() {
        moveBackward(25, 0.5);
        Wait(.5);
        turnClockwise(-92, 0.5);
        Wait(.5);
        moveBackward(2, 0.5);
        Wait(.5);
        turnCounterClockwise(20,0.5);
        Wait(.5);
        moveBackward(2, 0.5);
        Wait(.5);
    }
    public void rightBlueFarRedClose() {
        moveBackward(21, 0.5);
        Wait(.5);
        strafeLeft(11,0.5);
        Wait(.5);
//        moveBackward(3, 0.5);
//        Wait(.5);
    }
    public void centerPos() {
        moveBackward(15, 0.5);
        Wait(.5);
        strafeRight(13,0.5);
        Wait(.5);
        moveBackward(21, 0.5);
        Wait(.5);
        turnClockwise(-90, 0.5);
        Wait(.5);
        moveBackward(3, 0.5);
        Wait(.5);
    }
    public void centerBlueFarRedClose() {
        moveBackward(15, 0.5);
        Wait(.5);
        strafeLeft(13,0.5);
        Wait(.5);
        moveBackward(21, 0.5);
        Wait(.5);
        turnCounterClockwise(-90, 0.5);
        Wait(.5);
        moveBackward(3, 0.5);
        Wait(.5);
    }

    public void leftPos() {
        moveBackward(25, 0.5);
        Wait(.5);
        turnCounterClockwise(-85, 0.5);
        Wait(.5);
        moveBackward(2, 0.5);
        Wait(.5);
        turnClockwise(20,0.5);
        Wait(.5);
        moveBackward(1, 0.5);
        Wait(.5);
    }

    public void leftBlueFarRedClose() {
        moveBackward(25, 0.5);
        Wait(.5);
        turnCounterClockwise(-92, 0.5);
        Wait(.5);
        moveBackward(2, 0.5);
        Wait(.5);
        turnClockwise(20,0.5);
        Wait(.5);
        moveBackward(2, 0.5);
        Wait(.1);
    }
    public void frontToBackBlueRight() {
        turnClockwise(45, 0.5);
        Wait(.1);
        strafeRight(10, 0.5);
        Wait(.1);
        moveBackward(36, 0.5);
        Wait(.1);
    }
    public void frontToBackBlueCenter() {
        turnClockwise(45, 0.5);
        Wait(.1);
        strafeRight(10, 0.5);
        Wait(.1);
        moveBackward(36, 0.5);
        Wait(.1);
    }
    public void frontToBackRedLeft() {
        turnClockwise(45, 0.5);
        Wait(.1);
        strafeLeft(10,0.5);
        Wait(.1);
        moveBackward(36, 0.5);
        Wait(.1);
    }
    public void frontToBackRedCenter() {
        turnClockwise(45, 0.5);
        Wait(.1);
        strafeLeft(10, 0.5);
        Wait(.1);
        moveBackward(36, 0.5);
        Wait(.1);
    }
    public void Wait(double seconds) {
        runtime.reset();
        while (runtime.time() < seconds) {
            // this statement is supposed to be empty.
        }
    }
    public double GitClassSpeed () {
        return maxSpeed;
    }
}