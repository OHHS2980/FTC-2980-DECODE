package org.firstinspires.ftc.teamcode.Subsystems;

//import com.arcrobotics.ftclib.command.SubsystemBase;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drive extends SubsystemBase {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    MecanumDrive mecanum;
    GamepadEx gamepad;

    //public Drive is the constructor (method that is called when Drive
    //is created by teleop

    public Drive(HardwareMap hardwareMap, GamepadEx gamepad)
    {

        frontLeft = hardwareMap.get(DcMotor.class,"motor0");
        frontRight = hardwareMap.get(DcMotor.class,"motor1");
        backLeft = hardwareMap.get(DcMotor.class,"motor2");
        backRight = hardwareMap.get(DcMotor.class,"motor3");

        mecanum = new MecanumDrive(
                (Motor) frontLeft,
                (Motor) frontRight,
                (Motor) backLeft,
                (Motor) backRight
        );

        this.gamepad = gamepad;
    }

    @Override
    public void periodic(){
        mecanum.driveRobotCentric(
                gamepad.getLeftX(),
                gamepad.getLeftY(),
                gamepad.getRightY()

        );
    }

}
