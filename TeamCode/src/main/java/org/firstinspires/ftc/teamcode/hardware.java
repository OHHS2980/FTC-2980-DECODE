package org.firstinspires.ftc.teamcode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.util.ElapsedTime;
//class that names motors

public class hardware {

    DcMotorEx left1;
    DcMotorEx left2;
    DcMotorEx right1;
    DcMotorEx right2;

    com.arcrobotics.ftclib.hardware.motors.CRServo intakeL;
    CRServo intakeR;
    ServoEx flap;

    DcMotorEx outtakeR;

    DcMotorEx outtakeL;

    // IMU for field-oriented control
    IMU imu;


    //variables

    // CONFIG: Set which modules use through bore encoders
    boolean USE_LEFT_THROUGH_BORE = false;  // CHANGE THIS IN INIT
    boolean USE_RIGHT_THROUGH_BORE = false; // CHANGE THIS IN INIT

    // Through bore encoder specs
    double THROUGH_BORE_CPR = 8192; // Counts per revolution for REV Through Bore

    // Encoder offsets (set when you press BACK to recenter)
    int leftEncoderOffset = 0;
    int rightEncoderOffset = 160;

    // Differential swerve parameters
    double TICKS_PER_REVOLUTION = 8192;
    double ANGLE_GEAR_RATIO = 0.3;
    double MAX_DRIVE_VELOCITY = 5000;
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

    double headingOffset = 0; // Stores the heading when field orientation is reset

    // Optional: REV Through Bore Encoders for absolute angle sensing
    // Using DcMotor just for reading encoder position (not for motor control)
    DcMotor leftEncoder;
    DcMotor rightEncoder;


    public void init(HardwareMap ahwMap) {

        left1 = hardwareMap.get(DcMotorEx.class, "left1");
        left2 = hardwareMap.get(DcMotorEx.class, "left2");
        right1 = hardwareMap.get(DcMotorEx.class, "right1");
        right2 = hardwareMap.get(DcMotorEx.class, "right2");
        outtakeL = hardwareMap.get(DcMotorEx.class,"outtakeL");
        outtakeR = hardwareMap.get(DcMotorEx.class, "outtakeR");




        // Set motor directions
        left1.setDirection(DcMotorEx.Direction.FORWARD);
        left2.setDirection(DcMotorEx.Direction.FORWARD);
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

        // ========== CONFIGURATION ==========
        // Set to true for modules that have REV Through Bore encoders installed
        USE_LEFT_THROUGH_BORE = true;
        USE_RIGHT_THROUGH_BORE = true;
        // ===================================

        intakeL = new CRServo(hardwareMap,"intakeL");
        intakeR = new CRServo(hardwareMap,"intakeR");
        flap = new SimpleServo(hardwareMap,"flap",0,15);
        // Initialize motors

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

        leftTimer.reset();
        rightTimer.reset();



    }




    public double calculatePID(double target, double current, ElapsedTime timer,
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


    public void encoderReset(){
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

    public void flapRotate() {
        flap.rotateByAngle(15);
        try {
            sleep(200);
        } catch (Exception e) {
        }
        flap.rotateByAngle(-15);
    }

    public void runIntake(){
        intakeL.set(-1);
        intakeR.set(1);
    }

    public void stopIntake(){
        intakeL.set(0);
        intakeR.set(0);
    }
    /**
     * Get module angle - uses either motor encoders or through bore based on config
     */
    public double getLeftModuleAngle() {
        if (USE_LEFT_THROUGH_BORE) {
            return getThroughBoreAngle(leftEncoder, leftEncoderOffset);
        } else {
            return getModuleAngle(left1, left2);
        }
    }

    public double getRightModuleAngle() {
        if (USE_RIGHT_THROUGH_BORE) {
            return getThroughBoreAngle(rightEncoder, rightEncoderOffset);
        } else {
            return getModuleAngle(right1, right2);
        }
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

    private double getModuleAngle(DcMotorEx motor1, DcMotorEx motor2) {
        int pos1 = motor1.getCurrentPosition();
        int pos2 = motor2.getCurrentPosition();

        double angleTicks = (pos1 - pos2) / (2.0 * ANGLE_GEAR_RATIO);
        double angleDegrees = (angleTicks / TICKS_PER_REVOLUTION) * 360.0;

        while (angleDegrees > 180) angleDegrees -= 360;
        while (angleDegrees < -180) angleDegrees += 360;

        return angleDegrees;
    }




}
