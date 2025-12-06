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

@TeleOp(name = "desperation")
public class desperation extends OpMode {


    DcMotorEx backLeft;
    DcMotorEx frontLeft;
    DcMotorEx backRight;
    DcMotorEx frontRight;

    ServoEx flap;

    DcMotorEx outtake2;
    DcMotorEx intake;

    DcMotorEx outtake1;


    // IMU for field-oriented control
    IMU imu;
    double headingOffset = 0; // Stores the heading when field orientation is reset

    // Optional: REV Through Bore Encoders for absolute angle sensing
    // Using DcMotor just for reading encoder position (not for motor control)
    DcMotor leftEncoder;
    DcMotor rightEncoder;
    DcMotor intake2;

    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;




    @Override
    public void init() {





        // Initialize motors
        backLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontRight = hardwareMap.get(DcMotorEx.class, "backRight");

        /*flap = new SimpleServo(hardwareMap,"flap",-30,30);
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        outtake1 = hardwareMap.get(DcMotorEx.class,"outtake1");
        outtake2 = hardwareMap.get(DcMotorEx.class, "outtake2");
                intake2 = hardwareMap.get(DcMotorEx.class, "intake2");*/

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
        backLeft.setDirection(DcMotorEx.Direction.REVERSE);
        frontLeft.setDirection(DcMotorEx.Direction.FORWARD);
        backRight.setDirection(DcMotorEx.Direction.REVERSE);
        frontRight.setDirection(DcMotorEx.Direction.FORWARD);

        // Reset encoders
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set to run using encoder
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set zero power behavior
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        telemetry.addData("Status", "Initialized");
        telemetry.addData("Drive Mode", "FIELD-ORIENTED");
        //telemetry.addData("", "Press GP1 BACK to reset field orientation");
        telemetry.update();
        //flap.rotateByAngle(5);



    }

    @Override
    public void loop() {


        /*
         * MICHI NOTE:
         * this is where the code will check gamepad input
         */
        intake.setPower(-gamepad1.right_trigger);
        intake2.setPower(gamepad1.left_trigger);
        outtake1.setPower(gamepad1.right_trigger);
        outtake2.setPower(-gamepad1.right_trigger);
        if (gamepad1.a) {
            outtake1.setDirection(DcMotorSimple.Direction.REVERSE);
            outtake2.setDirection(DcMotorSimple.Direction.REVERSE);
            intake2.setDirection(DcMotorSimple.Direction.REVERSE);
        } else {
            outtake1.setDirection(DcMotorSimple.Direction.FORWARD);
            outtake2.setDirection(DcMotorSimple.Direction.FORWARD);
            intake2.setDirection(DcMotorSimple.Direction.FORWARD);
        }


        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;

        simpleDrive(x, y, gamepad1.right_stick_x);


        //IDK IF THESE ACTUALLY CORROSPOND TO THE MOTOR IN THE ARGUMENT!!
        backLeft.setPower(frontLeftPower);
        frontLeft.setPower(backLeftPower);

        backRight.setPower(frontRightPower);
        frontRight.setPower(backRightPower);
    }


    /*
     * MICHI NOTE:
     * SimpleDrive is the basic method that takes the values leftstickX, leftstickY, and rightStickY
     * using these values, it determines what the motors should do. \
     * this method is called in the Loop() method.
     * after this method, the output goes back to Loop() in line 224.
     */




    public void simpleDrive(double leftStickX, double leftStickY, double rightStickX){

        double x = leftStickX;
        double y = leftStickY;
        double turn = rightStickX;

        double theta = Math.atan2(y,x);
        double  power = Math.hypot(x,y);

        double sin = Math.sin(theta - Math.PI/4);
        double cos = Math.cos(theta - Math.PI/4);
        double max = Math.max(Math.abs(sin), Math.abs(cos));

        frontLeftPower = power * cos/max + turn;
        frontRightPower = power * sin/max - turn;
        backLeftPower = power* sin/max + turn;
        backRightPower = power * cos/max - turn;

        if ((power + Math.abs(turn)) > 1 ){
            frontLeftPower /= power + turn ;
            frontRightPower /= power + turn ;
            backLeftPower /= power + turn ;
            backRightPower /= power + turn;

        }
    }

}