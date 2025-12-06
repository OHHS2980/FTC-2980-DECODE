package org.firstinspires.ftc.teamcode;


import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.teamcode.subsystems.Drivebase;
import org.firstinspires.ftc.teamcode.subsystems.InOuttake;

@TeleOp(name = "desparate teleop2")

public class desparateTeleop2 extends OpMode {

    private GamepadEx gamepad1;
    private Drivebase drivebase;
    private InOuttake inOuttake;


    @Override
    public void init() {
        gamepad1 = gamepad1;
        drivebase = new Drivebase(hardwareMap);
        inOuttake = new InOuttake(hardwareMap);




    }

    @Override
    public void loop() {
        double x = gamepad1.gamepad.left_stick_x;
        double y = -gamepad1.gamepad.left_stick_y;

        drivebase.simpleDrive(x, y, gamepad1.gamepad.right_stick_x);


    }
}
