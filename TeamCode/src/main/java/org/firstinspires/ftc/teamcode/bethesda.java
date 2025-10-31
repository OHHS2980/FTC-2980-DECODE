package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;


import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@TeleOp(name = "bethesda")
public class bethesda extends OpMode {

    private hardware robot = new hardware();






    // Calibration mode
/*    boolean calibrationMode = false;
    int calibrationStartPos1 = 0;
    int calibrationStartPos2 = 0;
    ElapsedTime calibrationTimer = new ElapsedTime();
*/
    @Override
    public void init() {

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Left Encoder", robot.USE_LEFT_THROUGH_BORE ? "Through Bore" : "Motor Diff");
        telemetry.addData("Right Encoder", robot.USE_RIGHT_THROUGH_BORE ? "Through Bore" : "Motor Diff");
        telemetry.addData("Drive Mode", "FIELD-ORIENTED");
        telemetry.addData("", "Press GP1 BACK to reset field orientation");
        telemetry.update();
        //flap.rotateByAngle(5);

    }

    @Override
    public void loop() {

        robot.outtakeR.setPower(1);
        robot.outtakeL.setPower(-1);
        // FIELD-ORIENTED RESET: Press BACK on CONTROLLER 1 to reset field orientation
        if (gamepad1.back) {
            robot.imu.resetYaw();
            robot.headingOffset = 0;
            try { sleep(200); } catch (Exception e) {}
        }

        // RECENTER WHEELS: Press BACK button on CONTROLLER 2 to reset module angles to 0
        if (gamepad2.back) {
           robot.encoderReset();
        }

        if (gamepad1.x){
            robot.flapRotate();
        }


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
        // Get joystick input
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double rotation = gamepad1.right_stick_x;

        // Apply deadzone
        if (Math.abs(x) < 0.1) x = 0;
        if (Math.abs(y) < 0.1) y = 0;
        if (Math.abs(rotation) < 0.1) rotation = 0;

        // Get robot heading for field-oriented control
        double robotHeading = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Apply field-oriented transformation
        // Rotate joystick input by robot heading so forward is always "away from driver"
        double fieldX = x * Math.cos(-robotHeading) - y * Math.sin(-robotHeading);
        double fieldY = x * Math.sin(-robotHeading) + y * Math.cos(-robotHeading);

        // Use field-oriented coordinates for movement
        x = fieldX;
        y = fieldY;

        // Calculate target angle and magnitude
        double targetAngleRad = Math.atan2(y, x);
        double targetAngleDeg = Math.toDegrees(targetAngleRad);
        double magnitude = Math.sqrt(x * x + y * y);
        magnitude = Math.min(magnitude, 1.0);

        // Get current module angles
        double leftCurrentAngle = robot.getLeftModuleAngle();
        double rightCurrentAngle = robot.getRightModuleAngle();

        double leftMotor1Vel = 0;
        double leftMotor2Vel = 0;
        double rightMotor1Vel = 0;
        double rightMotor2Vel = 0;

        // TEST MODE: Press A on CONTROLLER 2 for pure angle test
        if (gamepad2.a && magnitude > 0.1) {
            double leftAngleVel = robot.calculatePID(
                    targetAngleDeg,
                    leftCurrentAngle,
                    robot.leftTimer,
                    robot.leftIntegral,
                    robot.leftLastError,
                    true
            );

            double rightAngleVel = robot.calculatePID(
                    targetAngleDeg,
                    rightCurrentAngle,
                    robot.rightTimer,
                    robot.rightIntegral,
                    robot.rightLastError,
                    false
            );

            leftMotor1Vel = leftAngleVel;
            leftMotor2Vel = -leftAngleVel;
            rightMotor1Vel = rightAngleVel;
            rightMotor2Vel = -rightAngleVel;

            telemetry.addData("=== TEST MODE ===", "Pure Angle Control");
            telemetry.addData("Target", "%.1f°", targetAngleDeg);
            telemetry.addData("Current L", "%.1f°", leftCurrentAngle);
            telemetry.addData("Error L", "%.1f°", robot.leftLastError);
            telemetry.addData("PID Output L", "%.0f", leftAngleVel);
            telemetry.addLine();
            telemetry.addData("Encoder Diff L", "%d",
                    robot.left1.getCurrentPosition() - robot.left2.getCurrentPosition());
            telemetry.addData("Angle calc", "(diff)/(2*%.2f) = %.1f ticks",
                    robot.ANGLE_GEAR_RATIO,
                    (robot.left1.getCurrentPosition() - robot.left2.getCurrentPosition()) / (2.0 * robot.ANGLE_GEAR_RATIO));
            telemetry.addData("Then * 360/8192", "= %.1f°", leftCurrentAngle);

        } else if (magnitude > 0.1) {
            // Normal operation with optimization
            OptimizedModuleState leftState = optimizeModuleAngle(
                    targetAngleDeg, leftCurrentAngle, magnitude);
            OptimizedModuleState rightState = optimizeModuleAngle(
                    targetAngleDeg, rightCurrentAngle, magnitude);

            double leftAngleVel =robot. calculatePID(
                    leftState.targetAngle,
                    leftCurrentAngle,
                    robot.leftTimer,
                    robot.leftIntegral,
                    robot.leftLastError,
                    true
            );

            double rightAngleVel = robot.calculatePID(
                    rightState.targetAngle,
                    rightCurrentAngle,
                    robot.rightTimer,
                    robot.rightIntegral,
                    robot.rightLastError,
                    false
            );

            double rotationScale = 0.5;

            // CRITICAL FIX: Reduce drive velocity when angle error is large
            // This allows the module to steer before driving at full speed
            double leftAngleError = Math.abs(robot.leftLastError);
            double rightAngleError = Math.abs(robot.rightLastError);
            double maxAngleError = Math.max(leftAngleError, rightAngleError);

            // Scale down drive speed if not pointing the right way
            double speedMultiplier = 1.0;
            if (maxAngleError > 45) {
                speedMultiplier = 0.3; // Very slow when far from target
            } else if (maxAngleError > 20) {
                speedMultiplier = 0.6; // Medium speed when getting close
            }
            // else full speed when angle error < 20 degrees

            double leftDriveVel = (leftState.speed + rotation * rotationScale) * robot.MAX_DRIVE_VELOCITY * speedMultiplier;
            double rightDriveVel = (rightState.speed - rotation * rotationScale) * robot.MAX_DRIVE_VELOCITY * speedMultiplier;

            leftMotor1Vel = leftDriveVel + leftAngleVel;
            leftMotor2Vel = leftDriveVel - leftAngleVel;
            rightMotor1Vel = rightDriveVel + rightAngleVel;
            rightMotor2Vel = rightDriveVel - rightAngleVel;

        } else if (Math.abs(rotation) > 0.1) {
            double rotationVel = rotation * robot.MAX_DRIVE_VELOCITY * 0.5;
            leftMotor1Vel = rotationVel;
            leftMotor2Vel = rotationVel;
            rightMotor1Vel = -rotationVel;
            rightMotor2Vel = -rotationVel;

            robot.leftIntegral = 0;
            robot.rightIntegral = 0;
            robot.leftLastError = 0;
            robot.rightLastError = 0;
        } else {
            robot.leftIntegral = 0;
            robot.rightIntegral = 0;
            robot.leftLastError = 0;
            robot.rightLastError = 0;
        }

        // Set motor velocities
        robot. left1.setVelocity(leftMotor1Vel);
        robot.left2.setVelocity(leftMotor2Vel);
        robot.right1.setVelocity(rightMotor1Vel);
        robot. right2.setVelocity(rightMotor2Vel);

        // Telemetry
        telemetry.addData("Mode", "Field-Oriented (GP1 BACK to reset)");
        telemetry.addData("Robot Heading", "%.1f°", Math.toDegrees(robotHeading));
        telemetry.addData("Target", "%.1f°", targetAngleDeg);
        telemetry.addLine();

        // Left module telemetry
        if (!robot.USE_LEFT_THROUGH_BORE) {
            telemetry.addData("Left (Motor Encoders)", "M1: %d | M2: %d | Diff: %d",
                    robot.left1.getCurrentPosition(), robot.left2.getCurrentPosition(),
                    robot.left1.getCurrentPosition() - robot.left2.getCurrentPosition());
        } else {
            telemetry.addData("Left (Through Bore)", "Pos: %d = %.1f°",
                    robot.leftEncoder.getCurrentPosition(), leftCurrentAngle);
        }
        telemetry.addData("Left", "Angle: %.1f° | Error: %.1f°",
                leftCurrentAngle, robot.leftLastError);
        telemetry.addData("Left Vel", "M1: %.0f | M2: %.0f", leftMotor1Vel, leftMotor2Vel);
        telemetry.addLine();

        // Right module telemetry
        if (!robot.USE_RIGHT_THROUGH_BORE) {
            telemetry.addData("Right (Motor Encoders)", "M1: %d | M2: %d | Diff: %d",
                    robot.right1.getCurrentPosition(), robot.right2.getCurrentPosition(),
                    robot.right1.getCurrentPosition() - robot.right2.getCurrentPosition());
        } else {
            telemetry.addData("Right (Through Bore)", "Pos: %d = %.1f°",
                    robot.rightEncoder.getCurrentPosition(), rightCurrentAngle);
        }
        telemetry.addData("Right", "Angle: %.1f° | Error: %.1f°",
                rightCurrentAngle, robot.rightLastError);
        telemetry.addData("Right Vel", "M1: %.0f | M2: %.0f", rightMotor1Vel, rightMotor2Vel);

        // Live tuning
        //if (gamepad1.dpad_up) kP += 1.0;
        //if (gamepad1.dpad_down) kP = Math.max(0, kP - 1.0);
        //if (gamepad1.dpad_right) ANGLE_GEAR_RATIO += 0.05;
        //if (gamepad1.dpad_left) ANGLE_GEAR_RATIO = Math.max(0.1, ANGLE_GEAR_RATIO - 0.05);

        telemetry.addLine();
        telemetry.addData("Tuning", "kP: %.1f | Ratio: %.3f", robot.kP, robot.ANGLE_GEAR_RATIO);
        telemetry.addData("", "D-pad ← → to adjust ratio live");
        telemetry.addData("RECENTER", "Press BACK button to reset angles");
        telemetry.update();
    }

    private OptimizedModuleState optimizeModuleAngle(double targetAngle,
                                                     double currentAngle,
                                                     double speed) {
        double error = targetAngle - currentAngle;

        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        if (Math.abs(error) > 90) {
            targetAngle += 180;
            while (targetAngle > 180) targetAngle -= 360;
            while (targetAngle < -180) targetAngle += 360;
            speed = -speed;
        }

        return new OptimizedModuleState(targetAngle, speed);
    }

    private static class OptimizedModuleState {
        double targetAngle;
        double speed;

        OptimizedModuleState(double targetAngle, double speed) {
            this.targetAngle = targetAngle;
            this.speed = speed;
        }
    }






}