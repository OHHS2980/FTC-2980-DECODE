package org.firstinspires.ftc.teamcode;
import com.arcrobotics.ftclib.command.CommandBase;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.subsystems.InOuttake;
import org.firstinspires.ftc.teamcode.commands.runIntakeCommand;

@Autonomous
public class deadReckoning extends CommandOpMode {

    private InOuttake subsystem;
    private GamepadEx gamepad;

    @Override
    public void initialize() {
        gamepad = new GamepadEx(gamepad1);

     subsystem = new InOuttake(hardwareMap);

        subsystem.runIntake(10);

    }


}


