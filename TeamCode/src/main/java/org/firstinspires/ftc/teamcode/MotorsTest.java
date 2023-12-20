package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="Motors Test", group="Linear OpMode")
public class MotorsTest extends LinearOpMode {

    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    @Override
    public void runOpMode() {
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "leftBack");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBack");

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Left Front Encoders", leftFrontDrive.getCurrentPosition());
        telemetry.addData("Left Back Encoders", leftBackDrive.getCurrentPosition());
        telemetry.addData("Right Front Encoders", rightFrontDrive.getCurrentPosition());
        telemetry.addData("Right Back Encoders", rightBackDrive.getCurrentPosition());
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {

            if (gamepad1.dpad_up) {
                leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }

            double leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            double leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            double rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            double rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad

            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);

            telemetry.addData("To Reset Encoders", "Press up on dpad.");
            telemetry.addData("Left Front (X)", leftFrontDrive.getPower());
            telemetry.addData("Left Front Encoders", leftFrontDrive.getCurrentPosition());
            telemetry.addData("Left Back (A)", leftBackDrive.getPower());
            telemetry.addData("Left Back Encoders", leftBackDrive.getCurrentPosition());
            telemetry.addData("Right Front (Y)", rightFrontDrive.getPower());
            telemetry.addData("Right Front Encoders", rightFrontDrive.getCurrentPosition());
            telemetry.addData("Right Back (B)", rightBackDrive.getPower());
            telemetry.addData("Right Back Encoders", rightBackDrive.getCurrentPosition());
            telemetry.update();
        }
    }
}
