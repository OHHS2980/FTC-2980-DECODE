package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.command.button.GamepadButton;

import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Commands.changeState;

@TeleOp
public class TELEOP_TEMP extends LinearOpMode {
    Limelight3A limelight;

    MecanumDrive mecanum;
    Motor flywheel;

    PIDController autoAlignPID;
    public void changeFlywheelSpeed()
    {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid() && result.getPipelineIndex() == 0) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            double ty = result.getTy(); // How far up or down the target is (degrees)

            // apriltag height = 30
            //limelight height = 10
            double distance = (20) / Math.tan(ty);

            double proportion = (distance / 200);

            flywheel.set(0.5 + proportion * 0.5);
        }
    }

    public void autoAlign()
    {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid() && result.getPipelineIndex() == 0) {
            double tx = result.getTx(); // How far left or right the target is (degrees)

            double speed = autoAlignPID.calculate(tx, 0);

            mecanum.driveWithMotorPowers(-speed, speed, speed, -speed);

        }
    }


    public enum STATE
    {
        driving,
        autoalign
    }

    STATE state;

    public void changeState(STATE newState)
    {
        state = newState;
    }


    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        //12
        //30

        state = STATE.driving;

        waitForStart();

        autoAlignPID.setPID(0.2, 0, 0.1);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();

        GamepadEx driveOp = new GamepadEx(gamepad1);
        Button buttonB = new GamepadButton(driveOp, GamepadKeys.Button.RIGHT_BUMPER);

        buttonB.whenPressed(new changeState());


        Servo pushServo = hardwareMap.get(Servo.class, "servo");
        flywheel = new Motor(hardwareMap, "flywheel");
        Motor fl = new Motor(hardwareMap, "fl"); //3 // REAL = 0
        Motor bl = new Motor(hardwareMap, "bl"); // 1  // REAL = 1
        Motor fr = new Motor(hardwareMap, "fr"); //2 // REAL = 2 // V
        Motor br = new Motor(hardwareMap, "br"); // 0 // REAL = 2 // V

        mecanum = new MecanumDrive(
                fl,
                fr,
                bl,
                br
        );

        flywheel.set(0.5);


        while (opModeIsActive()) {

            changeFlywheelSpeed();

            if (gamepad1.a)
            {
                pushServo.setPosition(0);
            }
            else
            {
                pushServo.setPosition(0.5);
            }

            switch (state)
            {
                case driving:

                    mecanum.driveRobotCentric(
                            -driveOp.getLeftX(),
                            driveOp.getRightX(),
                            driveOp.getLeftY()

                    );

                case autoalign:

                    autoAlign();

                default:

            }
        }
    }
}