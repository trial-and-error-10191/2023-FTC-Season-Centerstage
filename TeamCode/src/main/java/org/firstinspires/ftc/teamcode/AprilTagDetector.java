package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class AprilTagDetector {
    private AprilTagProcessor aprilTag;

    private AprilTagDetection desiredTag = null;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    private static final int DESIRED_TAG_ID = 5;

    private Telemetry telemetry = null;

    AprilTagDetector(HardwareMap hardwareMap, Telemetry telemetry) {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder()
                // TODO: 1/30/24 add the bounding boxes and axis
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagID(true)
                .build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Cam1"))
                .addProcessor(aprilTag)
                .build();

        this.telemetry = telemetry;
    }

    /**
     * Search for a specified april tag based on location of team prop
     */
    public void LocateTargetAprilTag() {

    }

    /**
     *
     * @return - want to return the april tag we see, with the idea to extract location information
     */
    public AprilTagDetection getTargetAprilTag() {
        return null;
    }
}
