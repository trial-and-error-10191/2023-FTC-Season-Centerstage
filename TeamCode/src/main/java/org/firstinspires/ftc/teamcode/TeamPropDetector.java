package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

public class TeamPropDetector {

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    private static final String TFOD_MODEL_ASSET = "centerstage_bluemayhem.tflite";
    private static final String[] LABELS = {
            "Blue Mayhem",
    };
    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private final TfodProcessor tfod;
    boolean seen = false;
    int borderLine = 450;
    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal = null;

    Telemetry telemetry = null;

    TeamPropDetector(HardwareMap hardwareMap, Telemetry telemetry) {
        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                // choose one of the following:
                //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                .setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                .setModelLabels(LABELS)
                //.setIsModelTensorFlow2(true)
                //.setIsModelQuantized(true)
                //.setModelInputSize(300)
                //.setModelAspectRatio(16.0 / 9.0
                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam).
        builder.setCamera(hardwareMap.get(WebcamName.class, "Cam1"));

        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        //builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        //tfod.setMinResultConfidence(0.75f);

        // Disable or re-enable the TFOD processor at any time.
        //visionPortal.setProcessorEnabled(tfod, true);

        this.telemetry = telemetry;
    }

    /**
     *
     * @return - returns an int to signal where the team prop is spotted
     * Personally, I would like to return something different (like an enumeration), but an int can suffice for now
     */
    public int identifyTeamProp() {
        seen = false; // setting it to false again so that the robot will correctly detect Mayhem on the left piece of tape
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        int location = -1;
        for (Recognition recognition : currentRecognitions) {
            double xValue = (recognition.getLeft() + recognition.getRight()) / 2;
            // To figure out this part, you will have to use the ConceptTensorFlowObjectDetection file
            // The first two x values represent the minimum and maximum value x has to be for the team prop to be considered center.
            // The second two y values represent the minimum and maximum value x has to be for the team prop to be considered center.
            if (xValue < borderLine) {
                // center
                location = 0;
                seen = true;
            }

            // The first two x values represent the minimum and maximum value x has to be for the team prop to be considered right.
            // The second two y values represent the minimum and maximum value x has to be for the team prop to be considered right.
            else if (xValue > borderLine) {  //
                // right
                location = 2;
                seen = true;

            }
        }
        // If the team prop is not seen on the center or right, it will assume it is on the left.
        if (!seen) {
            location = 3;
        }
        return location;
    }
}
