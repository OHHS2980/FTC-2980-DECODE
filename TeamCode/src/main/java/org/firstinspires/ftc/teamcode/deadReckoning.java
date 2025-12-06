package org.firstinspires.ftc.teamcode;
import com.arcrobotics.ftclib.command.CommandBase;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.Drivebase;
import org.firstinspires.ftc.teamcode.subsystems.InOuttake;
import org.firstinspires.ftc.teamcode.commands.runIntakeCommand;

@Autonomous
public class deadReckoning extends OpMode {

    private InOuttake inOuttake;
    private Drivebase drivebase;
    private GamepadEx gamepad;



    @Override
    public void init() {
        gamepad = new GamepadEx(gamepad1);

     inOuttake = new InOuttake(hardwareMap);

     drivebase = new Drivebase(hardwareMap);





    }


    @Override
    public void loop(){
        inOuttake.runIntake(10);

        drivebase.simpleDrive(0,30,0);
    }





}


