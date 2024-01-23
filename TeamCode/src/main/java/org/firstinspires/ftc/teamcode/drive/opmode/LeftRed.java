package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

/*
 * This is an example of a more complex path to really test the tuning.
 */
@Autonomous(group = "drive")
public class LeftRed extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(11.5,0,Math.toRadians(90));

        drive.setPoseEstimate(startPose);

        Trajectory myTrajectory1 = drive.trajectoryBuilder(new Pose2d(-36.00, -71.71, Math.toRadians(90.00)))
                .splineTo(new Vector2d(-36.00, -60.00), Math.toRadians(91.77))
                .splineTo(new Vector2d(0.00, -36.00), Math.toRadians(-3.21))
                .splineTo(new Vector2d(54.00, -36.00), Math.toRadians(-0.02))
                .build();




        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectory(myTrajectory1);



    }
}
