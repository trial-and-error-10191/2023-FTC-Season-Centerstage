// This is the drive train subsystem file.
// All drive train stuff should be found here.

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/** @noinspection ALL*/
public class DriveTrain {

    DcMotor leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive;

    private int lfPos, rfPos, lrPos, rrPos;

    static final double COUNTS_PER_MOTOR_REV = 537.7;
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // No External Gearing.
    static final double WHEEL_DIAMETER_INCHES = 3.78;     // For figuring circumference
    static final double clicksPerInch = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    private final double clicksPerDeg = clicksPerInch / 4.99; // empirically measured
    private ElapsedTime runtime = new ElapsedTime();


    private DistanceSensor BackWDRight;
    private DistanceSensor BackWDLeft;
    private double BackWDValueRight;
    private double BackWDValueLeft;

    private double RampDownStart = 16;
    private double RampDownEnd = 8;
    private double RampDownSpeed = 0.2;
    private double slope = (1 - RampDownSpeed) / (RampDownStart - RampDownEnd);
    private double intercept = 1 - slope * RampDownStart ;

    Telemetry telemetry;

    // All subsystems should have a hardware function that labels all of the hardware required of it.
    public DriveTrain(HardwareMap hwMap, Telemetry telemetry) {

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

        this.telemetry = telemetry;

        BackWDRight = hwMap.get(DistanceSensor.class, "sensor_distance_BackWDRight");
        BackWDLeft = hwMap.get(DistanceSensor.class, "sensor_distance_BackWDLeft");

        runtime.reset();
    }


    /**
     *
     * @param axial - power to move robot forwards/backwards (positive power -> moving forward)
     * @param lateral - power to move robot left/right (positive power -> moving right)
     * @param yaw - power to rotate robot (positive power -> rotate clockwise)
     */
    public void driveByPower(double axial, double lateral, double yaw) {
        double currentTime = runtime.milliseconds();
        telemetry.addData("Entering driveByPower function at", "%4.2f", currentTime);
        BackWDValueRight = BackWDRight.getDistance(DistanceUnit.INCH);
        BackWDValueLeft = BackWDLeft.getDistance(DistanceUnit.INCH);

//        double effectiveDistance = BackWDValueLeft < BackWDValueRight ? BackWDValueLeft : BackWDValueRight;
//        double DistanceEquationValue = slope * effectiveDistance + intercept;
//
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


        double leftFrontPower = 0;
        double rightFrontPower = 0;
        double leftBackPower = 0;
        double rightBackPower = 0;

        // I would like to deal with all of this modifications to the input power at a higher level
        // Want to avoid the case of drift due to drive sticks not resting completely at center.
        double deadZone = 0.05;
        // This code calculates the power to give to each motor.
        if (Math.abs(axial) > deadZone || Math.abs(lateral) > deadZone || Math.abs(yaw) > deadZone) {
            leftFrontPower = axial + lateral + yaw;
            rightFrontPower = axial - lateral - yaw;
            leftBackPower = axial - lateral + yaw;
            rightBackPower = axial + lateral - yaw;
        }
        telemetry.addData("Time to do deadzone calculations", "%4.2f", runtime.milliseconds() - currentTime);
        currentTime = runtime.milliseconds();

        // All code below this comment normalizes the values so no wheel power exceeds 100%.
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));
        if (max > 1.0) {
            leftFrontPower /= max; // leftFrontPower = leftFrontPower / max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }
        telemetry.addData("Time to normalize motor power", "%4.2f", runtime.milliseconds() - currentTime);
        currentTime = runtime.milliseconds();

        // Adjustable variable for sensitivity. The default is 0.5. (half power)
        double sensitivity = 0.5;
        // Calculates power using sensitivity variable.
//        leftFrontPower *= sensitivity;
//        leftBackPower *= sensitivity;
//        rightFrontPower *= sensitivity;
//        rightBackPower *= sensitivity;

        // Let's try to deal with switching driving controls at a higher level

//        // The next few lines make the direction boolean switch when the button is pressed.
//        // It includes a timer to avoid mistakes.
//        if (time.time() > .25 && !directionToggle && directionButton) {
//            directionToggle = true;
//            time.reset();
//        }
//        else if (time.time() > .25 && directionToggle && directionButton) {
//            directionToggle = false;
//            time.reset();
//        }

        // The next eleven lines gives the calculated power to each motor.
//        if (directionToggle) {
//            leftFrontDrive.setPower(leftFrontPower);
//            rightFrontDrive.setPower(rightFrontPower);
//            leftBackDrive.setPower(leftBackPower);
//            rightBackDrive.setPower(rightBackPower);
//        }
//
//        else {
//            leftFrontDrive.setPower(-leftFrontPower);
//            rightFrontDrive.setPower(-rightFrontPower);
//            leftBackDrive.setPower(-leftBackPower);
//            rightBackDrive.setPower(-rightBackPower);
//        }

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

        telemetry.addData("Time to apply motor power", "%4.2f", runtime.milliseconds() - currentTime);
        telemetry.update();
    }

    /**
     *
     * @param axialDistance - distance in inches to move the robot forwards/backwards (positive value -> moving forward)
     * @param lateralDistance - distance in inches to move the robot left/right (positive value -> moving right)
     * @param rotationAmount - amount in degrees to rotate robot (positive value -> clockwise rotation)
     */
    public void driveByEncoder(double axialDistance, double lateralDistance, double rotationAmount, double speed) {
        // Fetch Drive positions:
        lfPos = leftFrontDrive.getCurrentPosition();
        rfPos = rightFrontDrive.getCurrentPosition();
        lrPos = leftBackDrive.getCurrentPosition();
        rrPos = rightBackDrive.getCurrentPosition();

        // Sign is determined by mechanics of Mecanum drive train
        lfPos += encoderCountsFromLinearDistance(axialDistance);
        rfPos += encoderCountsFromLinearDistance(axialDistance);
        lrPos += encoderCountsFromLinearDistance(axialDistance);
        rrPos += encoderCountsFromLinearDistance(axialDistance);

        lfPos += encoderCountsFromLinearDistance(lateralDistance);
        rfPos -= encoderCountsFromLinearDistance(lateralDistance);
        lrPos -= encoderCountsFromLinearDistance(lateralDistance);
        rrPos += encoderCountsFromLinearDistance(lateralDistance);

        lfPos += encoderCountsFromAngularDistance(rotationAmount);
        rfPos -= encoderCountsFromAngularDistance(rotationAmount);
        lrPos += encoderCountsFromAngularDistance(rotationAmount);
        rrPos -= encoderCountsFromAngularDistance(rotationAmount);

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

        while (leftFrontDrive.isBusy() && rightFrontDrive.isBusy() &&
                leftBackDrive.isBusy() && rightBackDrive.isBusy()) {
            // Waiting for movement to complete
        }

        // Stop all motion:
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    private int encoderCountsFromAngularDistance(double rotationAmount) {
        return ((int)(rotationAmount * clicksPerDeg));
    }

    private int encoderCountsFromLinearDistance(double distance) {
        return ((int)(distance * clicksPerInch));
    }

    public double getBackWDValueRight(){
        return BackWDValueRight;
    }
    public double getBackWDValueLeft(){
        return BackWDValueLeft;
    }
//    public boolean getDriveDirection() {
//        return directionToggle;
//    }

    // Let's move these to the robot level.
//    public void rightPos() {
//        moveForward(24, 0.5);
//        Wait(.5);
//        turnClockwise(92, 0.5);
//        Wait(.5);
//
//    }
//    public void centerPos() {
//        moveForward(24, 0.5);
//        Wait(.5);
//
//    }
//    public void leftPos() {
//        moveForward(24, 0.5);
//        Wait(.5);
//        turnClockwise(-92, 0.5);
//        Wait(.5);
//    }

    // Let's put this at the robot level.
//    public void Wait(double seconds) {
//        runtime.reset();
//        while (runtime.time() < seconds) {
//            // this statement is supposed to be empty.
//        }
//    }
} // end class
