package org.firstinspires.ftc.teamcode;


import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "desperation")
public class desperation extends OpMode {


    DcMotorEx left1;
    DcMotorEx left2;
    DcMotorEx right1;
    DcMotorEx right2;


    ServoEx flap;

    DcMotorEx outtakeR;

    DcMotorEx outtakeL;

    // IMU for field-oriented control
    IMU imu;
    double headingOffset = 0; // Stores the heading when field orientation is reset

    // Optional: REV Through Bore Encoders for absolute angle sensing
    // Using DcMotor just for reading encoder position (not for motor control)
    DcMotor leftEncoder;
    DcMotor rightEncoder;

    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;


    @Override
    public void init() {



        flap = new SimpleServo(hardwareMap, "flap", -30, 30);
        // Initialize motors
        left1 = hardwareMap.get(DcMotorEx.class, "left1");
        left2 = hardwareMap.get(DcMotorEx.class, "left2");
        right1 = hardwareMap.get(DcMotorEx.class, "right1");
        right2 = hardwareMap.get(DcMotorEx.class, "right2");


        // Initialize IMU for field-oriented control
        imu = hardwareMap.get(IMU.class, "imu");

        // Define how the Control Hub is mounted on the robot
        // Adjust these parameters based on your hub orientation
        IMU.Parameters imuParameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.UP));
        imu.initialize(imuParameters);
        imu.resetYaw(); // Set current heading as 0

        // Initialize through bore encoders only if using them


        // Set motor directions
        left1.setDirection(DcMotorEx.Direction.REVERSE);
        left2.setDirection(DcMotorEx.Direction.REVERSE);
        right1.setDirection(DcMotorEx.Direction.REVERSE);
        right2.setDirection(DcMotorEx.Direction.REVERSE);

        // Reset encoders
        left1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set to run using encoder
        left1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        left2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set zero power behavior
        left1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        telemetry.addData("Status", "Initialized");
        telemetry.addData("Drive Mode", "FIELD-ORIENTED");
        telemetry.addData("", "Press GP1 BACK to reset field orientation");
        telemetry.update();
        //flap.rotateByAngle(5);


    }

    @Override
    public void loop() {
        //outtakeR.setPower(0.8);
        //outtakeL.setPower(-0.8);
        // FIELD-ORIENTED RESET: Press BACK on CONTROLLER 1 to reset field orientation
        if (gamepad1.left_bumper)

            if (gamepad1.back) {
                imu.resetYaw();
                headingOffset = 0;
                try {
                    sleep(200);
                } catch (Exception e) {
                }
            }


        //if (gamepad1.xWasReleased())
        //    flap.rotateByAngle(-30);
        //    try { sleep(200); } catch (Exception e) {}
        //    flap.rotateByAngle(30);

/*        // Toggle calibration mode with X button - CONTROLLER 2
        if (gamepad2.x && !calibrationMode) {
            calibrationMode = true;
            calibrationStartPos1 = left1.getCurrentPosition();
            calibrationStartPos2 = left2.getCurrentPosition();
            calibrationTimer.reset();
            try { Thread.sleep(300); } catch (Exception e) {}
        }
*/
        // AUTO CALIBRATION MODE - spins module and measures gear ratio
/*        if (calibrationMode) {
            double calibrationTime = calibrationTimer.seconds();

            // Spin for 3 seconds, then analyze
            if (calibrationTime < 3.0) {
                // Spin the module by creating differential velocity
                double spinVel = 1500; // Slow spin
                left1.setVelocity(spinVel);
                left2.setVelocity(-spinVel); // Opposite direction = pure rotation
                right1.setVelocity(0);
                right2.setVelocity(0);

                telemetry.addData("=== CALIBRATION ===", "");
                telemetry.addData("Status", "Spinning left module...");
                telemetry.addData("Time", "%.1f / 3.0 seconds", calibrationTime);

            } else {
                // Stop and calculate
                left1.setVelocity(0);
                left2.setVelocity(0);

                int currentPos1 = left1.getCurrentPosition();
                int currentPos2 = left2.getCurrentPosition();
                int deltaPos1 = currentPos1 - calibrationStartPos1;
                int deltaPos2 = currentPos2 - calibrationStartPos2;
                int posDiff = deltaPos1 - deltaPos2;

                // Calculate how many degrees the module actually rotated
                // We know: posDiff = (degrees rotated) * (TICKS_PER_REV / 360) * 2 * GEAR_RATIO
                // So: GEAR_RATIO = posDiff / (2 * TICKS_PER_REV * degrees / 360)

                // Estimate degrees rotated (rough, but gives us the ratio)
                double estimatedDegrees = (posDiff / (2.0 * ANGLE_GEAR_RATIO)) / TICKS_PER_REVOLUTION * 360.0;

                telemetry.addData("=== CALIBRATION RESULTS ===", "");
                telemetry.addData("Motor 1 moved", "%d ticks", deltaPos1);
                telemetry.addData("Motor 2 moved", "%d ticks", deltaPos2);
                telemetry.addData("Difference", "%d ticks", posDiff);
                telemetry.addLine();
                telemetry.addData("Est. rotation", "%.1f degrees", estimatedDegrees);
                telemetry.addData("Current ratio", "%.4f", ANGLE_GEAR_RATIO);
                telemetry.addLine();

                // If the module spun way more or less than expected, calculate correction
                if (Math.abs(estimatedDegrees) > 10) {
                    double expectedPosDiff = posDiff; // What we measured
                    double actualDegrees = estimatedDegrees; // What the ratio calculated
                    // We want actualDegrees to equal what really happened
                    // Adjust ratio proportionally
                    double ratioCorrection = Math.abs(posDiff) / (2.0 * TICKS_PER_REVOLUTION);

                    telemetry.addData("TRY THIS RATIO", "%.4f", ratioCorrection);
                    telemetry.addData("", "(Use D-pad ← → to adjust)");
                }

                telemetry.addLine();
                telemetry.addData("Press B", "to exit calibration (GP2)");

                if (gamepad2.b) {
                    calibrationMode = false;
                    try { Thread.sleep(300); } catch (Exception e) {}
                }
            }

            telemetry.update();
            return;
        }
*/

        /*
         * MICHI NOTE:
         * this is where the code will check gamepad input
         */


        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;

        double turn = gamepad1.right_stick_x;

        double theta = Math.atan2(y,x);
        double  power = Math.hypot(x,y);

        double sin = Math.sin(theta - Math.PI/4);
        double cos = Math.cos(theta - Math.PI/4);
        double max = Math.max(Math.abs(sin), Math.abs(cos));

        double frontLeftPower = power * cos/max + turn;
        double frontRightPower = power * sin/max - turn;
        double backLeftPower = power* sin/max + turn;
        double backRightPower = power * cos/max - turn;

        if ((power + Math.abs(turn)) >1 ){
            frontLeftPower /= power + turn;
            frontRightPower /= power + turn;
            backLeftPower /= power + turn;
            backRightPower /= power + turn;

        }

        left1.setPower(frontLeftPower);
        left2.setPower(backLeftPower);
        right1.setPower(frontRightPower);
        right2.setPower(backRightPower);

    }
}
