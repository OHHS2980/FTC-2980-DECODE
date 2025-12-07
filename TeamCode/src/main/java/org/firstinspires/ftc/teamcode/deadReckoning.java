package org.firstinspires.ftc.teamcode;
import com.arcrobotics.ftclib.command.CommandBase;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.Drivebase;
import org.firstinspires.ftc.teamcode.subsystems.InOuttake;
import org.firstinspires.ftc.teamcode.commands.runIntakeCommand;

@Autonomous
public class deadReckoning extends LinearOpMode {

    private InOuttake inOuttake;
    private Drivebase drivebase;





    @Override
    public void runOpMode() throws InterruptedException {
        inOuttake = new InOuttake(hardwareMap);

        drivebase = new Drivebase(hardwareMap);

        drivebase.simpleDrive(0,30,0);
        sleep(500);
        drivebase.simpleDrive(10,0,0);
        sleep(500);
        inOuttake.runOuttake(10);
    }
}


