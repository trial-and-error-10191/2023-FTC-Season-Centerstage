// This file is a system file.
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.TelemetryMessage;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Gobbler {
    public DriveTrain driveTrain;
    public Intake intake;
    public Outtake outtake;
    public PlaneHang planeHang;
    private TeamPropDetector teamPropDetector = null;

    Telemetry telemetry = null;

    // This combines all the subsystems.
    public Gobbler(HardwareMap hwMap, Telemetry telemetry) {
        driveTrain = new DriveTrain(hwMap, telemetry);
        intake = new Intake(hwMap);
        outtake = new Outtake(hwMap);
        planeHang = new PlaneHang(hwMap);
        teamPropDetector = new TeamPropDetector(hwMap, telemetry);
        this.telemetry = telemetry;
    }

    public void driveToCenterPos() {
        moveBackward(24, 0.5);
        Wait(.5);
    }

    public void driveToRightPos() {
        moveBackward(24, 0.5);
        Wait(.5);
        turnCounterClockwise(92, 0.5);
        Wait(.5);
    }

    public void driveToLeftPos() {
        moveBackward(24, 0.5);
        Wait(.5);
        turnClockwise(92, 0.5);
        Wait(.5);
    }

    private void moveForward(double distance, double speed) {
        driveTrain.driveByEncoder(Math.abs(distance), 0.0, 0.0, speed);
    }

    private void moveBackward(double distance, double speed) {
        driveTrain.driveByEncoder(-Math.abs(distance), 0.0, 0.0, speed);
    }

    private void turnClockwise(double distance, double  speed) {
        driveTrain.driveByEncoder(0.0, 0.0, Math.abs(distance), speed);
    }

    private void turnCounterClockwise(double distance, double  speed) {
        driveTrain.driveByEncoder(0.0, 0.0, -Math.abs(distance), speed);
    }

    /**
     *
     * @param waitTime how many seconds you want the function to wait before moving ot the next line of code.
     */
    private void Wait(double waitTime) {
        ElapsedTime waitTimer = new ElapsedTime();
        waitTimer.reset();
        while (waitTimer.seconds() < waitTime) {
            // Doing nothing for the specified amount of time
        }
    }
} // end class