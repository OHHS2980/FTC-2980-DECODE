package org.firstinspires.ftc.teamcode.subsystems;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
public class Drivebase extends SubsystemBase {


    DcMotorEx backLeft;
    DcMotorEx frontLeft;
    DcMotorEx backRight;
    DcMotorEx frontRight;

    double frontLeftPower;
    double frontRightPower;
    double backLeftPower;
    double backRightPower;

    public Drivebase(HardwareMap hardwareMap){
        backLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");

        frontLeft = hardwareMap.get(DcMotorEx.class, "backLeft");

        backRight = hardwareMap.get(DcMotorEx.class, "frontRight");

        frontRight = hardwareMap.get(DcMotorEx.class, "backRight");

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
    }




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

        backLeft.setPower(frontLeftPower);
        frontLeft.setPower(backLeftPower);

        backRight.setPower(frontRightPower);
        frontRight.setPower(backRightPower);
    }
}
