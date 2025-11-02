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

@TeleOp(name = "bethesda")
public class bethesda extends OpMode {

    // Motors for differential swerve - 2 motors per module
    DcMotorEx left1;
    DcMotorEx left2;
    DcMotorEx right1;
    DcMotorEx right2;

    CRServo intakeL;
    CRServo intakeR;
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

    // CONFIG: Set which modules use through bore encoders
    boolean USE_LEFT_THROUGH_BORE = false;  // CHANGE THIS IN INIT
    boolean USE_RIGHT_THROUGH_BORE = false; // CHANGE THIS IN INIT

    // Through bore encoder specs
    double THROUGH_BORE_CPR = 8192; // Counts per revolution for REV Through Bore

    // Encoder offsets (set when you press BACK to recenter)
    int leftEncoderOffset = 0;
    int rightEncoderOffset = -1998;

    // Differential swerve parameters
    double TICKS_PER_REVOLUTION = 8192;
    double ANGLE_GEAR_RATIO = 0.25;
    double MAX_DRIVE_VELOCITY = 2000;
    double MAX_ANGLE_VELOCITY = 4000;

    // PID constants
    double kP = 30.0;
    double kI = 0.2;
    double kD = 2.0;

    // PID state for left module
    double leftIntegral = 0;
    double leftLastError = 0;
    ElapsedTime leftTimer = new ElapsedTime();

    // PID state for right module
    double rightIntegral = 0;
    double rightLastError = 0;
    ElapsedTime rightTimer = new ElapsedTime();

    // Calibration mode
/*    boolean calibrationMode = false;
    int calibrationStartPos1 = 0;
    int calibrationStartPos2 = 0;
    ElapsedTime calibrationTimer = new ElapsedTime();
*/
    @Override
    public void init() {
        // ========== CONFIGURATION ==========
        // Set to true for modules that have REV Through Bore encoders installed
        USE_LEFT_THROUGH_BORE = true;
        USE_RIGHT_THROUGH_BORE = true;
        // ===================================

        intakeL = new CRServo(hardwareMap,"intakeL");
        intakeR = new CRServo(hardwareMap,"intakeR");
        flap = new SimpleServo(hardwareMap,"flap",-30,30);
        // Initialize motors
        left1 = hardwareMap.get(DcMotorEx.class, "left1");
        left2 = hardwareMap.get(DcMotorEx.class, "left2");
        right1 = hardwareMap.get(DcMotorEx.class, "right1");
        right2 = hardwareMap.get(DcMotorEx.class, "right2");
        outtakeL = hardwareMap.get(DcMotorEx.class,"outtakeL");
        outtakeR = hardwareMap.get(DcMotorEx.class, "outtakeR");

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
        if (USE_LEFT_THROUGH_BORE) {
            leftEncoder = hardwareMap.get(DcMotor.class, "leftEncoder");
            leftEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if (USE_RIGHT_THROUGH_BORE) {
            rightEncoder = hardwareMap.get(DcMotor.class, "rightEncoder");
            rightEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

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

        leftTimer.reset();
        rightTimer.reset();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Left Encoder", USE_LEFT_THROUGH_BORE ? "Through Bore" : "Motor Diff");
        telemetry.addData("Right Encoder", USE_RIGHT_THROUGH_BORE ? "Through Bore" : "Motor Diff");
        telemetry.addData("Drive Mode", "FIELD-ORIENTED");
        telemetry.addData("", "Press GP1 BACK to reset field orientation");
        telemetry.update();
        //flap.rotateByAngle(5);
    }

    @Override
    public void loop() {
        outtakeR.setPower(0.8);
        outtakeL.setPower(-0.8);
        // FIELD-ORIENTED RESET: Press BACK on CONTROLLER 1 to reset field orientation
        if (gamepad1.left_bumper)
            MAX_DRIVE_VELOCITY = 10000;
        if (gamepad1.back) {
            imu.resetYaw();
            headingOffset = 0;
            try { sleep(200); } catch (Exception e) {}
        }

        // RECENTER WHEELS: Press BACK button on CONTROLLER 2 to reset module angles to 0
        if (gamepad2.back) {
            // For motor encoders, reset them
            if (!USE_LEFT_THROUGH_BORE) {
                left1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                left2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                left1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                left2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else {
                // For through bore, store current position as offset
                leftEncoderOffset = leftEncoder.getCurrentPosition();
            }

            if (!USE_RIGHT_THROUGH_BORE) {
                right1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                right2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                right1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                right2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else {
                // For through bore, store current position as offset
                rightEncoderOffset = rightEncoder.getCurrentPosition();
            }

            try { sleep(200); } catch (Exception e) {}
        }
        intakeL.set(-1);
        intakeR.set(1);

        if (gamepad1.xWasReleased())
            flap.rotateByAngle(-30);
            try { sleep(200); } catch (Exception e) {}
            flap.rotateByAngle(30);

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
        double robotHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

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
        double leftCurrentAngle = getLeftModuleAngle();
        double rightCurrentAngle = getRightModuleAngle();

        double leftMotor1Vel = 0;
        double leftMotor2Vel = 0;
        double rightMotor1Vel = 0;
        double rightMotor2Vel = 0;

        // TEST MODE: Press A on CONTROLLER 2 for pure angle test
        if (gamepad2.a && magnitude > 0.1) {
            double leftAngleVel = calculatePID(
                    targetAngleDeg,
                    leftCurrentAngle,
                    leftTimer,
                    leftIntegral,
                    leftLastError,
                    true
            );

            double rightAngleVel = calculatePID(
                    targetAngleDeg,
                    rightCurrentAngle,
                    rightTimer,
                    rightIntegral,
                    rightLastError,
                    false
            );

            leftMotor1Vel = leftAngleVel;
            leftMotor2Vel = -leftAngleVel;
            rightMotor1Vel = rightAngleVel;
            rightMotor2Vel = -rightAngleVel;

            telemetry.addData("=== TEST MODE ===", "Pure Angle Control");
            telemetry.addData("Target", "%.1f°", targetAngleDeg);
            telemetry.addData("Current L", "%.1f°", leftCurrentAngle);
            telemetry.addData("Error L", "%.1f°", leftLastError);
            telemetry.addData("PID Output L", "%.0f", leftAngleVel);
            telemetry.addLine();
            telemetry.addData("Encoder Diff L", "%d",
                    left1.getCurrentPosition() - left2.getCurrentPosition());
            telemetry.addData("Angle calc", "(diff)/(2*%.2f) = %.1f ticks",
                    ANGLE_GEAR_RATIO,
                    (left1.getCurrentPosition() - left2.getCurrentPosition()) / (2.0 * ANGLE_GEAR_RATIO));
            telemetry.addData("Then * 360/8192", "= %.1f°", leftCurrentAngle);

        } else if (magnitude > 0.1) {
            // Normal operation with optimization
            OptimizedModuleState leftState = optimizeModuleAngle(
                    targetAngleDeg, leftCurrentAngle, magnitude);
            OptimizedModuleState rightState = optimizeModuleAngle(
                    targetAngleDeg, rightCurrentAngle, magnitude);

            double leftAngleVel = calculatePID(
                    leftState.targetAngle,
                    leftCurrentAngle,
                    leftTimer,
                    leftIntegral,
                    leftLastError,
                    true
            );

            double rightAngleVel = calculatePID(
                    rightState.targetAngle,
                    rightCurrentAngle,
                    rightTimer,
                    rightIntegral,
                    rightLastError,
                    false
            );

            double rotationScale = 0.5;

            // CRITICAL FIX: Reduce drive velocity when angle error is large
            // This allows the module to steer before driving at full speed
            double leftAngleError = Math.abs(leftLastError);
            double rightAngleError = Math.abs(rightLastError);
            double maxAngleError = Math.max(leftAngleError, rightAngleError);

            // Scale down drive speed if not pointing the right way
            double speedMultiplier = 1.0;
            if (maxAngleError > 45) {
                speedMultiplier = 0.3; // Very slow when far from target
            } else if (maxAngleError > 20) {
                speedMultiplier = 0.6; // Medium speed when getting close
            }
            // else full speed when angle error < 20 degrees

            double leftDriveVel = (leftState.speed + rotation * rotationScale) * MAX_DRIVE_VELOCITY * speedMultiplier;
            double rightDriveVel = (rightState.speed - rotation * rotationScale) * MAX_DRIVE_VELOCITY * speedMultiplier;

            leftMotor1Vel = leftDriveVel + leftAngleVel;
            leftMotor2Vel = leftDriveVel - leftAngleVel;
            rightMotor1Vel = rightDriveVel + rightAngleVel;
            rightMotor2Vel = rightDriveVel - rightAngleVel;

        } else if (Math.abs(rotation) > 0.1) {
            double rotationVel = rotation * MAX_DRIVE_VELOCITY * 0.5;
            leftMotor1Vel = rotationVel;
            leftMotor2Vel = rotationVel;
            rightMotor1Vel = -rotationVel;
            rightMotor2Vel = -rotationVel;

            leftIntegral = 0;
            rightIntegral = 0;
            leftLastError = 0;
            rightLastError = 0;
        } else {
            leftIntegral = 0;
            rightIntegral = 0;
            leftLastError = 0;
            rightLastError = 0;
        }

        // Set motor velocities
        left1.setVelocity(leftMotor1Vel);
        left2.setVelocity(leftMotor2Vel);
        right1.setVelocity(rightMotor1Vel);
        right2.setVelocity(rightMotor2Vel);

        // Telemetry
        telemetry.addData("Mode", "Field-Oriented (GP1 BACK to reset)");
        telemetry.addData("Robot Heading", "%.1f°", Math.toDegrees(robotHeading));
        telemetry.addData("Target", "%.1f°", targetAngleDeg);
        telemetry.addLine();

        // Left module telemetry
        if (!USE_LEFT_THROUGH_BORE) {
            telemetry.addData("Left (Motor Encoders)", "M1: %d | M2: %d | Diff: %d",
                    left1.getCurrentPosition(), left2.getCurrentPosition(),
                    left1.getCurrentPosition() - left2.getCurrentPosition());
        } else {
            telemetry.addData("Left (Through Bore)", "Pos: %d = %.1f°",
                    leftEncoder.getCurrentPosition(), leftCurrentAngle);
        }
        telemetry.addData("Left", "Angle: %.1f° | Error: %.1f°",
                leftCurrentAngle, leftLastError);
        telemetry.addData("Left Vel", "M1: %.0f | M2: %.0f", leftMotor1Vel, leftMotor2Vel);
        telemetry.addLine();

        // Right module telemetry
        if (!USE_RIGHT_THROUGH_BORE) {
            telemetry.addData("Right (Motor Encoders)", "M1: %d | M2: %d | Diff: %d",
                    right1.getCurrentPosition(), right2.getCurrentPosition(),
                    right1.getCurrentPosition() - right2.getCurrentPosition());
        } else {
            telemetry.addData("Right (Through Bore)", "Pos: %d = %.1f°",
                    rightEncoder.getCurrentPosition(), rightCurrentAngle);
        }
        telemetry.addData("Right", "Angle: %.1f° | Error: %.1f°",
                rightCurrentAngle, rightLastError);
        telemetry.addData("Right Vel", "M1: %.0f | M2: %.0f", rightMotor1Vel, rightMotor2Vel);

        // Live tuning
        //if (gamepad1.dpad_up) kP += 1.0;
        //if (gamepad1.dpad_down) kP = Math.max(0, kP - 1.0);
        //if (gamepad1.dpad_right) ANGLE_GEAR_RATIO += 0.05;
        //if (gamepad1.dpad_left) ANGLE_GEAR_RATIO = Math.max(0.1, ANGLE_GEAR_RATIO - 0.05);

        telemetry.addLine();
        telemetry.addData("Tuning", "kP: %.1f | Ratio: %.3f", kP, ANGLE_GEAR_RATIO);
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

    private double getModuleAngle(DcMotorEx motor1, DcMotorEx motor2) {
        int pos1 = motor1.getCurrentPosition();
        int pos2 = motor2.getCurrentPosition();

        double angleTicks = (pos1 - pos2) / (2.0 * ANGLE_GEAR_RATIO);
        double angleDegrees = (angleTicks / TICKS_PER_REVOLUTION) * 360.0;

        while (angleDegrees > 180) angleDegrees -= 360;
        while (angleDegrees < -180) angleDegrees += 360;

        return angleDegrees;
    }

    /**
     * Get angle from REV Through Bore encoder (absolute position)
     * Through Bore outputs 8192 counts per revolution via digital encoder
     */
    private double getThroughBoreAngle(DcMotor encoder, int offset) {
        int rawPosition = encoder.getCurrentPosition();
        int position = rawPosition - offset; // Apply offset for zeroing

        // Convert encoder counts to degrees
        // 8192 counts = 360 degrees
        double angle = (position % (int)THROUGH_BORE_CPR) * 360.0 / THROUGH_BORE_CPR;

        // Normalize to -180 to 180
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;

        return angle;
    }

    /**
     * Get module angle - uses either motor encoders or through bore based on config
     */
    private double getLeftModuleAngle() {
        if (USE_LEFT_THROUGH_BORE) {
            return -getThroughBoreAngle(leftEncoder, leftEncoderOffset);
        } else {
            return getModuleAngle(left1, left2);
        }
    }

    private double getRightModuleAngle() {
        if (USE_RIGHT_THROUGH_BORE) {
            return getThroughBoreAngle(rightEncoder, rightEncoderOffset);
        } else {
            return getModuleAngle(right1, right2);
        }
    }

    private double calculatePID(double target, double current, ElapsedTime timer,
                                double integral, double lastError, boolean isLeft) {
        double error = target - current;

        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        double dt = timer.seconds();
        timer.reset();

        if (dt < 0.001 || dt > 1.0) dt = 0.02;

        integral += error * dt;
        double integralMax = 50.0;
        integral = Math.max(-integralMax, Math.min(integralMax, integral));

        double derivative = (error - lastError) / dt;

        double output = kP * error + kI * integral + kD * derivative;

        if (isLeft) {
            leftIntegral = integral;
            leftLastError = error;
        } else {
            rightIntegral = integral;
            rightLastError = error;
        }

        return Math.max(-MAX_ANGLE_VELOCITY, Math.min(MAX_ANGLE_VELOCITY, output));
    }
}