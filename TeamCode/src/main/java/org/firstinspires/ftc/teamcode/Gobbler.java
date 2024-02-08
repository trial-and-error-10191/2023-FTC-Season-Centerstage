// This file is a system file.
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Gobbler {
    public DriveTrain driveTrain;
    public Intake intake;
    public Outtake outtake;
    public PlaneHang planeHang;

    // This combines all the subsystems.
    public Gobbler(HardwareMap hwMap) {
        driveTrain = new DriveTrain(hwMap);
        intake = new Intake(hwMap);
        outtake = new Outtake(hwMap);
        planeHang = new PlaneHang(hwMap);
    }

    public void driveToCenterPos() {
        moveForward(24, 0.5);
        Wait(.5);
    }

    private void moveForward(double distance, double speed) {
        driveTrain.driveByEncoder(Math.abs(distance), 0.0, 0.0, speed);
    }

    private void moveBackward(double distance, double speed) {
        driveTrain.driveByEncoder(-Math.abs(distance), 0.0, 0.0, speed);
    }

    /**
     *
     * @param waitTime
     */
    private void Wait(double waitTime) {
        ElapsedTime waitTimer = new ElapsedTime();
        waitTimer.reset();
        while (waitTimer.seconds() < waitTime) {
            // Doing nothing for the specified amount of time
        }
    }
}