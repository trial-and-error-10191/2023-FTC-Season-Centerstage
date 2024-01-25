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
public class MoveForward extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(11.5,0,Math.toRadians(90));

        drive.setPoseEstimate(startPose);

        Trajectory myTrajectory1 = drive.trajectoryBuilder(new Pose2d(-36.00, 72.00, Math.toRadians(270)))
                .forward(10)
                .build();
        Trajectory traj2 = drive.trajectoryBuilder(myTrajectory1.end())
                .strafeLeft(10)
                .build();




        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectory(myTrajectory1);
        drive.followTrajectory(traj2);



    }
}
