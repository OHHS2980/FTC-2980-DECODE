package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "coffee")
public class coffee extends OpMode {

    // ========== CONTROL BINDINGS ==========
    // Gamepad1 Controls:
    // - LEFT_STICK: Drive control (X and Y)
    // - A BUTTON: Calibrate module encoder offsets
    // - B BUTTON: Toggle field-oriented / robot-oriented mode
    // - Y BUTTON: Reset IMU heading (zero field orientation)
    // ======================================

    // Motors for differential swerve - 2 motors per module
    DcMotorEx left1;  // leftz
    DcMotorEx left2;  // lefto
    DcMotorEx right1; // rightz
    DcMotorEx right2; // righto

    // Through-bore absolute encoders (REV or similar analog encoders)
    AnalogInput leftEncoder;
    AnalogInput rightEncoder;

    // IMU for field-oriented drive
    IMU imu;
    double imuOffset = 0.0; // Offset to reset heading

    // Encoder offsets (calibrate these to set "forward" position)
    // Set these values when your modules are pointed forward (0 degrees)
    double LEFT_ENCODER_OFFSET = 0.0;  // Degrees
    double RIGHT_ENCODER_OFFSET = 0.0; // Degrees

    // Choose which angle source to use
    boolean USE_THROUGHBORE_ENCODERS = true; // Set to false to use motor encoders

    // Field-oriented control toggle
    boolean fieldOriented = true; // Set to false for robot-oriented
    boolean lastBPress = false;   // For toggle debounce
    boolean lastYPress = false;   // For IMU reset debounce

    // Differential swerve parameters
    double TICKS_PER_REVOLUTION = 8192;
    double ANGLE_GEAR_RATIO = 108.0/65.0; // Adjust this to match your differential ratio
    double MAX_DRIVE_VELOCITY = 2000; // Max velocity in ticks per second

    // PID constants - TUNE THESE VALUES
    double kP = 15.0;  // Proportional gain
    double kI = 0.0;   // Integral gain
    double kD = 0.5;   // Derivative gain

    // PID state for left module
    double leftIntegral = 0;
    double leftLastError = 0;
    ElapsedTime leftTimer = new ElapsedTime();

    // PID state for right module
    double rightIntegral = 0;
    double rightLastError = 0;
    ElapsedTime rightTimer = new ElapsedTime();

    @Override
    public void init() {
        // Initialize motors
        left1 = hardwareMap.get(DcMotorEx.class, "left1");
        left2 = hardwareMap.get(DcMotorEx.class, "left2");
        right1 = hardwareMap.get(DcMotorEx.class, "right1");
        right2 = hardwareMap.get(DcMotorEx.class, "right2");

        // Initialize through-bore encoders
        leftEncoder = hardwareMap.get(AnalogInput.class, "leftEncoder");
        rightEncoder = hardwareMap.get(AnalogInput.class, "rightEncoder");

        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");

        // Define the hub orientation - ADJUST THESE FOR YOUR ROBOT
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(logoDirection, usbDirection);

        imu.initialize(new IMU.Parameters(orientationOnRobot));
        imu.resetYaw();

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
        telemetry.addData("Encoder Source", USE_THROUGHBORE_ENCODERS ? "Through-Bore" : "Motor Differential");
        telemetry.addData("Drive Mode", fieldOriented ? "Field-Oriented" : "Robot-Oriented");
        telemetry.addData("Tune PID", "kP=%.2f, kI=%.2f, kD=%.2f", kP, kI, kD);
        telemetry.update();
    }

    @Override
    public void loop() {
        // Get joystick input
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;

        // CALIBRATION MODE: Press 'A' to set current position as offset
        if (gamepad1.a) {
            LEFT_ENCODER_OFFSET = readThroughBoreAngleRaw(leftEncoder);
            RIGHT_ENCODER_OFFSET = readThroughBoreAngleRaw(rightEncoder);
            telemetry.addData("CALIBRATED", "Module Offsets Set!");
            telemetry.addData("Left Offset", "%.1f°", LEFT_ENCODER_OFFSET);
            telemetry.addData("Right Offset", "%.1f°", RIGHT_ENCODER_OFFSET);
            telemetry.update();
            sleep(500); // Debounce
        }

        // TOGGLE FIELD-ORIENTED: Press 'B' to toggle
        if (gamepad1.b && !lastBPress) {
            fieldOriented = !fieldOriented;
            telemetry.addData("MODE CHANGED", fieldOriented ? "Field-Oriented" : "Robot-Oriented");
            telemetry.update();
            sleep(200); // Debounce
        }
        lastBPress = gamepad1.b;

        // RESET IMU HEADING: Press 'Y' to reset field orientation
        if (gamepad1.y && !lastYPress) {
            imu.resetYaw();
            imuOffset = 0.0;
            telemetry.addData("IMU RESET", "Field heading zeroed!");
            telemetry.update();
            sleep(200); // Debounce
        }
        lastYPress = gamepad1.y;

        // Get robot heading from IMU
        double robotHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

        // Apply field-oriented transformation if enabled
        double transformedX = x;
        double transformedY = y;

        if (fieldOriented) {
            // Rotate joystick input by robot heading
            double headingRad = Math.toRadians(robotHeading);
            transformedX = x * Math.cos(-headingRad) - y * Math.sin(-headingRad);
            transformedY = x * Math.sin(-headingRad) + y * Math.cos(-headingRad);
        }

        // Calculate target angle and magnitude
        double targetAngleRad = Math.atan2(transformedY, transformedX);
        double targetAngleDeg = Math.toDegrees(targetAngleRad);
        double magnitude = Math.sqrt(x * x + y * y);
        magnitude = Math.min(magnitude, 1.0);

        // Get current module angles from selected source
        double leftCurrentAngle, rightCurrentAngle;

        if (USE_THROUGHBORE_ENCODERS) {
            leftCurrentAngle = readThroughBoreAngle(leftEncoder, LEFT_ENCODER_OFFSET);
            rightCurrentAngle = readThroughBoreAngle(rightEncoder, RIGHT_ENCODER_OFFSET);
        } else {
            leftCurrentAngle = getModuleAngleFromMotors(left1, left2);
            rightCurrentAngle = getModuleAngleFromMotors(right1, right2);
        }

        // Calculate drive velocity from magnitude
        double driveVelocity = magnitude * MAX_DRIVE_VELOCITY;

        double leftAngleVelocity = 0;
        double rightAngleVelocity = 0;

        // Only apply angle control if joystick has significant input
        if (magnitude > 0.1) {
            // PID control for left module
            leftAngleVelocity = calculatePID(
                    targetAngleDeg,
                    leftCurrentAngle,
                    leftTimer,
                    leftIntegral,
                    leftLastError,
                    true // isLeft
            );

            // PID control for right module
            rightAngleVelocity = calculatePID(
                    targetAngleDeg,
                    rightCurrentAngle,
                    rightTimer,
                    rightIntegral,
                    rightLastError,
                    false // isRight
            );
        } else {
            // Reset integral when stopped
            leftIntegral = 0;
            rightIntegral = 0;
            leftLastError = 0;
            rightLastError = 0;
        }

        // Calculate differential velocities for each module
        double leftMotor1Vel = driveVelocity + leftAngleVelocity;
        double leftMotor2Vel = driveVelocity - leftAngleVelocity;
        double rightMotor1Vel = driveVelocity + rightAngleVelocity;
        double rightMotor2Vel = driveVelocity - rightAngleVelocity;

        // Set motor velocities
        left1.setVelocity(leftMotor1Vel);
        left2.setVelocity(leftMotor2Vel);
        right1.setVelocity(rightMotor1Vel);
        right2.setVelocity(rightMotor2Vel);

        // Telemetry
        telemetry.addData("Drive Mode", fieldOriented ? "FIELD-ORIENTED" : "Robot-Oriented");
        telemetry.addData("Robot Heading", "%.1f°", robotHeading);
        telemetry.addData("Encoder Mode", USE_THROUGHBORE_ENCODERS ? "Through-Bore" : "Motor Diff");
        telemetry.addLine();
        telemetry.addData("Joystick", "X:%.2f Y:%.2f", x, y);
        telemetry.addData("Target Angle", "%.1f°", targetAngleDeg);
        telemetry.addData("Magnitude", "%.2f", magnitude);
        telemetry.addLine();
        telemetry.addData("Left Module Angle", "%.1f°", leftCurrentAngle);
        telemetry.addData("Left Raw Voltage", "%.3fV", leftEncoder.getVoltage());
        telemetry.addData("Left Error", "%.1f°", leftLastError);
        telemetry.addLine();
        telemetry.addData("Right Module Angle", "%.1f°", rightCurrentAngle);
        telemetry.addData("Right Raw Voltage", "%.3fV", rightEncoder.getVoltage());
        telemetry.addData("Right Error", "%.1f°", rightLastError);
        telemetry.addLine();
        telemetry.addData("Controls", "A:Calibrate | B:Toggle Mode | Y:Reset Heading");
        telemetry.update();
    }

    /**
     * Read angle from through-bore encoder with offset applied
     */
    private double readThroughBoreAngle(AnalogInput encoder, double offset) {
        double rawAngle = readThroughBoreAngleRaw(encoder);
        double angle = rawAngle - offset;

        // Normalize to -180 to 180
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;

        return angle;
    }

    /**
     * Read raw angle from through-bore encoder (REV or similar)
     * REV Through Bore Encoder outputs 0-3.3V for 0-360 degrees
     */
    private double readThroughBoreAngleRaw(AnalogInput encoder) {
        double voltage = encoder.getVoltage();
        double maxVoltage = 3.3; // REV Through Bore uses 3.3V

        // Convert voltage to degrees (0-360)
        double angle = (voltage / maxVoltage) * 360.0;

        return angle;
    }

    /**
     * Calculate the current angle of a module from its two motor encoders
     * (Original method - kept as backup)
     */
    private double getModuleAngleFromMotors(DcMotorEx motor1, DcMotorEx motor2) {
        int pos1 = motor1.getCurrentPosition();
        int pos2 = motor2.getCurrentPosition();

        // Differential angle calculation
        double angleTicks = (pos1 - pos2) / (2.0 * ANGLE_GEAR_RATIO);

        // Convert to degrees
        double angleDegrees = (angleTicks / TICKS_PER_REVOLUTION) * 360.0;

        // Normalize to -180 to 180
        while (angleDegrees > 180) angleDegrees -= 360;
        while (angleDegrees < -180) angleDegrees += 360;

        return angleDegrees;
    }

    /**
     * PID controller for module angle
     */
    private double calculatePID(double target, double current, ElapsedTime timer,
                                double integral, double lastError, boolean isLeft) {
        // Calculate error with wrap-around handling
        double error = target - current;

        // Normalize error to -180 to 180
        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        // Calculate delta time
        double dt = timer.seconds();
        timer.reset();

        // Prevent division by zero
        if (dt == 0) dt = 0.02;

        // Integral term with anti-windup
        integral += error * dt;
        integral = Math.max(-100, Math.min(100, integral)); // Clamp integral

        // Derivative term
        double derivative = (error - lastError) / dt;

        // Calculate output
        double output = kP * error + kI * integral + kD * derivative;

        // Update state
        if (isLeft) {
            leftIntegral = integral;
            leftLastError = error;
        } else {
            rightIntegral = integral;
            rightLastError = error;
        }

        // Clamp output
        return Math.max(-MAX_DRIVE_VELOCITY, Math.min(MAX_DRIVE_VELOCITY, output));
    }
}