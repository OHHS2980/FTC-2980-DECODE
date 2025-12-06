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




    @Override
    public void init() {


     inOuttake = new InOuttake(hardwareMap);

     drivebase = new Drivebase(hardwareMap);





    }


    @Override
    public void loop(){
        inOuttake.runOuttake(1);

        drivebase.simpleDrive(0,30,0);
    }





}


