package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

// This file is the main TeleOp file.

@TeleOp(name = "Centerstage TeleOp", group = "LinearOpMode")
public class Centerstage_TeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {


        // Initiates the robots system and subsystems!
        Gobbler gobbler = new Gobbler(hardwareMap);

        ElapsedTime intakeToggleTime = new ElapsedTime();
        ElapsedTime droneToggleTime = new ElapsedTime();
        ElapsedTime trapToggleTime = new ElapsedTime();
        ElapsedTime directionToggleTime = new ElapsedTime();
        ElapsedTime hangServoTime = new ElapsedTime();
        ElapsedTime intakeDirectionToggle = new ElapsedTime();

        gobbler.outtake.closeMailbox();
        gobbler.planeHang.initServo();

        telemetry.addData("Status", "Waiting for Start");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            // This function controls the trapdoor.
            // The first input is the button used to control the trap door.
            // The second input is the time the function uses to space out inputs.
            gobbler.outtake.trapdoor(gamepad1.x, trapToggleTime);

            // This function controls the drone.
            // The first input is the button used to control the drone.
            // The second input is the time the function uses to space out inputs.
            gobbler.planeHang.launchDrone(gamepad1.y, droneToggleTime);

            // This function controls the hanging motors.
            // The first input is the button used to power the motors.
            // The second input is the button used to reverse the motors.
            gobbler.planeHang.hangMotors(gamepad1.dpad_up, gamepad1.dpad_down);

            // This function controls the hanging servo.
            // The first input is the button used to control the servo.
            // The second input is the time the function uses to space out inputs.
            gobbler.planeHang.hangServo(gamepad1.b, hangServoTime);

            // This function controls the intake and conveyor.
            // The first input is the button used to control the trap door.
            // The second input is the time the function uses to space out inputs.
            gobbler.intake.reverseIntake(gamepad1.right_stick_button, intakeDirectionToggle);
            gobbler.intake.driveIntake(gamepad1.a, intakeToggleTime);

            // This adjusts the speed of the robot
            gobbler.driveTrain.adjustMaxSpeed(gamepad1.dpad_right, gamepad1.dpad_left);

            // This controls the drive train using three double input methods.
            // The fourth input is a boolean for the direction toggle.
            // The last input is the time the function uses to space out inputs for the direction switch.
            gobbler.driveTrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.left_stick_button, directionToggleTime);

            // This functions uses one double input to drive the lift.
            gobbler.outtake.driveLift(gamepad1.left_trigger, gamepad1.right_trigger);

            // Provides telemetry for all motors, servos, and sensors.
            telemetry.addData("Front Driving Motors (Left, Right)", "%4.2f, %4.2f",
                    gobbler.driveTrain.leftFrontDrive.getPower(),
                    gobbler.driveTrain.rightFrontDrive.getPower());
            telemetry.addData("Back Driving Motors (Left, Right)", "%4.2f, %4.2f",
                    gobbler.driveTrain.leftBackDrive.getPower(),
                    gobbler.driveTrain.rightBackDrive.getPower());
            telemetry.addData("Front Driving Motor Encoders (Left, Right)", "%d, %d",
                    gobbler.driveTrain.leftFrontDrive.getCurrentPosition(),
                    gobbler.driveTrain.rightFrontDrive.getCurrentPosition());
            telemetry.addData("Back Driving Motor Encoders (Left, Right)", "%d, %d",
                    gobbler.driveTrain.leftBackDrive.getCurrentPosition(),
                    gobbler.driveTrain.rightBackDrive.getCurrentPosition());
            telemetry.addData("Intake Motor Power/Encoder",
                   gobbler.intake.intakeMotor.getPower());
            telemetry.addData("Lift Motor Power", "%4.2f, %d",
                    gobbler.outtake.liftMotor.getPower(),
                    gobbler.outtake.getLiftMotorPos());
            telemetry.addData("Bottom Limit Status",
                    gobbler.outtake.getBottomLimitStatus());
            telemetry.addData("Mailbox Status",
                   gobbler.outtake.getMailboxStatus());
            telemetry.addData("Drone Status",
                    String.valueOf(gobbler.planeHang.droneToggle));
//            telemetry.addData("Distance Sensors (Left, Right)", "%4.2f, %4.2f",
//                    gobbler.driveTrain.getDistanceLeftValue(),
//                    gobbler.driveTrain.getDistanceRightValue());
            telemetry.update();
        }
    }
}