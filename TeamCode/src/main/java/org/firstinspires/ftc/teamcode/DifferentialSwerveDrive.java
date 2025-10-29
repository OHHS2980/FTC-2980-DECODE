package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Differential Swerve with PID")
public class DifferentialSwerveDrive extends OpMode {

    // Motors for differential swerve - 2 motors per module
    DcMotorEx left1;  // leftz
    DcMotorEx left2;  // lefto
    DcMotorEx right1; // rightz
    DcMotorEx right2; // righto

    // Differential swerve parameters
    double TICKS_PER_REVOLUTION = 537.6;
    double ANGLE_GEAR_RATIO = 1.0; // Adjust this to match your differential ratio
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
        telemetry.addData("Tune PID", "kP=%.2f, kI=%.2f, kD=%.2f", kP, kI, kD);
        telemetry.update();
    }

    @Override
    public void loop() {
        // Get joystick input
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;

        // Calculate target angle and magnitude
        double targetAngleRad = Math.atan2(y, x);
        double targetAngleDeg = Math.toDegrees(targetAngleRad);
        double magnitude = Math.sqrt(x * x + y * y);
        magnitude = Math.min(magnitude, 1.0);

        // Get current module angles from differential encoder readings
        double leftCurrentAngle = getModuleAngle(left1, left2);
        double rightCurrentAngle = getModuleAngle(right1, right2);

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
        // motor1 velocity = drive + angle correction
        // motor2 velocity = drive - angle correction
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
        telemetry.addData("Target Angle", "%.1f°", targetAngleDeg);
        telemetry.addData("Magnitude", "%.2f", magnitude);
        telemetry.addLine();
        telemetry.addData("Left Module Angle", "%.1f°", leftCurrentAngle);
        telemetry.addData("Left Error", "%.1f°", leftLastError);
        telemetry.addData("Left Correction", "%.0f", leftAngleVelocity);
        telemetry.addLine();
        telemetry.addData("Right Module Angle", "%.1f°", rightCurrentAngle);
        telemetry.addData("Right Error", "%.1f°", rightLastError);
        telemetry.addData("Right Correction", "%.0f", rightAngleVelocity);
        telemetry.addLine();
        telemetry.addData("Drive Velocity", "%.0f", driveVelocity);
        telemetry.update();
    }

    /**
     * Calculate the current angle of a module from its two motor encoders
     */
    private double getModuleAngle(DcMotorEx motor1, DcMotorEx motor2) {
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